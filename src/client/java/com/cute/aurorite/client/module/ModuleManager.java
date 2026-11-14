package com.cute.aurorite.client.module;

import java.util.List;

import com.cute.aurorite.client.module.impl.AutoSprintModule;
import com.cute.aurorite.client.module.impl.ClickGuiModule;
import com.cute.aurorite.client.module.impl.ComboModule;
import com.cute.aurorite.client.module.impl.CpsModule;
import com.cute.aurorite.client.module.impl.FpsModule;
import com.cute.aurorite.client.module.impl.FreelookModule;
import com.cute.aurorite.client.module.impl.HealthModule;
import com.cute.aurorite.client.module.impl.KeystrokesModule;
import com.cute.aurorite.client.module.impl.NightVisionModule;
import com.cute.aurorite.client.module.impl.WatermarkModule;
import com.cute.aurorite.client.module.impl.ZoomModule;

/** Holds every module, in the order they appear in the ClickGUI. */
public final class ModuleManager {

	public static final CpsModule CPS = new CpsModule();
	public static final KeystrokesModule KEYSTROKES = new KeystrokesModule();
	public static final ComboModule COMBO = new ComboModule();
	public static final HealthModule HEALTH = new HealthModule();
	public static final FpsModule FPS = new FpsModule();
	public static final AutoSprintModule AUTO_SPRINT = new AutoSprintModule();
	public static final NightVisionModule NIGHT_VISION = new NightVisionModule();
	public static final ZoomModule ZOOM = new ZoomModule();
	public static final WatermarkModule WATERMARK = new WatermarkModule();
	public static final FreelookModule FREELOOK = new FreelookModule();
	public static final ClickGuiModule CLICK_GUI = new ClickGuiModule();

	private static final List<Module> MODULES = List.of(CPS, KEYSTROKES, COMBO, HEALTH, FPS, AUTO_SPRINT, NIGHT_VISION,
			ZOOM, WATERMARK, FREELOOK, CLICK_GUI);

	private ModuleManager() {
	}

	public static List<Module> modules() {
		return MODULES;
	}
}
