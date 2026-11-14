package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;

/** Replaces the window title with the client name. */
@Mixin(Minecraft.class)
public class WindowTitleMixin {

	private static final String TITLE = "AuroriteClient 26.3";

	@Inject(method = "createTitle", at = @At("RETURN"), cancellable = true)
	private void aurorite$windowTitle(CallbackInfoReturnable<String> callbackInfo) {
		callbackInfo.setReturnValue(TITLE);
	}
}
