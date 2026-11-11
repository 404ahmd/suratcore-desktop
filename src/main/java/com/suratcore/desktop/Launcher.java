package com.suratcore.desktop;

/**
 * Main launcher class to bypass JavaFX runtime component checks
 * when packaged into a standard non-modular JAR.
 */
public class Launcher {
    public static void main(String[] args) {
        App.main(args);
    }
}

