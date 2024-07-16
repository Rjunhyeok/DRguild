package kr.squirrel.guild.guis.shop;

import kr.squirrel.guild.guis.Container;
import kr.squirrel.guild.objects.shop.GuildShop;
import kr.squirrel.guild.utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GuildShopManagementContainer extends Container {

    private static final ItemStack GLASS = ItemUtil.getItem(Material.WHITE_STAINED_GLASS_PANE, "§e", null);
    private static final int[] GLASS_SLOTS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};

    public GuildShopManagementContainer() {
        super(9 * 6, "[ 길드 상점 ]");
        List<GuildShop.ShopContent> contents = GuildShop.getContents();
        for (int slot : GLASS_SLOTS) {
            INVENTORY.setItem(slot, GLASS);
        }
        int index = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 7; j++) {
                int slot = 10 + (i * 9) + j;
                if (contents.size() == index) {
                    break;
                }
                ItemStack itemStack = contents.get(index).getItemStack().clone();
                ItemMeta itemMeta = itemStack.getItemMeta();
                List<String> lore = new ArrayList<>();
                if (itemMeta.hasLore()) {
                    lore.addAll(itemMeta.getLore());
                }
                lore.add("§7가격 : " + contents.get(index).getPrice() + " Point");
                lore.add("");
                lore.add("§7쉬프트 좌클릭시 삭제");
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
                INVENTORY.setItem(slot, itemStack);
                index++;
            }
        }
    }

    @Override
    public void executeEvent(InventoryEvent inventoryEvent, EventType type) {
        switch (type) {
            case CLICK -> {
                InventoryClickEvent event = (InventoryClickEvent) inventoryEvent;
                event.setCancelled(true);
                int slot = event.getRawSlot();
                if (slot >= 10 && slot <= 43 && slot % 9 != 0 && slot % 9 != 8) {
                    int line = slot / 9;
                    int i = slot - line * 9 - 1;
                    int index = (line - 1) * 9 + i;
                    ItemStack itemStack = event.getCurrentItem();
                    if (itemStack == null) {
                        return;
                    }
                    if (event.getClick() == ClickType.SHIFT_LEFT) {
                        GuildShop.getContents().remove(index);
                        GuildShop.save();
                        Container.open((Player) event.getWhoClicked(), new GuildShopManagementContainer());
                    }
                }
            }
        }
    }
}
