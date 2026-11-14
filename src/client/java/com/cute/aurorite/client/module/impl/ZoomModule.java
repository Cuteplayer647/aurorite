package com.cute.aurorite.client.module.impl;

import com.cute.aurorite.client.module.Module;
import com.cute.aurorite.client.module.setting.KeySetting;
import com.cute.aurorite.client.module.setting.NumberSetting;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.Minecraft;

/**
 * Zoom: while the bound key is held the field of view is set to the configured
 * value. Releasing the key restores the player's own FOV setting.
 */
public class ZoomModule extends Module {

	private final KeySetting key = add(new KeySetting("按键", InputConstants.KEY_C));
	private final NumberSetting zoomFov = add(new NumberSetting("视角", 30.0f, 30.0f, 110.0f));

	private volatile boolean zooming;
	private int previousFov = 70;

	public ZoomModule() {
		super("Zoom");
	}

	public int getKey() {
		return key.getKey();
	}

	public void tick() {
		Minecraft minecraft = Minecraft.getInstance();

		if (minecraft == null || minecraft.options == null || minecraft.gui == null) {
			return;
		}

		// The key belongs to a screen while one is open, not to the camera.
		boolean held = isEnabled()
				&& minecraft.gui.screen() == null
				&& InputConstants.isKeyDown(key.getKey());

		if (held == zooming) {
			return;
		}

		zooming = held;

		if (held) {
			previousFov = minecraft.options.fov().get();
			minecraft.options.fov().set(Math.round(zoomFov.getValue()));
		} else {
			minecraft.options.fov().set(previousFov);
		}
	}

	public boolean isZooming() {
		return zooming;
	}

	/** Puts the player's own FOV back before the game stores its options. */
	public void restoreBeforeShutdown() {
		if (!zooming) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();

		if (minecraft != null && minecraft.options != null) {
			minecraft.options.fov().set(previousFov);
		}

		zooming = false;
	}
}
