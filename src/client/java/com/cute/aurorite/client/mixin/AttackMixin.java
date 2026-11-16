package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cute.aurorite.client.module.ModuleManager;
import com.cute.aurorite.client.util.AttackUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * Reports the attack type of a swing.
 *
 * <p>This has to run before {@code Player.attack}, because the attack strength and
 * crit state are consumed inside it; evaluating them later always looks like an
 * uncharged swing. Combo counting and the damage readout live in
 * {@code DamageReportMixin} instead, where the applied damage is known.
 */
@Mixin(MultiPlayerGameMode.class)
public class AttackMixin {

	@Inject(method = "attack", at = @At("HEAD"))
	private void aurorite$onSwing(Player player, Entity target, CallbackInfo callbackInfo) {
		if (player != Minecraft.getInstance().player) {
			return;
		}

		AttackUtil.beginSwing();

		if (!AttackUtil.isRealHit(player, target)) {
			return;
		}

		ModuleManager.DEBUG.reportAttack(player, target);
	}
}
