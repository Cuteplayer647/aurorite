package com.cute.aurorite.client.gui;

import com.cute.aurorite.client.config.AuroriteConfig;
import com.cute.aurorite.client.hud.HudRenderer;
import com.cute.aurorite.client.module.HudWidget;
import com.cute.aurorite.client.module.Module;
import com.cute.aurorite.client.module.ModuleManager;
import com.cute.aurorite.client.util.Viewport;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/**
 * The standalone HUD editor.
 *
 * <p>Choosing {@code hud edit} in the ClickGUI closes it and opens this screen,
 * which shows nothing but the HUD elements so they can be dragged freely. Esc
 * saves the positions and takes the player back to the ClickGUI.
 */
public class HudEditScreen extends Screen {

	private static final String HINT = "拖动 HUD 元素调整位置 · 按 Esc 保存并返回";

	private int areaWidth;
	private int areaHeight;

	private HudWidget dragging;
	private int grabOffsetX;
	private int grabOffsetY;

	public HudEditScreen() {
		super(Component.literal("HUD Edit"));
	}

	@Override
	protected void init() {
		super.init();

		areaWidth = Viewport.width(this);
		areaHeight = Viewport.height(this);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		// Same pattern as the vanilla pause menu: a draw first, then the blur marker.
		g.fill(0, 0, g.guiWidth(), g.guiHeight(), 0x01000000);

		if (Theme.BLUR) {
			extractBlurredBackground(g);
		}
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		HudRenderer.renderAll(g, true);

		for (Module module : ModuleManager.modules()) {
			if (module instanceof HudWidget widget && module.isEnabled()) {
				boolean hovered = dragging == widget || inside(mouseX, mouseY, widget, 0);

				g.outline(widget.getX() - 2, widget.getY() - 2,
						widget.getX() + widget.measureWidth(font) + 2,
						widget.getY() + widget.measureHeight(font) + 2,
						hovered ? Theme.ACCENT : 0xCC3D7BFF);
			}
		}

		g.text(font, HINT, (areaWidth - font.width(HINT)) / 2, 10, 0xFFFFFFFF, true);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double mouseX = event.x();
		double mouseY = event.y();

		for (Module module : ModuleManager.modules()) {
			if (!(module instanceof HudWidget widget) || !module.isEnabled()) {
				continue;
			}

			if (inside(mouseX, mouseY, widget, 4)) {
				dragging = widget;
				grabOffsetX = (int) mouseX - widget.getX();
				grabOffsetY = (int) mouseY - widget.getY();
				return true;
			}
		}

		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
		if (dragging != null) {
			int widgetWidth = dragging.measureWidth(font);
			int widgetHeight = dragging.measureHeight(font);
			int targetX = (int) event.x() - grabOffsetX;
			int targetY = (int) event.y() - grabOffsetY;

			dragging.setPosition(
					Math.max(0, Math.min(areaWidth - widgetWidth, targetX)),
					Math.max(0, Math.min(areaHeight - widgetHeight, targetY)));
			return true;
		}

		return super.mouseDragged(event, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		boolean wasDragging = dragging != null;
		dragging = null;
		AuroriteConfig.save();

		return wasDragging || super.mouseReleased(event);
	}

	@Override
	public void onClose() {
		// Esc saves the positions (they live on the modules) and reopens the GUI.
		AuroriteConfig.save();
		minecraft.gui.setScreen(new ClickGuiScreen());
	}

	private boolean inside(double pointX, double pointY, HudWidget widget, int padding) {
		int width = widget.measureWidth(font);
		int height = widget.measureHeight(font);

		return pointX >= widget.getX() - padding && pointX < widget.getX() + width + padding
				&& pointY >= widget.getY() - padding && pointY < widget.getY() + height + padding;
	}
}
