package com.sentinelav.core;

import com.sentinelav.analysis.FileScanner;
import com.sentinelav.realtime.RealTimeProtector;

/**
 * O Coração do Sentinel. 
 * Esta classe Singleton atua como o maestro, gerenciando o FileScanner 
 * e a proteção em tempo real (RealTimeProtector).
 * "Existem apenas duas coisas infinitas: o universo e a lista de dependências do Node.js"
 */
public class SentinelEngine {

    private static SentinelEngine instance;

    private final FileScanner scanner;
    private final RealTimeProtector protector;

    /**
     * Construtor privado (Singleton). Apenas uma instância pode existir,
     * afinal, dois motores rodando antivírus na mesma máquina é a receita pro caos.
     */
    private SentinelEngine() {
        this.scanner = new FileScanner();
        this.protector = new RealTimeProtector(scanner);
    }

    public static synchronized SentinelEngine getInstance() {
        if (instance == null) {
            instance = new SentinelEngine();
        }
        return instance;
    }

    public FileScanner getScanner() {
        return scanner;
    }

    public void start() {
        System.out.println("Engine do SentinelAV ativa.");

        String userHome = System.getProperty("user.home");
        System.out.println("Escaneando: " + userHome);

        scanner.scanDirectory(userHome);
    }

    public void enableRealTimeProtection() {
        protector.start();
    }

    public void disableRealTimeProtection() {
        protector.stop();
    }
    
    public boolean isRealTimeProtectionRunning() {
        return protector.isRunning();
    }
}
