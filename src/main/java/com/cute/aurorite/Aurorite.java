package com.cute.aurorite;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Aurorite implements ModInitializer {
	public static final String MOD_ID = "aurorite";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// Everything this client does happens on the client side; see
		// com.cute.aurorite.client.AuroriteClient.
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
