package dev.xilef.proxyGlobal.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.xilef.proxyGlobal.Main;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    public static String version;

    public static void init() {
        version = checkForNewVersion();
        Main.getInstance().getLogger().info("You are running on version: " + Main.getInstance().getDescription().getVersion());

        if (!version.equals(Main.getInstance().getDescription().getVersion())) {
            Main.getInstance().getLogger().info("There is a new update available with version: " + version);
        }
    }

    public static boolean isUpdateAvailable() {
        return version != null && version.equals(Main.getInstance().getDescription().getVersion());
    }

    public static String checkForNewVersion() {
        String currentVersion = Main.getInstance().getDescription().getVersion();

        try {
            String latestVersion = getLatestReleaseVersion();

            if (latestVersion.toLowerCase().startsWith("beta-")) {
                return currentVersion;
            }

            ComparableVersion latestComparableVersion = new ComparableVersion(latestVersion);
            ComparableVersion currentComparableVersion = new ComparableVersion(currentVersion);

            int compare = currentComparableVersion.compareTo(latestComparableVersion);

            if (compare < 0) {
                return latestVersion;
            }

            return currentVersion;
        } catch (NullPointerException | IOException e) {
            Main.getInstance().getLogger().warning("The plugin wasn't able to check for updates! Please check your internet connection.");
        }

        return null;
    }

    private static String getLatestReleaseVersion() throws IOException {
        String pluginCode = "113784";

        String apiUrl = "https://api.spiget.org/v2/resources/" + pluginCode + "/updates/latest?size=1";

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        JsonObject jsonResponse = JsonParser.parseReader(reader).getAsJsonObject();
        reader.close();

        if (jsonResponse.has("title")) {
            return jsonResponse.get("title").getAsString();
        }

        return null;
    }

}
