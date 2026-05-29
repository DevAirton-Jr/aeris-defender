package com.sentinelav.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * Utilitário de Criptografia para Assinaturas.
 * Pega um arquivo gigantesco e transforma num textinho de 64 caracteres.
 * Essencial para procurar arquivos maliciosos conhecidos (Blacklist).
 * 
 * "Calculando hashes desde 1990."
 */
public class HashUtils {

    /**
     * Calcula o hash SHA-256 de um arquivo.
     * Retorna null em caso de erro.
     */
    public static String sha256(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] buffer = new byte[8192];
            int read;

            while ((read = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }

            return bytesToHex(digest.digest());

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Converte bytes para string hexadecimal.
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
