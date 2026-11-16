package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.cute.aurorite.client.module.ModuleManager;
import com.cute.aurorite.client.module.impl.NightVisionModule;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;

/**
 * Full bright without touching the brightness option.
 *
 * <p>The lightmap reads {@code minecraft.options.gamma().get()} while extracting
 * the render state. Replacing that single read keeps the stored option at the
 * player's own value, so nothing out of range is ever written to {@code options.txt}.
 *
 * <p>{@code ordinal = 1} is the gamma read in {@code extract}: the method reads
 * three options in order, and gamma is the second one.
 */
@Mixin(LightmapRenderStateExtractor.class)
public class NightVisionMixin {

	@Redirect(method = "extract", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;",
			ordinal = 1))
	private Object aurorite$brightness(OptionInstance<Double> option) {
		if (ModuleManager.NIGHT_VISION.isFullBright()) {
			return NightVisionModule.FULL_BRIGHT;
		}

		return option.get();
	}
}
