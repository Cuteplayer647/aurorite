package com.cute.aurorite.client.module.setting;

/** An on/off option, rendered as a switch in the ClickGUI. */
public class BooleanSetting extends Setting {

	private boolean value;

	public BooleanSetting(String name, boolean defaultValue) {
		super(name);
		this.value = defaultValue;
	}

	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public void toggle() {
		this.value = !this.value;
	}
}
