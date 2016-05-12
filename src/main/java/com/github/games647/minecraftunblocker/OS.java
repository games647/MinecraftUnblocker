package com.github.games647.minecraftunblocker;

import java.util.Locale;

public enum OS {
    LINUX,
    MACOS,
    SOLARIS,
    WINDOWS,
    UNKNOWN;

    private OS() {
    }

    public static OS getPlatform() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        if (osName.contains("linux")) {
            return OS.LINUX;
        }

        if (osName.contains("win")) {
            return OS.WINDOWS;
        }

        if (osName.contains("mac")) {
            return OS.MACOS;
        }

        if (osName.contains("unix")) {
            return OS.LINUX;
        }

        return OS.UNKNOWN;
    }
}
