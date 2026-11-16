package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cute.aurorite.client.module.ModuleManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;

/**
 * Plays the vanilla block animation without touching the player.
 *
 * <p>Two paths, both purely visual:
 * <ul>
 * <li>If the held item reports {@link ItemUseAnimation#BLOCK}, the render state is
 * flagged as "using item" so vanilla runs its own block branch — the exact pose the
 * game uses. Only the render state is changed, never the entity, so there is no
 * movement slowdown and nothing is sent to the server.</li>
 * <li>Otherwise the four transforms of that same block branch are applied directly,
 * copied from the vanilla renderer.</li>
 * </ul>
 */
@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class BlockAnimationMixin {

	/** Marks the current item as "using" so vanilla picks its block branch. */
	@Inject(method = "submitArmWithItem", at = @At("HEAD"))
	private void aurorite$flagBlocking(PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState handState,
			float swing, float equipProgress, InteractionHand hand, float partialTick, ItemStack stack,
			float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector collector, int light,
			CallbackInfo callbackInfo) {
		if (hand != InteractionHand.MAIN_HAND || !ModuleManager.BLOCK_ANIMATION.isBlocking()) {
			return;
		}

		if (!stack.getUseAnimation().equals(ItemUseAnimation.BLOCK)) {
			return;
		}

		AvatarRenderState avatar = playerState.avatarRenderState;

		if (avatar == null) {
			return;
		}

		avatar.isUsingItem = true;
		avatar.useItemHand = InteractionHand.MAIN_HAND;
		handState.useItemRemainingTicks = Math.max(handState.useItemRemainingTicks, 1);
	}

	/** Fallback: apply the vanilla block transforms to items without the block animation. */
	@Inject(method = "submitArmWithItem",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"))
	private void aurorite$blockPose(PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState handState,
			float swing, float equipProgress, InteractionHand hand, float partialTick, ItemStack stack,
			float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector collector, int light,
			CallbackInfo callbackInfo) {
		if (hand != InteractionHand.MAIN_HAND || !ModuleManager.BLOCK_ANIMATION.isBlocking()) {
			return;
		}

		if (stack.getUseAnimation().equals(ItemUseAnimation.BLOCK)) {
			return;
		}

		AvatarRenderState avatar = playerState.avatarRenderState;
		float side = avatar != null && avatar.mainArm == HumanoidArm.LEFT ? -1.0f : 1.0f;

		// Values taken from the vanilla block branch of this same method.
		poseStack.translate(-0.14142136f * side, 0.08f, 0.14142136f);
		poseStack.rotateDegrees(Axis.XP, -102.25f);
		poseStack.rotateDegrees(Axis.YP, 13.365f * side);
		poseStack.rotateDegrees(Axis.ZP, 78.05f * side);
	}
}
