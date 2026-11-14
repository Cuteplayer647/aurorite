package com.cute.aurorite.client.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.cute.aurorite.client.module.HudWidget;
import com.cute.aurorite.client.module.Module;
import com.cute.aurorite.client.module.ModuleManager;
import com.cute.aurorite.client.module.setting.BooleanSetting;
import com.cute.aurorite.client.module.setting.KeySetting;
import com.cute.aurorite.client.module.setting.NumberSetting;
import com.cute.aurorite.client.module.setting.Setting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Persists module states, setting values and HUD positions to
 * {@code config/aurorite.json} so nothing resets between launches.
 */
public final class AuroriteConfig {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String FILE_NAME = "aurorite.json";

	private static boolean loading;

	private AuroriteConfig() {
	}

	public static void load() {
		Path path = path();

		if (!Files.exists(path)) {
			return;
		}

		loading = true;

		try {
			String text = Files.readString(path, StandardCharsets.UTF_8);
			JsonObject root = JsonParser.parseString(text).getAsJsonObject();
			JsonObject modules = root.getAsJsonObject("modules");

			if (modules == null) {
				return;
			}

			for (Module module : ModuleManager.modules()) {
				JsonObject entry = modules.getAsJsonObject(module.getName());

				if (entry == null) {
					continue;
				}

				// One module failing must not stop the rest of the config from loading.
				try {
					applyModule(module, entry);
				} catch (Exception exception) {
					System.err.println("[aurorite] could not restore " + module.getName() + ": "
							+ exception.getMessage());
				}
			}
		} catch (Exception exception) {
			System.err.println("[aurorite] could not read " + path + ": " + exception.getMessage());
		} finally {
			loading = false;
		}
	}

	private static void applyModule(Module module, JsonObject entry) {
		JsonObject settings = entry.getAsJsonObject("settings");

		if (settings != null) {
			for (Setting setting : module.getSettings()) {
				JsonElement value = settings.get(setting.getName());

				if (value == null) {
					continue;
				}

				if (setting instanceof BooleanSetting booleanSetting && value.isJsonPrimitive()) {
					booleanSetting.setValue(value.getAsBoolean());
				} else if (setting instanceof NumberSetting numberSetting && value.isJsonPrimitive()) {
					numberSetting.setValue(value.getAsFloat());
				} else if (setting instanceof KeySetting keySetting && value.isJsonPrimitive()) {
					keySetting.setKey(value.getAsInt());
				}
			}
		}

		if (module instanceof HudWidget widget) {
			if (entry.has("x") && entry.has("y")) {
				widget.setPosition(entry.get("x").getAsInt(), entry.get("y").getAsInt());
			}
		}

		// Enabled state last: the ClickGUI module must never restore as open.
		if (module.isPersistent() && entry.has("enabled")) {
			module.setEnabled(entry.get("enabled").getAsBoolean());
		}
	}

	public static void save() {
		if (loading) {
			return;
		}

		JsonObject root = new JsonObject();
		JsonObject modules = new JsonObject();

		for (Module module : ModuleManager.modules()) {
			JsonObject entry = new JsonObject();

			// Transient modules (the ClickGUI itself) still keep their settings,
			// only the enabled flag is left out.
			if (module.isPersistent()) {
				entry.addProperty("enabled", module.isEnabled());
			}

			JsonObject settings = new JsonObject();

			for (Setting setting : module.getSettings()) {
				if (setting instanceof BooleanSetting booleanSetting) {
					settings.addProperty(setting.getName(), booleanSetting.getValue());
				} else if (setting instanceof NumberSetting numberSetting) {
					settings.addProperty(setting.getName(), numberSetting.getValue());
				} else if (setting instanceof KeySetting keySetting) {
					settings.addProperty(setting.getName(), keySetting.getKey());
				}
			}

			entry.add("settings", settings);

			if (module instanceof HudWidget widget) {
				entry.addProperty("x", widget.getX());
				entry.addProperty("y", widget.getY());
			}

			modules.add(module.getName(), entry);
		}

		root.add("modules", modules);

		try {
			Path path = path();
			Files.createDirectories(path.getParent());
			Files.writeString(path, GSON.toJson(root), StandardCharsets.UTF_8);
		} catch (IOException exception) {
			System.err.println("[aurorite] could not write config: " + exception.getMessage());
		}
	}

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
	}
}
