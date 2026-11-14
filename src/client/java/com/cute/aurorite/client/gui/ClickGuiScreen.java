package com.cute.aurorite.client.gui;

import java.util.List;

import com.cute.aurorite.client.config.AuroriteConfig;
import com.cute.aurorite.client.module.Module;
import com.cute.aurorite.client.module.ModuleManager;
import com.cute.aurorite.client.module.setting.BooleanSetting;
import com.cute.aurorite.client.module.setting.KeySetting;
import com.cute.aurorite.client.module.setting.NumberSetting;
import com.cute.aurorite.client.module.setting.Setting;
import com.cute.aurorite.client.util.RenderUtil;
import com.cute.aurorite.client.util.Viewport;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/**
 * The Aurorite ClickGUI.
 *
 * <p>A glass window split in two: a 70% white left column holding the logo and
 * the module list, and a 50% white right column holding the settings of
 * whichever module was right clicked. Both columns scroll on their own, by
 * dragging their scrollbar or with the mouse wheel.
 */
public class ClickGuiScreen extends Screen {

	private static final int CORNER_RADIUS = 12;
	private static final int ROW_HEIGHT = 24;
	private static final int ROW_GAP = 2;
	private static final int SETTING_ROW_HEIGHT = 24;
	private static final int SWITCH_WIDTH = 24;
	private static final int SWITCH_HEIGHT = 12;
	private static final int HUD_EDIT_WIDTH = 86;
	private static final int HUD_EDIT_HEIGHT = 22;
	private static final int SCROLLBAR_WIDTH = 3;
	private static final int WHEEL_STEP = 22;

	private int panelX;
	private int panelY;
	private int panelWidth;
	private int panelHeight;
	private int leftWidth;
	private int logoHeight;
	private int sliderWidth;
	private int areaWidth;
	private int areaHeight;

	private Module selected;
	private NumberSetting draggingSlider;
	private KeySetting capturingKey;

	private int moduleScroll;
	private int settingsScroll;
	private boolean draggingModuleBar;
	private boolean draggingSettingsBar;
	private int barDragStartY;
	private int barDragStartScroll;

	public ClickGuiScreen() {
		super(Component.literal("ClickGUI"));
	}

	@Override
	protected void init() {
		super.init();

		ModuleManager.CLICK_GUI.setEnabled(true);

		areaWidth = Viewport.width(this);
		areaHeight = Viewport.height(this);

		// Kept well inside the window: on scaled desktops the drawable area can be
		// noticeably smaller than the size the game reports.
		panelWidth = Math.max(200, Math.min(areaWidth - 20, Math.round(areaWidth * 0.52f)));
		panelHeight = Math.max(140, Math.min(areaHeight - 20, Math.round(areaHeight * 0.55f)));
		panelX = (areaWidth - panelWidth) / 2;
		panelY = (areaHeight - panelHeight) / 2;

		leftWidth = clampInt(Math.round(panelWidth * 0.30f), 70, Math.max(80, panelWidth - 110));
		logoHeight = clampInt(Math.round(panelHeight * 0.27f), 34, 92);
		sliderWidth = clampInt(Math.round(panelWidth * 0.20f), 50, 110);
	}

	@Override
	public void removed() {
		ModuleManager.CLICK_GUI.setEnabled(false);
		draggingSlider = null;
		capturingKey = null;
		draggingModuleBar = false;
		draggingSettingsBar = false;
		AuroriteConfig.save();
	}

	@Override
	public boolean isPauseScreen() {
		// The world keeps running behind the interface.
		return false;
	}

	/** True while the interface is waiting for a new key binding. */
	public boolean isCapturingKey() {
		return capturingKey != null;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		// Mirrors what the vanilla pause menu does: draw into the background
		// stratum, then ask for the blur of everything drawn before it. The blur
		// is only processed when this stratum already holds at least one draw, so
		// the first fill covers the screen at an invisible alpha.
		g.fill(0, 0, g.guiWidth(), g.guiHeight(), 0x01000000);

		if (Theme.BLUR) {
			extractBlurredBackground(g);
		}
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		int rightX = panelX + leftWidth;
		int rightWidth = panelWidth - leftWidth;

		drawShell(g);

		// The two glass bodies meet exactly on the seam; nothing is drawn between.
		drawGlassBody(g, panelX, panelY, leftWidth, panelHeight, CORNER_RADIUS, 0, 0, CORNER_RADIUS, Theme.PANEL_LEFT);
		drawGlassBody(g, rightX, panelY, rightWidth, panelHeight, 0, CORNER_RADIUS, CORNER_RADIUS, 0, Theme.PANEL_RIGHT);

		drawLogo(g);
		drawModuleList(g, mouseX, mouseY);
		drawDetails(g, mouseX, mouseY);
		drawHudEditButton(g, mouseX, mouseY);
	}

	/** The bright rim that wraps the whole window. No dark shadow is drawn. */
	private void drawShell(GuiGraphicsExtractor g) {
		RenderUtil.roundedRect(g, panelX - 1, panelY - 1, panelWidth + 2, panelHeight + 2,
				CORNER_RADIUS + 1, Theme.GLASS_RIM);
	}

	/** Translucent body plus the sheen that makes it read as glass. */
	private void drawGlassBody(GuiGraphicsExtractor g, int x, int y, int w, int h,
			float rTL, float rTR, float rBR, float rBL, int body) {
		RenderUtil.roundedRect(g, x, y, w, h, rTL, rTR, rBR, rBL, body);
		RenderUtil.roundedRectGradient(g, x, y, w, Math.round(h * 0.55f), rTL, rTR, 0, 0,
				Theme.GLASS_SHEEN_TOP, Theme.GLASS_SHEEN_BOTTOM);
	}

	// ------------------------------------------------------------------
	// Left column
	// ------------------------------------------------------------------

	private void drawLogo(GuiGraphicsExtractor g) {
		RenderUtil.roundedRect(g, panelX, panelY, leftWidth, logoHeight, CORNER_RADIUS, 0, 0, 0, Theme.LOGO_BLOCK);
		g.fill(panelX + 10, panelY + logoHeight, panelX + leftWidth - 10, panelY + logoHeight + 1, Theme.SEPARATOR);

		String title = "Aurorite";
		float titleScale = Math.min(1.9f, Math.max(0.9f, logoHeight / 44.0f));
		int titleWidth = Math.round(font.width(title) * titleScale);
		int titleX = panelX + (leftWidth - titleWidth) / 2;
		int titleY = panelY + Math.max(5, logoHeight / 2 - 13);

		g.pose().pushMatrix();
		g.pose().translate(titleX, titleY);
		g.pose().scale(titleScale, titleScale);
		g.text(font, title, 0, 0, Theme.TEXT_ON, false);
		g.pose().popMatrix();

		if (logoHeight >= 56) {
			String subtitle = "C L I E N T";
			g.text(font, subtitle, panelX + (leftWidth - font.width(subtitle)) / 2,
					panelY + logoHeight - 20, Theme.TEXT_MUTED, false);
		}
	}

	private void drawModuleList(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		List<Module> modules = ModuleManager.modules();
		int top = moduleListTop();
		int bottom = moduleListBottom();
		int viewport = bottom - top;
		int maxScroll = moduleMaxScroll();

		moduleScroll = clampInt(moduleScroll, 0, maxScroll);

		int rowX = panelX + 6;
		int rowWidth = leftWidth - 12;

		g.enableScissor(panelX, top, panelX + leftWidth - 4, bottom);

		for (int index = 0; index < modules.size(); index++) {
			Module module = modules.get(index);
			int rowY = top + index * (ROW_HEIGHT + ROW_GAP) - moduleScroll;

			if (rowY + ROW_HEIGHT <= top || rowY >= bottom) {
				continue;
			}

			boolean hovered = inside(mouseX, mouseY, rowX, rowY, rowWidth, ROW_HEIGHT)
					&& inside(mouseX, mouseY, panelX, top, leftWidth, viewport);

			if (module == selected) {
				RenderUtil.roundedRect(g, rowX, rowY, rowWidth, ROW_HEIGHT, 6.0f, Theme.SELECTED_BG);
				RenderUtil.roundedRect(g, rowX, rowY + 6, 2, ROW_HEIGHT - 12, 1.0f, Theme.ACCENT);
			} else if (hovered) {
				RenderUtil.roundedRect(g, rowX, rowY, rowWidth, ROW_HEIGHT, 6.0f, Theme.HOVER_BG);
			}

			int labelColour = module.isEnabled() ? Theme.TEXT_ON : Theme.TEXT_OFF;
			g.text(font, module.getName(), rowX + 9, rowY + (ROW_HEIGHT - 8) / 2, labelColour, false);

			int dotColour = module.isEnabled() ? Theme.DOT_ON : Theme.DOT_OFF;
			RenderUtil.circle(g, rowX + rowWidth - 8, rowY + ROW_HEIGHT / 2.0f, 1.8f, dotColour);
		}

		g.disableScissor();

		drawScrollbar(g, moduleBarX(), top, viewport, moduleContentHeight(), moduleScroll, maxScroll, draggingModuleBar);
	}

	// ------------------------------------------------------------------
	// Right column
	// ------------------------------------------------------------------

	private void drawDetails(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		int rightX = panelX + leftWidth;
		int rightWidth = panelWidth - leftWidth;

		if (selected == null) {
			g.text(font, "右键点击功能查看详细设置",
					rightX + (rightWidth - font.width("右键点击功能查看详细设置")) / 2,
					panelY + panelHeight / 2 - 4, Theme.TEXT_MUTED, false);
			return;
		}

		boolean arrowHovered = inside(mouseX, mouseY, rightX + 4, panelY + 4, 20, 20);
		RenderUtil.chevronLeft(g, rightX + 14, panelY + 14, arrowHovered ? Theme.ACCENT : Theme.TEXT_ON);

		g.text(font, selected.getName(), rightX + 28, panelY + 10, Theme.TEXT_ON, false);
		g.fill(rightX + 8, panelY + 26, rightX + rightWidth - 8, panelY + 27, Theme.SEPARATOR);

		List<Setting> settings = selected.getSettings();

		if (settings.isEmpty()) {
			g.text(font, "该功能没有可调整的设置", rightX + 10, settingsTop(), Theme.TEXT_MUTED, false);
			return;
		}

		int top = settingsTop();
		int bottom = settingsBottom();
		int viewport = bottom - top;
		int maxScroll = settingsMaxScroll();

		settingsScroll = clampInt(settingsScroll, 0, maxScroll);

		g.enableScissor(rightX, top, rightX + rightWidth - 4, bottom);

		for (int index = 0; index < settings.size(); index++) {
			int rowY = top + index * SETTING_ROW_HEIGHT - settingsScroll;

			if (rowY + SETTING_ROW_HEIGHT <= top || rowY >= bottom) {
				continue;
			}

			drawSettingRow(g, settings.get(index), rowY, mouseX, mouseY);
		}

		g.disableScissor();

		drawScrollbar(g, settingsBarX(), top, viewport, settingsContentHeight(), settingsScroll, maxScroll,
				draggingSettingsBar);
	}

	private void drawSettingRow(GuiGraphicsExtractor g, Setting setting, int rowY, int mouseX, int mouseY) {
		int rightX = panelX + leftWidth;
		int rightWidth = panelWidth - leftWidth;
		int labelY = rowY + (SETTING_ROW_HEIGHT - 8) / 2;
		boolean hovered = inside(mouseX, mouseY, rightX + 8, rowY, rightWidth - 16, SETTING_ROW_HEIGHT)
				&& mouseY >= settingsTop() && mouseY < settingsBottom();

		if (hovered) {
			RenderUtil.roundedRect(g, rightX + 6, rowY, rightWidth - 12, SETTING_ROW_HEIGHT, 5.0f, Theme.HOVER_BG);
		}

		g.text(font, setting.getName(), rightX + 10, labelY, Theme.TEXT_OFF, false);

		if (setting instanceof BooleanSetting booleanSetting) {
			int boxX = switchX();
			int boxY = rowY + (SETTING_ROW_HEIGHT - SWITCH_HEIGHT) / 2;
			int track = booleanSetting.getValue() ? Theme.TOGGLE_ON : Theme.TOGGLE_OFF;

			RenderUtil.roundedRect(g, boxX, boxY, SWITCH_WIDTH, SWITCH_HEIGHT, SWITCH_HEIGHT / 2.0f, track);

			float knobRadius = SWITCH_HEIGHT / 2.0f - 1.5f;
			float knobCentreX = booleanSetting.getValue()
					? boxX + SWITCH_WIDTH - SWITCH_HEIGHT / 2.0f
					: boxX + SWITCH_HEIGHT / 2.0f;

			RenderUtil.circle(g, knobCentreX, boxY + SWITCH_HEIGHT / 2.0f, knobRadius, Theme.TOGGLE_KNOB);
		} else if (setting instanceof NumberSetting numberSetting) {
			int sliderX = sliderX();
			int sliderY = rowY + (SETTING_ROW_HEIGHT - 3) / 2;
			String value = numberSetting.getDisplayValue();

			g.text(font, value, sliderX - 5 - font.width(value), labelY, Theme.TEXT_MUTED, false);

			RenderUtil.roundedRect(g, sliderX, sliderY, sliderWidth, 3, 1.5f, Theme.SLIDER_TRACK);
			RenderUtil.roundedRect(g, sliderX, sliderY, Math.max(3.0f, sliderWidth * numberSetting.getProgress()), 3,
					1.5f, Theme.SLIDER_FILL);
			RenderUtil.circle(g, sliderX + sliderWidth * numberSetting.getProgress(), sliderY + 1.5f, 4.0f,
					Theme.TOGGLE_KNOB);
		} else if (setting instanceof KeySetting keySetting) {
			boolean capturing = keySetting == capturingKey;
			int boxW = 24;
			int boxH = 14;
			int boxX = panelX + panelWidth - 12 - boxW;
			int boxY = rowY + (SETTING_ROW_HEIGHT - boxH) / 2;
			String label = capturing ? "…" : keySetting.getDisplayName();

			RenderUtil.roundedRect(g, boxX, boxY, boxW, boxH, 4.0f,
					capturing ? 0x333D7BFF : 0x1F000000);
			g.text(font, label, boxX + (boxW - font.width(label)) / 2,
					boxY + (boxH - 8) / 2, capturing ? Theme.ACCENT : Theme.TEXT_ON, false);
		}
	}

	private void drawHudEditButton(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		int boxX = hudEditX();
		int boxY = hudEditY();
		boolean hovered = inside(mouseX, mouseY, boxX, boxY, HUD_EDIT_WIDTH, HUD_EDIT_HEIGHT);
		int background = hovered ? Theme.BUTTON_HOVER : Theme.BUTTON_BG;
		String label = "hud edit";

		RenderUtil.roundedRect(g, boxX, boxY, HUD_EDIT_WIDTH, HUD_EDIT_HEIGHT, 7.0f, background);
		g.text(font, label, boxX + (HUD_EDIT_WIDTH - font.width(label)) / 2,
				boxY + (HUD_EDIT_HEIGHT - 8) / 2, Theme.TEXT_ON, false);
	}

	private void drawScrollbar(GuiGraphicsExtractor g, int barX, int trackY, int trackHeight,
			int contentHeight, int scroll, int maxScroll, boolean active) {
		if (maxScroll <= 0) {
			return;
		}

		RenderUtil.roundedRect(g, barX, trackY, SCROLLBAR_WIDTH, trackHeight, SCROLLBAR_WIDTH / 2.0f, 0x1A000000);

		int thumbHeight = barThumbHeight(trackHeight, contentHeight, trackHeight);
		int thumbY = barThumbY(trackY, trackHeight, thumbHeight, scroll, maxScroll);
		int colour = active ? 0xE63D7BFF : 0x993D7BFF;

		RenderUtil.roundedRect(g, barX, thumbY, SCROLLBAR_WIDTH, thumbHeight, SCROLLBAR_WIDTH / 2.0f, colour);
	}

	// ------------------------------------------------------------------
	// Input
	// ------------------------------------------------------------------

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double mouseX = event.x();
		double mouseY = event.y();
		int button = event.button();

		// 26.3 reports SDL button numbers: left = 1, middle = 2, right = 3.
		boolean leftClick = button == InputConstants.MOUSE_BUTTON_LEFT;
		boolean rightClick = button == InputConstants.MOUSE_BUTTON_RIGHT;

		if (leftClick && (startBarDrag(mouseX, mouseY, true) || startBarDrag(mouseX, mouseY, false))) {
			return true;
		}

		if (inside(mouseX, mouseY, hudEditX(), hudEditY(), HUD_EDIT_WIDTH, HUD_EDIT_HEIGHT)) {
			// Leave the GUI and hand the player the HUD so it can be placed.
			minecraft.gui.setScreen(new HudEditScreen());
			return true;
		}

		List<Module> modules = ModuleManager.modules();
		int rowX = panelX + 6;
		int rowWidth = leftWidth - 12;
		int top = moduleListTop();

		if (inside(mouseX, mouseY, panelX, top, leftWidth, moduleListBottom() - top)) {
			for (int index = 0; index < modules.size(); index++) {
				int rowY = top + index * (ROW_HEIGHT + ROW_GAP) - moduleScroll;

				if (!inside(mouseX, mouseY, rowX, rowY, rowWidth, ROW_HEIGHT)) {
					continue;
				}

				Module module = modules.get(index);

				if (leftClick) {
					// Left click only switches the feature on or off.
					module.toggle();
				} else if (rightClick) {
					// Right click shows the details, replacing whatever was open.
					select(selected == module ? null : module);
				}

				return true;
			}
		}

		if (selected != null) {
			int rightX = panelX + leftWidth;

			if (inside(mouseX, mouseY, rightX + 4, panelY + 4, 20, 20)) {
				select(null);
				return true;
			}

			if (leftClick && clickSetting(mouseX, mouseY)) {
				return true;
			}
		}

		return super.mouseClicked(event, doubleClick);
	}

	private void select(Module module) {
		if (selected != module) {
			settingsScroll = 0;
		}

		selected = module;
		capturingKey = null;
	}

	private boolean clickSetting(double mouseX, double mouseY) {
		List<Setting> settings = selected.getSettings();

		for (int index = 0; index < settings.size(); index++) {
			int rowY = settingsTop() + index * SETTING_ROW_HEIGHT - settingsScroll;

			if (mouseY < rowY || mouseY >= rowY + SETTING_ROW_HEIGHT) {
				continue;
			}

			if (mouseY < settingsTop() || mouseY >= settingsBottom()) {
				continue;
			}

			Setting setting = settings.get(index);

			if (setting instanceof BooleanSetting booleanSetting) {
				booleanSetting.toggle();
				AuroriteConfig.save();
				return true;
			}

			if (setting instanceof NumberSetting numberSetting) {
				int sliderX = sliderX();

				if (mouseX >= sliderX - 8 && mouseX <= sliderX + sliderWidth + 8) {
					numberSetting.setProgress((float) (mouseX - sliderX) / sliderWidth);
					draggingSlider = numberSetting;
					AuroriteConfig.save();
					return true;
				}
			}

			if (setting instanceof KeySetting keySetting) {
				capturingKey = keySetting;
				return true;
			}
		}

		return false;
	}

	private boolean startBarDrag(double mouseX, double mouseY, boolean leftBar) {
		int top = leftBar ? moduleListTop() : settingsTop();
		int viewport = leftBar ? moduleListBottom() - moduleListTop() : settingsBottom() - settingsTop();
		int maxScroll = leftBar ? moduleMaxScroll() : settingsMaxScroll();

		if (maxScroll <= 0) {
			return false;
		}

		int barX = leftBar ? moduleBarX() : settingsBarX();
		int thumbHeight = barThumbHeight(viewport, viewport + maxScroll, viewport);
		int thumbY = barThumbY(top, viewport, thumbHeight, leftBar ? moduleScroll : settingsScroll, maxScroll);

		if (!inside(mouseX, mouseY, barX - 2, thumbY - 2, SCROLLBAR_WIDTH + 4, thumbHeight + 4)) {
			return false;
		}

		if (leftBar) {
			draggingModuleBar = true;
		} else {
			draggingSettingsBar = true;
		}

		barDragStartY = (int) mouseY;
		barDragStartScroll = leftBar ? moduleScroll : settingsScroll;

		return true;
	}

	private void updateBarDrag(double mouseY, boolean leftBar) {
		int viewport = leftBar ? moduleListBottom() - moduleListTop() : settingsBottom() - settingsTop();
		int maxScroll = leftBar ? moduleMaxScroll() : settingsMaxScroll();
		int thumbHeight = barThumbHeight(viewport, viewport + maxScroll, viewport);
		int travel = viewport - thumbHeight;

		if (travel <= 0) {
			return;
		}

		int delta = (int) Math.round((mouseY - barDragStartY) / (double) travel * maxScroll);
		int value = clampInt(barDragStartScroll + delta, 0, maxScroll);

		if (leftBar) {
			moduleScroll = value;
		} else {
			settingsScroll = value;
		}
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
		if (draggingModuleBar || draggingSettingsBar) {
			updateBarDrag(event.y(), draggingModuleBar);
			return true;
		}

		if (draggingSlider != null) {
			draggingSlider.setProgress((float) (event.x() - sliderX()) / sliderWidth);
			return true;
		}

		return super.mouseDragged(event, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		boolean wasBusy = draggingSlider != null || draggingModuleBar || draggingSettingsBar;

		draggingSlider = null;
		draggingModuleBar = false;
		draggingSettingsBar = false;
		AuroriteConfig.save();

		return wasBusy || super.mouseReleased(event);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		int moduleTop = moduleListTop();
		int moduleHeight = moduleListBottom() - moduleTop;

		if (moduleMaxScroll() > 0 && inside(mouseX, mouseY, panelX, moduleTop, leftWidth, moduleHeight)) {
			moduleScroll = clampInt(moduleScroll - (int) Math.round(scrollY * WHEEL_STEP), 0, moduleMaxScroll());
			return true;
		}

		if (selected != null && settingsMaxScroll() > 0
				&& inside(mouseX, mouseY, panelX + leftWidth, settingsTop(), panelWidth - leftWidth,
						settingsBottom() - settingsTop())) {
			settingsScroll = clampInt(settingsScroll - (int) Math.round(scrollY * WHEEL_STEP), 0,
					settingsMaxScroll());
			return true;
		}

		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (capturingKey != null) {
			if (event.isEscape()) {
				capturingKey = null;
				return true;
			}

			if (KeySetting.isAllowed(event.key())) {
				capturingKey.setKey(event.key());
				capturingKey = null;
				AuroriteConfig.save();
			}

			return true;
		}

		return super.keyPressed(event);
	}

	// ------------------------------------------------------------------
	// Geometry helpers
	// ------------------------------------------------------------------

	private int moduleListTop() {
		return panelY + logoHeight + 8;
	}

	private int moduleListBottom() {
		return panelY + panelHeight - 10;
	}

	private int moduleContentHeight() {
		return ModuleManager.modules().size() * (ROW_HEIGHT + ROW_GAP);
	}

	private int moduleMaxScroll() {
		return Math.max(0, moduleContentHeight() - (moduleListBottom() - moduleListTop()));
	}

	private int settingsTop() {
		return panelY + 36;
	}

	private int settingsBottom() {
		return hudEditY() - 6;
	}

	private int settingsContentHeight() {
		return selected == null ? 0 : selected.getSettings().size() * SETTING_ROW_HEIGHT;
	}

	private int settingsMaxScroll() {
		return Math.max(0, settingsContentHeight() - (settingsBottom() - settingsTop()));
	}

	private int moduleBarX() {
		return panelX + leftWidth - 5;
	}

	private int settingsBarX() {
		return panelX + panelWidth - 5;
	}

	private int barThumbHeight(int trackHeight, int contentHeight, int viewport) {
		if (contentHeight <= viewport) {
			return trackHeight;
		}

		return Math.max(12, Math.round(trackHeight * (float) viewport / contentHeight));
	}

	private int barThumbY(int trackY, int trackHeight, int thumbHeight, int scroll, int maxScroll) {
		if (maxScroll <= 0) {
			return trackY;
		}

		return trackY + Math.round((trackHeight - thumbHeight) * (scroll / (float) maxScroll));
	}

	private int sliderX() {
		return panelX + panelWidth - 12 - sliderWidth;
	}

	private int switchX() {
		return panelX + panelWidth - 12 - SWITCH_WIDTH;
	}

	private int hudEditX() {
		return panelX + panelWidth - HUD_EDIT_WIDTH - 10;
	}

	private int hudEditY() {
		return panelY + panelHeight - HUD_EDIT_HEIGHT - 10;
	}

	private static int clampInt(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	private static boolean inside(double pointX, double pointY, int x, int y, int width, int height) {
		return pointX >= x && pointX < x + width && pointY >= y && pointY < y + height;
	}
}
