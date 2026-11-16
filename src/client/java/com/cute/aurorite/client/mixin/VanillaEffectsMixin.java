package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cute.aurorite.client.module.ModuleManager;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;

/**
 * Hides the vanilla top-right status effect list.
 *
 * <p>Only hidden while the client's own effect HUD is on, so switching that module
 * off brings the vanilla display back instead of losing it.
 */
@Mixin(Hud.class)
public class VanillaEffectsMixin {

	@Inject(method = "extractEffects", at = @At("HEAD"), cancellable = true)
	private void aurorite$hideVanillaEffects(GuiGraphicsExtractor g, DeltaTracker deltaTracker,
			CallbackInfo callbackInfo) {
		if (ModuleManager.POTION_EFFECTS.isVanillaHidden()) {
			callbackInfo.cancel();
		}
	}
}
