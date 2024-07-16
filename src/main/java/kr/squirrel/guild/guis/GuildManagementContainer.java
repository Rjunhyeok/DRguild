package kr.squirrel.guild.guis;

import kr.squirrel.guild.objects.Guild;
import kr.squirrel.guild.objects.Rank;
import kr.squirrel.guild.utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GuildManagementContainer extends Container {
    private static final ItemStack GLASS = ItemUtil.getItem(Material.WHITE_STAINED_GLASS_PANE, "§e", null);
    private static final ItemStack MEMBER_MANAGEMENT = ItemUtil.getItem(Material.PLAYER_HEAD, "§f[§a길드원 관리§f]", null);
    private static final ItemStack APPLYING_MANAGEMENT = ItemUtil.getItem(Material.CHEST, "§f[§a길드 신청§f]", null);
    private static final ItemStack JOIN_STATUS_FREE = ItemUtil.getItem(Material.LIME_WOOL, "§f[§a자유 가입§f]", List.of("§7클릭 시 변경합니다"));
    private static final ItemStack JOIN_STATUS_APPLYING = ItemUtil.getItem(Material.RED_WOOL, "§f[§a선택 가입§f]", List.of("§7클릭 시 변경합니다"));
    private static final ItemStack BREAK_GUILD = ItemUtil.getItem(Material.REDSTONE_BLOCK, "§f[§c길드 해체§f]", null);
    private static final int MEMBER_MANAGEMENT_SLOT = 10;
    private static final int APPLYING_MANAGEMENT_SLOT = 12;
    private static final int JOIN_STATUS_SLOT = 14;
    private static final int BREAK_GUILD_SLOT = 16;

    public GuildManagementContainer(Player player) {
        super(9 * 3, "[ 길드 관리 ]");

        for (int i = 0; i < 27; i++) {
            INVENTORY.setItem(i, GLASS);
        }
        Guild guild = Guild.get(player);
        INVENTORY.setItem(MEMBER_MANAGEMENT_SLOT, MEMBER_MANAGEMENT);
        INVENTORY.setItem(APPLYING_MANAGEMENT_SLOT, APPLYING_MANAGEMENT);
        INVENTORY.setItem(JOIN_STATUS_SLOT, guild.isFreeJoin() ? JOIN_STATUS_FREE : JOIN_STATUS_APPLYING);
        INVENTORY.setItem(BREAK_GUILD_SLOT, BREAK_GUILD);
    }

    @Override
    public void executeEvent(InventoryEvent inventoryEvent, EventType type) {
        switch (type) {
            case CLICK -> {
                InventoryClickEvent event = (InventoryClickEvent) inventoryEvent;
                event.setCancelled(true);

                Player player = (Player) event.getWhoClicked();
                Guild guild = Guild.get(player);

                final int CLICKED_SLOT = event.getRawSlot();
                switch (CLICKED_SLOT) {
                    case MEMBER_MANAGEMENT_SLOT -> {
                        Container.open(player, new GuildMemberManagementContainer(player));
                    }
                    case APPLYING_MANAGEMENT_SLOT -> {
                        Container.open(player, new GuildApplyingMembersContainer(player));
                    }
                    case JOIN_STATUS_SLOT -> {
                        if (guild.isFreeJoin()) {
                            guild.setFreeJoin(false);
                        } else {
                            guild.setFreeJoin(true);
                        }
                        Container.open(player, new GuildManagementContainer(player));
                    }
                    case BREAK_GUILD_SLOT -> {
                        if (Guild.getRank(player) != Rank.MASTER) {
                            return;
                        }
                        Container.open(player, new GuildBreakConfirmContainer());
                    }
                }
            }
        }
    }
}
