package com.sentinelav.analysis;

import java.io.File;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Calculadora de Destino.
 * Combina os resultados da Análise Comportamental, Magic Bytes e Assinaturas 
 * para gerar o Score final de Risco. Acima de 5 pontos, o martelo da Quarentena cai.
 * 
 * "Aqui, você é culpado até que se prove o contrário."
 */
public class RiskAnalyzer {

    private static final int RISK_THRESHOLD = 5;

    private static final Set<String> SUSPICIOUS_EXTENSIONS = Set.of(
            "exe", "dll", "bat", "ps1", "js", "vbs", "jar"
    );

    private static final Pattern[] SUSPICIOUS_NAME_PATTERNS = {
            Pattern.compile(".*password.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*invoice.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*update.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*keygen.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*crack.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*admin.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*login.*", Pattern.CASE_INSENSITIVE)
    };

    private static final long MIN_SIZE = 1_024;        // 1 KB
    private static final long MAX_SIZE = 50_000_000;   // 50 MB

    public int calculateRiskScore(File file) {
        int score = 0;

        boolean isSusExt = isSuspiciousExtension(file);
        boolean isExecutable = MagicBytesChecker.isExecutableBinary(file);

        if (isSusExt) score += 2;
        
        // Se for um executável verdadeiro mas não tiver extensão suspeita, está camuflado
        if (isExecutable && !isSusExt) {
            score += 5; // Punição severa
        }

        if (isSuspiciousName(file)) score += 3;
        if (isSuspiciousSize(file)) score += 2;

        String packer = PackerDetector.detectPacker(file);
        if (packer != null) {
            score += 3;
        }

        return score;
    }

    public boolean isHighRisk(File file) {
        return calculateRiskScore(file) >= RISK_THRESHOLD;
    }

    public String getRiskReasons(File file) {
        StringBuilder reasons = new StringBuilder();

        boolean isSusExt = isSuspiciousExtension(file);
        boolean isExecutable = MagicBytesChecker.isExecutableBinary(file);

        if (isSusExt) {
            reasons.append("[Extensão suspeita] ");
        }
        if (isExecutable && !isSusExt) {
            reasons.append("[Executável camuflado] ");
        }

        if (isSuspiciousName(file)) {
            reasons.append("[Nome suspeito] ");
        }
        if (isSuspiciousSize(file)) {
            reasons.append("[Tamanho anômalo] ");
        }

        String packer = PackerDetector.detectPacker(file);
        if (packer != null) {
            reasons.append("[Packer detectado: ").append(packer).append("] ");
        }

        String result = reasons.toString().trim();
        return result.isEmpty()
                ? "[Nenhum fator suspeito]"
                : result;
    }

    private boolean isSuspiciousExtension(File file) {
        String name = file.getName().toLowerCase();
        int dot = name.lastIndexOf('.');
        if (dot == -1) return false;

        String ext = name.substring(dot + 1);
        return SUSPICIOUS_EXTENSIONS.contains(ext);
    }

    private boolean isSuspiciousName(File file) {
        String name = file.getName();
        for (Pattern p : SUSPICIOUS_NAME_PATTERNS) {
            if (p.matcher(name).matches()) {
                return true;
            }
        }
        return false;
    }

    private boolean isSuspiciousSize(File file) {
        long size = file.length();
        return size < MIN_SIZE || size > MAX_SIZE;
    }
}
