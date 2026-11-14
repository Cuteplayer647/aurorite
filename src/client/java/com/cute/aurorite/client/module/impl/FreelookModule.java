package com.cute.aurorite.client.module.impl;

import com.cute.aurorite.client.module.Module;
import com.cute.aurorite.client.module.setting.KeySetting;
import com.cute.aurorite.client.module.setting.NumberSetting;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;

/**
 * Freelook: while the bound key is held the game switches to third person and
 * the mouse orbits the camera around the player without turning the body.
 * Releasing the key returns to the previous perspective.
 */
public class FreelookModule extends Module {

	private final KeySetting key = add(new KeySetting("按键", InputConstants.KEY_Y));
	private final NumberSetting sensitivity = add(new NumberSetting("灵敏度", 55.0f, 10.0f, 200.0f));

	private volatile boolean looking;
	private CameraType previousCamera = CameraType.FIRST_PERSON;
	private float yawOffset;
	private float pitchOffset;

	public FreelookModule() {
		super("FreeLook");
	}

	public int getKey() {
		return key.getKey();
	}

	/** True while the freelook key is held. */
	public boolean isLooking() {
		return looking;
	}

	/**
	 * Re-reads the key state and switches perspective on the edges. Called once
	 * per tick and before handling look input.
	 */
	public void refresh() {
		Minecraft minecraft = Minecraft.getInstance();

		if (minecraft == null || minecraft.options == null || minecraft.gui == null) {
			return;
		}

		// Never engage while a menu or the ClickGUI is open: keys there belong to
		// the screen, not to the camera.
		boolean held = isEnabled()
				&& minecraft.gui.screen() == null
				&& InputConstants.isKeyDown(key.getKey());

		if (held == looking) {
			return;
		}

		looking = held;

		if (held) {
			previousCamera = minecraft.options.getCameraType();
			minecraft.options.setCameraType(CameraType.THIRD_PERSON_BACK);
		} else {
			yawOffset = 0.0f;
			pitchOffset = 0.0f;
			minecraft.options.setCameraType(previousCamera);
		}
	}

	/** Accumulates the mouse look that was kept away from the player. */
	public void addLook(double yaw, double pitch) {
		float scale = sensitivity.getValue() / 100.0f;

		yawOffset += (float) yaw * scale;
		pitchOffset = Math.max(-90.0f, Math.min(90.0f, pitchOffset + (float) pitch * scale));
	}

	public float getYawOffset() {
		return yawOffset;
	}

	public float getPitchOffset() {
		return pitchOffset;
	}

	/** Restores the perspective as soon as the key is released. */
	public void tick() {
		refresh();
	}
}
