package kr.squirrel.guild.guis.run;

import kr.squirrel.guild.Main;
import kr.squirrel.guild.guis.Container;
import kr.squirrel.guild.objects.LocationDTO;
import kr.squirrel.guild.objects.run.GuildRun;
import kr.squirrel.guild.objects.run.GuildRunUser;
import kr.squirrel.guild.objects.run.MatchingGuild;
import kr.squirrel.guild.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GuildRunMatchingContainer extends Container {

    public static Map<String, GuildRunMatchingContainer> data = new HashMap<>();
    public static Map<String, MatchingGuild> matchingGuildList = new HashMap<>();

    private static final ItemStack NONE_READY = ItemUtil.getItem(Material.RED_WOOL, "§f[ §c준비 안됨 §f]", null);
    private static final ItemStack READY = ItemUtil.getItem(Material.LIME_WOOL, "§f[ §c준비 완료 §f]", null);
    private static final int[] EMPTY_PLAYER_SLOT = new int[]{10, 12, 14, 16};
    private static final int[] PLAYER_READY_SLOT = new int[]{19, 21, 23, 25};

    private WaitingPlayer[] players = new WaitingPlayer[4];
    private String guild;

    public GuildRunMatchingContainer(String guild) {
        super(9 * 4, "[ 길드런 ]");
        for (int slot : PLAYER_READY_SLOT) {
            INVENTORY.setItem(slot, NONE_READY);
        }
        this.guild = guild;
    }

    @Override
    public void executeEvent(InventoryEvent inventoryEvent, EventType type) {
        switch (type) {
            case CLICK -> {
                InventoryClickEvent event = (InventoryClickEvent) inventoryEvent;
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                int slot = event.getRawSlot();
                for (int i = 0; i < 4; i++) {
                    if (slot != PLAYER_READY_SLOT[i]) {
                        continue;
                    }
                    if (!isPlayer(player.getUniqueId(), i)) {
                        continue;
                    }
                    players[i].swapReady();
                    if (checkAllReady()) {
                        match(true);
                    }
                    update();
                }
            }
            case OPEN -> {
                InventoryOpenEvent event = (InventoryOpenEvent) inventoryEvent;
                join((Player) event.getPlayer());
                update();
            }
            case CLOSE -> {
                InventoryCloseEvent event = (InventoryCloseEvent) inventoryEvent;
                quit((Player) event.getPlayer());
                update();
            }
        }
    }

    public boolean checkAllReady() {
        for (int i = 0; i < 4; i++) {
            if (players[i] == null) {
                return false;
            }
            if (!players[i].ready) {
                return false;
            }
        }
        return true;
    }

    public void match(boolean first) {
        if (first) {
            List<GuildRunUser> players = new ArrayList<>();
            for (WaitingPlayer waitingPlayer : this.players) {
                players.add(new GuildRunUser(waitingPlayer.uuid, LocationDTO.toLocationDTO(Bukkit.getPlayer(waitingPlayer.uuid).getLocation())));
            }
            matchingGuildList.put(this.guild, new MatchingGuild(this.guild, players));
        }
        if (checkAllReady()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!matchingGuildList.containsKey(guild)) {
                        return;
                    }
                    for (GuildRunUser waitingPlayer : matchingGuildList.get(guild).getPlayers()) {
                        Player player = Bukkit.getPlayer(waitingPlayer.getUUID());
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                        if (matchingGuildList.values().size() > 1) {
                            for (Map.Entry<String, MatchingGuild> lGuild : matchingGuildList.entrySet()) {
                                if (lGuild.getValue().getGuild().equals(guild)) {
                                    continue;
                                }
                                GuildRun.startGame(GuildRun.getAvailableMap(), matchingGuildList.get(guild), lGuild.getValue());
                                matchingGuildList.remove(guild);
                                matchingGuildList.remove(lGuild.getKey());

                                players = new WaitingPlayer[4];
                                return;
                            }
                        }
                    }
                    match(false);
                }
            }.runTaskLater(Main.getInstance(), 10);
        } else {
            matchingGuildList.remove(this.guild);
        }
    }

    public boolean isFull() {
        for (int i = 0; i < 4; i++) {
            if (players[i] == null) {
                return false;
            }
        }
        return true;
    }

    public void join(Player player) {
        for (int i = 0; i < 4; i++) {
            if (players[i] != null) {
                continue;
            }
            players[i] = new WaitingPlayer(player.getUniqueId());
            return;
        }
    }

    public void quit(Player player) {
        for (int i = 0; i < 4; i++) {
            if (players[i] == null) {
                continue;
            }
            if (!players[i].uuid.toString().equals(player.getUniqueId().toString())) {
                continue;
            }
            players[i] = null;
            return;
        }
    }

    public boolean isPlayer(UUID uuid, int index) {
        if (players[index] == null) {
            return false;
        }
        if (uuid.toString().equals(players[index].uuid.toString())) {
            return true;
        }
        return false;
    }

    public void update() {
        for (int i = 0; i < 4; i++) {
            if (players[i] == null) {
                INVENTORY.setItem(EMPTY_PLAYER_SLOT[i], null);
                INVENTORY.setItem(PLAYER_READY_SLOT[i], NONE_READY);
                continue;
            }
            Player player = Bukkit.getPlayer(players[i].uuid);
            ItemStack head = ItemUtil.getHeadItem(players[i].uuid.toString(), "§f[ " + player.getName() + " ]", null);
            INVENTORY.setItem(EMPTY_PLAYER_SLOT[i], head);
            INVENTORY.setItem(PLAYER_READY_SLOT[i], players[i].ready ? READY : NONE_READY);
        }
    }

    public static class WaitingPlayer {
        private UUID uuid;
        private boolean ready;

        public WaitingPlayer(UUID uuid) {
            this.uuid = uuid;
            this.ready = false;
        }

        public void swapReady() {
            this.ready = this.ready ? false : true;
        }
    }
}
