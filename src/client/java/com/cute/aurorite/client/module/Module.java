package com.cute.aurorite.client.module;

import java.util.ArrayList;
import java.util.List;

import com.cute.aurorite.client.module.setting.Setting;

/**
 * A toggleable client feature. Left clicking the feature in the ClickGUI
 * enables it, clicking again disables it.
 */
public abstract class Module {

	private final String name;
	private final List<Setting> settings = new ArrayList<>();

	private boolean enabled;

	protected Module(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		if (this.enabled == enabled) {
			return;
		}

		this.enabled = enabled;
		onToggle(enabled);
	}

	public void toggle() {
		setEnabled(!enabled);
	}

	/** Called whenever the enabled state actually changes. */
	protected void onToggle(boolean enabled) {
	}

	public List<Setting> getSettings() {
		return settings;
	}

	/** Whether this module's state is written to the config file. */
	public boolean isPersistent() {
		return true;
	}

	protected <T extends Setting> T add(T setting) {
		settings.add(setting);
		return setting;
	}
}
