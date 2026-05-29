package com.sentinelav.analysis;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A Espinha Dorsal do Antivírus.
 * Varre o disco de forma impiedosa, conta os arquivos usando NIO.2 
 * para máxima performance e os envia para a guilhotina da análise heurística em multi-threading.
 * 
 * "O segredo da velocidade não é ler rápido, é ignorar coisas pesadas inteligentemente."
 */
public class FileScanner {

    public interface ScanProgressListener {
        void onProgress(int current, int total);
    }

    private final ThreatDetector detector;
    private final BehaviorAnalyzer behaviorAnalyzer;
    private final QuarantineManager quarantineManager;
    private final ExecutorService executor;

    private final AtomicInteger totalFiles = new AtomicInteger();
    private final AtomicInteger suspiciousFiles = new AtomicInteger();

    private ScanProgressListener progressListener;
    private int totalFilesToScan = 0;

    private final ConcurrentMap<String, Boolean> scannedHashes = new ConcurrentHashMap<>();

    private static final int MAX_DEPTH = 4;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "exe", "dll", "jar", "bat", "ps1", "js", "vbs"
    );

    private static final Set<String> IGNORED_DIRECTORIES = Set.of(
            ".git", "node_modules", ".gradle", ".idea",
            "AppData", "Windows", "Program Files", "Program Files (x86)"
    );

    public FileScanner() {
        this.detector = new ThreatDetector();
        this.behaviorAnalyzer = new BehaviorAnalyzer();
        this.quarantineManager = new QuarantineManager("quarantine");

        int threads = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(threads);
    }

    public void setProgressListener(ScanProgressListener listener) {
        this.progressListener = listener;
    }

    private int countFilesFast(Path start) {
        try (Stream<Path> stream = Files.walk(start)) {
            return (int) stream.filter(Files::isRegularFile).count();
        } catch (Exception e) {
            return 1;
        }
    }

    public void scanDirectory(String path) {
        File root = new File(path);
        if (!root.exists()) {
            System.err.println("Diretório não encontrado: " + path);
            return;
        }

        System.out.println("> Calculando estrutura de arquivos...");
        totalFilesToScan = countFilesFast(root.toPath());
        if (totalFilesToScan == 0) totalFilesToScan = 1;
        System.out.println("> Encontrados " + totalFilesToScan + " arquivos. Iniciando varredura profunda...");

        totalFiles.set(0);
        suspiciousFiles.set(0);

        scan(root, 0);

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nScan finalizado.");
        System.out.println("Total de arquivos analisados: " + totalFiles.get());
        System.out.println("Arquivos suspeitos: " + suspiciousFiles.get());
    }

    public void scanSingleFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) return;

        if (isAllowedExtension(file)) {
            executor.submit(() -> analyzeFile(file));
        } else if (file.length() < 10_000_000 && !isSafeMediaExtension(file)) {
            executor.submit(() -> {
                if (MagicBytesChecker.isExecutableBinary(file)) {
                    analyzeFile(file);
                }
            });
        }
    }

    private void scan(File file, int depth) {
        if (file == null || depth > MAX_DEPTH) return;

        if (file.isDirectory()) {
            if (IGNORED_DIRECTORIES.contains(file.getName())) return;

            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) scan(f, depth + 1);
            }
            return;
        }

        if (file.isFile()) {
            if (isAllowedExtension(file)) {
                executor.submit(() -> analyzeFile(file));
            } else if (file.length() < 10_000_000 && !isSafeMediaExtension(file)) {
                // Arquivo pequeno sem extensão de mídia segura: verifica o header em background
                executor.submit(() -> {
                    if (MagicBytesChecker.isExecutableBinary(file)) {
                        analyzeFile(file);
                    }
                });
            }
        }
    }

    private boolean isAllowedExtension(File file) {
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        if (dot == -1) return false;
        return ALLOWED_EXTENSIONS.contains(name.substring(dot + 1).toLowerCase());
    }

    private boolean isSafeMediaExtension(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") ||
               name.endsWith(".mp3") || name.endsWith(".mp4") || name.endsWith(".gif");
    }

    private void analyzeFile(File file) {
        if (file == null || !file.exists()) return;

        int current = totalFiles.incrementAndGet();
        if (progressListener != null) {
            progressListener.onProgress(current, totalFilesToScan);
        }

        String hash = HashUtils.sha256(file);
        if (hash == null || scannedHashes.putIfAbsent(hash, true) != null) return;

        int score = detector.getRiskScore(file);
        String reasons = detector.getRiskReasons(file);

        int behaviorScore = behaviorAnalyzer.calculateBehaviorScore(file);
        if (behaviorScore > 0) {
            score += behaviorScore;
            String behaviorReasons = behaviorAnalyzer.getBehaviorReasons(file);
            if (behaviorReasons != null && !behaviorReasons.isEmpty()) {
                reasons += " " + behaviorReasons;
            }
        }

        if (score >= 5) {
            suspiciousFiles.incrementAndGet();
            System.out.println("[SUSPEITO] " + file.getAbsolutePath() +
                    " (Score: " + score + ") " + reasons);

            // Move para quarentena
            quarantineManager.moveToQuarantine(file);
        } else {
            System.out.println("[OK] " + file.getName() + " (Score: " + score + ")");
        }
    }

    // Métodos auxiliares para acessar a quarentena
    public File[] listQuarantineFiles() {
        return quarantineManager.listQuarantineFiles();
    }

    public boolean restoreFileFromQuarantine(File quarantinedFile, File originalPath) {
        return quarantineManager.restoreFromQuarantine(quarantinedFile, originalPath);
    }

    public boolean deleteFileFromQuarantine(File quarantinedFile) {
        return quarantineManager.deleteFromQuarantine(quarantinedFile);
    }

    public int getTotalFiles() {
        return totalFiles.get();
    }

    public int getSuspiciousFiles() {
        return suspiciousFiles.get();
    }
}
