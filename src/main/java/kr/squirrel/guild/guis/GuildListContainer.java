package kr.squirrel.guild.guis;

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

public class GuildListContainer extends Container {

    private static final ItemStack GLASS = ItemUtil.getItem(Material.WHITE_STAINED_GLASS_PANE, "§e", null);
    private static final ItemStack PREVIOUS_PAGE = ItemUtil.getItem(Material.PAPER, "§f[§7이전 페이지§f]", null);
    private static final ItemStack NEXT_PAGE = ItemUtil.getItem(Material.PAPER, "§f[§7다음 페이지§f]", null);
    private static final ItemStack PREVIOUS_SCREEN = ItemUtil.getItem(Material.PAPER, "§f[§c돌아가기§f]", null);
    private static final Set<Integer> GLASS_LOCATIONS = Set.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 46, 47, 48, 50, 51, 52);
    private static final int PREVIOUS_PAGE_SLOT = 45;
    private static final int NEXT_PAGE_SLOT = 53;
    private static final int PREVIOUS_SCREEN_SLOT = 49;

    private int page = 0;

    public GuildListContainer() {
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
        INVENTORY.setItem(PREVIOUS_SCREEN_SLOT, PREVIOUS_SCREEN);

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
                        "§7[ 클릭 시 가입 " + (guild.isFreeJoin() ? "" : "신청") + " ]"
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
                    case PREVIOUS_SCREEN_SLOT -> {
                        Container.open(player, new GuildMainContainer(player));
                    }
                }
            }
        }
    }
}
