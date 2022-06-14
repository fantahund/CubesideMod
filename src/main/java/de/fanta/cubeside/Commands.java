package de.fanta.cubeside;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.brigadier.CommandDispatcher;
import de.fanta.cubeside.util.ChatSkullAPI.ChatSkull;
import de.fanta.cubeside.util.ChatUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.GameMode;

import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class Commands {

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
                .then(
                        argument("player", string())
                                .executes(context -> {
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

    }

}
