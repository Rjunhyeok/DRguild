package kr.squirrel.guild.guis;

import kr.squirrel.guild.objects.Guild;
import kr.squirrel.guild.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class GuildApplyingMembersContainer extends Container {

    public GuildApplyingMembersContainer(Player player) {
        super(9 * 6, "[ 길드 신청 ]");
        Guild guild = Guild.get(player);
        List<String> applyingPlayers = guild.getApplyingPlayers();
        for (int i = 0; i < applyingPlayers.size(); i++) {
            INVENTORY.setItem(i, ItemUtil.getHeadItem(applyingPlayers.get(i), Bukkit.getOfflinePlayer(UUID.fromString(applyingPlayers.get(i))).getName(), List.of("§7쉬프트 좌클릭 시 가입을 승인합니다", "§7쉬프트 우클릭 시 가입을 거절합니다")));
        }
    }

    @Override
    public void executeEvent(InventoryEvent inventoryEvent, EventType type) {
        switch (type) {
            case CLICK -> {
                InventoryClickEvent event = (InventoryClickEvent) inventoryEvent;
                event.setCancelled(true);

                Player player = (Player) event.getWhoClicked();

                final ItemStack CLICKED_ITEM = event.getCurrentItem();
                final int CLICKED_SLOT = event.getRawSlot();
                if (CLICKED_SLOT >= 54) {
                    return;
                }
                if (CLICKED_ITEM == null) {
                    return;
                }
                Guild guild = Guild.get(player);
                switch (event.getClick()) {
                    case SHIFT_LEFT -> {
                        guild.join(Bukkit.getOfflinePlayer(CLICKED_ITEM.getItemMeta().getDisplayName()));
                    }
                    case SHIFT_RIGHT -> {
                        guild.getApplyingPlayers().remove(CLICKED_SLOT);
                    }
                }
                Container.open(player, new GuildApplyingMembersContainer(player));
            }
        }
    }
}
