package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cute.aurorite.client.gui.AuroraBackground;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LevelLoadingScreen;

/**
 * The world loading screen overrides the background itself, so it needs its own
 * hook to show the static artwork instead of the vanilla gradient.
 */
@Mixin(LevelLoadingScreen.class)
public class LevelLoadingScreenMixin {

	@Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
	private void aurorite$staticLoadingBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick,
			CallbackInfo callbackInfo) {
		AuroraBackground.render(g);
		callbackInfo.cancel();
	}
}
