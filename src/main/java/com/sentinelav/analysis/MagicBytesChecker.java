package com.sentinelav.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Investigador de DNA Digital.
 * Ignora a extensão falsa (ex: virus.txt) e lê o "cabeçalho mágico" (Magic Bytes) 
 * direto no escovão de bits para saber a verdadeira identidade do arquivo.
 * 
 * "Na internet, ninguém sabe que você é um .exe disfarçado de .pdf."
 */
public class MagicBytesChecker {

    public enum FileType {
        WINDOWS_EXECUTABLE("MZ"), // 4D 5A
        PDF("%PDF"),              // 25 50 44 46
        ZIP("PK"),                // 50 4B
        ELF("\u007FELF"),         // 7F 45 4C 46
        UNKNOWN("");

        private final String signature;

        FileType(String signature) {
            this.signature = signature;
        }

        public String getSignature() {
            return signature;
        }
    }

    public static FileType detectFileType(File file) {
        if (file == null || !file.exists() || file.length() < 4) {
            return FileType.UNKNOWN;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[4];
            int read = fis.read(header);
            if (read < 2) return FileType.UNKNOWN;

            // Check MZ
            if (header[0] == 0x4D && header[1] == 0x5A) {
                return FileType.WINDOWS_EXECUTABLE;
            }
            // Check PDF
            if (header[0] == 0x25 && header[1] == 0x50 && header[2] == 0x44 && header[3] == 0x46) {
                return FileType.PDF;
            }
            // Check ZIP / JAR / APK
            if (header[0] == 0x50 && header[1] == 0x4B) {
                return FileType.ZIP;
            }
            // Check ELF
            if (header[0] == 0x7F && header[1] == 0x45 && header[2] == 0x4C && header[3] == 0x46) {
                return FileType.ELF;
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler magic bytes: " + e.getMessage());
        }

        return FileType.UNKNOWN;
    }

    public static boolean isExecutableBinary(File file) {
        FileType type = detectFileType(file);
        return type == FileType.WINDOWS_EXECUTABLE || type == FileType.ELF;
    }
}
