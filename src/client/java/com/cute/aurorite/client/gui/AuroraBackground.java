package com.cute.aurorite.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

/** Draws the static aurora artwork as a full-screen menu background. */
public final class AuroraBackground {

	public static final Identifier SPRITE = Identifier.fromNamespaceAndPath("aurorite", "aurora");

	private static final float IMAGE_ASPECT = 16.0f / 9.0f;

	private AuroraBackground() {
	}

	/** Covers the screen with the artwork without stretching it. */
	public static void render(GuiGraphicsExtractor g) {
		int width = g.guiWidth();
		int height = g.guiHeight();

		int drawWidth = width;
		int drawHeight = height;

		if (width / (float) height > IMAGE_ASPECT) {
			drawHeight = Math.round(width / IMAGE_ASPECT);
		} else {
			drawWidth = Math.round(height * IMAGE_ASPECT);
		}

		g.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE,
				(width - drawWidth) / 2, (height - drawHeight) / 2, drawWidth, drawHeight);
	}
}
