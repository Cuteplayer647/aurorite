package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cute.aurorite.client.gui.ClickGuiScreen;
import com.cute.aurorite.client.module.ModuleManager;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;

/**
 * Opens and closes the ClickGUI with the binding configured in its settings.
 *
 * <p>This is the only place the hotkey is handled. {@code keyPress} runs for
 * both the press and the release of a key, so the action is checked explicitly:
 * letting the screen close on the release would undo the press that opened it.
 */
@Mixin(KeyboardHandler.class)
public class ClickGuiKeyMixin {

	@Inject(method = "keyPress", at = @At("HEAD"))
	private void aurorite$toggleClickGui(long window, int action, KeyEvent event, CallbackInfo callbackInfo) {
		if (action != InputConstants.PRESS) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();

		// While the interface is waiting for a new binding, keys belong to it.
		if (minecraft.gui.screen() instanceof ClickGuiScreen screen && screen.isCapturingKey()) {
			return;
		}

		if (event.key() != ModuleManager.CLICK_GUI.getHotkey()) {
			return;
		}

		if (minecraft.gui.screen() instanceof ClickGuiScreen) {
			minecraft.gui.setScreen(null);
			return;
		}

		// Do not steal the key while another screen (chat, inventory, ...) is open.
		if (minecraft.gui.screen() != null) {
			return;
		}

		ModuleManager.CLICK_GUI.setEnabled(true);
		minecraft.gui.setScreen(new ClickGuiScreen());
	}
}
