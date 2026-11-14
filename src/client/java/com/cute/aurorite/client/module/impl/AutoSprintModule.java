package com.cute.aurorite.client.module.impl;

import com.cute.aurorite.client.module.Module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/** Keeps the player sprinting while they hold the forward key. */
public class AutoSprintModule extends Module {

	public AutoSprintModule() {
		super("自动疾跑");
	}

	/** Runs once per client tick. */
	public void tick() {
		if (!isEnabled()) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;

		if (player == null || player.isSprinting()) {
			return;
		}

		if (!minecraft.options.keyUp.isDown() || player.isShiftKeyDown() || player.isUsingItem()) {
			return;
		}

		player.setSprinting(true);
	}
}
