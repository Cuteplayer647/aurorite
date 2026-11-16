package com.cute.aurorite.client.module.impl;

import com.cute.aurorite.client.module.Module;
import com.cute.aurorite.client.module.setting.BooleanSetting;
import com.cute.aurorite.client.module.setting.NumberSetting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Draws a plain coloured box around other players.
 *
 * <p>This is its own implementation, independent of the vanilla hitbox view
 * (F3 + B): it never reads or changes that option, and it builds the twelve box
 * edges itself instead of reusing the vanilla cuboid helper. No eye marker and no
 * view or velocity lines are drawn, only the outline.
 */
public class HitboxModule extends Module {

	private final NumberSetting red = add(new NumberSetting("红色", 255.0f, 0.0f, 255.0f));
	private final NumberSetting green = add(new NumberSetting("绿色", 255.0f, 0.0f, 255.0f));
	private final NumberSetting blue = add(new NumberSetting("蓝色", 255.0f, 0.0f, 255.0f));
	private final NumberSetting alpha = add(new NumberSetting("透明度", 255.0f, 0.0f, 255.0f));
	private final BooleanSetting includeSelf = add(new BooleanSetting("包含自己", true));
	private final BooleanSetting selfThirdPersonOnly = add(new BooleanSetting("仅第三视角显示自己", true));

	public HitboxModule() {
		super("Hitbox");
	}

	public int getColour() {
		return ARGB.color(Math.round(alpha.getValue()), Math.round(red.getValue()),
				Math.round(green.getValue()), Math.round(blue.getValue()));
	}

	/** Emits one box per other player; called while the gizmo collector is open. */
	public void render() {
		if (!isEnabled()) {
			return;
		}

		try {
			Minecraft minecraft = Minecraft.getInstance();
			ClientLevel level = minecraft.level;

			if (level == null || minecraft.player == null) {
				return;
			}

			float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true);
			int colour = getColour();
			boolean firstPerson = minecraft.options.getCameraType().isFirstPerson();
			int drew = 0;

			for (Player player : level.players()) {
				// Invisible players stay invisible: drawing their box would be a wallhack.
				if (player.isRemoved() || player.isInvisible()) {
					continue;
				}

				if (player == minecraft.player) {
					// The own box only makes sense when the camera can see the body.
					if (!includeSelf.getValue() || (selfThirdPersonOnly.getValue() && firstPerson)) {
						continue;
					}
				}

				// Move the collision box by the same interpolation the entity is drawn with,
				// otherwise the outline lags a frame behind the model.
				Vec3 offset = player.getPosition(partialTick).subtract(player.position());

				drawBox(player.getBoundingBox().move(offset).inflate(BOX_INFLATE), colour);
				drew++;
			}

			if (!reported) {
				reported = true;
				// One line per session so a "nothing renders" report can be told apart
				// from "the hook never ran" or "the own box is hidden in first person".
				System.out.println("[aurorite] hitbox emitted " + drew + " box(es) (players="
						+ level.players().size() + ", firstPerson=" + firstPerson
						+ ", includeSelf=" + includeSelf.getValue()
						+ ", selfThirdPersonOnly=" + selfThirdPersonOnly.getValue() + ")");
			}
		} catch (Exception exception) {
			// Never let a render failure take the frame down; report it once.
			if (!crashed) {
				crashed = true;
				System.err.println("[aurorite] hitbox render failed: " + exception);
			}
		}
	}

	/** Keeps the outline clear of the model surface so it cannot be depth-culled. */
	private static final double BOX_INFLATE = 0.02;

	private boolean reported;
	private boolean crashed;

	/** Emits the twelve edges of the box. */
	private static void drawBox(AABB box, int colour) {
		double x0 = box.minX;
		double y0 = box.minY;
		double z0 = box.minZ;
		double x1 = box.maxX;
		double y1 = box.maxY;
		double z1 = box.maxZ;

		// bottom
		line(x0, y0, z0, x1, y0, z0, colour);
		line(x1, y0, z0, x1, y0, z1, colour);
		line(x1, y0, z1, x0, y0, z1, colour);
		line(x0, y0, z1, x0, y0, z0, colour);

		// top
		line(x0, y1, z0, x1, y1, z0, colour);
		line(x1, y1, z0, x1, y1, z1, colour);
		line(x1, y1, z1, x0, y1, z1, colour);
		line(x0, y1, z1, x0, y1, z0, colour);

		// verticals
		line(x0, y0, z0, x0, y1, z0, colour);
		line(x1, y0, z0, x1, y1, z0, colour);
		line(x1, y0, z1, x1, y1, z1, colour);
		line(x0, y0, z1, x0, y1, z1, colour);
	}

	private static void line(double x0, double y0, double z0, double x1, double y1, double z1, int colour) {
		// Depth tested on purpose: the outline is hidden behind walls exactly like the
		// vanilla hitbox view, so it cannot be used as a see-through wallhack.
		Gizmos.line(new Vec3(x0, y0, z0), new Vec3(x1, y1, z1), colour);
	}
}
