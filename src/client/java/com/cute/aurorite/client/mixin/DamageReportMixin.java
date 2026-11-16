package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.cute.aurorite.client.module.ModuleManager;
import com.cute.aurorite.client.util.AttackUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * The single place where a landed hit is counted.
 *
 * <p>{@code Player.attack} runs on the client as a prediction and hands the final
 * damage (after strength scaling, crit, sweep and enchantments) to
 * {@code hurtOrSimulate}, so this is where both the combo and the debug readout
 * get their data. Hits that the server would discard — dead, invulnerable or out
 * of reach targets, and swings landing inside the hurt cooldown — are rejected
 * first, so neither the combo nor the damage readout counts a swing that dealt
 * no damage.
 */
@Mixin(Entity.class)
public class DamageReportMixin {

	@Inject(method = "hurtOrSimulate", at = @At("HEAD"))
	private void aurorite$reportAttackDamage(DamageSource source, float amount,
			CallbackInfoReturnable<Boolean> callbackInfo) {
		Minecraft minecraft = Minecraft.getInstance();
		Entity self = (Entity) (Object) this;
		Player player = minecraft.player;

		if (player == null || self == player) {
			return;
		}

		if (source.getEntity() != player) {
			return;
		}

		if (!AttackUtil.acceptHit(player, self)) {
			return;
		}

		if (AttackUtil.isFirstVictimOfSwing()) {
			ModuleManager.COMBO.onAttack(self.getId());
		}

		ModuleManager.DEBUG.reportDamage(amount);
	}
}
