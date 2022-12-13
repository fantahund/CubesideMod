package de.fanta.cubeside;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.brigadier.CommandDispatcher;
import de.fanta.cubeside.config.Configs;
import de.fanta.cubeside.util.ChatSkullAPI.ChatSkull;
import de.fanta.cubeside.util.ChatUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class Commands {

    private static boolean teleport = false;
    private static final ArrayList<String> playerList = new ArrayList<>();

    private static final Ordering<PlayerListEntry> ENTRY_ORDERING = Ordering.from((playerListEntry, playerListEntry2) -> {
        Team team = playerListEntry.getScoreboardTeam();
        Team team2 = playerListEntry2.getScoreboardTeam();
        return ComparisonChain.start().compareTrueFirst(playerListEntry.getGameMode() != GameMode.SPECTATOR, playerListEntry2.getGameMode() != GameMode.SPECTATOR).compare(team != null ? team.getName() : "", team2 != null ? team2.getName() : "").compare(playerListEntry.getProfile().getName(), playerListEntry2.getProfile().getName(), String::compareToIgnoreCase).result();
    });

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {

        if (dispatcher == null) {
            System.out.println("Command Dispatcher is null");
            return;
        }

        dispatcher.register(literal("addskulltolore")
                .then(argument("player", string()).executes(context -> {
                                    if (CubesideClientFabric.hasPermission("cubeside.addskulltolore")) {
                                        ChatSkull.setItemLore(getString(context, "player"));
                                    } else {
                                        ChatUtils.sendErrorMessage("Keine Rechte!");
                                    }
                                    return 1;
                                })
                                .suggests((context, builder) -> {
                                    ClientPlayNetworkHandler clientPlayNetworkHandler = context.getSource().getPlayer().networkHandler;
                                    List<PlayerListEntry> list = ENTRY_ORDERING.sortedCopy(clientPlayNetworkHandler.getPlayerList());
                                    for (PlayerListEntry playerListEntry : list) {
                                        builder.suggest(playerListEntry.getProfile().getName());
                                    }
                                    return builder.buildFuture();
                                })
                )
                .executes(context -> {
                    ChatUtils.sendErrorMessage("Du musst ein Spielernamen eingeben!");
                    return 1;
                }));

        dispatcher.register(literal("afkcheck")
                .then(argument("status", string()).executes(context -> {
                    if (!CubesideClientFabric.hasPermission("cubeside.afkcheck")) {
                        ChatUtils.sendErrorMessage("Keine Berechtigung!");
                        return 1;
                    }

                    String status = getString(context, "status");
                    switch (status) {
                        case "start":
                            if (teleport) {
                                ChatUtils.sendErrorMessage("AFK Check bereits aktiv.");
                                return 1;
                            }
                            ClientPlayNetworkHandler clientPlayNetworkHandler = context.getSource().getPlayer().networkHandler;
                            List<PlayerListEntry> list = ENTRY_ORDERING.sortedCopy(clientPlayNetworkHandler.getPlayerList());
                            for (PlayerListEntry playerListEntry : list) {
                                if (playerListEntry != null) {
                                    String playername = playerListEntry.getProfile().getName();
                                    if (!Objects.equals(playername, MinecraftClient.getInstance().player.getName().getString()) && !Configs.PermissionSettings.AdminList.getStrings().contains(playername)) {
                                        playerList.add(playername);
                                    }
                                }
                            }

                            if (playerList.isEmpty()) {
                                ChatUtils.sendErrorMessage("Es ist kein Spieler online");
                                break;
                            }

                            teleport = true;
                            String teleportPlayer = playerList.get(0);
                            MinecraftClient.getInstance().player.networkHandler.sendCommand("tt p " + teleportPlayer);
                            ChatUtils.sendNormalMessage("Du wurdest zu " + teleportPlayer + " teleportiert.");
                            playerList.remove(teleportPlayer);
                            break;
                        case "next":
                            if (teleport) {
                                if (!playerList.isEmpty()) {
                                    String portPlayer = playerList.get(0);
                                    MinecraftClient.getInstance().player.networkHandler.sendCommand("tt p " + portPlayer);
                                    ChatUtils.sendNormalMessage("Du wurdest zu " + portPlayer + " teleportiert.");
                                    playerList.remove(portPlayer);
                                } else {
                                    ChatUtils.sendNormalMessage("Du hast dich zu allen Spielern Teleportiert.");
                                    teleport = false;
                                }
                            } else {
                                ChatUtils.sendErrorMessage("AFKCheck wurde nicht gestartet!");
                            }
                            break;
                        case "stop":
                            if (teleport) {
                                playerList.clear();
                                teleport = false;
                                ChatUtils.sendNormalMessage("AFK Check gestoppt.");
                            } else {
                                ChatUtils.sendErrorMessage("Aktuell ist kein AFK Check gestartet!");
                            }
                            break;
                        default:
                            ChatUtils.sendErrorMessage("/afkcheck [start|next|stop]");
                    }
                    return 1;
                }).suggests(((context, builder) -> {
                    builder.suggest("start");
                    builder.suggest("next");
                    builder.suggest("stop");
                    return builder.buildFuture();
                }))));
    }

}
