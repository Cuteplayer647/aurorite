package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cute.aurorite.client.module.ModuleManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;

/** Resets the combo when the comboed entity damages the local player. */
@Mixin(ClientPacketListener.class)
public class DamageMixin {

	@Inject(method = "handleDamageEvent", at = @At("HEAD"))
	private void aurorite$resetCombo(ClientboundDamageEventPacket packet, CallbackInfo callbackInfo) {
		LocalPlayer player = Minecraft.getInstance().player;

		if (player == null || packet.entityId() != player.getId()) {
			return;
		}

		ModuleManager.COMBO.onDamagedBy(packet.sourceCauseId(), packet.sourceDirectId());
	}
}
