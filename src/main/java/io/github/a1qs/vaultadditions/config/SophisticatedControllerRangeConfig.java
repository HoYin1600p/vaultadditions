package io.github.a1qs.vaultadditions.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.a1qs.vaultadditions.VaultAdditions;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SophisticatedControllerRangeConfig {
    public static final int DEFAULT_RANGE = 24;
    public static final int WARNING_RANGE = 50;
    public static final int MAX_RANGE = 96;

    private static final String COMMENT = "Controlled by Vault Additions. Use /vaultadditions sophisticated-range <value> to update.";
    private static final String RANGE_KEY = "controllerRange";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static int controllerRange = loadRange();

    public static int getControllerRange() {
        return controllerRange;
    }

    public static void setControllerRange(int range) {
        controllerRange = clamp(range);
        save();
    }

    public static Path getConfigPath() {
        return FMLPaths.CONFIGDIR.get()
                .resolve("sophisticated-storage-override")
                .resolve("controller-range.json");
    }

    private static int loadRange() {
        Path path = getConfigPath();
        if (!Files.exists(path)) {
            save(DEFAULT_RANGE);
            return DEFAULT_RANGE;
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            if (json.has(RANGE_KEY)) {
                return clamp(json.get(RANGE_KEY).getAsInt());
            }
        } catch (Exception e) {
            VaultAdditions.LOGGER.error("Failed to read Sophisticated Storage controller range config at {}", path, e);
        }

        save(DEFAULT_RANGE);
        return DEFAULT_RANGE;
    }

    private static int clamp(int range) {
        return Math.max(1, Math.min(MAX_RANGE, range));
    }

    private static void save() {
        save(controllerRange);
    }

    private static void save(int range) {
        Path path = getConfigPath();
        JsonObject json = new JsonObject();
        json.addProperty("_comment", COMMENT);
        json.addProperty(RANGE_KEY, clamp(range));

        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(json), StandardCharsets.UTF_8);
        } catch (Exception e) {
            VaultAdditions.LOGGER.error("Failed to write Sophisticated Storage controller range config at {}", path, e);
        }
    }
}
