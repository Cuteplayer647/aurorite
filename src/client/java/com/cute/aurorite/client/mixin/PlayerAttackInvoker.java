package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * Reads the vanilla attack classification so the debug readout reports the same
 * type the game actually applied, instead of duplicating its rules.
 */
@Mixin(Player.class)
public interface PlayerAttackInvoker {

	/** Vanilla's critical check: falling, not on the ground, not sprinting, ... */
	@Invoker("canCriticalAttack")
	boolean aurorite$canCriticalAttack(Entity target);

	/** Vanilla's sweep check; the flags are fullCharge, critical and knockback. */
	@Invoker("isSweepAttack")
	boolean aurorite$isSweepAttack(boolean fullCharge, boolean critical, boolean knockback);

	/** Vanilla's own "may this be attacked" guard, used at the top of {@code attack}. */
	@Invoker("cannotAttack")
	boolean aurorite$cannotAttack(Entity target);
}
