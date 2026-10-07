package br.com.conectaPro.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/**
 * Traduz exceções de negócio em respostas HTTP corretas (404/409/400) em vez de 500. Não existe
 * handler genérico para Exception de propósito: erros inesperados continuam sendo 500, para não
 * esconder bugs.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(NoSuchElementException e) {
    return build(HttpStatus.NOT_FOUND, e.getMessage(), null);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<Map<String, Object>> handleConflict(IllegalStateException e) {
    return build(HttpStatus.CONFLICT, e.getMessage(), null);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException e) {
    return build(HttpStatus.BAD_REQUEST, e.getMessage(), null);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, Object>> handleIntegrity(DataIntegrityViolationException e) {
    String msg = "A operação viola uma restrição de dados (ex.: valor duplicado).";
    return build(HttpStatus.CONFLICT, msg, null);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException e) {
    return build(
        HttpStatus.BAD_REQUEST,
        "Corpo da requisição inválido ou em formato incorreto (ex.: datas em dd/MM/yyyy).",
        null);
  }

  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<Map<String, Object>> handleMissingPart(
      MissingServletRequestPartException e) {
    String msg = "Parte obrigatória ausente no multipart: '" + e.getRequestPartName() + "'.";
    return build(HttpStatus.BAD_REQUEST, msg, null);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<Map<String, Object>> handleMissingParam(
      MissingServletRequestParameterException e) {
    String msg = "Parâmetro obrigatório ausente: '" + e.getParameterName() + "'.";
    return build(HttpStatus.BAD_REQUEST, msg, null);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
    Map<String, String> errors = new LinkedHashMap<>();
    e.getBindingResult()
        .getFieldErrors()
        .forEach(f -> errors.putIfAbsent(f.getField(), f.getDefaultMessage()));
    return build(HttpStatus.BAD_REQUEST, "Dados inválidos.", errors);
  }

  private ResponseEntity<Map<String, Object>> build(
      HttpStatus status, String message, Map<String, String> errors) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    body.put("message", message);
    if (errors != null) {
      body.put("errors", errors);
    }
    return ResponseEntity.status(status).body(body);
  }
}
