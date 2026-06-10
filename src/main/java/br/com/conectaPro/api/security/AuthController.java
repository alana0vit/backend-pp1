package br.com.conectaPro.api.security;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.conectaPro.dto.ForgotPasswordDTO;
import br.com.conectaPro.dto.LoginRequestDTO;
import br.com.conectaPro.dto.LoginResponseDTO;
import br.com.conectaPro.dto.ResetPasswordDTO;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth/Login")
public class AuthController {

    private final UserRepository userRepository;

    @Operation(summary = "Faz login no sistema")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {

        // 1. Busca o usuário pelo e-mail
        Optional<User> userOpt = userRepository.findByEmail(request.email());

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // 2. Checa se a senha enviada bate com o hash do banco
            // if (passwordEncoder.matches(request.password(), user.getPassword())) { //
            // Comentado para não usar o pwd encoder
            boolean senhaValida = request.password().equals(user.getPassword());
            if (senhaValida) {
                // Login bem sucedido - Retornamos os dados sem o Token
                return ResponseEntity.ok(new LoginResponseDTO(
                        "sou um token", // Token enviado como genérico
                        user.getId(),
                        user.getName(),
                        user.getUserType()));
            }
        }

        // 3. Se falhar e-mail ou senha, retorna 401
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha inválidos");
    }

    @Operation(summary = "Solicita recuperação de senha")
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody ForgotPasswordDTO request) {

        Optional<User> userOpt = userRepository.findByEmail(request.email());

        if (userOpt.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Usuário não encontrado.");
        }

        User user = userOpt.get();

        String token = UUID.randomUUID().toString();

        user.setRecoveryToken(token);
        user.setRecoveryTokenExpiration(
                LocalDateTime.now().plusMinutes(30));

        userRepository.save(user);

        return ResponseEntity.ok(token);
    }

    @Operation(summary = "Redefine a senha")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordDTO request) {

        Optional<User> userOpt = userRepository.findByRecoveryToken(request.token());

        if (userOpt.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Token inválido.");
        }

        User user = userOpt.get();

        if (user.getRecoveryTokenExpiration() == null
                || user.getRecoveryTokenExpiration()
                        .isBefore(LocalDateTime.now())) {

            return ResponseEntity
                    .badRequest()
                    .body("Token expirado.");
        }

        user.setPassword(request.newPassword());

        user.setRecoveryToken(null);
        user.setRecoveryTokenExpiration(null);

        userRepository.save(user);

        return ResponseEntity.ok("Senha alterada com sucesso.");
    }
}