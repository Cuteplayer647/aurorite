package com.cute.aurorite.client.module.impl;

import com.cute.aurorite.client.module.HudWidget;
import com.cute.aurorite.client.module.Module;
import com.cute.aurorite.client.module.setting.BooleanSetting;
import com.cute.aurorite.client.module.setting.NumberSetting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

/** Shows the current framerate as "FPS 120" on the HUD. */
public class FpsModule extends Module implements HudWidget {

	private final BooleanSetting dark = add(new BooleanSetting("深色模式", false));
	private final BooleanSetting rounded = add(new BooleanSetting("圆角", true));
	private final NumberSetting backgroundOpacity = add(new NumberSetting("背景不透明度", 70.0f, 0.0f, 100.0f));

	private int hudX = 8;
	private int hudY = 154;

	public FpsModule() {
		super("FPS显示");
	}

	public String buildLabel() {
		return "FPS " + Minecraft.getInstance().getFps();
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
