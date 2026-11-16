package com.cute.aurorite.client.module.impl;

import com.cute.aurorite.client.module.Module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;

/**
 * 1.8.9 style sword block, purely visual.
 *
 * <p>Nothing is changed on the player: no "using item" state, so there is no
 * movement slowdown and nothing is sent to the server. The rendering side is
 * handled by {@code BlockAnimationMixin}, which plays the vanilla block pose.
 */
public class BlockAnimationModule extends Module {

	public BlockAnimationModule() {
		super("格挡动画");
	}

	/** Whether the main hand sword should be drawn in the blocking pose. */
	public boolean isBlocking() {
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;

		if (!isEnabled() || player == null) {
			return false;
		}

		if (minecraft.gui == null || minecraft.gui.screen() != null) {
			return false;
		}

		if (!minecraft.options.keyUse.isDown()) {
			return false;
		}

		if (!isBlockCapable(player.getMainHandItem())) {
			return false;
		}

		// A usable offhand item wins over blocking, but a sword in the offhand does not
		// count as usable, so the main hand sword can still block.
		ItemStack offhand = player.getOffhandItem();

		return offhand.isEmpty() || isBlockCapable(offhand) || isSword(offhand);
	}

	/** Items that can block natively, or a sword as a fallback. */
	private static boolean isBlockCapable(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}

		return stack.getUseAnimation().equals(ItemUseAnimation.BLOCK) || isSword(stack);
	}

	/** Swords are plain items with components in 26.3, so they are matched by id. */
	private static boolean isSword(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}

		Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());

		return id.getPath().endsWith("_sword");
	}
}
