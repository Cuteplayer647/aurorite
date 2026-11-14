package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cute.aurorite.client.module.ModuleManager;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;

/** Feeds physical mouse presses to the CPS module without changing behaviour. */
@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

	@Inject(method = "onButton", at = @At("HEAD"))
	private void aurorite$countClick(long window, MouseButtonInfo buttonInfo, int action, CallbackInfo callbackInfo) {
		if (action == InputConstants.PRESS) {
			ModuleManager.CPS.registerClick(buttonInfo.button());
		}
	}
}
