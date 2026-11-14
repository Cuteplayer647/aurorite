package com.cute.aurorite.client.module.impl;

import com.cute.aurorite.client.module.HudWidget;
import com.cute.aurorite.client.module.Module;
import com.cute.aurorite.client.module.setting.BooleanSetting;
import com.cute.aurorite.client.module.setting.NumberSetting;

import net.minecraft.client.gui.Font;

/** Draws a compact WASD + space keyboard on the HUD. */
public class KeystrokesModule extends Module implements HudWidget {

	public static final int CELL = 16;
	public static final int GAP = 2;
	public static final int SPACE_HEIGHT = 10;

	private final BooleanSetting showSpace = add(new BooleanSetting("显示空格键", true));
	private final BooleanSetting dark = add(new BooleanSetting("深色模式", false));
	private final BooleanSetting rounded = add(new BooleanSetting("圆角", true));
	private final NumberSetting backgroundOpacity = add(new NumberSetting("背景不透明度", 70.0f, 0.0f, 100.0f));

	private int hudX = 8;
	private int hudY = 34;

	public KeystrokesModule() {
		super("按键显示");
	}

	public boolean shouldShowSpace() {
		return showSpace.getValue();
	}

	/** Whether the key caps are drawn with rounded corners. */
	public boolean isRounded() {
		return rounded.getValue();
	}

	/** Dark mode: black key caps with white letters. */
	public boolean isDark() {
		return dark.getValue();
	}

	/** 0..255 alpha derived from the opacity setting. */
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
		return CELL * 3 + GAP * 2;
	}

	@Override
	public int measureHeight(Font font) {
		int height = CELL * 2 + GAP;

		if (shouldShowSpace()) {
			height += GAP + SPACE_HEIGHT;
		}

		return height;
	}

	@Override
	public void setPosition(int x, int y) {
		this.hudX = x;
		this.hudY = y;
	}
}
