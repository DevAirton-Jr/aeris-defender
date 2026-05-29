package com.sentinelav.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * O Raio-X do Sentinel.
 * Detecta se o arquivo executável está empacotado/espremido por ferramentas 
 * obscuras como UPX, ASPack ou Themida (tática muito usada por malwares 
 * para dificultar a análise e esconder assinaturas).
 * 
 * "Desempacotando problemas desde que o UPX foi inventado."
 */
public class PackerDetector {

    // Simple signature scanner for the first few kilobytes
    private static final byte[][] KNOWN_PACKER_SIGNATURES = {
        "UPX0".getBytes(),
        "UPX1".getBytes(),
        "ASPack".getBytes(),
        "MPRESS".getBytes(),
        "Themida".getBytes()
    };

    public static String detectPacker(File file) {
        if (file == null || !file.exists() || file.length() < 512) {
            return null;
        }

        // Only scan if it's an executable
        if (!MagicBytesChecker.isExecutableBinary(file)) {
            return null;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            // Read up to 8KB of the header to find section names
            byte[] buffer = new byte[8192];
            int read = fis.read(buffer);
            if (read <= 0) return null;

            for (byte[] sig : KNOWN_PACKER_SIGNATURES) {
                if (indexOf(buffer, read, sig) != -1) {
                    return new String(sig);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao buscar packers: " + e.getMessage());
        }

        return null;
    }

    private static int indexOf(byte[] data, int dataLen, byte[] pattern) {
        if (pattern.length == 0 || dataLen < pattern.length) return -1;
        for (int i = 0; i <= dataLen - pattern.length; i++) {
            boolean match = true;
            for (int j = 0; j < pattern.length; j++) {
                if (data[i + j] != pattern[j]) {
                    match = false;
                    break;
                }
            }
            if (match) return i;
        }
        return -1;
    }
}
