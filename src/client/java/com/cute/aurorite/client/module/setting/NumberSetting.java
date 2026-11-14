package com.cute.aurorite.client.module.setting;

/** A bounded numeric option, rendered as a slider in the ClickGUI. */
public class NumberSetting extends Setting {

	private final float min;
	private final float max;

	private float value;

	public NumberSetting(String name, float defaultValue, float min, float max) {
		super(name);
		this.min = min;
		this.max = max;
		this.value = clamp(defaultValue);
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = clamp(value);
	}

	public float getMin() {
		return min;
	}

	public float getMax() {
		return max;
	}

	/** Position of the current value in the range, as 0..1. */
	public float getProgress() {
		return max - min <= 0.0f ? 0.0f : (value - min) / (max - min);
	}

	/** Applies a 0..1 position to the value. */
	public void setProgress(float progress) {
		setValue(min + (max - min) * Math.max(0.0f, Math.min(1.0f, progress)));
	}

	/** The value rounded for display. */
	public String getDisplayValue() {
		return Integer.toString(Math.round(value));
	}

	private float clamp(float raw) {
		return Math.max(min, Math.min(max, raw));
	}
}
