package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cute.aurorite.client.gui.AuroraBackground;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

/**
 * Puts the static aurora artwork behind every menu screen (world list,
 * multiplayer, settings, ...), replacing the rotating panorama and the dirt
 * background. In-game screens keep the vanilla background.
 */
@Mixin(Screen.class)
public class MenuBackgroundMixin {

	@Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
	private void aurorite$staticMenuBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick,
			CallbackInfo callbackInfo) {
		if (Minecraft.getInstance().level != null) {
			return;
		}

		AuroraBackground.render(g);
		callbackInfo.cancel();
	}
}
