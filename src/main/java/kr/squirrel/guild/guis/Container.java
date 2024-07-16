package kr.squirrel.guild.guis;

import kr.squirrel.guild.libraries.SimpleInventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class Container implements SimpleInventoryHolder {

    public static void open(Player player, Container container) {
        player.openInventory(container.getInventory());
    }

    protected final Inventory INVENTORY;

    public Container(int size, String title) {
        this.INVENTORY = Bukkit.createInventory(this, size, title);
    }

    @Override
    public Inventory getInventory() {
        return INVENTORY;
    }
}
