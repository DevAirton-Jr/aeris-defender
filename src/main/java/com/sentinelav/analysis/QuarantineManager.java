package com.sentinelav.analysis;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * A Prisão de Segurança Máxima do Sentinel.
 * Encapsula e desativa os arquivos maliciosos identificados movendo-os
 * para o corredor da morte (Quarentena), removendo assim seus direitos de execução.
 * 
 * "Onde os malwares vão para refletir sobre seus crimes."
 */
public class QuarantineManager {

    private final File quarantineDir;

    public QuarantineManager(String dirName) {
        quarantineDir = new File(dirName);
        if (!quarantineDir.exists()) {
            quarantineDir.mkdirs();
        }
    }

    /**
     * Move um arquivo para a quarentena.
     */
    public void moveToQuarantine(File file) {
        if (file == null || !file.exists() || file.isDirectory()) return;

        File target = new File(quarantineDir, file.getName());
        try {
            Files.move(file.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[QUARENTENA] " + file.getAbsolutePath() + " -> " + target.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("[ERRO] Não foi possível mover para quarentena: " + file.getAbsolutePath());
        }
    }

    /**
     * Restaura um arquivo da quarentena para seu diretório original.
     * O arquivo original precisa existir no path original registrado.
     */
    public boolean restoreFromQuarantine(File quarantinedFile, File originalPath) {
        if (quarantinedFile == null || !quarantinedFile.exists()) return false;
        if (originalPath == null) return false;

        try {
            Path parentDir = originalPath.toPath().getParent();
            if (!Files.exists(parentDir)) Files.createDirectories(parentDir);

            Files.move(quarantinedFile.toPath(), originalPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[RESTAURADO] " + originalPath.getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("[ERRO] Não foi possível restaurar: " + quarantinedFile.getAbsolutePath());
            return false;
        }
    }

    /**
     * Exclui um arquivo da quarentena de forma segura (sobrescrevendo os bytes antes).
     */
    public boolean deleteFromQuarantine(File quarantinedFile) {
        if (quarantinedFile == null || !quarantinedFile.exists()) return false;

        try {
            long size = quarantinedFile.length();
            byte[] empty = new byte[8192];
            try (var out = Files.newOutputStream(quarantinedFile.toPath(), StandardOpenOption.WRITE)) {
                long written = 0;
                while (written < size) {
                    int toWrite = (int) Math.min(empty.length, size - written);
                    out.write(empty, 0, toWrite);
                    written += toWrite;
                }
            }

            return quarantinedFile.delete();
        } catch (IOException e) {
            System.err.println("[ERRO] Não foi possível excluir com segurança: " + quarantinedFile.getAbsolutePath());
            return false;
        }
    }

    /**
     * Lista todos os arquivos atualmente na quarentena.
     */
    public File[] listQuarantineFiles() {
        return quarantineDir.listFiles();
    }
}
