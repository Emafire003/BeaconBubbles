package me.emafire003.dev.beaconbubbles;

import net.fabricmc.api.ModInitializer;
import net.minecraft.state.property.BooleanProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeaconBubbles implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "beaconbubbles";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final BooleanProperty HAS_BEAM = BooleanProperty.of("has_beam");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Making bubbles in the water using a laser beam...");
	}
}
