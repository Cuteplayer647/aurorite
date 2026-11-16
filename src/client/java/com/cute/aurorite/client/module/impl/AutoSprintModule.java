package com.cute.aurorite.client.module.impl;

import com.cute.aurorite.client.module.Module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/**
 * Keeps the player sprinting while they hold the forward key.
 *
 * <p>Sprinting is neither started nor kept while the player runs into a wall or
 * is using an item (eating, drinking, blocking, ...). Vanilla cancels the sprint
 * in those cases, but this module would otherwise re-enable it on the next tick,
 * which is what made the sprint state stick.
 */
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

		if (player == null) {
			return;
		}

		// Vanilla ignores small collisions (slabs, carpets), so only a real wall counts.
		boolean pushedAgainstWall = player.horizontalCollision && !player.minorHorizontalCollision;
		boolean blocked = player.isShiftKeyDown()
				|| player.isUsingItem()
				|| player.isPassenger()
				|| pushedAgainstWall
				|| (!player.getAbilities().mayfly && !player.getFoodData().hasEnoughFood());

		if (minecraft.options.keyUp.isDown() && !blocked) {
			if (!player.isSprinting()) {
				player.setSprinting(true);
			}

			return;
		}

		// Actively stop a sprint this module would keep alive; anything else is
		// vanilla's business.
		if (player.isSprinting() && (player.isUsingItem() || pushedAgainstWall)) {
			player.setSprinting(false);
		}
	}
}
