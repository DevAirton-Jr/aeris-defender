package com.sentinelav.analysis;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Módulo principal de identificação de ameaças conhecidas (Assinaturas).
 * Basicamente compara a identidade do arquivo contra a 'Lista de Mais Procurados'.
 * 
 * "Se cheira como malware e age como malware, bloqueie e pergunte depois."
 */
public class ThreatDetector {

    // Simulação do Banco de Dados de Assinaturas Virais.
    // Em produção, isso seria carregado de um arquivo .dat criptografado, ou API em Nuvem.
    private final Map<String, String> signatures = new HashMap<>();
    private final RiskAnalyzer riskAnalyzer;

    public ThreatDetector() {
        this.riskAnalyzer = new RiskAnalyzer();
    }

    /**
     * Retorna o score total de risco do arquivo.
     */
    public int getRiskScore(File file) {
        if (file == null || !file.exists()) {
            return 0;
        }
        return riskAnalyzer.calculateRiskScore(file);
    }

    /**
     * Retorna true se o arquivo ultrapassar o limiar de risco.
     */
    public boolean isHighRisk(File file) {
        return riskAnalyzer.isHighRisk(file);
    }

    /**
     * Retorna os motivos que contribuíram para o score de risco.
     */
    public String getRiskReasons(File file) {
        if (file == null || !file.exists()) {
            return "";
        }
        return riskAnalyzer.getRiskReasons(file);
    }

    /**
     * Método de compatibilidade / atalho semântico.
     */
    public boolean isSuspicious(File file) {
        return isHighRisk(file);
    }
}
