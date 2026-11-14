package com.cute.aurorite.client.gui;

/** Colours and look-up constants for the Aurorite interface. */
public final class Theme {

	private Theme() {
	}

	/** Whether the background behind the interface is blurred. */
	public static final boolean BLUR = true;

	/** Left column, 70% opaque white. */
	public static final int PANEL_LEFT = 0xB3FFFFFF;

	/** Right column, 50% opaque white. */
	public static final int PANEL_RIGHT = 0x80FFFFFF;

	/** Soft highlight reserved for the client logo. */
	public static final int LOGO_BLOCK = 0x38FFFFFF;

	/** Liquid glass: bright rim, top sheen and a shaded bottom edge. */
	public static final int GLASS_RIM = 0x59FFFFFF;
	public static final int GLASS_SHEEN_TOP = 0x54FFFFFF;
	public static final int GLASS_SHEEN_BOTTOM = 0x00FFFFFF;
	public static final int GLASS_SHADE_TOP = 0x00FFFFFF;
	public static final int GLASS_SHADE_BOTTOM = 0x1A000000;

	public static final int SEPARATOR = 0x1F000000;
	public static final int SHADOW = 0x24000000;

	/** Enabled module label, black. */
	public static final int TEXT_ON = 0xFF000000;

	/** Disabled module label, dark grey. */
	public static final int TEXT_OFF = 0xFF5A5A5A;

	public static final int TEXT_MUTED = 0xFF7B7B7B;

	public static final int HOVER_BG = 0x2BFFFFFF;
	public static final int SELECTED_BG = 0x45FFFFFF;

	public static final int ACCENT = 0xFF3D7BFF;
	public static final int DOT_ON = 0xFF3D7BFF;
	public static final int DOT_OFF = 0x33000000;

	public static final int BUTTON_BG = 0xB3FFFFFF;
	public static final int BUTTON_HOVER = 0xD9FFFFFF;

	public static final int TOGGLE_OFF = 0x33000000;
	public static final int TOGGLE_ON = 0xFF3D7BFF;
	public static final int TOGGLE_KNOB = 0xFFFFFFFF;

	public static final int SLIDER_TRACK = 0x33000000;
	public static final int SLIDER_FILL = 0xFF3D7BFF;

	/** In-game HUD cards. */
	public static final int HUD_CARD = 0xCCFFFFFF;
	public static final int HUD_TEXT = 0xFF1F1F1F;
	public static final int KEY_IDLE = 0x99FFFFFF;
	public static final int KEY_TEXT = 0xFF1F1F1F;

	/** A held key in light mode: black, drawn with the configured opacity. */
	public static final int KEY_DOWN = 0xFF000000;
	public static final int KEY_TEXT_DOWN = 0xFFFFFFFF;
}
