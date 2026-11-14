package com.cute.aurorite.client.module.impl;

import com.cute.aurorite.client.module.HudWidget;
import com.cute.aurorite.client.module.Module;
import com.cute.aurorite.client.module.setting.BooleanSetting;
import com.cute.aurorite.client.module.setting.NumberSetting;

import net.minecraft.client.gui.Font;

/**
 * Counts the hits landed on one entity in a row.
 *
 * <p>The counter goes up on every hit. It resets when the comboed entity hits
 * the player back, after five seconds without a hit, or when the player hits a
 * different entity — in which case counting starts over on the new target.
 */
public class ComboModule extends Module implements HudWidget {

	private static final long TIMEOUT_MS = 5000L;

	private final BooleanSetting dark = add(new BooleanSetting("深色模式", false));
	private final BooleanSetting rounded = add(new BooleanSetting("圆角", true));
	private final NumberSetting backgroundOpacity = add(new NumberSetting("背景不透明度", 70.0f, 0.0f, 100.0f));

	private volatile int combo;
	private volatile int targetId = -1;
	private volatile long lastHitAt;

	private int hudX = 8;
	private int hudY = 86;

	public ComboModule() {
		super("Combo");
	}

	/** Called when the local player lands a hit on an entity. */
	public void onAttack(int entityId) {
		long now = System.currentTimeMillis();

		if (entityId != targetId || isExpired(now)) {
			// A new target (or a stale combo) starts the count over at one.
			combo = 0;
		}

		combo++;
		targetId = entityId;
		lastHitAt = now;
	}

	/** Called when the local player is damaged; cause and direct ids come from the packet. */
	public void onDamagedBy(int causeId, int directId) {
		if (combo > 0 && (causeId == targetId || directId == targetId)) {
			reset();
		}
	}

	/** Drops the combo once it has been idle for five seconds. */
	public void tick() {
		if (combo > 0 && isExpired(System.currentTimeMillis())) {
			reset();
		}
	}

	public int getCombo() {
		return combo;
	}

	public String buildLabel() {
		return "Combo " + combo;
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

	private boolean isExpired(long now) {
		return now - lastHitAt > TIMEOUT_MS;
	}

	private void reset() {
		combo = 0;
		targetId = -1;
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
		return font.width(buildLabel()) + 12;
	}

	@Override
	public int measureHeight(Font font) {
		return 16;
	}

	@Override
	public void setPosition(int x, int y) {
		this.hudX = x;
		this.hudY = y;
	}
}
