package com.aeris.defender.core;

import com.aeris.defender.scanner.FileScanner;

import java.nio.file.Path;

public class Engine {

    public void start() {
        System.out.println("[AerisDefender] Engine iniciada.");

        FileScanner scanner = new FileScanner();
        try {
            scanner.scan(Path.of("C:/Users"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        System.out.println("[AerisDefender] Engine finalizada.");
    }
}
