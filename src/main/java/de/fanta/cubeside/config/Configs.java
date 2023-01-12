package de.fanta.cubeside.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.fanta.cubeside.CubesideClientFabric;
import fi.dy.masa.malilibcs.config.ConfigUtils;
import fi.dy.masa.malilibcs.config.IConfigBase;
import fi.dy.masa.malilibcs.config.IConfigHandler;
import fi.dy.masa.malilibcs.config.options.ConfigBoolean;
import fi.dy.masa.malilibcs.config.options.ConfigColor;
import fi.dy.masa.malilibcs.config.options.ConfigColorList;
import fi.dy.masa.malilibcs.config.options.ConfigDouble;
import fi.dy.masa.malilibcs.config.options.ConfigInteger;
import fi.dy.masa.malilibcs.config.options.ConfigString;
import fi.dy.masa.malilibcs.config.options.ConfigStringList;
import fi.dy.masa.malilibcs.util.Color4f;
import fi.dy.masa.malilibcs.util.FileUtils;
import fi.dy.masa.malilibcs.util.JsonUtils;
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
        public static final ConfigBoolean ActionBarShadow = new ConfigBoolean("ActionBarShadow", true, Text.translatable("options.cubeside.actionbarshadow").getString());

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                ThirdPersonElytra,
                ElytraAlarm,
                ShowInvisibleArmorstands,
                ShowInvisibleEntitiesInSpectator,
                AFKPling,
                ClickableTpaMessage,
                TpaSound,
                GamemodeSwitcher,
                ActionBarShadow
        );
    }

    public static class Chat {
        public static final ConfigBoolean ChatTimeStamps = new ConfigBoolean("ChatTimeStamps", false, Text.translatable("options.cubeside.chattimestamps").getString());
        public static final ConfigColor TimeStampColor = new ConfigColor("TimeStampColor", "#ffffff", Text.translatable("options.cubeside.timestampcolor").getString());
        public static final ConfigBoolean SaveMessagesToDatabase = new ConfigBoolean("SaveMessagesToDatabase", false, Text.translatable("options.cubeside.savemessagestodatabase").getString());
        public static final ConfigInteger DaysTheMessagesAreStored = new ConfigInteger("DaysTheMessagesAreStored", 10, 1, 30, true, Text.translatable("options.cubeside.daysthemessagesarestored").getString());
        public static final ConfigInteger ChatMessageLimit = new ConfigInteger("ChatMessageLimit", 100, 1, 100000, true, Text.translatable("options.cubeside.chatlimit").getString());
        public static final ConfigBoolean DisplayChatInfo = new ConfigBoolean("DisplayChatInfo", true, Text.translatable("options.cubeside.displaychatinfo").getString());
        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                ChatTimeStamps,
                TimeStampColor,
                SaveMessagesToDatabase,
                DaysTheMessagesAreStored,
                ChatMessageLimit,
                DisplayChatInfo
        );
    }

    public static class ChunkLoading {
        public static final ConfigBoolean FullVerticalView = new ConfigBoolean("FullVerticalView", true, Text.translatable("options.cubeside.fullverticalview").getString());
        public static final ConfigBoolean UnloadChunks = new ConfigBoolean("UnloadChunks", true, Text.translatable("options.cubeside.unloadchunks").getString());
        public static final ConfigInteger FakeViewDistance = new ConfigInteger("FakeViewDistance", 32, 1, 64, true, Text.translatable("options.cubeside.fakeviewdistance").getString());

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

    public static class HitBox {
        public static final ConfigBoolean RainbowEntityHitBox = new ConfigBoolean("RainbowEntityHitBox", false, Text.translatable("options.cubeside.rainbowentityhitbox").getString());
        public static final ConfigColorList RainbowEntityHitBoxColorList = new ConfigColorList("RainbowEntityHitBoxColorList", ImmutableList.of(Color4f.fromColor(16711684), Color4f.fromColor(16754176), Color4f.fromColor(16769280), Color4f.fromColor(65305), Color4f.fromColor(35071), Color4f.fromColor(13959423)), Text.translatable("options.cubeside.rainbowentityhitboxcolorlist").getString());
        public static final ConfigDouble RainbowEntityHitBoxSpeed = new ConfigDouble("RainbowEntityHitBoxSpeed", 0.1, 0.0, 1, true, Text.translatable("options.cubeside.rainbowentityhitboxspeed").getString());
        public static final ConfigDouble EntityHitBoxVisibility = new ConfigDouble("EntityHitBoxVisibility", 1, 0.0, 1, true, Text.translatable("options.cubeside.entityhitboxvisibility").getString());
        public static final ConfigColor EntityHitBoxColor = new ConfigColor("EntityHitBoxColor", "#ffffff", Text.translatable("options.cubeside.entityhitboxcolor").getString());
        public static final ConfigBoolean EntityHitBoxDirection = new ConfigBoolean("EntityHitBoxDirection", true, Text.translatable("options.cubeside.entityhitboxdirection").getString());
        public static final ConfigBoolean RainbowBlockHitBox = new ConfigBoolean("RainbowBlockHitBox", false, Text.translatable("options.cubeside.rainbowblockhitbox").getString());
        public static final ConfigColorList RainbowBlockHitBoxColorList = new ConfigColorList("RainbowBlockHitBoxColorList", ImmutableList.of(Color4f.fromColor(16711684), Color4f.fromColor(16754176), Color4f.fromColor(16769280), Color4f.fromColor(65305), Color4f.fromColor(35071), Color4f.fromColor(13959423)), Text.translatable("options.cubeside.rainbowblockhitboxcolorlist").getString());
        public static final ConfigDouble RainbowBlockHitBoxSpeed = new ConfigDouble("RainbowBlockHitBoxSpeed", 0.1, 0.0, 1, true, Text.translatable("options.cubeside.rainbowblockhitboxspeed").getString());
        public static final ConfigDouble BlockHitBoxVisibility = new ConfigDouble("BlockHitBoxVisibility", 0.4, 0.0, 1, true, Text.translatable("options.cubeside.blockhitboxvisibility").getString());
        public static final ConfigColor BlockHitBoxColor = new ConfigColor("BlockHitBoxColor", "#000000", Text.translatable("options.cubeside.blockhitboxcolor").getString());

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                RainbowEntityHitBox,
                RainbowEntityHitBoxColorList,
                RainbowEntityHitBoxSpeed,
                EntityHitBoxColor,
                EntityHitBoxVisibility,
                EntityHitBoxDirection,
                RainbowBlockHitBox,
                RainbowBlockHitBoxColorList,
                RainbowBlockHitBoxSpeed,
                BlockHitBoxColor,
                BlockHitBoxVisibility
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
        public static final ConfigStringList AdminList = new ConfigStringList("AdminList", ImmutableList.of("Eiki", "Brokkonaut", "jonibohni", "_Scorcho", "Starjon", "Becky0810", "Scoptixxx"), "Admin Liste");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                AutoChat,
                AutoChatAntwort,
                AdminList
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
                ConfigUtils.readConfigBase(root, "Generic", Generic.OPTIONS);
                ConfigUtils.readConfigBase(root, "Chat", Chat.OPTIONS);
                ConfigUtils.readConfigBase(root, "ChunkLoading", ChunkLoading.OPTIONS);
                ConfigUtils.readConfigBase(root, "Fun", Fun.OPTIONS);
                ConfigUtils.readConfigBase(root, "Hitbox", HitBox.OPTIONS);
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
            ConfigUtils.writeConfigBase(root, "Hitbox", HitBox.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Fixes", Fixes.OPTIONS);
            ConfigUtils.writeConfigBase(root, "PermissionSettings", PermissionSettings.OPTIONS);

            root.add("config_version", new JsonPrimitive(CONFIG_VERSION));

            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));

            CubesideClientFabric.LOGGER.info("[CubesideMod] Config Saved");
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
