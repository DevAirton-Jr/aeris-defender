package com.sentinelav.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Módulo de Análise Comportamental (O Psiquiatra de Arquivos).
 * Tenta adivinhar se um arquivo vai fazer besteira baseado 
 * no jeito que ele se veste (nome, atributos ocultos, extensões falsas).
 */
public class BehaviorAnalyzer {

    /**
     * Calcula um score de risco baseado apenas em comportamento.
     */
    public int calculateBehaviorScore(File file) {
        if (file == null || !file.exists()) return 0;

        int score = 0;

        if (accessesSensitiveDirectories(file)) score += 2;
        if (isExecutableScript(file)) score += 2;
        if (isHiddenFile(file)) score += 1;

        return score;
    }

    /**
     * Retorna true se houver qualquer indício comportamental suspeito.
     */
    public boolean isSuspiciousBehavior(File file) {
        return calculateBehaviorScore(file) >= 2;
    }

    /**
     * Retorna uma string explicando os motivos do score comportamental.
     */
    public String getBehaviorReasons(File file) {
        if (file == null || !file.exists()) return "";

        List<String> reasons = new ArrayList<>();

        if (accessesSensitiveDirectories(file)) {
            reasons.add("Acesso a diretórios sensíveis do sistema");
        }

        if (isExecutableScript(file)) {
            reasons.add("Script executável potencialmente perigoso");
        }

        if (isHiddenFile(file)) {
            reasons.add("Arquivo oculto");
        }

        if (reasons.isEmpty()) return "";

        return "[Comportamento: " + String.join(", ", reasons) + "]";
    }

    private boolean accessesSensitiveDirectories(File file) {
        String path = file.getAbsolutePath().toLowerCase();

        return path.contains("system32")
                || path.contains("windows")
                || path.contains("program files");
    }

    private boolean isExecutableScript(File file) {
        String name = file.getName().toLowerCase();

        return name.endsWith(".bat")
                || name.endsWith(".ps1")
                || name.endsWith(".vbs")
                || name.endsWith(".sh");
    }

    private boolean isHiddenFile(File file) {
        return file.isHidden();
    }
}
