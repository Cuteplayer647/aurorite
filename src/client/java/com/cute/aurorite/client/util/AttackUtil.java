package com.cute.aurorite.client.util;

import java.util.HashMap;
import java.util.Map;

import com.cute.aurorite.client.mixin.PlayerAttackInvoker;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * Decides whether a swing actually dealt damage.
 *
 * <p>The client predicts {@code Player.attack} locally, so a swing that the server
 * rejects still runs through all the damage maths. Counting those would make the
 * combo and the damage readout report hits that never happened, so the decision is
 * made with the same rules the server applies.
 */
public final class AttackUtil {

	/** Vanilla only lets a hit through this long after the previous one, in ticks. */
	private static final int HURT_COOLDOWN_TICKS = 10;

	/** Vanilla's own "still in hurt cooldown" threshold on the entity timer. */
	private static final int INVULNERABLE_THRESHOLD = 10;

	/** Last accepted hit per entity, so a rejected swing cannot refresh it. */
	private static final Map<Integer, Integer> LAST_HIT_TICK = new HashMap<>();

	private static boolean countedThisSwing;

	private AttackUtil() {
	}

	/** Called when a new swing starts, before its damage is applied. */
	public static void beginSwing() {
		countedThisSwing = false;
	}

	/** True for the first victim of the current swing, false for the rest of a sweep. */
	public static boolean isFirstVictimOfSwing() {
		if (countedThisSwing) {
			return false;
		}

		countedThisSwing = true;
		return true;
	}

	/**
	 * Whether the swing is aimed at something it could damage at all.
	 *
	 * <p>Used for the attack type readout, which is evaluated when the swing starts
	 * (the attack strength and crit state are still valid there). The combo and the
	 * damage readout use {@link #acceptHit} instead, because they must also reject
	 * the hurt cooldown.
	 */
	public static boolean isRealHit(Player player, Entity target) {
		if (player == null || !isDamagingTarget(target)) {
			return false;
		}

		if (((PlayerAttackInvoker) (Object) player).aurorite$cannotAttack(target)) {
			return false;
		}

		return player.isWithinEntityInteractionRange(target, 0.0);
	}

	/**
	 * Registers a hit and reports whether it can actually apply damage.
	 *
	 * @return false when the target is not damageable, is still in its hurt cooldown,
	 *         or the previous accepted hit on it was less than ten ticks ago
	 */
	public static boolean acceptHit(Player player, Entity target) {
		if (!isRealHit(player, target)) {
			return false;
		}

		// The entity timer works when the client knows it, the local map covers the
		// gap while the server's answer is still in flight.
		if (target.getInvulnerableTime() > INVULNERABLE_THRESHOLD) {
			return false;
		}

		int tick = player.tickCount;
		Integer last = LAST_HIT_TICK.get(target.getId());

		if (last != null && tick - last < HURT_COOLDOWN_TICKS) {
			return false;
		}

		if (LAST_HIT_TICK.size() > 256) {
			LAST_HIT_TICK.clear();
		}

		LAST_HIT_TICK.put(target.getId(), tick);

		return true;
	}

	/** Whether the target can take damage at all, ignoring the attacker's reach. */
	public static boolean isDamagingTarget(Entity target) {
		if (target == null || target.isRemoved()) {
			return false;
		}

		return target.isAlive() && !target.isSpectator() && !target.isInvulnerable();
	}
}
