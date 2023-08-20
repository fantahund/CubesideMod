package de.fanta.cubeside;

import de.fanta.cubeside.config.Configs;
import de.fanta.cubeside.util.ChatHudMethods;
import de.fanta.cubeside.util.ChatUtils;
import de.fanta.cubeside.util.SoundThread;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.Level;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Events {

    private boolean flyingLastTick = false;
    private Perspective lastmode = Perspective.FIRST_PERSON;
    private SoundEvent sound;

    private SoundThread soundThread;

    private boolean connect;

    private LocalDate lastTickDate;

    public Events() {
    }

    public void init() {
        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            if (Configs.Chat.SaveMessagesToDatabase.getBooleanValue() && !CubesideClientFabric.databaseinuse) {
                if (handler.getServerInfo() != null) {
                    String server = handler.getServerInfo().address.toLowerCase();
                    List<Text> messages = CubesideClientFabric.getDatabase().loadMessages(server);
                    List<String> commands = CubesideClientFabric.getDatabase().loadCommands(server);

                    if (!connect) {
                        CubesideClientFabric.setLoadingMessages(true);
                        messages.forEach(((ChatHudMethods) client.inGameHud.getChatHud())::addStoredChatMessage);
                        CubesideClientFabric.LOGGER.log(Level.INFO, (long) messages.size() + " messages loaded.");
                        commands.forEach(((ChatHudMethods) client.inGameHud.getChatHud())::addStoredCommand);
                        CubesideClientFabric.LOGGER.log(Level.INFO, (long) commands.size() + " commands loaded.");
                        CubesideClientFabric.setLoadingMessages(false);
                        connect = true;
                        CubesideClientFabric.messageQueue.forEach(text -> client.inGameHud.getChatHud().addMessage(text));
                        CubesideClientFabric.messageQueue.clear();
                    }
                }

                if (Configs.HitBox.KeepEntityHitBox.getBooleanValue()) {
                    client.getEntityRenderDispatcher().setRenderHitboxes(Configs.HitBox.ShowHitBox.getBooleanValue());
                }
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (Configs.Chat.SaveMessagesToDatabase.getBooleanValue()) {
                connect = false;
                CubesideClientFabric.setRank(null);
            }
            CubesideClientFabric.setChatInfo(null);
        });

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player != null) {
                if (Configs.Generic.ThirdPersonElytra.getBooleanValue()) {
                    if (mc.player.isFallFlying()) {
                        if (!flyingLastTick) {
                            flyingLastTick = true;
                            lastmode = mc.options.getPerspective();
                            mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
                        }
                    } else {
                        if (flyingLastTick) {
                            flyingLastTick = false;
                            mc.options.setPerspective(lastmode);
                        }
                    }
                }

                if (Configs.Generic.ElytraAlarm.getBooleanValue()) {
                    if (sound == null) {
                        Identifier location = new Identifier(CubesideClientFabric.MODID, "alarm");
                        sound = SoundEvent.of(location);
                    }
                    if (mc.player.isFallFlying() && mc.player.getY() <= mc.world.getBottomY()) {
                        if (soundThread == null) {
                            soundThread = SoundThread.of(1944, sound, mc.player);
                            soundThread.start();
                        }
                    } else if (soundThread != null && soundThread.isRunning()) {
                        soundThread.stopThread();
                        soundThread = null;
                    }
                }

                while (KeyBinds.AUTO_CHAT.wasPressed()) {
                    if (CubesideClientFabric.hasPermission("cubeside.autochat")) {
                        if (Configs.PermissionSettings.AutoChat.getBooleanValue()) {
                            Configs.PermissionSettings.AutoChat.setBooleanValue(false);
                            mc.player.sendMessage(Text.of("§cAuto Chat deaktiviert"), true);
                        } else {
                            Configs.PermissionSettings.AutoChat.setBooleanValue(true);
                            mc.player.sendMessage(Text.of("§aAuto Chat aktiviert"), true);
                        }
                        Configs.saveToFile();
                    } else {
                        ChatUtils.sendErrorMessage("AutoChat kannst du erst ab Staff benutzen!");
                    }
                }

                while (KeyBinds.TOGGLE_SHOW_ENTITIES_IN_SPECTATOR_MODE.wasPressed()) {
                    if (Configs.Generic.ShowInvisibleEntitiesInSpectator.getBooleanValue()) {
                        Configs.Generic.ShowInvisibleEntitiesInSpectator.setBooleanValue(false);
                        mc.player.sendMessage(Text.of("§aUnsichtbare Entities werden jetzt im Spectator nicht mehr angezeigt!"), true);
                    } else {
                        Configs.Generic.ShowInvisibleEntitiesInSpectator.setBooleanValue(true);
                        mc.player.sendMessage(Text.of("§aUnsichtbare Entities werden jetzt im Spectator wieder angezeigt!"), true);
                    }
                    Configs.saveToFile();
                }

                while (KeyBinds.SET_MINING_ASSISTANT_START_POINT.wasPressed()) {
                    MiningAssistent.setStartPos(MinecraftClient.getInstance().player.getBlockPos());
                }

                while (KeyBinds.TOGGLE_MINING_ASSISTANT.wasPressed()) {
                    Configs.MiningAssistent.MiningAssistentEnabled.setBooleanValue(!Configs.MiningAssistent.MiningAssistentEnabled.getBooleanValue());
                    Configs.saveToFile();
                    mc.player.sendMessage(Text.of("MiningAssistent set to: " + (Configs.MiningAssistent.MiningAssistentEnabled.getBooleanValue() ? "§atrue" : "§cfalse")), true);
                }

                while (KeyBinds.WOOD_STRIPING.wasPressed()) {
                    Configs.Generic.WoodStriping.setBooleanValue(!Configs.Generic.WoodStriping.getBooleanValue());
                    Configs.saveToFile();
                    mc.player.sendMessage(Text.of("WoodStriping set to: " + (Configs.Generic.WoodStriping.getBooleanValue() ? "§atrue" : "§cfalse")), true);
                }

                while (KeyBinds.CREATE_GRASS_PATH.wasPressed()) {
                    Configs.Generic.CreateGrassPath.setBooleanValue(!Configs.Generic.CreateGrassPath.getBooleanValue());
                    Configs.saveToFile();
                    mc.player.sendMessage(Text.of("CreateGrassPath set to: " + (Configs.Generic.CreateGrassPath.getBooleanValue() ? "§atrue" : "§cfalse")), true);
                }

                while (KeyBinds.EDIT_SIGN.wasPressed()) {
                    Configs.Generic.SignEdit.setBooleanValue(!Configs.Generic.SignEdit.getBooleanValue());
                    Configs.saveToFile();
                    mc.player.sendMessage(Text.of("SignEdit set to: " + (Configs.Generic.SignEdit.getBooleanValue() ? "§atrue" : "§cfalse")), true);
                }
            }

            if (Configs.Chat.SaveMessagesToDatabase.getBooleanValue()) {
                LocalDate date = LocalDate.now();
                if (lastTickDate != null) {
                    if (!lastTickDate.isEqual(date)) {
                        ChatUtils.sendNormalMessage("Neuer Tag: " + date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    }
                }
                lastTickDate = date;
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> CubesideClientFabric.commands.register(dispatcher));

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> MiningAssistent.render(context.matrixStack()));

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            ItemStack itemInHand = player.getStackInHand(hand);
            BlockPos blockPos = hitResult.getBlockPos();
            BlockState blockState = world.getBlockState(blockPos);

            if (!Configs.Generic.WoodStriping.getBooleanValue() && itemInHand.getItem() instanceof ToolItem && AxeItem.STRIPPED_BLOCKS.containsKey(blockState.getBlock())) {
                return ActionResult.FAIL;
            }

            if (!Configs.Generic.CreateGrassPath.getBooleanValue() && itemInHand.getItem() instanceof ShovelItem && ShovelItem.PATH_STATES.containsKey(blockState.getBlock())) {
                return ActionResult.FAIL;
            }

            if (!Configs.Generic.SignEdit.getBooleanValue() && blockState.getBlock() instanceof AbstractSignBlock) {
                return ActionResult.FAIL;
            }

            return ActionResult.PASS;
        });
    }
}
