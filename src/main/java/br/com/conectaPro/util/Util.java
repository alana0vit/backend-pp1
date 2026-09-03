package br.com.conectaPro.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class Util {

  private static final String SUBPASTA_IMAGENS = "imagens_cadastradas";

  private static String diretorioImagens;

  @Value("${app.imagens.diretorio}")
  public void setDiretorioImagens(String diretorio) {
    Util.diretorioImagens = diretorio;
  }

  /**
   * Salva a imagem enviada com um nome de arquivo seguro (sem espaços, acentos ou caracteres que
   * quebrem URLs) e retorna apenas o NOME do arquivo salvo (não o caminho completo). Esse nome deve
   * ser usado junto ao endpoint GET /api/images/{nomeArquivo} para recuperar a imagem depois.
   */
  public static String fazerUploadImagem(MultipartFile imagem) {
    if (imagem == null || imagem.isEmpty()) {
      System.out.println("Arquivo vazio ou nulo, upload ignorado.");
      return null;
    }

    try {
      String extensao = extrairExtensaoSegura(imagem.getOriginalFilename());
      String nomeArquivo = UUID.randomUUID().toString().replace("-", "") + extensao;

      Path pastaDestino = getPastaDestino();
      Files.createDirectories(pastaDestino);

      Path destino = pastaDestino.resolve(nomeArquivo).normalize();

      // Garante que o caminho final continua dentro da pasta de destino
      // (proteção contra path traversal)
      if (!destino.startsWith(pastaDestino)) {
        System.out.println("Nome de arquivo inválido, upload recusado.");
        return null;
      }

      try (InputStream in = imagem.getInputStream()) {
        Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
      }

      System.out.println("Arquivo armazenado em: " + destino.toAbsolutePath());
      return nomeArquivo;

    } catch (IOException e) {
      System.out.println("Falha ao carregar o arquivo: " + e.getMessage());
      return null;
    }
  }

  /**
   * Apaga do disco a imagem com o nome informado (o mesmo nome retornado por fazerUploadImagem).
   * Retorna true se o arquivo existia e foi removido.
   */
  public static boolean apagarImagem(String nomeArquivo) {
    if (nomeArquivo == null || nomeArquivo.isBlank()) {
      return false;
    }

    try {
      Path pastaDestino = getPastaDestino();
      Path arquivo = pastaDestino.resolve(nomeArquivo).normalize();

      if (!arquivo.startsWith(pastaDestino)) {
        return false;
      }

      return Files.deleteIfExists(arquivo);
    } catch (IOException e) {
      System.out.println("Falha ao apagar o arquivo '" + nomeArquivo + "': " + e.getMessage());
      return false;
    }
  }

  /**
   * Resolve o caminho absoluto no disco para um nome de arquivo de imagem já salvo. Usado pelo
   * endpoint que serve as imagens (GET). Retorna null caso o nome seja inválido ou tente escapar da
   * pasta de imagens (path traversal).
   */
  public static Path resolverCaminhoImagem(String nomeArquivo) {
    if (nomeArquivo == null || nomeArquivo.isBlank()) {
      return null;
    }

    Path pastaDestino = getPastaDestino();
    Path arquivo = pastaDestino.resolve(nomeArquivo).normalize();

    if (!arquivo.startsWith(pastaDestino)) {
      return null;
    }

    return arquivo;
  }

  private static Path getPastaDestino() {
    return Paths.get(diretorioImagens, SUBPASTA_IMAGENS);
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
