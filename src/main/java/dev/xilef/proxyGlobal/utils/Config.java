package dev.xilef.proxyGlobal.utils;

import dev.xilef.proxyGlobal.Main;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class Config {

    @Getter private static final Plugin instance = Main.getInstance();
    @Getter private static final Logger logger = Main.getInstance().getLogger();

    public static String getMessage(String id) {
        return getConfig().getString("configuration." + id + ".message", "§cmessage not found");
    }

    public static boolean isEnabled(String id) {
        return getConfig().getBoolean("configuration." + id + ".enabled", false);
    }

    public static Configuration getConfig() {
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getInstance().getDataFolder(), "config.yml"));
        } catch (IOException e) {
            getLogger().severe("Failed to load config: " + e.getMessage());
            return null;
        }
    }

    public static void saveDefaultConfig() {
        try {
            // Create plugin config folder if it doesn't exist
            if (!getInstance().getDataFolder().exists()) {
                getLogger().info("Created config folder: " + getInstance().getDataFolder().mkdir());
            }

            File configFile = new File(getInstance().getDataFolder(), "config.yml");

            // Copy default config if it doesn't exist
            if (!configFile.exists()) {
                FileOutputStream outputStream = new FileOutputStream(configFile); // Throws IOException
                InputStream in = getInstance().getResourceAsStream("config.yml"); // This file must exist in the jar resources folder
                in.transferTo(outputStream); // Throws IOException
            }
        } catch (IOException e) {
            getLogger().severe("Failed to save default config: " + e.getMessage());
        }
    }

}
