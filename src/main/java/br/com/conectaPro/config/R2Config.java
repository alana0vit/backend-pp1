package br.com.conectaPro.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

/**
 * Configura o cliente S3 apontando para o Cloudflare R2 (armazenamento das imagens de
 * usuário/demanda). O R2 é compatível com a API do S3, então usamos o SDK oficial da AWS,
 * apenas sobrescrevendo o endpoint para o domínio da Cloudflare.
 */
@Configuration
public class R2Config {

  @Value("${r2.account.id}")
  private String accountId;

  @Value("${r2.access.key.id}")
  private String accessKeyId;

  @Value("${r2.secret.access.key}")
  private String secretAccessKey;

  @Bean
  public S3Client s3Client() {
    String endpoint = "https://" + accountId + ".r2.cloudflarestorage.com";

    return S3Client.builder()
        .endpointOverride(URI.create(endpoint))
        // A Cloudflare não usa regiões AWS; "auto" é o valor esperado pelo R2.
        .region(Region.of("auto"))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
        .serviceConfiguration(
            // pathStyleAccessEnabled evita erro de assinatura ("SignatureDoesNotMatch")
            // observado no R2 com o estilo virtual-hosted padrão do SDK.
            S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .chunkedEncodingEnabled(false)
                .build())
        .build();
  }
}
