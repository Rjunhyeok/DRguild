package kr.squirrel.guild.guis.management;

import kr.squirrel.guild.guis.Container;
import kr.squirrel.guild.langs.Lang;
import kr.squirrel.guild.listeners.GuildListener;
import kr.squirrel.guild.objects.Guild;
import kr.squirrel.guild.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GuildAdminManagementContainer extends Container {

    private static final ItemStack GLASS = ItemUtil.getItem(Material.WHITE_STAINED_GLASS_PANE, "§e", null);
    private static final ItemStack PREVIOUS_PAGE = ItemUtil.getItem(Material.PAPER, "§f[§7이전 페이지§f]", null);
    private static final ItemStack NEXT_PAGE = ItemUtil.getItem(Material.PAPER, "§f[§7다음 페이지§f]", null);
    private static final ItemStack PREVIOUS_SCREEN = ItemUtil.getItem(Material.PAPER, "§f[§c돌아가기§f]", null);
    private static final Set<Integer> GLASS_LOCATIONS = Set.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 46, 47, 48, 50, 51, 52);
    private static final int PREVIOUS_PAGE_SLOT = 45;
    private static final int NEXT_PAGE_SLOT = 53;

    private int page = 0;

    public GuildAdminManagementContainer() {
        super(9 * 6, "[ 길드 목록 ]");
        setGUI();
    }

    private void setGUI() {
        INVENTORY.clear();
        for (int slot : GLASS_LOCATIONS) {
            INVENTORY.setItem(slot, GLASS);
        }
        INVENTORY.setItem(PREVIOUS_PAGE_SLOT, PREVIOUS_PAGE);
        INVENTORY.setItem(NEXT_PAGE_SLOT, NEXT_PAGE);

        List<Guild> guilds = Guild.getAll();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 7; j++) {
                int index = (i * 7 + j) + page * 28;
                if (guilds.size() - 1 < index) {
                    return;
                }
                int slot = 10 + (i * 9) + j;
                INVENTORY.setItem(slot, getGuildItem(guilds.get(index)));
            }
        }
    }

    private ItemStack getGuildItem(Guild guild) {
        return ItemUtil.getItem(
                Material.ENCHANTED_BOOK,
                guild.getName(),
                List.of(
                        "§7[ 길드 마스터 : " + Bukkit.getOfflinePlayer(UUID.fromString(guild.getMaster())).getName() + " ]",
                        "§7[ 길드 레벨 : " + guild.getLevel() + " ]",
                        "§7[ 길드 인원수 : " + guild.getMemberCount() + " / " + guild.getMaxPlayer() + " ]",
                        "§7[ 길드 가입 조건 : " + (guild.isFreeJoin() ? "자유 가입" : "승인 가입") + " ]",
                        "§7[ 길드 포인트 : " + guild.getPoint() + " ]",
                        "",
                        "§7좌클릭 시 길드원 목록 확인",
                        "§7쉬프트 좌클릭 시 길드 소개 강제 변경",
                        "§7쉬프트 우클릭 시 길드 강제 해체"
                )
        );
    }

    @Override
    public void executeEvent(InventoryEvent inventoryEvent, EventType type) {
        switch (type) {
            case CLICK -> {
                InventoryClickEvent event = (InventoryClickEvent) inventoryEvent;
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                final int CLICKED_SLOT = event.getRawSlot();
                if (CLICKED_SLOT >= 54) {
                    return;
                }
                final ItemStack CLICKED_ITEM = event.getCurrentItem();
                if (CLICKED_ITEM != null && CLICKED_ITEM.getType() == Material.ENCHANTED_BOOK) {
                    String guildName = CLICKED_ITEM.getItemMeta().getDisplayName();
                    Guild guild = Guild.get(guildName);
                    switch (event.getClick()) {
                        case LEFT -> {
                            Container.open(player, new GuildAdminMemberContainer(guildName));
                        }
                        case SHIFT_LEFT -> {
                            GuildListener.nameSettings.put(player.getUniqueId().toString(), 3);
                            Lang.send(player, Lang.TYPE_TO_SET);
                            player.closeInventory();
                        }
                        case SHIFT_RIGHT -> {
                            Container.open(player, new GuildAdminBreakConfirmContainer(guildName));
                        }
                    }
                    return;
                }
                switch (CLICKED_SLOT) {
                    case PREVIOUS_PAGE_SLOT -> {
                        if (page == 0) {
                            return;
                        }
                        page--;
                        setGUI();
                    }
                    case NEXT_PAGE_SLOT -> {
                        int count = Guild.getAll().size();
                        if (count > page * 28) {
                            page++;
                            setGUI();
                        }
                        return;
                    }
                }
            }
        }
    }
}
