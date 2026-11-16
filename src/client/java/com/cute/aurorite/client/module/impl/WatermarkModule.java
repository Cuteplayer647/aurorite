package com.cute.aurorite.client.module.impl;

import com.cute.aurorite.client.module.HudWidget;
import com.cute.aurorite.client.module.Module;
import com.cute.aurorite.client.module.setting.ModeSetting;

import net.minecraft.client.gui.Font;
import net.minecraft.resources.Identifier;

/**
 * Shows the client watermark on the HUD.
 *
 * <p>The artwork is a GUI sprite (loaded with the GUI atlas on every resource
 * reload) rather than a standalone texture, which is the reliable way to get a
 * custom image into the interface. Nothing else is drawn around it.
 *
 * <p>Three styles ship with the client and are picked in the module settings.
 */
public class WatermarkModule extends Module implements HudWidget {

	public static final Identifier SPRITE = Identifier.fromNamespaceAndPath("aurorite", "watermark");

	/** The styles, in the order the mode setting cycles through them. */
	private static final String[] STYLES = { "default", "prism", "gradient" };

	/** Width on screen; the height follows the artwork's aspect ratio. */
	private static final int WIDTH = 96;

	/** Source artwork sizes, one per style. */
	private static final int[] SOURCE_WIDTHS = { 544, 495, 362 };
	private static final int[] SOURCE_HEIGHTS = { 132, 112, 96 };

	/** Sprites, one per style. */
	private static final Identifier[] SPRITES = {
			SPRITE,
			Identifier.fromNamespaceAndPath("aurorite", "watermark_prism"),
			Identifier.fromNamespaceAndPath("aurorite", "watermark_gradient")
	};

	private final ModeSetting style = add(new ModeSetting("样式", 0, STYLES));

	private int hudX = 8;
	private int hudY = 112;

	public WatermarkModule() {
		super("Watermark");
	}

	/** Index of the selected style. */
	private int styleIndex() {
		String value = style.getValue();

		for (int index = 0; index < STYLES.length; index++) {
			if (STYLES[index].equalsIgnoreCase(value)) {
				return index;
			}
		}

		return 0;
	}

	public Identifier getSprite() {
		return SPRITES[styleIndex()];
	}

	public int getWatermarkWidth() {
		return WIDTH;
	}

	public int getWatermarkHeight() {
		int index = styleIndex();

		return Math.round(WIDTH * (float) SOURCE_HEIGHTS[index] / SOURCE_WIDTHS[index]);
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
