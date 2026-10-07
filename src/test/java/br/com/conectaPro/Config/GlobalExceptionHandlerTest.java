package br.com.conectaPro.Config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.conectaPro.config.GlobalExceptionHandler;
import java.util.Map;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  @DisplayName("NoSuchElementException vira 404 com a mensagem")
  void naoEncontradoVira404() {
    ResponseEntity<Map<String, Object>> resposta =
        handler.handleNotFound(new NoSuchElementException("Usuário não encontrado"));

    assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
    assertEquals("Usuário não encontrado", resposta.getBody().get("message"));
    assertEquals(404, resposta.getBody().get("status"));
  }

  @Test
  @DisplayName("IllegalStateException vira 409")
  void conflitoVira409() {
    ResponseEntity<Map<String, Object>> resposta =
        handler.handleConflict(new IllegalStateException("Transição inválida"));

    assertEquals(HttpStatus.CONFLICT, resposta.getStatusCode());
    assertEquals("Transição inválida", resposta.getBody().get("message"));
  }

  @Test
  @DisplayName("IllegalArgumentException vira 400")
  void argumentoInvalidoVira400() {
    ResponseEntity<Map<String, Object>> resposta =
        handler.handleBadRequest(new IllegalArgumentException("Pontuação inválida."));

    assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
    assertEquals("Pontuação inválida.", resposta.getBody().get("message"));
  }

  @Test
  @DisplayName("Parte multipart ausente vira 400 citando o campo")
  void parteAusenteVira400() {
    ResponseEntity<Map<String, Object>> resposta =
        handler.handleMissingPart(new MissingServletRequestPartException("foto"));

    assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
    assertTrue(((String) resposta.getBody().get("message")).contains("foto"));
  }
}
