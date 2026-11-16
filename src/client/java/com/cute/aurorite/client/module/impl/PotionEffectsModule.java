package com.cute.aurorite.client.module.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.cute.aurorite.client.module.HudWidget;
import com.cute.aurorite.client.module.Module;
import com.cute.aurorite.client.module.setting.BooleanSetting;
import com.cute.aurorite.client.module.setting.NumberSetting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * Status effect list: one row per active effect with its icon, name, level and
 * remaining time.
 *
 * <p>The vanilla top-right effect display is hidden while this is on, so the two
 * never show the same information twice.
 */
public class PotionEffectsModule extends Module implements HudWidget {

	private final BooleanSetting dark = add(new BooleanSetting("深色模式", true));
	private final BooleanSetting rounded = add(new BooleanSetting("圆角", true));
	private final NumberSetting backgroundOpacity = add(new NumberSetting("背景不透明度", 70.0f, 0.0f, 100.0f));
	private final BooleanSetting hideVanilla = add(new BooleanSetting("隐藏原版显示", true));

	/** Row height, and the size of the effect icon inside it. */
	public static final int ROW_HEIGHT = 20;
	public static final int ICON_SIZE = 16;

	private int hudX = 8;
	private int hudY = 196;

	public PotionEffectsModule() {
		super("药水效果");
	}

	/** Active effects, longest lasting first. */
	public List<MobEffectInstance> effects() {
		LocalPlayer player = Minecraft.getInstance().player;
		List<MobEffectInstance> list = new ArrayList<>();

		if (player == null) {
			return list;
		}

		list.addAll(player.getActiveEffects());
		// Infinite effects are shown last, everything else sorted by time left.
		list.sort(Comparator.comparingInt((MobEffectInstance instance) -> instance.isInfiniteDuration() ? 0 : 1)
				.thenComparingInt(MobEffectInstance::getDuration).reversed());

		return list;
	}

	public boolean isVanillaHidden() {
		return isEnabled() && hideVanilla.getValue();
	}

	public boolean isDark() {
		return dark.getValue();
	}

	public boolean isRounded() {
		return rounded.getValue();
	}

	public int getBackgroundAlpha() {
		return Math.round(backgroundOpacity.getValue() * 255.0f / 100.0f);
	}

	/** The label of one row: effect name, level and time left. */
	public static String describe(MobEffectInstance instance) {
		MobEffect effect = instance.getEffect().value();
		String name = effect.getDisplayName().getString();
		int amplifier = instance.getAmplifier();

		String level = amplifier <= 0 ? "" : " " + roman(amplifier + 1);

		return name + level + "  " + time(instance);
	}

	private static String roman(int value) {
		return switch (value) {
			case 2 -> "II";
			case 3 -> "III";
			case 4 -> "IV";
			case 5 -> "V";
			default -> String.valueOf(value);
		};
	}

	private static String time(MobEffectInstance instance) {
		if (instance.isInfiniteDuration()) {
			return "∞";
		}

		int seconds = instance.getDuration() / 20;
		int minutes = seconds / 60;

		return String.format(Locale.ROOT, "%d:%02d", minutes, seconds % 60);
	}

	public Holder<MobEffect> effectHolder(MobEffectInstance instance) {
		return instance.getEffect();
	}

	@Override
	public int getX() {
		return hudX;
	}

	@Override
	public int getY() {
		return hudY;
	}

	@Override
	public int measureWidth(Font font) {
		int widest = 0;

		for (MobEffectInstance instance : effects()) {
			widest = Math.max(widest, font.width(describe(instance)));
		}

		return ICON_SIZE + 6 + Math.max(widest, 40) + 12;
	}

	@Override
	public int measureHeight(Font font) {
		return Math.max(1, effects().size()) * ROW_HEIGHT;
	}

	@Override
	public void setPosition(int x, int y) {
		this.hudX = x;
		this.hudY = y;
	}
}
