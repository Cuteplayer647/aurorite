package com.cute.aurorite.client.hud;

import com.cute.aurorite.client.gui.ClickGuiScreen;
import com.cute.aurorite.client.gui.HudEditScreen;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

/** Registers the client HUD with Fabric's HUD layer API. */
public class AuroriteHud implements HudElement {

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, DeltaTracker deltaTracker) {
		// Both client screens draw the HUD elements themselves so they can be
		// dragged there; skip the normal HUD pass to avoid drawing them twice.
		Screen screen = Minecraft.getInstance().gui.screen();

		if (screen instanceof ClickGuiScreen || screen instanceof HudEditScreen) {
			return;
		}

		HudRenderer.renderAll(g);
	}
}
