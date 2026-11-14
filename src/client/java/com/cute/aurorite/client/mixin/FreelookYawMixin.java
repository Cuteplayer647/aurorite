package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.cute.aurorite.client.module.ModuleManager;
import com.cute.aurorite.client.module.impl.FreelookModule;

import net.minecraft.client.player.LocalPlayer;

/**
 * Freelook yaw.
 *
 * <p>{@code LocalPlayer} overrides {@code getViewYRot} and returns its body yaw
 * directly, so the offset has to be added here rather than in {@code Entity}.
 * The pitch is not overridden, which is why only the vertical axis used to work.
 */
@Mixin(LocalPlayer.class)
public class FreelookYawMixin {

	@Inject(method = "getViewYRot", at = @At("RETURN"), cancellable = true)
	private void aurorite$freelookYaw(float partialTick, CallbackInfoReturnable<Float> callbackInfo) {
		FreelookModule freelook = ModuleManager.FREELOOK;

		if (freelook.isLooking()) {
			callbackInfo.setReturnValue(callbackInfo.getReturnValue() + freelook.getYawOffset());
		}
	}
}
