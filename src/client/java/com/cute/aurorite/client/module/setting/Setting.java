package com.cute.aurorite.client.module.setting;

/** A single configurable option of a {@link com.cute.aurorite.client.module.Module}. */
public abstract class Setting {

	private final String name;

	protected Setting(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
