package com.cute.aurorite.client.hud;

import java.util.List;

import com.cute.aurorite.client.gui.Theme;
import com.cute.aurorite.client.module.ModuleManager;
import com.cute.aurorite.client.module.impl.ComboModule;
import com.cute.aurorite.client.module.impl.CpsModule;
import com.cute.aurorite.client.module.impl.FpsModule;
import com.cute.aurorite.client.module.impl.HealthModule;
import com.cute.aurorite.client.module.impl.KeystrokesModule;
import com.cute.aurorite.client.module.impl.PotionEffectsModule;
import com.cute.aurorite.client.module.impl.ScaffoldHelperModule;
import com.cute.aurorite.client.module.impl.WatermarkModule;
import com.cute.aurorite.client.util.RenderUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;

/** Draws the client HUD elements (CPS counter, keystrokes and combo counter). */
public final class HudRenderer {

	private HudRenderer() {
	}

	public static void renderAll(GuiGraphicsExtractor g) {
		renderAll(g, false);
	}

	/**
	 * @param force show the modules whose own conditions are not met, which the HUD
	 *              editor needs so every element can be positioned
	 */
	public static void renderAll(GuiGraphicsExtractor g, boolean force) {
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

		ScaffoldHelperModule scaffold = ModuleManager.SCAFFOLD_HELPER;

		if (force ? scaffold.isEnabled() : scaffold.isActive()) {
			renderScaffoldHelper(g, font, scaffold);
		}

		PotionEffectsModule potions = ModuleManager.POTION_EFFECTS;

		if (potions.isEnabled()) {
			renderPotionEffects(g, font, potions);
		}

		WatermarkModule watermark = ModuleManager.WATERMARK;

		if (watermark.isEnabled()) {
			renderWatermark(g, watermark);
		}
	}

	public static void renderHealth(GuiGraphicsExtractor g, Font font, HealthModule module) {
		String label = module.buildLabel();
		int x = module.getX();
		int y = module.getY();
		float radius = module.isRounded() ? 5.0f : 0.0f;

		if (module.isNewMode()) {
			renderVanillaHearts(g, font, module, label, x, y);

			return;
		}

		int width = module.measureWidth(font);
		int height = module.measureHeight(font);

		// The card is always the light style; only the text colour tracks health.
		RenderUtil.roundedRect(g, x, y, width, height, radius, background(module.getBackgroundAlpha(), false));
		// Shadow on, like vanilla text, so the value stays readable over a light card.
		g.text(font, label, x + 6, y + (height - 8) / 2, module.getHealthColour(), true);
	}

	/** Vanilla heart row, centred, with the readout above it and absorption hearts on the right. */
	private static void renderVanillaHearts(GuiGraphicsExtractor g, Font font, HealthModule module, String label,
			int x, int y) {
		LocalPlayer player = Minecraft.getInstance().player;

		if (player == null) {
			return;
		}

		int contentX = x + 6;
		int contentWidth = module.measureWidth(font) - 12;

		// Text first, centred over the whole widget.
		g.text(font, label, contentX + (contentWidth - font.width(label)) / 2,
				y + (HealthModule.TEXT_HEIGHT - 8) / 2, module.getHealthColour(), true);

		int heartY = y + HealthModule.TEXT_HEIGHT + HealthModule.HEART_GAP;
		int hearts = module.heartCount();
		int rowWidth = hearts * 8;
		int rowX = contentX + (contentWidth - rowWidth) / 2;

		int healthPoints = Math.max(0, (int) Math.ceil(player.getHealth()));
		int absorptionPoints = Math.max(0, (int) Math.ceil(module.getAbsorption()));
		boolean blinking = module.isBlinking();

		Identifier container = heartSprite(blinking ? "container_blinking" : "container");

		for (int index = 0; index < hearts; index++) {
			int offset = rowX + index * 8;

			g.blitSprite(RenderPipelines.GUI_TEXTURED, container, offset, heartY, 9, 9);

			// The extra hearts past the first ten are the absorption ones.
			if (index < HealthModule.HEARTS_PER_ROW) {
				int remaining = healthPoints - index * 2;

				if (remaining >= 2) {
					g.blitSprite(RenderPipelines.GUI_TEXTURED,
							heartSprite(blinking ? "full_blinking" : "full"), offset, heartY, 9, 9);
				} else if (remaining == 1) {
					g.blitSprite(RenderPipelines.GUI_TEXTURED,
							heartSprite(blinking ? "half_blinking" : "half"), offset, heartY, 9, 9);
				}
			} else {
				int remaining = absorptionPoints - (index - HealthModule.HEARTS_PER_ROW) * 2;

				if (remaining >= 2) {
					g.blitSprite(RenderPipelines.GUI_TEXTURED,
							heartSprite(blinking ? "absorbing_full_blinking" : "absorbing_full"),
							offset, heartY, 9, 9);
				} else if (remaining == 1) {
					g.blitSprite(RenderPipelines.GUI_TEXTURED,
							heartSprite(blinking ? "absorbing_half_blinking" : "absorbing_half"),
							offset, heartY, 9, 9);
				}
			}
		}
	}

	private static Identifier heartSprite(String name) {
		return Identifier.fromNamespaceAndPath("minecraft", "hud/heart/" + name);
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
		g.blitSprite(RenderPipelines.GUI_TEXTURED, module.getSprite(),
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

	public static void renderScaffoldHelper(GuiGraphicsExtractor g, Font font, ScaffoldHelperModule module) {
		drawCard(g, font, module.buildLabel(), module.getX(), module.getY(),
				module.measureWidth(font), module.measureHeight(font),
				module.isDark(), module.isRounded(), module.getBackgroundAlpha());
	}

	/** Effect list: icon, name with level, and time left per row. */
	public static void renderPotionEffects(GuiGraphicsExtractor g, Font font, PotionEffectsModule module) {
		List<MobEffectInstance> effects = module.effects();

		if (effects.isEmpty()) {
			return;
		}

		int x = module.getX();
		int y = module.getY();
		int width = module.measureWidth(font);
		boolean dark = module.isDark();
		int alpha = module.getBackgroundAlpha();
		float radius = module.isRounded() ? 5.0f : 0.0f;

		for (int index = 0; index < effects.size(); index++) {
			MobEffectInstance instance = effects.get(index);
			int rowY = y + index * PotionEffectsModule.ROW_HEIGHT;

			RenderUtil.roundedRect(g, x, rowY, width, PotionEffectsModule.ROW_HEIGHT - 2, radius,
					background(alpha, dark));

			Identifier icon = Hud.getMobEffectSprite(module.effectHolder(instance));
			int iconY = rowY + (PotionEffectsModule.ROW_HEIGHT - 2 - PotionEffectsModule.ICON_SIZE) / 2;

			g.blitSprite(RenderPipelines.GUI_TEXTURED, icon, x + 3, iconY,
					PotionEffectsModule.ICON_SIZE, PotionEffectsModule.ICON_SIZE);

			g.text(font, PotionEffectsModule.describe(instance), x + PotionEffectsModule.ICON_SIZE + 7,
					rowY + (PotionEffectsModule.ROW_HEIGHT - 2 - 8) / 2, labelColour(dark), false);
		}
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
