package br.com.conectaPro.util;

import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
public class Util {

  // Prefixo (equivalente a uma "pasta") usado dentro do bucket do R2.
  private static final String PREFIXO_IMAGENS = "imagens_cadastradas/";

  private static S3Client s3Client;
  private static String bucketName;

  @Autowired
  public void setS3Client(S3Client s3Client) {
    Util.s3Client = s3Client;
  }

  @Value("${r2.bucket.name}")
  public void setBucketName(String bucket) {
    Util.bucketName = bucket;
  }

  /**
   * Envia a imagem para o Cloudflare R2 com um nome de arquivo seguro (sem espaços, acentos ou
   * caracteres que quebrem URLs) e retorna apenas o NOME do arquivo salvo (não a chave completa,
   * nem uma URL). Esse nome deve ser usado junto ao endpoint GET /api/images/{nomeArquivo} para
   * recuperar a imagem depois.
   */
  public static String fazerUploadImagem(MultipartFile imagem) {
    if (imagem == null || imagem.isEmpty()) {
      System.out.println("Arquivo vazio ou nulo, upload ignorado.");
      return null;
    }

    String extensao = extrairExtensaoSegura(imagem.getOriginalFilename());
    String nomeArquivo = UUID.randomUUID().toString().replace("-", "") + extensao;
    String key = PREFIXO_IMAGENS + nomeArquivo;

    try {
      PutObjectRequest request =
          PutObjectRequest.builder()
              .bucket(bucketName)
              .key(key)
              .contentType(imagem.getContentType())
              .build();

      s3Client.putObject(
          request, RequestBody.fromInputStream(imagem.getInputStream(), imagem.getSize()));

      System.out.println("Arquivo armazenado no R2 com a chave: " + key);
      return nomeArquivo;

    } catch (Exception e) {
      System.out.println("Falha ao enviar o arquivo para o R2: " + e.getMessage());
      return null;
    }
  }

  /**
   * Apaga do R2 a imagem com o nome informado (o mesmo nome retornado por fazerUploadImagem).
   * Retorna true se a chamada de exclusão foi bem-sucedida.
   */
  public static boolean apagarImagem(String nomeArquivo) {
    String key = validarEChavear(nomeArquivo);
    if (key == null) {
      return false;
    }

    try {
      s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(key).build());
      return true;
    } catch (S3Exception e) {
      System.out.println("Falha ao apagar o arquivo '" + nomeArquivo + "' do R2: " + e.getMessage());
      return false;
    }
  }

  /**
   * Baixa do R2 o conteúdo (bytes + metadados) do arquivo com o nome informado. Usado pelo
   * endpoint que serve as imagens (GET). Retorna null caso o nome seja inválido ou o objeto não
   * exista no bucket.
   */
  public static ResponseInputStream<GetObjectResponse> baixarImagem(String nomeArquivo) {
    String key = validarEChavear(nomeArquivo);
    if (key == null) {
      return null;
    }

    try {
      return s3Client.getObject(GetObjectRequest.builder().bucket(bucketName).key(key).build());
    } catch (NoSuchKeyException e) {
      return null;
    } catch (S3Exception e) {
      System.out.println("Falha ao baixar o arquivo '" + nomeArquivo + "' do R2: " + e.getMessage());
      return null;
    }
  }

  /**
   * Valida que o nome de arquivo informado tem o formato esperado (o mesmo que geramos em
   * fazerUploadImagem) e monta a chave completa dentro do bucket. Retorna null se o nome for
   * nulo/vazio ou não bater com o formato esperado — isso também bloqueia qualquer tentativa de
   * usar "/" ou ".." para acessar outra chave/prefixo do bucket.
   */
  private static String validarEChavear(String nomeArquivo) {
    if (nomeArquivo == null || nomeArquivo.isBlank()) {
      return null;
    }

    if (!nomeArquivo.matches("^[a-f0-9]{32}(\\.[a-z0-9]{1,5})?$")) {
      System.out.println("Nome de arquivo inválido, operação recusada: " + nomeArquivo);
      return null;
    }

    return PREFIXO_IMAGENS + nomeArquivo;
  }

  private static String extrairExtensaoSegura(String nomeOriginal) {
    if (nomeOriginal == null) {
      return "";
    }

    // Usa apenas o nome do arquivo, ignorando qualquer caminho embutido
    String nomeSemCaminho = Paths.get(nomeOriginal).getFileName().toString();
    int idxPonto = nomeSemCaminho.lastIndexOf('.');

    if (idxPonto < 0 || idxPonto == nomeSemCaminho.length() - 1) {
      return "";
    }

    String extensao = nomeSemCaminho.substring(idxPonto).toLowerCase();

    // Só aceita extensões simples (letras/números), evitando qualquer
    // caractere estranho (espaço, barra, etc.) vazar para o nome final
    if (!extensao.matches("\\.[a-z0-9]{1,5}")) {
      return "";
    }

    return extensao;
  }
}
