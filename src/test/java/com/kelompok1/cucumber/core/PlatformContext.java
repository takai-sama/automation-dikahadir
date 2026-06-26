package com.kelompok1.cucumber.core;

/**
 * Holder for the active Platform during a test run.
 *
 * Uses InheritableThreadLocal so child threads (spawned by the parallel
 * DataProvider in each runner) automatically inherit the platform value
 * set by the runner's static initializer in the parent thread.
 *
 * Without InheritableThreadLocal, parallel=true in the DataProvider would
 * spawn scenario threads that see null — causing "Platform not set" errors
 * even though the static block fired correctly in the runner class.
 */
public class PlatformContext {

    private static final InheritableThreadLocal<Platform> current =
        new InheritableThreadLocal<>();

    private PlatformContext() {}

    public static void set(Platform platform) {
        current.set(platform);
    }

    public static Platform get() {
        Platform platform = current.get();
        if (platform == null) {
            throw new IllegalStateException(
                "Platform not set. Ensure the static initializer in " +
                "WebTestRunner or MobileTestRunner ran before this call.");
        }
        return platform;
    }

    public static void clear() {
        current.remove();
    }
}
