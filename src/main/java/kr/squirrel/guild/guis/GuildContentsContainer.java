package kr.squirrel.guild.guis;

import kr.squirrel.guild.commands.run.GuildRunCommand;
import kr.squirrel.guild.configurations.GuildAttendanceConfig;
import kr.squirrel.guild.guis.quest.GuildQuestContainer;
import kr.squirrel.guild.guis.run.GuildRunMatchingContainer;
import kr.squirrel.guild.guis.shop.GuildShopContainer;
import kr.squirrel.guild.langs.GuildAttendanceLang;
import kr.squirrel.guild.langs.GuildRunLang;
import kr.squirrel.guild.objects.Guild;
import kr.squirrel.guild.objects.Rank;
import kr.squirrel.guild.objects.run.GuildRun;
import kr.squirrel.guild.systems.PlayTime;
import kr.squirrel.guild.utils.InventoryUtil;
import kr.squirrel.guild.utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GuildContentsContainer extends Container {

    private static final ItemStack GUILD_QUEST = ItemUtil.getItem(Material.PAPER, "§f[ §a길드 퀘스트 §f]", null);
    private static final ItemStack GUILD_RUN = ItemUtil.getItem(Material.PAPER, "§f[ §a길드런 §f]", null);
    private static final ItemStack GUILD_SHOP = ItemUtil.getItem(Material.BARRIER, "§f[ §a길드 상점 §f]", null);
    private static final int GUILD_ATTENDANCE_SLOT = 10;
    private static final int GUILD_QUEST_SLOT = 12;
    private static final int GUILD_RUN_SLOT = 14;
    private static final int GUILD_SHOP_SLOT = 16;
    private static final int[] REWARD_LIST = new int[]{1, 3, 5, 10, 20};

    public GuildContentsContainer(Player player) {
        super(9 * 3, "[ 길드 컨텐츠 ]");
        Guild guild = Guild.get(player);
        List<String> attendance = guild.getAttendance().getOrDefault(LocalDate.now().toString(), new ArrayList<>());
        List<Integer> attendanceReward = guild.getAttendanceReward().getOrDefault(LocalDate.now().toString(), new ArrayList<>());
        long time = PlayTime.get(player);
        long hour = time / (60 * 60);
        long minute = time / 60 - (hour * 60);
        long second = time % 60;
        INVENTORY.setItem(GUILD_ATTENDANCE_SLOT, ItemUtil.getItem(Material.PAPER, "§f[ §a길드 출석 §f]",
                        List.of(
                                "§7오늘의 플레이타임 : " + hour + "시간 " + minute + "분 " + second + "초",
                                "§7오늘의 누적 길드 출석 횟수 : " + attendance.size(),
                                attendance.contains(player.getUniqueId().toString()) ? "§a(출석 완료)" : "§c(출석 미완료)",
                                "",
                                "§71회 출석보상 : 수령 " + (attendanceReward.contains(1) ? "완료" : "미완료"),
                                "§73회 출석보상 : 수령 " + (attendanceReward.contains(3) ? "완료" : "미완료"),
                                "§75회 출석보상 : 수령 " + (attendanceReward.contains(5) ? "완료" : "미완료"),
                                "§710회 출석보상 : 수령 " + (attendanceReward.contains(10) ? "완료" : "미완료"),
                                "§720회 출석보상 : 수령 " + (attendanceReward.contains(20) ? "완료" : "미완료"),
                                "§7쉬프트 + 좌클릭 시 출석 보상을 모두 수령합니다 §c(길드 마스터만 가능)")
                )

        );
        INVENTORY.setItem(GUILD_QUEST_SLOT, GUILD_QUEST);
        INVENTORY.setItem(GUILD_RUN_SLOT, GUILD_RUN);
        INVENTORY.setItem(GUILD_SHOP_SLOT, GUILD_SHOP);
    }

    @Override
    public void executeEvent(InventoryEvent inventoryEvent, EventType type) {
        switch (type) {
            case CLICK -> {
                InventoryClickEvent event = (InventoryClickEvent) inventoryEvent;
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                Guild guild = Guild.get(player);
                switch (event.getRawSlot()) {
                    case GUILD_ATTENDANCE_SLOT -> {
                        switch (event.getClick()) {
                            case LEFT -> {
                                List<String> attendance = guild.getAttendance().getOrDefault(LocalDate.now().toString(), new ArrayList<>());
                                if (attendance.contains(player.getUniqueId().toString())) {
                                    return;
                                }
                                if (PlayTime.get(player) < 60 * 60) {
                                    GuildAttendanceLang.send(player, GuildAttendanceLang.REQUIRE_ENOUGH_PLAYTIME);
                                    return;
                                }
                                attendance.add(player.getUniqueId().toString());
                                guild.getContribution().put(player.getUniqueId().toString(), guild.getContribution().getOrDefault(player.getUniqueId().toString(), 0l) + GuildAttendanceConfig.ATTENDANCE_CONTRIBUTION);
                                guild.getAttendance().put(LocalDate.now().toString(), attendance);
                                guild.setPoint(guild.getPoint() + GuildAttendanceConfig.ATTENDANCE_CONTRIBUTION);
                                guild.write();
                                Container.open(player, new GuildContentsContainer(player));
                                return;
                            }
                            case SHIFT_LEFT -> {
                                Rank rank = Guild.getRank(player);
                                if (rank != Rank.MASTER) {
                                    return;
                                }
                                List<Integer> rewardList = new ArrayList<>();
                                List<GuildAttendanceConfig.Reward> rewardContentsList = new ArrayList<>();
                                List<String> attendance = guild.getAttendance().getOrDefault(LocalDate.now().toString(), new ArrayList<>());
                                List<Integer> attendanceReward = guild.getAttendanceReward().getOrDefault(LocalDate.now().toString(), new ArrayList<>());
                                int size = attendance.size();
                                for (int list : REWARD_LIST) {
                                    if (size >= list) {
                                        if (!attendanceReward.contains(list)) {
                                            rewardList.add(list);
                                            rewardContentsList.addAll(GuildAttendanceConfig.rewards.get(list));
                                        }
                                    }
                                }
                                if (InventoryUtil.getSpace(player) < rewardContentsList.size()) {
                                    GuildAttendanceLang.send(player, GuildAttendanceLang.REQUIRE_EMPTY_SPACE, s -> s.replaceAll("<space>", rewardContentsList.size() + ""));
                                    return;
                                }
                                for (GuildAttendanceConfig.Reward reward : rewardContentsList) {
                                    player.getInventory().addItem(reward.getItem());
                                }
                                attendanceReward.addAll(rewardList);
                                guild.getAttendanceReward().put(LocalDate.now().toString(), attendanceReward);
                                guild.write();
                                Container.open(player, new GuildContentsContainer(player));
                                return;
                            }
                        }
                    }
                    case GUILD_QUEST_SLOT -> {
                        Container.open(player, new GuildQuestContainer());
                    }
                    case GUILD_RUN_SLOT -> {
                        if (!GuildRunCommand.guildRun) {
                            player.sendMessage("§c현재 길드런 이용 가능 시간이 아닙니다");
                            player.closeInventory();
                            return;
                        }
                        if (GuildRun.getAvailableMap() == null) {
                            GuildRunLang.send(player, GuildRunLang.NONE_SPACE);
                            return;
                        }
                        String guildName = guild.getName();
                        if (GuildRun.isPlaying(guildName)) {
                            GuildRunLang.send(player, GuildRunLang.ALREADY_PLAYING);
                            return;
                        }
                        GuildRunMatchingContainer container = GuildRunMatchingContainer.data.get(guildName);
                        if (container.isFull()) {
                            GuildRunLang.send(player, GuildRunLang.ALREADY_FULL);
                            return;
                        }
                        Container.open(player, container);
                    }
                    case GUILD_SHOP_SLOT -> {
                        Container.open(player, new GuildShopContainer());
                    }
                }
            }
        }
    }
}
