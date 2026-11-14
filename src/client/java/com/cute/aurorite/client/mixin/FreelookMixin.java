package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.cute.aurorite.client.module.ModuleManager;
import com.cute.aurorite.client.module.impl.FreelookModule;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

/**
 * Freelook: keeps the player's body still while the camera turns.
 *
 * <p>Mouse look reaches the player through {@code turn}; while the freelook key
 * is held those deltas go to the module instead. The camera reads its angles
 * from {@code getViewYRot}/{@code getViewXRot}, so the offset is added there.
 */
@Mixin(Entity.class)
public class FreelookMixin {

	@Inject(method = "turn", at = @At("HEAD"), cancellable = true)
	private void aurorite$holdBodyStill(double yRot, double xRot, CallbackInfo callbackInfo) {
		FreelookModule freelook = ModuleManager.FREELOOK;

		if ((Object) this != Minecraft.getInstance().player) {
			return;
		}

		freelook.refresh();

		if (freelook.isLooking()) {
			freelook.addLook(yRot, xRot);
			callbackInfo.cancel();
		}
	}

	@Inject(method = "getViewYRot", at = @At("RETURN"), cancellable = true)
	private void aurorite$freelookYaw(float partialTick, CallbackInfoReturnable<Float> callbackInfo) {
		FreelookModule freelook = ModuleManager.FREELOOK;

		if ((Object) this == Minecraft.getInstance().player && freelook.isLooking()) {
			callbackInfo.setReturnValue(callbackInfo.getReturnValue() + freelook.getYawOffset());
		}
	}

	@Inject(method = "getViewXRot", at = @At("RETURN"), cancellable = true)
	private void aurorite$freelookPitch(float partialTick, CallbackInfoReturnable<Float> callbackInfo) {
		FreelookModule freelook = ModuleManager.FREELOOK;

		if ((Object) this == Minecraft.getInstance().player && freelook.isLooking()) {
			float pitch = callbackInfo.getReturnValue() + freelook.getPitchOffset();
			callbackInfo.setReturnValue(Math.max(-90.0f, Math.min(90.0f, pitch)));
		}
	}
}
