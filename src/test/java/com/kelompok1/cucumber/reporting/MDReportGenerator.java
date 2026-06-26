package com.kelompok1.cucumber.reporting;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MDReportGenerator {

    private static final Pattern QUOTED_STRING = Pattern.compile("\"([^\"]*)\"");
    private static final String OUTPUT_DIR = "target/sit-reports";

    public static void generate(String platform) {
        String expectedPath = "target/cucumber-reports/" + platform + "/cucumber.json";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss"));
        String fileName = "SIT_Report_" + platform.toUpperCase() + "_" + timestamp + ".md";

        System.out.println("[SIT MD] ============================================================");
        System.out.println("[SIT MD] Generating SIT MD report for platform: " + platform);

        try {
            Files.createDirectories(Paths.get(OUTPUT_DIR));

            Path jsonFile = Paths.get(expectedPath);
            System.out.println("[SIT MD] Reading JSON: " + jsonFile.toAbsolutePath());

            String content = readJson(jsonFile);
            if (content.isEmpty()) {
                System.err.println("[SIT MD] FAILED: Could not read JSON. No MD report written.");
                return;
            }

            // Strip BOM if present
            if (content.charAt(0) == '\uFEFF') {
                content = content.substring(1);
            }

            String trimmed = content.trim();
            if (!trimmed.startsWith("[")) {
                System.err.println("[SIT MD] FAILED: JSON does not start with '['. First 200 chars:");
                System.err.println(trimmed.substring(0, Math.min(200, trimmed.length())));
                return;
            }

            JSONArray features = new JSONArray(trimmed);
            System.out.println("[SIT MD] Parsed " + features.length() + " feature(s)");

            StringBuilder md = new StringBuilder();
            md.append("# SIT Report — ").append(capitalize(platform)).append("\n\n");
            md.append("| Generated | ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append(" |\n");
            md.append("| Platform | ").append(capitalize(platform)).append(" |\n");
            md.append("| Total Features | ").append(features.length()).append(" |\n\n");

            md.append("| Test Case ID | Module | Test Scenario | Type | Platform | Preconditions | Test Data | Test Step | Expected Result | Actual Result | Status | Note | Evidence |\n");
            md.append("|---|---|---|---|---|---|---|---|---|---|---|---|---|\n");

            int tcCounter = 1;

            for (int f = 0; f < features.length(); f++) {
                JSONObject feature = features.getJSONObject(f);
                String module = feature.optString("name", "Unnamed");
                JSONArray elements = feature.optJSONArray("elements");
                if (elements == null) continue;

                String preconditions = extractBackground(elements);

                for (int e = 0; e < elements.length(); e++) {
                    JSONObject element = elements.getJSONObject(e);
                    if (!"scenario".equals(element.optString("type"))) continue;

                    String scenarioName = element.optString("name", "");
                    List<String> tags = extractTags(element.optJSONArray("tags"));

                    String type = resolveType(tags);
                    String plat = resolvePlatform(tags, platform);

                    JSONArray steps = element.optJSONArray("steps");
                    if (steps == null) steps = new JSONArray();

                    String testSteps = extractTestSteps(steps);
                    String testData = extractTestData(steps);
                    String expectedResult = extractExpectedResult(steps);
                    String status = determineStatus(steps);
                    String errorMsg = extractErrorMessage(steps);

                    String actualResult = "passed".equalsIgnoreCase(status) ? "As expected" : errorMsg;
                    String note = "passed".equalsIgnoreCase(status) ? "" : errorMsg;
                    String evidence = "failed".equalsIgnoreCase(status) ? "Screenshot attached" : "";

                    String tcId = "TC-" + String.format("%03d", tcCounter++);

                    md.append("| ").append(esc(tcId))
                      .append(" | ").append(esc(module))
                      .append(" | ").append(esc(scenarioName))
                      .append(" | ").append(esc(type))
                      .append(" | ").append(esc(plat))
                      .append(" | ").append(esc(preconditions))
                      .append(" | ").append(esc(testData))
                      .append(" | ").append(esc(testSteps))
                      .append(" | ").append(esc(expectedResult))
                      .append(" | ").append(esc(actualResult))
                      .append(" | ").append(esc(status.toUpperCase()))
                      .append(" | ").append(esc(note))
                      .append(" | ").append(esc(evidence))
                      .append(" |\n");
                }
            }

            Path out = Paths.get(OUTPUT_DIR, fileName);
            Files.writeString(out, md.toString());
            System.out.println("[SIT MD] SUCCESS: Report written to " + out.toAbsolutePath());

        } catch (Exception ex) {
            System.err.println("[SIT MD] CRASH: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static String readJson(Path path) {
        try {
            if (!Files.exists(path)) {
                System.err.println("[SIT MD] JSON not found: " + path);
                return "";
            }
            byte[] bytes = Files.readAllBytes(path);
            String content = new String(bytes, StandardCharsets.UTF_8);
            System.out.println("[SIT MD] JSON size: " + content.length() + " chars");
            return content;
        } catch (IOException e) {
            System.err.println("[SIT MD] Failed to read JSON: " + e.getMessage());
            return "";
        }
    }

    private static String extractBackground(JSONArray elements) {
        for (int i = 0; i < elements.length(); i++) {
            JSONObject el = elements.getJSONObject(i);
            if ("background".equals(el.optString("type"))) {
                return extractSteps(el.optJSONArray("steps"));
            }
        }
        return "";
    }

    private static List<String> extractTags(JSONArray tags) {
        List<String> list = new ArrayList<>();
        if (tags == null) return list;
        for (int i = 0; i < tags.length(); i++) {
            list.add(tags.getJSONObject(i).optString("name", ""));
        }
        return list;
    }

    private static String resolveType(List<String> tags) {
        if (tags.contains("@happy-path")) return "Happy Path";
        if (tags.contains("@positive")) return "Positive";
        if (tags.contains("@negative")) return "Negative";
        if (tags.contains("@edge-case")) return "Edge Case";
        return "";
    }

    private static String resolvePlatform(List<String> tags, String fallback) {
        if (tags.contains("@web")) return "Web";
        if (tags.contains("@mobile")) return "Mobile";
        return capitalize(fallback);
    }

    private static String extractSteps(JSONArray steps) {
        if (steps == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < steps.length(); i++) {
            JSONObject step = steps.getJSONObject(i);
            String keyword = step.optString("keyword", "").trim();
            String name = step.optString("name", "");
            if (sb.length() > 0) sb.append("<br>");
            sb.append(keyword).append(" ").append(name);
        }
        return sb.toString();
    }

    private static String extractTestSteps(JSONArray steps) {
        if (steps == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < steps.length(); i++) {
            JSONObject step = steps.getJSONObject(i);
            String keyword = step.optString("keyword", "").trim();
            if ("Then".equalsIgnoreCase(keyword)) break;
            if (sb.length() > 0) sb.append("<br>");
            sb.append(keyword).append(" ").append(step.optString("name", ""));
        }
        return sb.toString();
    }

    private static String extractExpectedResult(JSONArray steps) {
        if (steps == null) return "";
        StringBuilder sb = new StringBuilder();
        boolean foundThen = false;
        for (int i = 0; i < steps.length(); i++) {
            JSONObject step = steps.getJSONObject(i);
            String keyword = step.optString("keyword", "").trim();
            if ("Then".equalsIgnoreCase(keyword)) foundThen = true;
            if (foundThen) {
                if (sb.length() > 0) sb.append("<br>");
                sb.append(keyword).append(" ").append(step.optString("name", ""));
            }
        }
        return sb.toString();
    }

    private static String extractTestData(JSONArray steps) {
        if (steps == null) return "";
        Set<String> items = new LinkedHashSet<>();
        String lastRealKeyword = "";
        for (int i = 0; i < steps.length(); i++) {
            JSONObject step = steps.getJSONObject(i);
            String keyword = step.optString("keyword", "").trim();
            String name = step.optString("name", "");

            if (!"And".equalsIgnoreCase(keyword) && !"But".equalsIgnoreCase(keyword)) {
                lastRealKeyword = keyword;
            }

            if ("Given".equalsIgnoreCase(lastRealKeyword) || "When".equalsIgnoreCase(lastRealKeyword)) {
                Matcher m = QUOTED_STRING.matcher(name);
                while (m.find()) {
                    String val = m.group(1).trim();
                    if (!val.isEmpty()) items.add(val);
                }
            }
        }
        return items.isEmpty() ? "See test-data.properties" : String.join(", ", items);
    }

    private static String determineStatus(JSONArray steps) {
        if (steps == null) return "skipped";
        boolean hasFailed = false;
        boolean hasSkipped = false;
        for (int i = 0; i < steps.length(); i++) {
            JSONObject result = steps.getJSONObject(i).optJSONObject("result");
            if (result == null) continue;
            String status = result.optString("status", "");
            if ("failed".equals(status)) hasFailed = true;
            if ("skipped".equals(status)) hasSkipped = true;
        }
        if (hasFailed) return "failed";
        if (hasSkipped) return "skipped";
        return "passed";
    }

    private static String extractErrorMessage(JSONArray steps) {
        if (steps == null) return "";
        for (int i = 0; i < steps.length(); i++) {
            JSONObject result = steps.getJSONObject(i).optJSONObject("result");
            if (result == null) continue;
            if ("failed".equals(result.optString("status", ""))) {
                String msg = result.optString("error_message", "");
                int idx = msg.indexOf('\n');
                return idx > 0 ? msg.substring(0, idx).trim() : msg.trim();
            }
        }
        return "";
    }

    private static String esc(String text) {
        if (text == null) return "";
        return text.replace("|", "\\|").replace("\n", " ").replace("\r", "");
    }

    private static String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}
