package de.fanta.cubeside;

import de.fanta.cubeside.util.ChatUtil;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Cubeside implements ModInitializer {
    public static final String MODID = "Cubeside";
    public static final String PREFIX = "§9[§aCubeside§9] ";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    private ChatUtil chatUtil;

    @Override
    public void onInitialize() {
        Config.deserialize();
        chatUtil = new ChatUtil();

        LOGGER.info(MODID + "Mod Loaded");
    }
}
