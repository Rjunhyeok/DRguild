package kr.squirrel.guild.guis;

import kr.squirrel.guild.configurations.Config;
import kr.squirrel.guild.langs.Lang;
import kr.squirrel.guild.listeners.GuildListener;
import kr.squirrel.guild.objects.Guild;
import kr.squirrel.guild.objects.Rank;
import kr.squirrel.guild.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class GuildMainContainer extends Container {

    private static final ItemStack GLASS = ItemUtil.getItem(Material.WHITE_STAINED_GLASS_PANE, "§e", null);
    private static final ItemStack CREATE_GUILD = ItemUtil.getItem(
            Material.BOOK,
            "§f[§a길드 창설§f]",
            List.of("§7클릭 시 길드를 창설합니다")
    );
    private static final ItemStack GUILD_LIST = ItemUtil.getItem(
            Material.DEEPSLATE_GOLD_ORE,
            "§f[§a길드 목록§f]",
            null
    );
    private static final ItemStack GUILD_MANAGEMENT = ItemUtil.getItem(
            Material.DEEPSLATE_REDSTONE_ORE,
            "§f[§a길드 관리§f]",
            null
    );
    private static final ItemStack GUILD_CONTENTS = ItemUtil.getItem(
            Material.DEEPSLATE_EMERALD_ORE,
            "§f[§a길드 컨텐츠§f]",
            null
    );
    private static final ItemStack CHANGE_DESCRIPTION = ItemUtil.getItem(
            Material.BOOK,
            "§f[§a길드 소개 변경§f]",
            null
    );
    private static final ItemStack GUILD_QUIT = ItemUtil.getItem(
            Material.BARRIER,
            "§f[§c길드 탈퇴§f]",
            null
    );

    private static final int CREATE_GUILD_SLOT = 4;
    private static final int GUILD_INFO_JOIN_SLOT = 10;
    private static final int GUILD_LIST_SLOT = 12;
    private static final int GUILD_MANAGEMENT_SLOT = 14;
    private static final int GUILD_CONTENTS_SLOT = 16;
    private static final int CHANGE_DESCRIPTION_SLOT = 22;
    private static final int GUILD_QUIT_SLOT = 26;

    public GuildMainContainer(Player player) {
        super(9 * 3, "[ 길드 ]");
        for (int i = 0; i < 27; i++) {
            INVENTORY.setItem(i, GLASS);
        }
        final ItemStack GUILD_INFO_JOIN;
        if (Guild.hasGuild(player)) {
            Guild guild = Guild.get(player);
            GUILD_INFO_JOIN = ItemUtil.getItem(
                    Material.DEEPSLATE_IRON_ORE,
                    guild.getName(),
                    List.of(
                            "§7[ 길드 마스터 : " + Bukkit.getOfflinePlayer(UUID.fromString(guild.getMaster())).getName() + " ]",
                            "§7[ 길드 레벨 : " + guild.getLevel() + " ]",
                            "§7[ 경험치 : " + guild.getExp() + " / " + Config.EXP.get(guild.getLevel()) + " ]",
                            "§7[ 길드 인원수 : " + guild.getMemberCount() + " / " + guild.getMaxPlayer() + " ]",
                            "§7[ 길드 포인트 : " + guild.getPoint() + " ]",
                            "§7[ 길드 소개 : " + guild.getDescription() + " ]"
                    )
            );
            if (Guild.getRank(player) == Rank.MASTER) {
                INVENTORY.setItem(CHANGE_DESCRIPTION_SLOT, CHANGE_DESCRIPTION);
            } else {
                INVENTORY.setItem(GUILD_QUIT_SLOT, GUILD_QUIT);
            }
        } else {
            GUILD_INFO_JOIN = ItemUtil.getItem(
                    Material.DEEPSLATE_IRON_ORE,
                    "§f[§a길드 가입§f]",
                    null
            );
        }
        INVENTORY.setItem(CREATE_GUILD_SLOT, CREATE_GUILD);
        INVENTORY.setItem(GUILD_INFO_JOIN_SLOT, GUILD_INFO_JOIN);
        INVENTORY.setItem(GUILD_LIST_SLOT, GUILD_LIST);
        INVENTORY.setItem(GUILD_MANAGEMENT_SLOT, GUILD_MANAGEMENT);
        INVENTORY.setItem(GUILD_CONTENTS_SLOT, GUILD_CONTENTS);
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
                    case CREATE_GUILD_SLOT -> {
                        if (Guild.hasGuild(player)) {
                            return;
                        }
                        GuildListener.nameSettings.put(player.getUniqueId().toString(), 1);
                        player.closeInventory();
                        Lang.send(player, Lang.CHAT_GUILD_NAME);
                    }
                    case GUILD_INFO_JOIN_SLOT -> {
                        Container container;
                        if (Guild.hasGuild(player)) {
                            container = new GuildMemberInfoContainer(player);
                        } else {
                            container = new GuildApplyingListContainer();
                        }
                        Container.open(player, container);
                    }
                    case GUILD_LIST_SLOT -> {
                        Container.open(player, new GuildListContainer());
                    }
                    case GUILD_MANAGEMENT_SLOT -> {
                        if (!Guild.hasGuild(player)) {
                            Lang.send(player, Lang.REQUIRE_GUILD);
                            return;
                        }
                        if (Guild.getRank(player) == Rank.MEMBER) {
                            Lang.send(player, Lang.IS_MEMBER);
                            return;
                        }
                        Container.open(player, new GuildManagementContainer(player));
                    }
                    case CHANGE_DESCRIPTION_SLOT -> {
                        if (!Guild.hasGuild(player)) {
                            return;
                        }
                        if (Guild.getRank(player) != Rank.MASTER) {
                            return;
                        }
                        GuildListener.nameSettings.put(player.getUniqueId().toString(), 3);
                        Lang.send(player, Lang.TYPE_TO_SET);
                        player.closeInventory();
                    }
                    case GUILD_CONTENTS_SLOT -> {
                        if (!Guild.hasGuild(player)) {
                            Lang.send(player, Lang.REQUIRE_GUILD);
                            return;
                        }
                        Container.open(player, new GuildContentsContainer(player));
                    }
                    case GUILD_QUIT_SLOT -> {
                        if (!Guild.hasGuild(player)) {
                            return;
                        }
                        if (Guild.getRank(player) == Rank.MASTER) {
                            return;
                        }
                        Guild.quit(player);
                        Lang.send(player, Lang.GUILD_QUIT);
                        player.closeInventory();
                    }
                }
            }
        }
    }
}
