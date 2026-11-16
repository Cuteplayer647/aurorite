package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cute.aurorite.client.gui.AuroraBackground;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.GenericMessageScreen;

/**
 * The message screen shown while resources are prepared draws the vanilla
 * panorama itself, overriding the shared {@code Screen} background, so it needs
 * its own hook to show the static artwork.
 */
@Mixin(GenericMessageScreen.class)
public class GenericMessageScreenMixin {

	@Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
	private void aurorite$staticBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick,
			CallbackInfo callbackInfo) {
		AuroraBackground.render(g);
		callbackInfo.cancel();
	}
}
