package com.cute.aurorite.client.util;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Drawing helpers on top of {@link GuiGraphicsExtractor}.
 *
 * <p>The 26.3 GUI pipeline only offers axis-aligned rectangles, so rounded
 * corners are built from scanlines. The four corner blocks are drawn pixel by
 * pixel with 2x2 supersampling, which is what keeps the curves smooth at the
 * large radii this interface uses; everything straight is a single quad.
 */
public final class RenderUtil {

	private RenderUtil() {
	}

	/** A rounded rectangle with the same radius on all four corners. */
	public static void roundedRect(GuiGraphicsExtractor g, float x, float y, float w, float h, float radius, int argb) {
		roundedRect(g, x, y, w, h, radius, radius, radius, radius, argb);
	}

	/**
	 * A rounded rectangle with an individual radius per corner.
	 *
	 * @param rTL top-left radius
	 * @param rTR top-right radius
	 * @param rBR bottom-right radius
	 * @param rBL bottom-left radius
	 * @param argb the fill colour, alpha included
	 */
	public static void roundedRect(GuiGraphicsExtractor g, float x, float y, float w, float h,
			float rTL, float rTR, float rBR, float rBL, int argb) {
		if (w <= 0.0f || h <= 0.0f) {
			return;
		}

		int alpha = (argb >>> 24) & 0xFF;

		if (alpha == 0) {
			return;
		}

		int rgb = argb & 0x00FFFFFF;

		float maxRadius = Math.min(w, h) * 0.5f;
		rTL = clamp(rTL, 0.0f, maxRadius);
		rTR = clamp(rTR, 0.0f, maxRadius);
		rBR = clamp(rBR, 0.0f, maxRadius);
		rBL = clamp(rBL, 0.0f, maxRadius);

		int left = (int) Math.floor(x);
		int top = (int) Math.floor(y);
		int right = (int) Math.ceil(x + w);
		int bottom = (int) Math.ceil(y + h);

		if (right <= left || bottom <= top) {
			return;
		}

		int topBand = (int) Math.ceil(Math.max(rTL, rTR));
		int bottomBand = (int) Math.ceil(Math.max(rBL, rBR));

		// Corner blocks, anti-aliased.
		corner(g, x + rTL, y + rTL, rTL, left, top, (int) Math.ceil(x + rTL), (int) Math.ceil(y + rTL), rgb, alpha);
		corner(g, x + w - rTR, y + rTR, rTR, (int) Math.floor(x + w - rTR), top, right, (int) Math.ceil(y + rTR), rgb, alpha);
		corner(g, x + rBL, y + h - rBL, rBL, left, (int) Math.floor(y + h - rBL), (int) Math.ceil(x + rBL), bottom, rgb, alpha);
		corner(g, x + w - rBR, y + h - rBR, rBR, (int) Math.floor(x + w - rBR),
				(int) Math.floor(y + h - rBR), right, bottom, rgb, alpha);

		int solid = (alpha << 24) | rgb;

		// Straight edges.
		int stripLeft = (int) Math.ceil(x + rTL);
		int stripRight = (int) Math.floor(x + w - rTR);

		if (stripRight > stripLeft) {
			g.fill(stripLeft, top, stripRight, top + topBand, solid);
		}

		stripLeft = (int) Math.ceil(x + rBL);
		stripRight = (int) Math.floor(x + w - rBR);

		if (stripRight > stripLeft) {
			g.fill(stripLeft, bottom - bottomBand, stripRight, bottom, solid);
		}

		// Middle block.
		int middleTop = top + topBand;
		int middleBottom = bottom - bottomBand;

		if (middleBottom > middleTop) {
			g.fill(left, middleTop, right, middleBottom, solid);
		}
	}

	/** A filled circle, using the same anti-aliased corner routine. */
	public static void circle(GuiGraphicsExtractor g, float centreX, float centreY, float radius, int argb) {
		roundedRect(g, centreX - radius, centreY - radius, radius * 2.0f, radius * 2.0f, radius, argb);
	}

	/**
	 * A rounded rectangle filled with a vertical gradient, used for the glass
	 * sheen and shading.
	 */
	public static void roundedRectGradient(GuiGraphicsExtractor g, float x, float y, float w, float h,
			float rTL, float rTR, float rBR, float rBL, int topArgb, int bottomArgb) {
		if (w <= 0.0f || h <= 0.0f) {
			return;
		}

		float topAlpha = ((topArgb >>> 24) & 0xFF) / 255.0f;
		float bottomAlpha = ((bottomArgb >>> 24) & 0xFF) / 255.0f;

		if (topAlpha <= 0.0f && bottomAlpha <= 0.0f) {
			return;
		}

		int topRgb = topArgb & 0x00FFFFFF;
		int bottomRgb = bottomArgb & 0x00FFFFFF;

		float maxRadius = Math.min(w, h) * 0.5f;
		rTL = clamp(rTL, 0.0f, maxRadius);
		rTR = clamp(rTR, 0.0f, maxRadius);
		rBR = clamp(rBR, 0.0f, maxRadius);
		rBL = clamp(rBL, 0.0f, maxRadius);

		int top = Math.round(y);
		int bottom = Math.round(y + h);
		int rows = bottom - top;

		if (rows <= 0) {
			return;
		}

		for (int row = 0; row < rows; row++) {
			float t = rows == 1 ? 0.0f : row / (float) (rows - 1);
			int rgb = lerpRgb(topRgb, bottomRgb, t);
			int alpha = Math.round(255.0f * (topAlpha + (bottomAlpha - topAlpha) * t));

			if (alpha <= 0) {
				continue;
			}

			float left = x + cornerInset(row, rTL);
			float right = x + w - cornerInset(row, rTR);
			int rowTop = top + row;
			rowSpan(g, left, right, rowTop, rowTop + 1, rgb, alpha);
		}
	}

	/** A "&lt;" chevron, drawn from two rotated bars. */
	public static void chevronLeft(GuiGraphicsExtractor g, float centreX, float centreY, int argb) {
		g.pose().pushMatrix();
		g.pose().translate(centreX - 1.0f, centreY - 3.0f);
		g.pose().rotate((float) Math.toRadians(-45.0));
		g.fill(-4, -1, 4, 1, argb);
		g.pose().popMatrix();

		g.pose().pushMatrix();
		g.pose().translate(centreX - 1.0f, centreY + 3.0f);
		g.pose().rotate((float) Math.toRadians(45.0));
		g.fill(-4, -1, 4, 1, argb);
		g.pose().popMatrix();
	}

	// ------------------------------------------------------------------
	// Internals
	// ------------------------------------------------------------------

	/**
	 * Draws one quarter-circle corner block, supersampling each pixel so the
	 * curve stays smooth instead of stepping.
	 */
	private static void corner(GuiGraphicsExtractor g, float centreX, float centreY, float radius,
			int blockLeft, int blockTop, int blockRight, int blockBottom, int rgb, int alpha) {
		if (radius <= 0.0f) {
			return;
		}

		float radiusSquared = radius * radius;
		int blockW = blockRight - blockLeft;
		int blockH = blockBottom - blockTop;

		if (blockW <= 0 || blockH <= 0) {
			return;
		}

		for (int py = blockTop; py < blockBottom; py++) {
			for (int px = blockLeft; px < blockRight; px++) {
				int covered = 0;

				for (int sy = 0; sy < 2; sy++) {
					for (int sx = 0; sx < 2; sx++) {
						float sampleX = px + (sx + 0.5f) * 0.5f;
						float sampleY = py + (sy + 0.5f) * 0.5f;
						float dx = sampleX - centreX;
						float dy = sampleY - centreY;

						if (dx * dx + dy * dy <= radiusSquared) {
							covered++;
						}
					}
				}

				if (covered == 0) {
					continue;
				}

				int effective = covered == 4 ? alpha : Math.min(alpha, Math.round(alpha * covered / 4.0f));

				if (effective > 0) {
					g.fill(px, py, px + 1, py + 1, (effective << 24) | rgb);
				}
			}
		}
	}

	private static float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}

	/** Horizontal inset of a rounded corner at a given row. */
	private static float cornerInset(int row, float radius) {
		if (radius <= 0.5f) {
			return 0.0f;
		}

		float centre = row + 0.5f;

		if (centre >= radius) {
			return 0.0f;
		}

		float distance = radius - centre;

		return radius - (float) Math.sqrt(Math.max(0.0f, radius * radius - distance * distance));
	}

	private static void rowSpan(GuiGraphicsExtractor g, float left, float right, int y0, int y1, int rgb, int alpha) {
		if (right - left <= 0.01f || y1 <= y0) {
			return;
		}

		int firstSolid = (int) Math.ceil(left);
		int lastSolidExclusive = (int) Math.floor(right);

		if (lastSolidExclusive <= firstSolid) {
			int column = (int) Math.floor(left);
			float coverage = Math.min(right, column + 1.0f) - Math.max(left, column);

			if (coverage > 0.01f) {
				fillCovered(g, column, y0, y1, rgb, alpha, coverage);
			}

			return;
		}

		g.fill(firstSolid, y0, lastSolidExclusive, y1, (alpha << 24) | rgb);

		if (firstSolid > left) {
			int column = firstSolid - 1;
			float coverage = Math.min(column + 1.0f, right) - Math.max(column, left);

			if (coverage > 0.01f) {
				fillCovered(g, column, y0, y1, rgb, alpha, coverage);
			}
		}

		if (lastSolidExclusive < right) {
			int column = lastSolidExclusive;
			float coverage = Math.min(column + 1.0f, right) - Math.max(column, left);

			if (coverage > 0.01f) {
				fillCovered(g, column, y0, y1, rgb, alpha, coverage);
			}
		}
	}

	private static void fillCovered(GuiGraphicsExtractor g, int column, int y0, int y1, int rgb, int alpha, float coverage) {
		int effective = Math.min(alpha, Math.round(alpha * Math.min(1.0f, coverage)));

		if (effective > 0) {
			g.fill(column, y0, column + 1, y1, (effective << 24) | rgb);
		}
	}

	private static int lerpRgb(int from, int to, float t) {
		int fr = (from >> 16) & 0xFF;
		int fg = (from >> 8) & 0xFF;
		int fb = from & 0xFF;
		int tr = (to >> 16) & 0xFF;
		int tg = (to >> 8) & 0xFF;
		int tb = to & 0xFF;

		int r = Math.round(fr + (tr - fr) * t);
		int g = Math.round(fg + (tg - fg) * t);
		int b = Math.round(fb + (tb - fb) * t);

		return (r << 16) | (g << 8) | b;
	}
}
