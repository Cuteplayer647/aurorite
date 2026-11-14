package com.cute.aurorite.client.module.impl;

import com.cute.aurorite.client.module.HudWidget;
import com.cute.aurorite.client.module.Module;

import net.minecraft.client.gui.Font;
import net.minecraft.resources.Identifier;

/**
 * Shows the client watermark on the HUD.
 *
 * <p>The artwork is a GUI sprite (loaded with the GUI atlas on every resource
 * reload) rather than a standalone texture, which is the reliable way to get a
 * custom image into the interface. Nothing else is drawn around it.
 */
public class WatermarkModule extends Module implements HudWidget {

	public static final Identifier SPRITE = Identifier.fromNamespaceAndPath("aurorite", "watermark");

	/** Width on screen; the height follows the artwork's aspect ratio. */
	private static final int WIDTH = 96;
	private static final int SOURCE_WIDTH = 544;
	private static final int SOURCE_HEIGHT = 132;

	private int hudX = 8;
	private int hudY = 112;

	public WatermarkModule() {
		super("Watermark");
	}

	public int getWatermarkWidth() {
		return WIDTH;
	}

	public int getWatermarkHeight() {
		return Math.round(WIDTH * (float) SOURCE_HEIGHT / SOURCE_WIDTH);
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
		return getWatermarkWidth();
	}

	@Override
	public int measureHeight(Font font) {
		return getWatermarkHeight();
	}

	@Override
	public void setPosition(int x, int y) {
		this.hudX = x;
		this.hudY = y;
	}
}
