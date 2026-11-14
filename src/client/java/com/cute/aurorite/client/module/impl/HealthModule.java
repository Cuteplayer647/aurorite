package com.cute.aurorite.client.module.impl;

import java.util.Locale;

import com.cute.aurorite.client.module.HudWidget;
import com.cute.aurorite.client.module.Module;
import com.cute.aurorite.client.module.setting.BooleanSetting;
import com.cute.aurorite.client.module.setting.NumberSetting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;

/** Shows the player's current health as "12.5 ❤" on the HUD. */
public class HealthModule extends Module implements HudWidget {

	private final BooleanSetting rounded = add(new BooleanSetting("圆角", true));
	private final NumberSetting backgroundOpacity = add(new NumberSetting("背景不透明度", 70.0f, 0.0f, 100.0f));

	private int hudX = 8;
	private int hudY = 134;

	public HealthModule() {
		super("血量显示");
	}

	public String buildLabel() {
		return String.format(Locale.ROOT, "%.1f \u2764", getHealth());
	}

	public float getHealth() {
		LocalPlayer player = Minecraft.getInstance().player;

		return player == null ? 0.0f : player.getHealth();
	}

	/** Green at 14 or more, yellow from 8 to 13.9, red below 8. */
	public int getHealthColour() {
		float health = getHealth();

		if (health >= 14.0f) {
			return 0xFF2FA84F;
		}

		if (health >= 8.0f) {
			return 0xFFD9A400;
		}

		return 0xFFE03131;
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
