package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cute.aurorite.client.gui.AuroraBackground;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;

/**
 * Puts the static aurora artwork behind every menu screen (world list,
 * multiplayer, settings, ...), replacing the rotating panorama and the dirt
 * background. In-game screens keep the vanilla background.
 *
 * <p>{@link ProgressScreen} is included even though a world is still loaded while
 * it is on screen, because that is the "saving world" screen and it should look
 * like the rest of the menus rather than falling back to the dirt background.
 */
@Mixin(Screen.class)
public class MenuBackgroundMixin {

	@Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
	private void aurorite$staticMenuBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick,
			CallbackInfo callbackInfo) {
		Minecraft minecraft = Minecraft.getInstance();
		Screen self = (Screen) (Object) this;

		if (minecraft.level != null && !(self instanceof ProgressScreen)) {
			return;
		}

		AuroraBackground.render(g);
		callbackInfo.cancel();
	}
}
