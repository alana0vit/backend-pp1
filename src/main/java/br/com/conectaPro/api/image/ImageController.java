package br.com.conectaPro.api.image;

import br.com.conectaPro.util.Util;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@RestController
@RequestMapping("/api/images")
@CrossOrigin
@Tag(
    name = "Images",
    description = "Upload/Download de imagens (foto do usuário e imagens da demanda)")
public class ImageController {

  @Operation(summary = "Recupera (baixa/exibe) uma imagem previamente enviada")
  @GetMapping("/{nomeArquivo}")
  public ResponseEntity<Resource> getImagem(@PathVariable String nomeArquivo) {
    ResponseInputStream<GetObjectResponse> objeto = Util.baixarImagem(nomeArquivo);

    if (objeto == null) {
      return ResponseEntity.notFound().build();
    }

    String contentType = objeto.response().contentType();
    MediaType mediaType =
        contentType != null
            ? MediaType.parseMediaType(contentType)
            : MediaType.APPLICATION_OCTET_STREAM;

    Resource recurso = new InputStreamResource(objeto);

    return ResponseEntity.ok()
        .contentType(mediaType)
        .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable")
        .body(recurso);
  }
}
