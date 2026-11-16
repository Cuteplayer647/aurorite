package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cute.aurorite.client.module.ModuleManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.phys.Vec3;

/**
 * Reports the knockback the local player receives.
 *
 * <p>Damage and knockback are resolved by the server, which then tells the client
 * the resulting velocity through this packet, so its value is the exact knockback
 * the player was given — including knockback resistance and the on-ground vertical
 * cap — without recomputing any of it here.
 *
 * <p>The handler runs on the network thread until it hops to the main thread, and
 * touching the chat from there disconnects the player, so the report is handed to
 * the client executor instead of being sent inline.
 */
@Mixin(ClientPacketListener.class)
public class VelocityMixin {

	@Inject(method = "handleSetEntityMotion", at = @At("HEAD"))
	private void aurorite$reportKnockback(ClientboundSetEntityMotionPacket packet, CallbackInfo callbackInfo) {
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;

		if (player == null || packet.id() != player.getId()) {
			return;
		}

		Vec3 movement = packet.movement();

		minecraft.execute(() -> ModuleManager.DEBUG.reportVelocity(movement));
	}
}
