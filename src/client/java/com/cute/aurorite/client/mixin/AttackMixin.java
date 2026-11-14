package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cute.aurorite.client.module.ModuleManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/** Feeds the local player's attacks to the Combo module. */
@Mixin(MultiPlayerGameMode.class)
public class AttackMixin {

	@Inject(method = "attack", at = @At("HEAD"))
	private void aurorite$countCombo(Player player, Entity target, CallbackInfo callbackInfo) {
		if (player == Minecraft.getInstance().player) {
			ModuleManager.COMBO.onAttack(target.getId());
		}
	}
}
