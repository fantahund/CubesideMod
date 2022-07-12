package de.fanta.cubeside.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import net.minecraft.text.Text;

import java.io.File;

public class Configs implements IConfigHandler {

    private static final String CONFIG_FILE_NAME = "cubeside.json";
    private static final int CONFIG_VERSION = 1;

    public static class Generic {
        public static final ConfigBoolean ThirdPersonElytra = new ConfigBoolean("ThirdPersonElytra", false, Text.translatable("options.cubeside.thirdpersonelytra").getString());
        public static final ConfigBoolean ElytraAlarm = new ConfigBoolean("ElytraAlarm", false, Text.translatable("options.cubeside.elytraalarm").getString());
        public static final ConfigBoolean ShowInvisibleArmorstands = new ConfigBoolean("ShowInvisibleArmorstands", true, Text.translatable("options.cubeside.showinvisiblearmorstands").getString());
        public static final ConfigBoolean ShowInvisibleEntitiesInSpectator = new ConfigBoolean("ShowInvisibleEntitiesInSpectator", true, Text.translatable("options.cubeside.showinvisibleentitiesinspectator").getString());
        public static final ConfigBoolean AFKPling = new ConfigBoolean("AFKPling", false, Text.translatable("options.cubeside.afkpling").getString());
        public static final ConfigBoolean ClickableTpaMessage = new ConfigBoolean("ClickableTpaMessage", true, Text.translatable("options.cubeside.clickabletpamessage").getString());
        public static final ConfigBoolean TpaSound = new ConfigBoolean("TpaSound", true, Text.translatable("options.cubeside.tpasound").getString());
        public static final ConfigBoolean GamemodeSwitcher = new ConfigBoolean("GamemodeSwitcher", true, Text.translatable("options.cubeside.gamemodeswitcher").getString());

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                ThirdPersonElytra,
                ElytraAlarm,
                ShowInvisibleArmorstands,
                ShowInvisibleEntitiesInSpectator,
                AFKPling,
                ClickableTpaMessage,
                TpaSound,
                GamemodeSwitcher
        );
    }

    public static class Chat {
        public static final ConfigBoolean ChatTimeStamps = new ConfigBoolean("ChatTimeStamps", false, Text.translatable("options.cubeside.chattimestamps").getString());
        public static final ConfigColor TimeStampColor = new ConfigColor("TimeStampColor", "#ffffff", Text.translatable("options.cubeside.timestampcolor").getString());
        public static final ConfigBoolean SaveMessagesToDatabase = new ConfigBoolean("SaveMessagesToDatabase", false, Text.translatable("options.cubeside.savemessagestodatabase").getString());
        public static final ConfigInteger DaysTheMessagesAreStored = new ConfigInteger("DaysTheMessagesAreStored", 10, Text.translatable("options.cubeside.daysthemessagesarestored").getString());
        public static final ConfigInteger ChatMessageLimit = new ConfigInteger("ChatMessageLimit", 100, Text.translatable("options.cubeside.chatlimit").getString());

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                ChatTimeStamps,
                TimeStampColor,
                SaveMessagesToDatabase,
                DaysTheMessagesAreStored,
                ChatMessageLimit
        );
    }

    public static class ChunkLoading {
        public static final ConfigBoolean FullVerticalView = new ConfigBoolean("FullVerticalView", true, Text.translatable("options.cubeside.fullverticalview").getString());
        public static final ConfigBoolean UnloadChunks = new ConfigBoolean("UnloadChunks", true, Text.translatable("options.cubeside.unloadchunks").getString());
        public static final ConfigInteger FakeViewDistance = new ConfigInteger("FakeViewDistance", 32, Text.translatable("options.cubeside.fakeviewdistance").getString());

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                FullVerticalView,
                UnloadChunks,
                FakeViewDistance
        );
    }

    public static class Fun {
        public static final ConfigBoolean DropItemFancy = new ConfigBoolean("DropItemFancy", false, Text.translatable("options.cubeside.dropitemfancy").getString());
        public static final ConfigBoolean DisableChristmasChest = new ConfigBoolean("DisableChristmasChest", false, Text.translatable("options.cubeside.removechristmaschest").getString());

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                DropItemFancy,
                DisableChristmasChest
        );
    }

    public static class Fixes {
        public static final ConfigBoolean SimpleSignGlow = new ConfigBoolean("SimpleSignGlow", false, Text.translatable("options.cubeside.simplesignglow").getString());

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                SimpleSignGlow
        );
    }

    public static class PermissionSettings {
        public static final ConfigBoolean AutoChat = new ConfigBoolean("AutoChat", false, "AutoChat Enabled");
        public static final ConfigString AutoChatAntwort = new ConfigString("AutoChatAntwort", "Ich habe grade leider keine Zeit!", "AutoChat Message");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                AutoChat,
                AutoChatAntwort
        );
    }

    public static void loadFromFile() {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);

        if (!configFile.exists()) {
            saveToFile();
        }

        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject()) {
                JsonObject root = element.getAsJsonObject();
                ConfigUtils.readConfigBase(root, "Generic", Configs.Generic.OPTIONS);
                ConfigUtils.readConfigBase(root, "Chat", Chat.OPTIONS);
                ConfigUtils.readConfigBase(root, "ChunkLoading", ChunkLoading.OPTIONS);
                ConfigUtils.readConfigBase(root, "Fun", Fun.OPTIONS);
                ConfigUtils.readConfigBase(root, "Fixes", Fixes.OPTIONS);
                ConfigUtils.readConfigBase(root, "PermissionSettings", PermissionSettings.OPTIONS);
            }
        }
    }

    public static void saveToFile() {
        File dir = FileUtils.getConfigDirectory();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
            JsonObject root = new JsonObject();

            ConfigUtils.writeConfigBase(root, "Generic", Generic.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Chat", Chat.OPTIONS);
            ConfigUtils.writeConfigBase(root, "ChunkLoading", ChunkLoading.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Fun", Fun.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Fixes", Fixes.OPTIONS);
            ConfigUtils.writeConfigBase(root, "PermissionSettings", PermissionSettings.OPTIONS);

            root.add("config_version", new JsonPrimitive(CONFIG_VERSION));

            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }

    @Override
    public void load() {
        loadFromFile();
    }

    @Override
    public void save() {
        saveToFile();
    }

}
