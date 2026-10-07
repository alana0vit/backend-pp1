package br.com.conectaPro.Storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import br.com.conectaPro.util.Util;
import java.io.IOException;
import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ExtendWith(MockitoExtension.class)
class UtilUploadTest {

  @Mock private S3Client s3Client;

  @BeforeEach
  void setUp() {
    Util util = new Util();
    util.setS3Client(s3Client);
    util.setBucketName("bucket-teste");
  }

  @AfterEach
  void tearDown() {
    // Util guarda o cliente em campo estático: não deixa o mock vazar para outros testes
    Util util = new Util();
    util.setS3Client(null);
    util.setBucketName(null);
  }

  @Test
  @DisplayName("Envia o arquivo inteiro, com conteúdo reenviável, e devolve um nome seguro")
  void enviaArquivoCompletoEReenviavel() throws IOException {
    byte[] conteudo = new byte[300_000]; // maior que o buffer de 128 KB do SDK
    new Random(1).nextBytes(conteudo);
    MockMultipartFile arquivo =
        new MockMultipartFile("foto", "Captura de tela 2026-10-06 144214.png", "image/png", conteudo);

    String nome = Util.fazerUploadImagem(arquivo);

    assertNotNull(nome);
    assertTrue(nome.matches("^[a-f0-9]{32}\\.png$"));

    ArgumentCaptor<PutObjectRequest> request = ArgumentCaptor.forClass(PutObjectRequest.class);
    ArgumentCaptor<RequestBody> body = ArgumentCaptor.forClass(RequestBody.class);
    verify(s3Client).putObject(request.capture(), body.capture());

    assertEquals("bucket-teste", request.getValue().bucket());
    assertEquals("imagens_cadastradas/" + nome, request.getValue().key());
    assertEquals("image/png", request.getValue().contentType());

    // O SDK pode pedir o conteúdo mais de uma vez (checksum, retentativa): as duas leituras
    // precisam devolver o arquivo inteiro.
    var provider = body.getValue().contentStreamProvider();
    assertArrayEquals(conteudo, provider.newStream().readAllBytes());
    assertArrayEquals(conteudo, provider.newStream().readAllBytes());
  }

  @Test
  @DisplayName("Se o R2 falhar, devolve null (o chamador transforma em erro)")
  void falhaNoR2DevolveNull() {
    when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
        .thenThrow(new RuntimeException("falhou"));
    MockMultipartFile arquivo =
        new MockMultipartFile("foto", "a.png", "image/png", "conteudo".getBytes());

    assertNull(Util.fazerUploadImagem(arquivo));
  }

  @Test
  @DisplayName("Arquivo vazio não chama o R2")
  void arquivoVazioNaoChamaR2() {
    MockMultipartFile vazio = new MockMultipartFile("foto", "a.png", "image/png", new byte[0]);

    assertNull(Util.fazerUploadImagem(vazio));
    verifyNoInteractions(s3Client);
  }

  @Test
  @DisplayName("Nome com '..' ou '/' é recusado sem chamar o R2")
  void nomeMaliciosoNaoChamaR2() {
    assertNull(Util.baixarImagem("../segredo.png"));
    assertEquals(false, Util.apagarImagem("pasta/outro.png"));
    verifyNoInteractions(s3Client);
  }
}
