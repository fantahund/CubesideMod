package de.fanta.cubeside.command;

import de.fanta.cubeside.util.ChatSkullAPI.ChatSkull;
import de.fanta.cubeside.util.ChatUtils;
import de.iani.cubesideutils.commands.ArgsParser;
import de.iani.cubesideutils.fabric.commands.SubCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class AddSkullToLoreCommand extends SubCommand {
    @Override
    public boolean onCommand(FabricClientCommandSource sender, String alias, String commandString, ArgsParser args) {
        if (!args.hasNext()) {
            ChatUtils.sendErrorMessage("Du musst einen Spieler name angeben.");
            return true;
        }

        String name = args.getNext();
        ChatSkull.setItemLore(name);
        return true;
    }

    @Override
    public String getRequiredPermission() {
        return "cubeside.addskulltolore";
    }
}
