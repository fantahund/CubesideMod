package de.fanta.cubeside;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class ConfigMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (!FabricLoader.getInstance().isModLoaded("cloth-config2")) {
            CubesideClient.LOGGER.warn("Couldn't find Cloth Config, config menu disabled!");
            return parent -> null;
        }
        return new Builder()::build;
    }

    static class Builder {
        Screen build(Screen parent) {
            ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(new TranslatableText("title.cubeside.config"));
            builder.setSavingRunnable(Config::serialize);
            ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("category.cubeside.general"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("options.cubeside.chattimestamps"), Config.chattimestamps).setDefaultValue(false).setSaveConsumer(val -> Config.chattimestamps = val).build());
            general.addEntry(entryBuilder.startColorField(new TranslatableText("options.cubeside.timestampcolor"), Config.timestampColor).setDefaultValue(TextColor.fromFormatting(Formatting.WHITE)).setSaveConsumer3(textColor -> Config.timestampColor = textColor).build());
            general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("options.cubeside.thirdpersonelytra"), Config.thirdPersonElytra).setDefaultValue(false).setSaveConsumer(val -> Config.thirdPersonElytra = val).build());
            general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("options.cubeside.dropitemfancy"), Config.dropItemFancy).setDefaultValue(false).setSaveConsumer(val -> Config.dropItemFancy = val).build());
            general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("options.cubeside.elytraalarm"), Config.elytraAlarm).setDefaultValue(false).setSaveConsumer(val -> Config.elytraAlarm = val).build());
            general.addEntry(entryBuilder.startIntField(new TranslatableText("options.cubeside.chatlimit"), Config.chatMessageLimit).setDefaultValue(100).setSaveConsumer(integer -> Config.chatMessageLimit = integer).build());

            //Eiki
            general.addEntry(entryBuilder.startBooleanToggle(Text.of("AutoChat Enabled"), Config.autochat).setDefaultValue(false).setSaveConsumer(val -> Config.autochat = val).build());
            general.addEntry(entryBuilder.startStrField(Text.of("AutoChat Message"), Config.antwort).setDefaultValue("Ich habe grade leider keine Zeit!").setSaveConsumer(String -> Config.antwort = String).build());
            return builder.build();
        }
    }
}
