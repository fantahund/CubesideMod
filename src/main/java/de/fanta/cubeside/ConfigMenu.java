package de.fanta.cubeside;

/*import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class ConfigMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (!FabricLoader.getInstance().isModLoaded("cloth-config2")) {
            CubesideClientFabric.LOGGER.warn("Couldn't find Cloth Config, config menu disabled!");
            return parent -> null;
        }
        return new Builder()::build;
    }

    static class Builder {
        Screen build(Screen parent) {
            ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Text.translatable("title.cubeside.config"));
            builder.setSavingRunnable(Config::serialize);
            ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.cubeside.general"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.cubeside.chattimestamps"), Config.chattimestamps).setDefaultValue(false).setSaveConsumer(val -> Config.chattimestamps = val).build());
            general.addEntry(entryBuilder.startColorField(Text.translatable("options.cubeside.timestampcolor"), Config.timestampColor).setDefaultValue(TextColor.fromFormatting(Formatting.WHITE)).setSaveConsumer3(textColor -> Config.timestampColor = textColor).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.cubeside.thirdpersonelytra"), Config.thirdPersonElytra).setDefaultValue(false).setSaveConsumer(val -> Config.thirdPersonElytra = val).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.cubeside.dropitemfancy"), Config.dropItemFancy).setDefaultValue(false).setSaveConsumer(val -> Config.dropItemFancy = val).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.cubeside.elytraalarm"), Config.elytraAlarm).setDefaultValue(false).setSaveConsumer(val -> Config.elytraAlarm = val).build());

            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.cubeside.savemessagestodatabase"), Config.saveMessagestoDatabase).setDefaultValue(false).setSaveConsumer(val -> Config.saveMessagestoDatabase = val).build());
            general.addEntry(entryBuilder.startIntField(Text.translatable("options.cubeside.daysthemessagesarestored"), Config.daystheMessagesareStored).setDefaultValue(10).setSaveConsumer(integer -> Config.daystheMessagesareStored = integer).build());

            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.cubeside.showinvisiblearmorstands"), Config.showInvisibleArmorstands).setDefaultValue(true).setSaveConsumer(val -> Config.showInvisibleArmorstands = val).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.cubeside.showinvisibleentitiesinspectator"), Config.showInvisibleEntitiesinSpectator).setDefaultValue(true).setSaveConsumer(val -> Config.showInvisibleEntitiesinSpectator = val).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.cubeside.simplesignglow"), Config.simpleSignGlow).setDefaultValue(false).setSaveConsumer(val -> Config.simpleSignGlow = val).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.cubeside.afkpling"), Config.afkPling).setDefaultValue(false).setSaveConsumer(val -> Config.afkPling = val).build());
            general.addEntry(entryBuilder.startIntField(Text.translatable("options.cubeside.chatlimit"), Config.chatMessageLimit).setDefaultValue(100).setSaveConsumer(integer -> Config.chatMessageLimit = integer).build());

            //Eiki
            general.addEntry(entryBuilder.startBooleanToggle(Text.of("AutoChat Enabled"), Config.autochat).setDefaultValue(false).setSaveConsumer(val -> Config.autochat = val).build());
            general.addEntry(entryBuilder.startStrField(Text.of("AutoChat Message"), Config.antwort).setDefaultValue("Ich habe grade leider keine Zeit!").setSaveConsumer(String -> Config.antwort = String).build());

            //Viewdistance
            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.cubeside.fullverticalview"), Config.fullverticalview).setDefaultValue(true).setSaveConsumer(val -> Config.fullverticalview = val).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.cubeside.unloadchunks"), Config.unloadchunks).setDefaultValue(true).setSaveConsumer(val -> Config.unloadchunks = val).build());
            general.addEntry(entryBuilder.startIntSlider(Text.translatable("options.cubeside.fakeviewdistance"), Config.fakeviewdistance, 1, 64).setDefaultValue(32).setSaveConsumer(val -> Config.fakeviewdistance = val).build());

            //tpa
            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.cubeside.clickabletpamessage"), Config.clickabletpamessage).setDefaultValue(true).setSaveConsumer(val -> Config.clickabletpamessage = val).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.cubeside.tpasound"), Config.tpasound).setDefaultValue(true).setSaveConsumer(val -> Config.tpasound = val).build());

            //christmas
            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.cubeside.removechristmaschest"), Config.removeChristmasChest).setDefaultValue(false).setSaveConsumer(val -> Config.removeChristmasChest = val).build());

            general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("options.cubeside.gamemodeswitcher"), Config.gamemodeSwitcher).setDefaultValue(true).setSaveConsumer(val -> Config.gamemodeSwitcher = val).build());

            return builder.build();
        }
    }
}*/
