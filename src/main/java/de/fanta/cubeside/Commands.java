package de.fanta.cubeside;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import de.fanta.cubeside.util.ChatSkullAPI.ChatSkull;
import de.fanta.cubeside.util.ChatUtil;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.GameMode;

import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class Commands {

    private static final Ordering<PlayerListEntry> ENTRY_ORDERING = Ordering.from((playerListEntry, playerListEntry2) -> {
        Team team = playerListEntry.getScoreboardTeam();
        Team team2 = playerListEntry2.getScoreboardTeam();
        return ComparisonChain.start().compareTrueFirst(playerListEntry.getGameMode() != GameMode.SPECTATOR, playerListEntry2.getGameMode() != GameMode.SPECTATOR).compare(team != null ? team.getName() : "", team2 != null ? team2.getName() : "").compare(playerListEntry.getProfile().getName(), playerListEntry2.getProfile().getName(), String::compareToIgnoreCase).result();
    });

    public void register() {

        ClientCommandManager.DISPATCHER.register(literal("addskulltolore")
                .then(
                        argument("player", string())
                                .executes(context -> {
                                    if (CubesideClient.getInstance().hasPermission("cubeside.addskulltolore")) {
                                        ChatSkull.setItemLore(getString(context, "player"));
                                    } else {
                                        ChatUtil.sendErrorMessage("Keine Rechte!");
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
                    ChatUtil.sendErrorMessage("Du musst ein Spielernamen eingeben!");
                    return 1;
                }));

    }

}
