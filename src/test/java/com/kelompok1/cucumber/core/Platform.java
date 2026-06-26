package com.kelompok1.cucumber.core;

/**
 * Enum representing the two test platforms in this project.
 *
 * WEB    → https://magang.dikahadir.com/authentication/login (admin panel)
 *          creds: admin@hadir.com / MagangSQA_JC@123
 *          redirect: /dashboards/pending
 *
 * MOBILE → https://magang.dikahadir.com/absen/login (mobile mirroring panel)
 *          creds: hadirsqa1@gmail.com / SQA@Hadir12345
 *          redirect: /apps/absent
 *
 * Each platform loads its own config and test-data properties file.
 */
public enum Platform {
    WEB("config-web.properties", "test-data-web.properties"),
    MOBILE("config-mobile.properties", "test-data-mobile.properties");

    private final String configFile;
    private final String testDataFile;

    Platform(String configFile, String testDataFile) {
        this.configFile   = configFile;
        this.testDataFile = testDataFile;
    }

    public String getConfigFile()   { return configFile; }
    public String getTestDataFile() { return testDataFile; }
}
