package kr.squirrel.guild.guis;

import kr.squirrel.guild.objects.Guild;
import kr.squirrel.guild.objects.Rank;
import kr.squirrel.guild.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class GuildMemberManagementContainer extends Container {
    private static final ItemStack GLASS = ItemUtil.getItem(Material.WHITE_STAINED_GLASS_PANE, "§e", null);
    private static final ItemStack PREVIOUS_SCREEN = ItemUtil.getItem(Material.PAPER, "§f[§c돌아가기§f]", null);
    private static final Set<Integer> GLASS_LOCATIONS = Set.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 50, 51, 52, 53);
    private static final int PREVIOUS_SCREEN_SLOT = 49;

    public GuildMemberManagementContainer(Player player) {
        super(9 * 6, "[ 길드 멤버 관리 ]");
        for (int slot : GLASS_LOCATIONS) {
            INVENTORY.setItem(slot, GLASS);
        }
        INVENTORY.setItem(PREVIOUS_SCREEN_SLOT, PREVIOUS_SCREEN);
        Guild guild = Guild.get(player);
        INVENTORY.setItem(10, guild.getMemberInfoInManagement(Rank.MASTER, guild.getMaster()));
        int slot = 11;
        for (String uuid : guild.getMembers().getOrDefault(Rank.SUB_MASTER, new ArrayList<>())) {
            INVENTORY.setItem(slot, guild.getMemberInfoInManagement(Rank.SUB_MASTER, uuid));
            slot++;
            slot = set(slot);
        }
        for (String uuid : guild.getMembers().getOrDefault(Rank.MEMBER, new ArrayList<>())) {
            INVENTORY.setItem(slot, guild.getMemberInfoInManagement(Rank.MEMBER, uuid));
            slot++;
            slot = set(slot);
        }
    }

    private int set(int slot) {
        switch (slot) {
            case 18 -> {
                return 20;
            }
            case 27 -> {
                slot = 29;
            }
            case 36 -> {
                slot = 38;
            }
        }
        return slot;
    }

    @Override
    public void executeEvent(InventoryEvent inventoryEvent, EventType type) {
        switch (type) {
            case CLICK -> {
                InventoryClickEvent event = (InventoryClickEvent) inventoryEvent;
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                Guild guild = Guild.get(player);
                final ItemStack CLICKED_ITEM = event.getCurrentItem();
                if (CLICKED_ITEM != null && CLICKED_ITEM.getType() == Material.PLAYER_HEAD) {
                    String uuid = Bukkit.getOfflinePlayer(CLICKED_ITEM.getItemMeta().getDisplayName()).getUniqueId().toString();
                    ClickType clickType = event.getClick();
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    Rank rank = Guild.getRank(offlinePlayer);
                    if (offlinePlayer.getUniqueId().toString().equals(player.getUniqueId().toString())) {
                        return;
                    }
                    switch (clickType) {
                        case LEFT -> {
                            if (Guild.getRank(player) == Rank.MASTER) {
                                guild.setStatus(Rank.MEMBER, uuid);
                            }
                        }
                        case RIGHT -> {
                            if (Guild.getRank(player) == Rank.MASTER) {
                                Container.open(player, new GuildExpelConfirmContainer(uuid, rank));
                                return;
                            } else if (rank == Rank.SUB_MASTER) {
                                return;
                            } else {
                                Container.open(player, new GuildExpelConfirmContainer(uuid, rank));
                            }
                        }
                        case SHIFT_LEFT -> {
                            if (rank == Rank.MASTER || Guild.getRank(player) == Rank.MEMBER) {
                                return;
                            }
                            guild.setStatus(Rank.SUB_MASTER, uuid);
                        }
                        case SHIFT_RIGHT -> {
                            if (Guild.getRank(player) != Rank.MASTER) {
                                guild.setStatus(Rank.MASTER, uuid);
                                guild.setStatus(Rank.SUB_MASTER, player.getUniqueId().toString());
                            }
                        }
                    }
                    Container.open(player, new GuildManagementContainer(player));
                }

                final int CLICKED_SLOT = event.getRawSlot();
                switch (CLICKED_SLOT) {
                    case PREVIOUS_SCREEN_SLOT -> {
                        Container.open(player, new GuildManagementContainer(player));
                    }
                }
            }
        }
    }
}
