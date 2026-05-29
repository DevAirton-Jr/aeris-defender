package com.sentinelav.realtime;

import com.sentinelav.analysis.FileScanner;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * O Cão de Guarda do Sentinel.
 * Usa a API nativa do sistema operacional (WatchService) para interceptar 
 * modificações de arquivos no instante em que ocorrem.
 * 
 * "Se um arquivo espirrar, o RealTimeProtector fala 'Saúde'."
 */
public class RealTimeProtector {

    private final FileScanner scanner;
    private WatchService watchService;
    private Thread watchThread;
    
    // Thread-safe flag (porque threads normais são como crianças em loja de doce)
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Map<WatchKey, Path> keys = new HashMap<>();

    /**
     * Injeta a dependência do FileScanner para delegar a punição caso 
     * o arquivo recém-criado seja maligno.
     */
    public RealTimeProtector(FileScanner scanner) {
        this.scanner = scanner;
    }

    public void start() {
        if (running.get()) return;

        try {
            watchService = FileSystems.getDefault().newWatchService();
            registerCommonDirectories();

            running.set(true);
            watchThread = new Thread(this::processEvents);
            watchThread.setDaemon(true);
            watchThread.start();
            System.out.println("🛡️ Proteção em Tempo Real ATIVADA.");
        } catch (IOException e) {
            System.err.println("Erro ao iniciar Proteção em Tempo Real: " + e.getMessage());
        }
    }

    public void stop() {
        if (!running.get()) return;
        running.set(false);
        try {
            if (watchService != null) {
                watchService.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("⚠️ Proteção em Tempo Real DESATIVADA.");
    }

    public boolean isRunning() {
        return running.get();
    }

    private void registerCommonDirectories() throws IOException {
        String userHome = System.getProperty("user.home");
        Path[] paths = {
                Paths.get(userHome, "Downloads"),
                Paths.get(userHome, "Desktop"),
                Paths.get(userHome, "Documents"),
                Paths.get(userHome, "Área de Trabalho"), // Em sistemas em PT-BR
                Paths.get(userHome, "Documentos")
        };

        for (Path path : paths) {
            if (Files.exists(path) && Files.isDirectory(path)) {
                WatchKey key = path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY);
                keys.put(key, path);
                System.out.println("Monitorando: " + path);
            }
        }
    }

    private void processEvents() {
        while (running.get()) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException | ClosedWatchServiceException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey não reconhecido.");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path name = ev.context();
                Path child = dir.resolve(name);

                File file = child.toFile();
                if (file.isFile() && file.exists()) {
                    System.out.println("[REAL-TIME] Arquivo modificado/criado: " + file.getName());
                    // Delegar a varredura para o scanner (processado em background)
                    scanner.scanSingleFile(file);
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }
}
