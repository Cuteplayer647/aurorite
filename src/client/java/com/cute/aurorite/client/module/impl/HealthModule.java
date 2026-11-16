package com.cute.aurorite.client.module.impl;

import java.util.Locale;

import com.cute.aurorite.client.module.HudWidget;
import com.cute.aurorite.client.module.Module;
import com.cute.aurorite.client.module.setting.BooleanSetting;
import com.cute.aurorite.client.module.setting.ModeSetting;
import com.cute.aurorite.client.module.setting.NumberSetting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;

/** Shows the player's current health as "12.5 ❤" on the HUD. */
public class HealthModule extends Module implements HudWidget {

	private final ModeSetting mode = add(new ModeSetting("模式", 0, "old", "new"));
	private final BooleanSetting rounded = add(new BooleanSetting("圆角", true));
	private final NumberSetting backgroundOpacity = add(new NumberSetting("背景不透明度", 70.0f, 0.0f, 100.0f));

	private int hudX = 8;
	private int hudY = 134;

	public HealthModule() {
		super("血量显示");
	}

	public String buildLabel() {
		return String.format(Locale.ROOT, "%.1f \u2764", getHealth());
	}

	public float getHealth() {
		LocalPlayer player = Minecraft.getInstance().player;

		return player == null ? 0.0f : player.getHealth();
	}

	/** Whether the hearts should blink this frame, matching the vanilla rule. */
	public boolean isBlinking() {
		LocalPlayer player = Minecraft.getInstance().player;

		if (player == null) {
			return false;
		}

		float total = player.getHealth() + player.getAbsorptionAmount();

		return total <= BLINK_THRESHOLD && (player.tickCount / 3) % 2 == 0;
	}

	/** Green at 14 or more, yellow from 8 to 13.9, red below 8. */
	public int getHealthColour() {
		float health = getHealth();

		if (health >= 14.0f) {
			return 0xFF2FA84F;
		}

		if (health >= 8.0f) {
			return 0xFFD9A400;
		}

		return 0xFFE03131;
	}

	public boolean isRounded() {
		return rounded.getValue();
	}

	/** {@code new} draws the vanilla heart bar, {@code old} only the text card. */
	public boolean isNewMode() {
		return mode.is("new");
	}

	public int getBackgroundAlpha() {
		return Math.round(backgroundOpacity.getValue() * 255.0f / 100.0f);
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
		if (isNewMode()) {
			return Math.max(HEARTS_PER_ROW * 8, font.width(buildLabel())) + 12;
		}

		return font.width(buildLabel()) + 12;
	}

	@Override
	public int measureHeight(Font font) {
		return isNewMode() ? TEXT_HEIGHT + HEART_GAP + HEARTS_HEIGHT : 16;
	}

	/** Ten health hearts, plus one per two absorption points. */
	public int heartCount() {
		LocalPlayer player = Minecraft.getInstance().player;
		float absorption = player == null ? 0.0f : player.getAbsorptionAmount();

		return HEARTS_PER_ROW + (int) Math.ceil(absorption / 2.0);
	}

	/** How many extra hearts come from the absorption effect. */
	public int absorptionHeartCount() {
		LocalPlayer player = Minecraft.getInstance().player;
		float absorption = player == null ? 0.0f : player.getAbsorptionAmount();

		return (int) Math.ceil(absorption / 2.0);
	}

	public float getAbsorption() {
		LocalPlayer player = Minecraft.getInstance().player;

		return player == null ? 0.0f : player.getAbsorptionAmount();
	}

	/** The ten hearts of the vanilla bar, eight pixels apart. */
	public static final int HEARTS_PER_ROW = 10;
	public static final int HEARTS_HEIGHT = 9;
	public static final int TEXT_HEIGHT = 8;
	public static final int HEART_GAP = 4;

	/** Health at or below this many points makes the hearts blink, like vanilla. */
	public static final float BLINK_THRESHOLD = 4.0f;

	@Override
	public void setPosition(int x, int y) {
		this.hudX = x;
		this.hudY = y;
	}
}
