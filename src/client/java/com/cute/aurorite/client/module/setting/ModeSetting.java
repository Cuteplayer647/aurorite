package com.cute.aurorite.client.module.setting;

/** A setting that cycles through a fixed list of named modes. */
public class ModeSetting extends Setting {

	private final String[] modes;

	private int index;

	public ModeSetting(String name, int index, String... modes) {
		super(name);

		if (modes.length == 0) {
			throw new IllegalArgumentException("a mode setting needs at least one mode");
		}

		this.modes = modes.clone();
		this.index = Math.floorMod(index, this.modes.length);
	}

	/** The mode that is currently selected. */
	public String getValue() {
		return modes[index];
	}

	public boolean is(String mode) {
		return modes[index].equalsIgnoreCase(mode);
	}

	/** Moves to the next mode, wrapping around. */
	public void cycle() {
		index = (index + 1) % modes.length;
	}

	/** Selects a mode by name; unknown names are ignored. */
	public void setValue(String mode) {
		for (int i = 0; i < modes.length; i++) {
			if (modes[i].equalsIgnoreCase(mode)) {
				index = i;
				return;
			}
		}
	}
}
