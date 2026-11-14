package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.cute.aurorite.client.module.ModuleManager;

import net.minecraft.client.Minecraft;

/**
 * Blocks left and right click interaction while freelook is active.
 *
 * <p>The body does not follow the camera during freelook, so attacking or using
 * an item would aim somewhere the player is not looking. Both entry points are
 * cancelled, including the held-button paths.
 */
@Mixin(Minecraft.class)
public class FreelookInteractionMixin {

	@Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
	private void aurorite$blockAttack(CallbackInfoReturnable<Boolean> callbackInfo) {
		if (ModuleManager.FREELOOK.isLooking()) {
			callbackInfo.setReturnValue(false);
		}
	}

	@Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
	private void aurorite$blockUseItem(CallbackInfo callbackInfo) {
		if (ModuleManager.FREELOOK.isLooking()) {
			callbackInfo.cancel();
		}
	}

	@Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
	private void aurorite$blockHeldAttack(boolean leftDown, CallbackInfo callbackInfo) {
		if (ModuleManager.FREELOOK.isLooking()) {
			callbackInfo.cancel();
		}
	}
}
