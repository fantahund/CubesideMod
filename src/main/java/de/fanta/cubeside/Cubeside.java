package de.fanta.cubeside;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Cubeside implements ModInitializer {
    public static final String MODID = "Cubeside";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Override
    public void onInitialize() {
        LOGGER.info(MODID + "Mod Loaded");
        Config.deserialize();
    }
}
