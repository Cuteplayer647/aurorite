package com.cute.aurorite.client.module.impl;

import com.cute.aurorite.client.mixin.PlayerAttackInvoker;
import com.cute.aurorite.client.module.Module;
import com.cute.aurorite.client.module.setting.BooleanSetting;
import com.cute.aurorite.client.util.ChatUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/** Chat reports for development: currently the type of attack that was performed. */
public class DebugModule extends Module {

	private final BooleanSetting attackType = add(new BooleanSetting("Attack Type", false));
	private final BooleanSetting attackDamage = add(new BooleanSetting("Attack Damage", false));
	private final BooleanSetting velocity = add(new BooleanSetting("Velocity", false));

	public DebugModule() {
		super("Debug");
	}

	public boolean isAttackTypeEnabled() {
		return isEnabled() && attackType.getValue();
	}

	public boolean isAttackDamageEnabled() {
		return isEnabled() && attackDamage.getValue();
	}

	public boolean isVelocityEnabled() {
		return isEnabled() && velocity.getValue();
	}

	/**
	 * Prints the knockback the local player received, split into the horizontal
	 * magnitude and the vertical component, to three decimals.
	 *
	 * @param movement the velocity the server applied, straight from the motion packet
	 */
	public void reportVelocity(Vec3 movement) {
		if (!isVelocityEnabled()) {
			return;
		}

		double horizontal = Math.sqrt(movement.x * movement.x + movement.z * movement.z);
		String horizontalText = String.format(java.util.Locale.ROOT, "%.3f", horizontal);
		String verticalText = String.format(java.util.Locale.ROOT, "%.3f", movement.y);

		MutableComponent body = Component.literal("[").withStyle(ChatFormatting.WHITE)
				.append(Component.literal("Debug").withStyle(ChatFormatting.WHITE))
				.append(Component.literal("] ").withStyle(ChatFormatting.WHITE))
				.append(Component.literal("Velocity: ").withStyle(ChatFormatting.GRAY))
				.append(Component.literal("Horizontal ").withStyle(ChatFormatting.GRAY))
				.append(Component.literal(horizontalText).withStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)))
				.append(Component.literal(", Vertical ").withStyle(ChatFormatting.GRAY))
				.append(Component.literal(verticalText).withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));

		ChatUtil.send(body);
	}

	/** Prints the damage an attack applied. */
	public void reportDamage(float amount) {
		if (!isAttackDamageEnabled()) {
			return;
		}

		String value = String.format(java.util.Locale.ROOT, "%.1f", amount);

		MutableComponent body = Component.literal("[").withStyle(ChatFormatting.WHITE)
				.append(Component.literal("Debug").withStyle(ChatFormatting.WHITE))
				.append(Component.literal("] ").withStyle(ChatFormatting.WHITE))
				.append(Component.literal("Attack Damage: ").withStyle(ChatFormatting.GRAY))
				.append(Component.literal(value).withStyle(Style.EMPTY.withColor(ChatFormatting.RED)));

		ChatUtil.send(body);
	}

	/** Prints the attack type to chat, using vanilla's own classification. */
	public void reportAttack(Player player, Entity target) {
		if (!isAttackTypeEnabled()) {
			return;
		}

		PlayerAttackInvoker invoker = (PlayerAttackInvoker) (Object) player;

		float charge = player.getAttackStrengthScale(0.5f);
		boolean fullCharge = charge > 0.9f;
		boolean knockback = player.isSprinting() && fullCharge;
		boolean critical = fullCharge && invoker.aurorite$canCriticalAttack(target);
		boolean sweep = invoker.aurorite$isSweepAttack(fullCharge, critical, knockback);

		String type;
		ChatFormatting colour;

		if (sweep) {
			type = "Sweep Attack";
			colour = ChatFormatting.AQUA;
		} else if (critical) {
			type = "Critical Attack";
			colour = ChatFormatting.GOLD;
		} else if (!fullCharge) {
			type = "Weak Attack";
			colour = ChatFormatting.GRAY;
		} else if (knockback) {
			type = "Knockback Attack";
			colour = ChatFormatting.LIGHT_PURPLE;
		} else {
			type = "Normal Attack";
			colour = ChatFormatting.WHITE;
		}

		MutableComponent body = Component.literal("[").withStyle(ChatFormatting.WHITE)
				.append(Component.literal("Debug").withStyle(ChatFormatting.WHITE))
				.append(Component.literal("] ").withStyle(ChatFormatting.WHITE))
				.append(Component.literal("Attack Type: ").withStyle(ChatFormatting.GRAY))
				.append(Component.literal(type).withStyle(Style.EMPTY.withColor(colour)));

		ChatUtil.send(body);
	}
}
