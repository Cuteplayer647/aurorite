package com.cute.aurorite.client.module;

import net.minecraft.client.gui.Font;

/** A module that is also drawn on the in-game HUD and can be dragged. */
public interface HudWidget {

	int getX();

	int getY();

	int measureWidth(Font font);

	int measureHeight(Font font);

	void setPosition(int x, int y);
}
