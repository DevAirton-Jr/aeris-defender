package com.sentinelav.ui;

/**
 * Launcher Oficial da Interface Gráfica do SentinelAV.
 * Serve como um Wrapper (ponte) para iniciar a Application JavaFX sem 
 * disparar erros bizarros de módulo (Jigsaw) que o Java 9+ inventou.
 */
public class AppLauncher {

    /**
     * O verdadeiro Big Bang da UI do Sentinel.
     * @param args argumentos enviados via CLI.
     */
    public static void main(String[] args) {
        // Reza a lenda que se você rodar isso como Root, o PC ganha senciência.
        SentinelApp.launch(SentinelApp.class, args);
    }
}
