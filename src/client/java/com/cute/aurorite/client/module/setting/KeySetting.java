package com.cute.aurorite.client.module.setting;

import com.mojang.blaze3d.platform.InputConstants;

/**
 * A single keyboard binding, restricted to the 26 letters and the 10 digits.
 *
 * <p>The value is an {@code InputConstants} key code (an SDL scancode), which
 * is the same space {@code KeyEvent#key()} reports.
 */
public class KeySetting extends Setting {

	private int key;

	public KeySetting(String name, int defaultKey) {
		super(name);
		this.key = isAllowed(defaultKey) ? defaultKey : InputConstants.KEY_P;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		if (isAllowed(key)) {
			this.key = key;
		}
	}

	/** Only A-Z and 0-9 may be bound. */
	public static boolean isAllowed(int key) {
		return (key >= InputConstants.KEY_A && key <= InputConstants.KEY_Z)
				|| (key >= InputConstants.KEY_1 && key <= InputConstants.KEY_0);
	}

	/** The letter or digit shown in the interface. */
	public String getDisplayName() {
		if (key >= InputConstants.KEY_A && key <= InputConstants.KEY_Z) {
			return String.valueOf((char) ('A' + key - InputConstants.KEY_A));
		}

		if (key >= InputConstants.KEY_1 && key <= InputConstants.KEY_9) {
			return String.valueOf((char) ('1' + key - InputConstants.KEY_1));
		}

		if (key == InputConstants.KEY_0) {
			return "0";
		}

		return "?";
	}
}
