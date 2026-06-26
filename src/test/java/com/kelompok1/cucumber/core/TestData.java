package com.kelompok1.cucumber.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Platform-aware test data loader.
 *
 * Loads the correct test-data file based on the active platform:
 *   - Platform.WEB    → test-data-web.properties
 *   - Platform.MOBILE → test-data-mobile.properties
 *
 * This is the single source of truth for test data values.
 * No credentials or expected strings are hardcoded in Java or feature files.
 *
 * Usage:
 *   TestData.get("user.valid.email")
 *   TestData.validEmail()
 *   TestData.validPassword()
 */
public class TestData {

    // Cache: one Properties per test-data file
    private static final Map<String, Properties> cache = new ConcurrentHashMap<>();

    private TestData() {}

    // =========================================================================
    // Generic accessor
    // =========================================================================

    /**
     * Returns the value for the given key from the active platform's test-data file.
     * Throws loudly if the key is missing so failures are obvious.
     */
    public static String get(String key) {
        String value = platformProperties().getProperty(key);
        if (value == null) {
            String fileName = PlatformContext.get().getTestDataFile();
            throw new RuntimeException(
                "Missing test data key: '" + key + "' in " + fileName);
        }
        return value;
    }

    // =========================================================================
    // Named convenience accessors
    // =========================================================================

    public static String validEmail()    { return get("user.valid.email"); }
    public static String validPassword() { return get("user.valid.password"); }

    public static String errorInvalidCredential()  { return get("error.login.invalid.credential"); }
    public static String errorPasswordRequired()   { return get("error.login.password.required"); }
    public static String errorEmailMissingAt()     { return get("error.login.email.missing.at"); }

    // =========================================================================
    // Internal
    // =========================================================================

    private static Properties platformProperties() {
        String fileName = PlatformContext.get().getTestDataFile();
        return cache.computeIfAbsent(fileName, TestData::load);
    }

    private static Properties load(String fileName) {
        Properties props = new Properties();
        try (InputStream in = TestData.class.getClassLoader().getResourceAsStream(fileName)) {
            if (in == null) {
                throw new RuntimeException("Test data file not found on classpath: " + fileName);
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test data file: " + fileName, e);
        }
        return props;
    }
}
