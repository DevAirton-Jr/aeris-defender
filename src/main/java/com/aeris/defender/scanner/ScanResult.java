package com.aeris.defender.scanner;

import java.nio.file.Path;

public class ScanResult {

    private final Path file;
    private final boolean malicious;

    public ScanResult(Path file, boolean malicious) {
        this.file = file;
        this.malicious = malicious;
    }

    public Path getFile() {
        return file;
    }

    public boolean isMalicious() {
        return malicious;
    }
}
