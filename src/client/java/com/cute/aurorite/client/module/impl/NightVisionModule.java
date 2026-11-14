package com.cute.aurorite.client.module.impl;

import com.cute.aurorite.client.mixin.OptionInstanceAccessor;
import com.cute.aurorite.client.module.Module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;

/**
 * Full bright through the gamma option rather than a night vision effect.
 *
 * <p>The brightness slider is capped at 1.0, so the stored value is written
 * directly. The original value is restored when the module is switched off or
 * the game shuts down, and a value left behind by an earlier crash is cleaned up
 * on the next launch.
 */
public class NightVisionModule extends Module {

	private static final double FULL_BRIGHT = 10.0;
	private static final double DEFAULT_GAMMA = 0.5;

	private double previousGamma = DEFAULT_GAMMA;
	private boolean applied;

	public NightVisionModule() {
		super("夜视");
	}

	@Override
	protected void onToggle(boolean enabled) {
		if (enabled) {
			applyFullBright();
		} else {
			restore();
		}
	}

	public void tick() {
		Minecraft minecraft = Minecraft.getInstance();

		if (minecraft == null || minecraft.options == null) {
			return;
		}

		if (isEnabled()) {
			if (!applied) {
				applyFullBright();
			}
		} else if (applied) {
			restore();
		} else if (minecraft.options.gamma().get() > 1.0) {
			// Left over from a previous session that ended abruptly.
			write(minecraft.options.gamma(), DEFAULT_GAMMA);
		}
	}

	/** Restores the original brightness before the game writes its options. */
	public void restoreBeforeShutdown() {
		restore();
	}

	private void applyFullBright() {
		Minecraft minecraft = Minecraft.getInstance();

		// Options only exist once the client is fully constructed; the tick retries.
		if (minecraft == null || minecraft.options == null) {
			return;
		}

		OptionInstance<Double> gamma = minecraft.options.gamma();
		double current = gamma.get();

		// Only remember a sane slider value as the one to go back to.
		previousGamma = current <= 1.0 ? current : DEFAULT_GAMMA;

		write(gamma, FULL_BRIGHT);
		applied = true;
	}

	private void restore() {
		if (!applied) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();

		if (minecraft != null && minecraft.options != null) {
			write(minecraft.options.gamma(), previousGamma);
		}

		applied = false;
	}

	private static void write(OptionInstance<Double> option, double value) {
		((OptionInstanceAccessor) (Object) option).aurorite$setRawValue(value);
	}
}
