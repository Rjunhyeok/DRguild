package kr.squirrel.guild.utils;

import org.bukkit.entity.Player;

public class TextUtil {

    private static String PREFIX = "§f[§aGUILD§f] ";

    public static void t(Player player, String msg) {
        player.sendMessage(PREFIX + msg);
    }

}
