package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.cute.aurorite.client.gui.AuroraBackground;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LoadingOverlay;

/**
 * The overlay shown while a world's resources are prepared paints a solid colour
 * over the whole screen, hiding the artwork of the loading screen underneath.
 *
 * <p>Any full-screen fill is replaced with the static background, so the artwork
 * carries through from the loading screen into the world.
 */
@Mixin(LoadingOverlay.class)
public class LoadingOverlayMixin {

	@Redirect(method = "extractRenderState",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"))
	private void aurorite$staticBackground(GuiGraphicsExtractor g, int x1, int y1, int x2, int y2, int colour) {
		if (x1 == 0 && y1 == 0 && x2 == g.guiWidth() && y2 == g.guiHeight()) {
			AuroraBackground.render(g);
			return;
		}

		g.fill(x1, y1, x2, y2, colour);
	}
}
