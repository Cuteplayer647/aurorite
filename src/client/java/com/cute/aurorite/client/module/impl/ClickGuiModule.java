package com.cute.aurorite.client.module.impl;

import com.cute.aurorite.client.gui.ClickGuiScreen;
import com.cute.aurorite.client.module.Module;
import com.cute.aurorite.client.module.setting.KeySetting;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.Minecraft;

/** The ClickGUI itself: enabling the feature opens the interface. */
public class ClickGuiModule extends Module {

	private final KeySetting hotkey = add(new KeySetting("按键", InputConstants.KEY_P));

	public ClickGuiModule() {
		super("ClickGUI");
	}

	/** The key that opens and closes the interface. */
	public int getHotkey() {
		return hotkey.getKey();
	}

	public KeySetting getHotkeySetting() {
		return hotkey;
	}

	@Override
	public boolean isPersistent() {
		// The GUI being open is transient, it must not come back after a restart.
		return false;
	}

	@Override
	public void toggle() {
		Minecraft minecraft = Minecraft.getInstance();

		if (minecraft.gui.screen() instanceof ClickGuiScreen) {
			minecraft.gui.setScreen(null);
			return;
		}

		setEnabled(true);
		minecraft.gui.setScreen(new ClickGuiScreen());
	}
}
