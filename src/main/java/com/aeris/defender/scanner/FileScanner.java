package com.aeris.defender.scanner;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FileScanner {

    public List<ScanResult> scan(Path root) throws IOException {
        List<ScanResult> results = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(root)) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        System.out.println("Analisando: " + file);
                        results.add(new ScanResult(file, false));
                    });
        }

        return results;
    }
}
