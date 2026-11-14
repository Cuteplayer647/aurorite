package com.cute.aurorite.client.util;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/**
 * Works out how large the drawable GUI area really is.
 *
 * <p>{@link Screen#width} comes from the window size the game believes it has,
 * which can disagree with the drawable framebuffer on scaled desktops. Taking
 * the smallest of the candidates keeps the interface inside the visible area.
 */
public final class Viewport {

	private Viewport() {
	}

	public static int width(Screen screen) {
		Window window = Minecraft.getInstance().getWindow();
		int scale = Math.max(1, window.getGuiScale());

		return Math.min(screen.width, Math.min(window.getGuiScaledWidth(),
				Math.min(window.getWidth() / scale, window.getScreenWidth() / scale)));
	}

	public static int height(Screen screen) {
		Window window = Minecraft.getInstance().getWindow();
		int scale = Math.max(1, window.getGuiScale());

		return Math.min(screen.height, Math.min(window.getGuiScaledHeight(),
				Math.min(window.getHeight() / scale, window.getScreenHeight() / scale)));
	}
}
