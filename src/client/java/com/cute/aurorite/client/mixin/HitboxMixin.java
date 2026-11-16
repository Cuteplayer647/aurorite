package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cute.aurorite.client.module.ModuleManager;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.extract.LevelExtractor;

/**
 * Emits the hitbox gizmos right after the vanilla debug renderers emit theirs.
 *
 * <p>That is the only point in the frame where the gizmo collector is guaranteed
 * to be open and its contents are drained immediately afterwards, which is why
 * the vanilla boxes show up. Emitting from the render thread did not render at
 * all, so the geometry goes through the same window the debug renderers use.
 */
@Mixin(LevelExtractor.class)
public class HitboxMixin {

	@Inject(method = "extract",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/debug/DebugRenderer;emitGizmos(Lnet/minecraft/client/renderer/culling/Frustum;DDDF)V",
					shift = At.Shift.AFTER))
	private void aurorite$renderHitboxes(DeltaTracker deltaTracker, Camera camera, float partialTick,
			CallbackInfo callbackInfo) {
		ModuleManager.HITBOX.render();
	}
}
