package com.kelompok1.cucumber.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kelompok1.cucumber.exceptions.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Platform-aware configuration reader.
 *
 * Loads the correct properties file based on the active platform:
 *   - Platform.WEB    → config-web.properties
 *   - Platform.MOBILE → config-mobile.properties
 *
 * Load priority (first match wins):
 *   1. JVM system property  (-Dkey=value)
 *   2. OS environment variable  (KEY=value, dots → underscores + uppercase)
 *   3. Platform config file fallback
 *
 * Files are cached per platform so they are only loaded once per JVM run.
 */
public class ConfigReader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);

    // Cache: one Properties object per config file name
    private static final Map<String, Properties> cache = new ConcurrentHashMap<>();

    private ConfigReader() {}

    // =========================================================================
    // Public API
    // =========================================================================

    public static String getProperty(String key) {
        // 1. JVM system property
        String value = System.getProperty(key);
        if (value != null) {
            logger.debug("Property '{}' from system property", key);
            return value;
        }

        // 2. OS environment variable (e.g. "base.url" → "BASE_URL")
        String envKey = key.toUpperCase().replace(".", "_");
        value = System.getenv(envKey);
        if (value != null) {
            logger.debug("Property '{}' from env var '{}'", key, envKey);
            return value;
        }

        // 3. Platform-specific config file
        value = platformProperties().getProperty(key);
        logger.debug("Property '{}' from config file: {}", key, value);
        return value;
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(getProperty(key, "false"));
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getProperty(key, String.valueOf(defaultValue)));
    }

    public static int getInt(String key) {
        return Integer.parseInt(getProperty(key, "0"));
    }

    public static int getInt(String key, int defaultValue) {
        return Integer.parseInt(getProperty(key, String.valueOf(defaultValue)));
    }

    // =========================================================================
    // Internal
    // =========================================================================

    private static Properties platformProperties() {
        String fileName = PlatformContext.get().getConfigFile();
        return cache.computeIfAbsent(fileName, ConfigReader::load);
    }

    private static Properties load(String fileName) {
        Properties props = new Properties();
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (in == null) {
                throw new ConfigurationException("Config file not found on classpath: " + fileName);
            }
            props.load(in);
            logger.info("Loaded config: {}", fileName);
        } catch (IOException e) {
            throw new ConfigurationException("Failed to load config file: " + fileName, e);
        }
        return props;
    }
}
