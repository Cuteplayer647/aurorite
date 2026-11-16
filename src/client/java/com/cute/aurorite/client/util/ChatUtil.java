package com.cute.aurorite.client.util;

import com.cute.aurorite.client.gui.Theme;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

/**
 * Sends client messages to the chat box.
 *
 * <p>Every message starts with {@code [Aurorite]} in white brackets with the
 * client name in the watermark colour.
 */
public final class ChatUtil {

	private static final Style BRACKET = Style.EMPTY.withColor(ChatFormatting.WHITE);
	private static final Style CLIENT_NAME = Style.EMPTY.withColor(TextColor.fromRgb(Theme.CHAT_CLIENT));

	private ChatUtil() {
	}

	/** The {@code [Aurorite] } prefix, ready to append to a message. */
	public static MutableComponent prefix() {
		return Component.literal("[").withStyle(BRACKET)
				.append(Component.literal("Aurorite").withStyle(CLIENT_NAME))
				.append(Component.literal("] ").withStyle(BRACKET));
	}

	/** Sends {@code body} to the chat box behind the {@code [Aurorite]} prefix. */
	public static void send(Component body) {
		Minecraft minecraft = Minecraft.getInstance();

		if (minecraft.gui == null) {
			return;
		}

		minecraft.gui.chatListener().handleSystemMessage(prefix().append(body), false);
	}
}
