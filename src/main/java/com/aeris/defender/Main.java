package com.aeris.defender;

import com.aeris.defender.core.Engine;

public class Main {

    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.start();

        Runtime.getRuntime().addShutdownHook(
                new Thread(engine::shutdown)
        );
    }
}
