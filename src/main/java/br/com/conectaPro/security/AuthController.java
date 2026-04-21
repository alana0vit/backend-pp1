package br.com.conectaPro.security;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.conectaPro.dto.LoginRequestDTO;
import br.com.conectaPro.dto.LoginResponseDTO;
import br.com.conectaPro.model.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loging(@Valid @RequestBody LoginRequestDTO request) {

        // Autentica o usuário usando o CustomUserDetailsService por baixo
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        // Agora extraímos os userDetails autenticados
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        // Gera o token
        String jwtToken = jwtService.generateToken(userDetails);

        // Retorna o custom payload
        return ResponseEntity.ok(new LoginResponseDTO(
                jwtToken,
                user.getId(),
                user.getName(),
                user.getUserType()));

    }

    @PostMapping("/recover-password")
    public ResponseEntity<String> recoverPassword(@RequestBody String email) {
        // TODO: verificar se o email existe no banco
        // TODO: criar uma entidade PasswordResetToken linkada com o User, com uma data
        // pra expirar

        String resetToken = UUID.randomUUID().toString();

        // Por hora um mock pra enviar um email com o processo
        log.info("Mock envio de email para: {} com o token para restaurar a senha: {}", email, resetToken);

        return ResponseEntity.ok("Se o email existe, um link para recuração de senha foi enviado.");
    }

}
