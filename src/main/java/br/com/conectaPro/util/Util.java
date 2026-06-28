package br.com.conectaPro.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class Util {

    private static String diretorioImagens;

    @Value("${app.imagens.diretorio}")
    public void setDiretorioImagens(String diretorio) {
        Util.diretorioImagens = diretorio;
    }

    public static String fazerUploadImagem(MultipartFile imagem) {

        boolean sucessoUpload = false;
        String nomeArquivoComDataHora = null;

        if (imagem != null && !imagem.isEmpty()) {

            LocalDateTime agora = LocalDateTime.now();
            String dataHora = agora.getYear() + "-"
                    + agora.getMonthValue() + "-"
                    + agora.getDayOfMonth() + "-"
                    + agora.getHour() + "-"
                    + agora.getMinute() + "-"
                    + agora.getSecond() + " - ";

            String nomeOriginalArquivo = imagem.getOriginalFilename();
            nomeArquivoComDataHora = dataHora + nomeOriginalArquivo;

            try {
                String pastaDestino = diretorioImagens + File.separator + "imagens_cadastradas";
                File dir = new File(pastaDestino);

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File serverFile = new File(dir.getAbsolutePath() + File.separator + nomeArquivoComDataHora);
                try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile))) {
                    stream.write(imagem.getBytes());
                }

                System.out.println("Arquivo armazenado em: " + serverFile.getAbsolutePath());
                System.out.println("Upload do arquivo '" + nomeOriginalArquivo + "' realizado com sucesso.");
                sucessoUpload = true;

            } catch (Exception e) {
                System.out.println("Falha ao carregar o arquivo '" + nomeOriginalArquivo + "': " + e.getMessage());
            }

        } else {
            System.out.println("Arquivo vazio ou nulo, upload ignorado.");
        }

        return sucessoUpload ? nomeArquivoComDataHora : null;
    }
}