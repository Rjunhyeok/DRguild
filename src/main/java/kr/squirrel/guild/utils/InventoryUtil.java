package kr.squirrel.guild.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {

    public static int getSpace(Player player) {
        int count = 0;
        for (ItemStack itemStack : player.getInventory().getStorageContents()) {
            if (itemStack == null) {
                count++;
            }
        }
        return count;
    }

}
