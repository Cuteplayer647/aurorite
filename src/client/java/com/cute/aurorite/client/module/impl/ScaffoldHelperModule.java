package com.cute.aurorite.client.module.impl;

import com.cute.aurorite.client.module.HudWidget;
import com.cute.aurorite.client.module.Module;
import com.cute.aurorite.client.module.setting.BooleanSetting;
import com.cute.aurorite.client.module.setting.NumberSetting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Scaffold helper: while a block is held and the player is looking down (the
 * usual bridging pose) it shows how many of that block are left.
 *
 * <p>Counts the hotbar, the main inventory and the offhand. Container contents
 * such as shulker boxes are never inspected, so blocks stored inside them are
 * not included.
 */
public class ScaffoldHelperModule extends Module implements HudWidget {

	private final BooleanSetting dark = add(new BooleanSetting("深色模式", false));
	private final BooleanSetting rounded = add(new BooleanSetting("圆角", true));
	private final NumberSetting backgroundOpacity = add(new NumberSetting("背景不透明度", 70.0f, 0.0f, 100.0f));
	private final NumberSetting triggerAngle = add(new NumberSetting("触发角度", 55.0f, 20.0f, 90.0f));

	private int hudX = 8;
	private int hudY = 174;

	public ScaffoldHelperModule() {
		super("ScaffoldHelper");
	}

	/** The block stack in the main hand, or an empty stack. */
	private ItemStack heldBlock() {
		LocalPlayer player = Minecraft.getInstance().player;

		if (player == null) {
			return ItemStack.EMPTY;
		}

		ItemStack held = player.getMainHandItem();

		if (held.isEmpty() || !(held.getItem() instanceof BlockItem)) {
			return ItemStack.EMPTY;
		}

		return held;
	}

	/** True while the block is held and the view is pitched down far enough. */
	public boolean isActive() {
		if (!isEnabled()) {
			return false;
		}

		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;

		if (player == null || minecraft.gui == null || minecraft.gui.screen() != null) {
			return false;
		}

		if (heldBlock().isEmpty()) {
			return false;
		}

		return player.getXRot() >= triggerAngle.getValue();
	}

	/** Total of the held block in the inventory plus the offhand. */
	public int countRemaining() {
		LocalPlayer player = Minecraft.getInstance().player;
		ItemStack held = heldBlock();

		if (player == null || held.isEmpty()) {
			return 0;
		}

		Item item = held.getItem();
		Inventory inventory = player.getInventory();
		int total = 0;

		for (int slot = 0; slot < Inventory.INVENTORY_SIZE; slot++) {
			ItemStack stack = inventory.getItem(slot);

			if (!stack.isEmpty() && stack.is(item)) {
				total += stack.getCount();
			}
		}

		ItemStack offhand = player.getOffhandItem();

		if (!offhand.isEmpty() && offhand.is(item)) {
			total += offhand.getCount();
		}

		return total;
	}

	public String buildLabel() {
		return countRemaining() + " Blocks";
	}

	public boolean isDark() {
		return dark.getValue();
	}

	public boolean isRounded() {
		return rounded.getValue();
	}

	public int getBackgroundAlpha() {
		return Math.round(backgroundOpacity.getValue() * 255.0f / 100.0f);
	}

	@Override
	public int getX() {
		return hudX;
	}

	@Override
	public int getY() {
		return hudY;
	}

	@Override
	public int measureWidth(Font font) {
		return font.width(buildLabel()) + 12;
	}

	@Override
	public int measureHeight(Font font) {
		return 16;
	}

	@Override
	public void setPosition(int x, int y) {
		this.hudX = x;
		this.hudY = y;
	}
}
