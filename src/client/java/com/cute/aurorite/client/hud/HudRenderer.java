package com.cute.aurorite.client.hud;

import com.cute.aurorite.client.gui.Theme;
import com.cute.aurorite.client.module.ModuleManager;
import com.cute.aurorite.client.module.impl.ComboModule;
import com.cute.aurorite.client.module.impl.CpsModule;
import com.cute.aurorite.client.module.impl.FpsModule;
import com.cute.aurorite.client.module.impl.HealthModule;
import com.cute.aurorite.client.module.impl.KeystrokesModule;
import com.cute.aurorite.client.module.impl.WatermarkModule;
import com.cute.aurorite.client.util.RenderUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;

/** Draws the client HUD elements (CPS counter, keystrokes and combo counter). */
public final class HudRenderer {

	private HudRenderer() {
	}

	public static void renderAll(GuiGraphicsExtractor g) {
		Minecraft minecraft = Minecraft.getInstance();
		Font font = minecraft.font;
		CpsModule cps = ModuleManager.CPS;

		if (cps.isEnabled()) {
			renderCps(g, font, cps);
		}

		KeystrokesModule keystrokes = ModuleManager.KEYSTROKES;

		if (keystrokes.isEnabled()) {
			renderKeystrokes(g, font, keystrokes, minecraft);
		}

		ComboModule combo = ModuleManager.COMBO;

		if (combo.isEnabled()) {
			renderCombo(g, font, combo);
		}

		HealthModule health = ModuleManager.HEALTH;

		if (health.isEnabled()) {
			renderHealth(g, font, health);
		}

		FpsModule fps = ModuleManager.FPS;

		if (fps.isEnabled()) {
			renderFps(g, font, fps);
		}

		WatermarkModule watermark = ModuleManager.WATERMARK;

		if (watermark.isEnabled()) {
			renderWatermark(g, watermark);
		}
	}

	public static void renderHealth(GuiGraphicsExtractor g, Font font, HealthModule module) {
		String label = module.buildLabel();
		int width = module.measureWidth(font);
		int height = module.measureHeight(font);
		int x = module.getX();
		int y = module.getY();
		float radius = module.isRounded() ? 5.0f : 0.0f;

		// The card is always the light style; only the text colour tracks health.
		RenderUtil.roundedRect(g, x, y, width, height, radius, background(module.getBackgroundAlpha(), false));
		// Shadow on, like vanilla text, so the value stays readable over a light card.
		g.text(font, label, x + 6, y + (height - 8) / 2, module.getHealthColour(), true);
	}

	/** A simple HUD card: rounded background, label on the left. */
	private static void drawCard(GuiGraphicsExtractor g, Font font, String label, int x, int y, int width, int height,
			boolean dark, boolean rounded, int alpha) {
		float radius = rounded ? 5.0f : 0.0f;

		RenderUtil.roundedRect(g, x, y, width, height, radius, background(alpha, dark));
		g.text(font, label, x + 6, y + (height - 8) / 2, labelColour(dark), false);
	}

	/** Draws the watermark artwork as-is: no card, no border, nothing else. */
	public static void renderWatermark(GuiGraphicsExtractor g, WatermarkModule module) {
		g.blitSprite(RenderPipelines.GUI_TEXTURED, WatermarkModule.SPRITE,
				module.getX(), module.getY(),
				module.getWatermarkWidth(), module.getWatermarkHeight());
	}

	public static void renderCps(GuiGraphicsExtractor g, Font font, CpsModule module) {
		drawCard(g, font, module.buildLabel(), module.getX(), module.getY(),
				module.measureWidth(font), module.measureHeight(font),
				module.isDark(), module.isRounded(), module.getBackgroundAlpha());
	}

	public static void renderCombo(GuiGraphicsExtractor g, Font font, ComboModule module) {
		drawCard(g, font, module.buildLabel(), module.getX(), module.getY(),
				module.measureWidth(font), module.measureHeight(font),
				module.isDark(), module.isRounded(), module.getBackgroundAlpha());
	}

	public static void renderFps(GuiGraphicsExtractor g, Font font, FpsModule module) {
		drawCard(g, font, module.buildLabel(), module.getX(), module.getY(),
				module.measureWidth(font), module.measureHeight(font),
				module.isDark(), module.isRounded(), module.getBackgroundAlpha());
	}

	public static void renderKeystrokes(GuiGraphicsExtractor g, Font font, KeystrokesModule module, Minecraft minecraft) {
		int x = module.getX();
		int y = module.getY();
		int alpha = module.getBackgroundAlpha();
		boolean dark = module.isDark();
		float radius = module.isRounded() ? 4.0f : 0.0f;

		int step = KeystrokesModule.CELL + KeystrokesModule.GAP;

		drawKey(g, font, x + step, y, "W", minecraft.options.keyUp.isDown(), alpha, radius, dark);
		drawKey(g, font, x, y + step, "A", minecraft.options.keyLeft.isDown(), alpha, radius, dark);
		drawKey(g, font, x + step, y + step, "S", minecraft.options.keyDown.isDown(), alpha, radius, dark);
		drawKey(g, font, x + step * 2, y + step, "D", minecraft.options.keyRight.isDown(), alpha, radius, dark);

		if (module.shouldShowSpace()) {
			int spaceY = y + step * 2;
			int spaceWidth = KeystrokesModule.CELL * 3 + KeystrokesModule.GAP * 2;
			boolean pressed = minecraft.options.keyJump.isDown();
			String label = "SPACE";

			RenderUtil.roundedRect(g, x, spaceY, spaceWidth, KeystrokesModule.SPACE_HEIGHT, radius,
					keyBackground(alpha, pressed, dark));
			g.text(font, label, x + (spaceWidth - font.width(label)) / 2,
					spaceY + (KeystrokesModule.SPACE_HEIGHT - 8) / 2, keyLabelColour(pressed, dark), false);
		}
	}

	private static void drawKey(GuiGraphicsExtractor g, Font font, int x, int y, String label, boolean pressed,
			int alpha, float radius, boolean dark) {
		RenderUtil.roundedRect(g, x, y, KeystrokesModule.CELL, KeystrokesModule.CELL, radius,
				keyBackground(alpha, pressed, dark));
		g.text(font, label, x + (KeystrokesModule.CELL - font.width(label)) / 2,
				y + (KeystrokesModule.CELL - 8) / 2, keyLabelColour(pressed, dark), false);
	}

	/** Card background: white in light mode, black in dark mode, opacity always from settings. */
	private static int background(int alpha, boolean dark) {
		return (alpha << 24) | (dark ? 0x000000 : 0xFFFFFF);
	}

	private static int labelColour(boolean dark) {
		return dark ? 0xFFFFFFFF : Theme.HUD_TEXT;
	}

	/**
	 * Key cap background. Light mode: white when idle, black when held. Dark mode:
	 * the inverse, so a held key still stands out from the black idle cap.
	 */
	private static int keyBackground(int alpha, boolean pressed, boolean dark) {
		int rgb;

		if (pressed) {
			rgb = dark ? 0xFFFFFF : 0x000000;
		} else {
			rgb = dark ? 0x000000 : 0xFFFFFF;
		}

		return (alpha << 24) | rgb;
	}

	private static int keyLabelColour(boolean pressed, boolean dark) {
		boolean lightText = pressed != dark;

		return lightText ? 0xFFFFFFFF : 0xFF1F1F1F;
	}
}
