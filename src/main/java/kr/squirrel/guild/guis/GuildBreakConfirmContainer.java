package kr.squirrel.guild.guis;

import kr.squirrel.guild.objects.Guild;
import kr.squirrel.guild.objects.Rank;
import kr.squirrel.guild.utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;

public class GuildBreakConfirmContainer extends Container {

    private static final ItemStack CONFIRM = ItemUtil.getItem(Material.LIME_WOOL, "§f[ §a해체 §f]", null);
    private static final ItemStack REJECT = ItemUtil.getItem(Material.RED_WOOL, "§f[ §c취소 §f]", null);
    private static final int CONFIRM_SLOT = 10;
    private static final int REJECT_SLOT = 16;

    public GuildBreakConfirmContainer() {
        super(9 * 3, "[ 길드 해체 ]");
        INVENTORY.setItem(CONFIRM_SLOT, CONFIRM);
        INVENTORY.setItem(REJECT_SLOT, REJECT);
    }

    @Override
    public void executeEvent(InventoryEvent inventoryEvent, EventType type) {
        switch (type) {
            case CLICK -> {
                InventoryClickEvent event = (InventoryClickEvent) inventoryEvent;
                event.setCancelled(true);

                Player player = (Player) event.getWhoClicked();
                Guild guild = Guild.get(player);
                Rank rank = Guild.getRank(player);
                if (rank != Rank.MASTER) {
                    return;
                }
                switch (event.getRawSlot()) {
                    case CONFIRM_SLOT -> {
                        player.closeInventory();
                        guild.breakGuild();
                    }
                    case REJECT_SLOT -> {
                        player.closeInventory();
                    }
                }
            }
        }
    }
}
