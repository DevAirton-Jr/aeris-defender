package app;

import com.sentinelav.core.SentinelEngine;

/**
 * Ponto de entrada alternativo da aplicação.
 * Esta classe é o gatilho absoluto do ecossistema Sentinel.
 * 
 * "Onde quer que um byte malicioso tente se esconder, o Sentinel começa aqui."
 */
public class Main {

    /**
     * Método principal de execução isolada (Standalone).
     * @param args argumentos de linha de comando
     */
    public static void main(String[] args) {
        System.out.println("Iniciando SentinelAV...");
        
        // Se esse código não funcionar de primeira, a culpa é das flutuações quânticas.
        SentinelEngine engine = SentinelEngine.getInstance();
        engine.start();
    }
}
