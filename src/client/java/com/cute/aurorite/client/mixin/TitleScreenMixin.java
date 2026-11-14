package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cute.aurorite.client.gui.AuroraBackground;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.TitleScreen;

/** Replaces the rotating panorama behind the main menu with a static image. */
@Mixin(TitleScreen.class)
public class TitleScreenMixin {

	@Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
	private void aurorite$staticBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick,
			CallbackInfo callbackInfo) {
		AuroraBackground.render(g);
		callbackInfo.cancel();
	}
}
