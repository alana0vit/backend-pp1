package br.com.conectaPro.api.security;

import br.com.conectaPro.dto.LoginRequestDTO;
import br.com.conectaPro.dto.LoginResponseDTO;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {

        // 1. Busca o usuário pelo e-mail
        Optional<User> userOpt = userRepository.findByEmail(request.email());

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // 2. Checa se a senha enviada bate com o hash do banco
            if (passwordEncoder.matches(request.password(), user.getPassword())) {

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
}