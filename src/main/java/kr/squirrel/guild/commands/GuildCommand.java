package kr.squirrel.guild.commands;

import kr.squirrel.guild.guis.Container;
import kr.squirrel.guild.guis.GuildMainContainer;
import kr.squirrel.guild.guis.management.GuildAdminManagementContainer;
import kr.squirrel.guild.libraries.SimpleCommandBuilder;
import org.bukkit.entity.Player;

public class GuildCommand {

    public static void register() {
        new SimpleCommandBuilder("길드")
                .aliases("guild")
                .commandExecutor((sender, command, label, args) -> {
                    if (sender instanceof Player player) {
                        if (!player.isOp()) {
                            Container.open(player, new GuildMainContainer(player));
                            return false;
                        }
                        if (args.length == 0) {
                            Container.open(player, new GuildMainContainer(player));
                            return false;
                        }
                        if (args[0].equals("매니져")) {
                            Container.open(player, new GuildAdminManagementContainer());
                            return false;
                        }
                    }
                    return false;
                })
                .tabCompleter((sender, command, label, args) -> null)
                .register();
    }

}
