package com.cute.aurorite.client.module.impl;

import java.util.ArrayDeque;
import java.util.Deque;

import com.cute.aurorite.client.module.HudWidget;
import com.cute.aurorite.client.module.Module;
import com.cute.aurorite.client.module.setting.BooleanSetting;
import com.cute.aurorite.client.module.setting.NumberSetting;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.gui.Font;

/** Counts left and right clicks per second and shows them on the HUD. */
public class CpsModule extends Module implements HudWidget {

	private static final long WINDOW_MS = 1000L;

	private final Deque<Long> leftClicks = new ArrayDeque<>();
	private final Deque<Long> rightClicks = new ArrayDeque<>();

	private final BooleanSetting showLeft = add(new BooleanSetting("显示左键", true));
	private final BooleanSetting showRight = add(new BooleanSetting("显示右键", true));
	private final BooleanSetting dark = add(new BooleanSetting("深色模式", false));
	private final BooleanSetting rounded = add(new BooleanSetting("圆角", true));
	private final NumberSetting backgroundOpacity = add(new NumberSetting("背景不透明度", 70.0f, 0.0f, 100.0f));

	private int hudX = 8;
	private int hudY = 8;

	public CpsModule() {
		super("CPS显示");
	}

	/** Called for every physical mouse press. */
	public void registerClick(int button) {
		long now = System.currentTimeMillis();

		// 26.3 reports SDL button numbers: left = 1, middle = 2, right = 3.
		if (button == InputConstants.MOUSE_BUTTON_LEFT) {
			leftClicks.addLast(now);
		} else if (button == InputConstants.MOUSE_BUTTON_RIGHT) {
			rightClicks.addLast(now);
		}

		trim(now);
	}

	public int getLeftCps() {
		trim(System.currentTimeMillis());
		return leftClicks.size();
	}

	public int getRightCps() {
		trim(System.currentTimeMillis());
		return rightClicks.size();
	}

	/** 0..255 alpha for the HUD card background. */
	public int getBackgroundAlpha() {
		return Math.round(backgroundOpacity.getValue() * 255.0f / 100.0f);
	}

	/** Whether the HUD card is drawn with rounded corners. */
	public boolean isRounded() {
		return rounded.getValue();
	}

	/** Dark mode: black card with white text. */
	public boolean isDark() {
		return dark.getValue();
	}

	public String buildLabel() {		boolean left = showLeft.getValue();
		boolean right = showRight.getValue();

		if (left && right) {
			return "CPS L" + getLeftCps() + " R" + getRightCps();
		}

		if (left) {
			return "CPS " + getLeftCps();
		}

		if (right) {
			return "CPS " + getRightCps();
		}

		return "CPS";
	}

	private void trim(long now) {
		while (!leftClicks.isEmpty() && now - leftClicks.peekFirst() > WINDOW_MS) {
			leftClicks.pollFirst();
		}

		while (!rightClicks.isEmpty() && now - rightClicks.peekFirst() > WINDOW_MS) {
			rightClicks.pollFirst();
		}
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
