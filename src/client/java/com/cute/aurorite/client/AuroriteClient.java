package com.cute.aurorite.client;

import com.cute.aurorite.client.config.AuroriteConfig;
import com.cute.aurorite.client.hud.AuroriteHud;
import com.cute.aurorite.client.module.ModuleManager;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;

public class AuroriteClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		AuroriteConfig.load();

		HudElementRegistry.addLast(
				Identifier.fromNamespaceAndPath("aurorite", "client_hud"),
				new AuroriteHud());

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			ModuleManager.COMBO.tick();
			ModuleManager.AUTO_SPRINT.tick();
			ModuleManager.FREELOOK.tick();
			ModuleManager.NIGHT_VISION.tick();
			ModuleManager.ZOOM.tick();
		});

		// Put the brightness and the field of view back before the game stores its options.
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			ModuleManager.NIGHT_VISION.restoreBeforeShutdown();
			ModuleManager.ZOOM.restoreBeforeShutdown();
		});
	}
}
