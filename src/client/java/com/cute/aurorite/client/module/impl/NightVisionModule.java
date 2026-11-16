package com.cute.aurorite.client.module.impl;

import com.cute.aurorite.client.module.Module;

import net.minecraft.client.Minecraft;

/**
 * Full bright by overriding the brightness the lightmap uses, rather than a
 * night vision effect or a change to the player's options.
 *
 * <p>See {@code NightVisionMixin}, which supplies the boosted value at read time.
 */
public class NightVisionModule extends Module {

	/** The gamma the lightmap is given while the module is on. */
	public static final double FULL_BRIGHT = 10.0;

	private static final double DEFAULT_GAMMA = 0.5;

	public NightVisionModule() {
		super("夜视");
	}

	public boolean isFullBright() {
		return isEnabled();
	}

	public void tick() {
		Minecraft minecraft = Minecraft.getInstance();

		if (minecraft == null || minecraft.options == null) {
			return;
		}

		// A value left behind by an older build that wrote the option directly.
		if (minecraft.options.gamma().get() > 1.0) {
			minecraft.options.gamma().set(DEFAULT_GAMMA);
		}
	}
}
