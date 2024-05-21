package de.fanta.cubeside;

import com.mojang.brigadier.CommandDispatcher;
import de.fanta.cubeside.command.AFKCheckCommand;
import de.fanta.cubeside.command.AddSkullToLoreCommand;
import de.iani.cubesideutils.fabric.commands.CommandRouter;
import de.iani.cubesideutils.fabric.commands.CommandUtil;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class Commands {
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        if (dispatcher == null) {
            System.out.println("Command Dispatcher is null");
            return;
        }

        CommandRouter skullRouter = new CommandRouter();
        skullRouter.addCommandMapping(new AddSkullToLoreCommand());
        CommandUtil.registerCommand(dispatcher, "addskulltolore", skullRouter);

        CommandRouter afkCheckRouter = new CommandRouter();
        afkCheckRouter.addCommandMapping(new AFKCheckCommand());
        CommandUtil.registerCommand(dispatcher, "afkcheck", afkCheckRouter);
    }

}
