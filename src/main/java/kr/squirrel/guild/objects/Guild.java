package kr.squirrel.guild.objects;

import kr.squirrel.guild.configurations.Config;
import kr.squirrel.guild.Main;
import kr.squirrel.guild.systems.PlayerJoinTracker;
import kr.squirrel.guild.guis.run.GuildRunMatchingContainer;
import kr.squirrel.guild.utils.ItemUtil;
import kr.squirrel.guild.utils.YAMLUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Guild implements Writable {
    private static final File DIRECTORY = new File(Main.getInstance().getDataFolder(), "guilds");
    private static final Map<String, Guild> GUILDS = new HashMap<>();

    public static Guild get(String name) {
        return GUILDS.values().stream().filter(guild -> guild.name.equals(name)).findFirst().get();
    }

    public static Guild get(OfflinePlayer player) {
        String uuid = player.getUniqueId().toString();
        for (Guild guild : GUILDS.values()) {
            if (guild.master.equals(player.getUniqueId().toString())) {
                return guild;
            }
            for (List<String> member : guild.members.values()) {
                if (!member.contains(uuid)) {
                    continue;
                }
                return guild;
            }
        }
        return null;
    }

    public static boolean hasGuild(Player player) {
        String uuid = player.getUniqueId().toString();
        for (Guild guild : GUILDS.values()) {
            if (guild.master.equals(player.getUniqueId().toString())) {
                return true;
            }
            for (List<String> member : guild.members.values()) {
                if (!member.contains(uuid)) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    public static boolean exists(String name) {
        return GUILDS.values().stream().filter(guild -> guild.name.equals(name)).count() == 0 ? false : true;
    }

    public static void create(Player player, String name) {
        UUID uuid = UUID.randomUUID();
        Guild guild = new Guild(uuid.toString(), name, player.getUniqueId().toString(), new EnumMap<>(Rank.class), 5, 1, 0, player.getName() + "님의 길드", false, new ArrayList<>(), new HashMap<>() {{
            put(LocalDate.now().toString(), new ArrayList<>());
        }}, new HashMap<>() {{
            put(LocalDate.now().toString(), new ArrayList<>());
        }}, 0, new HashMap<>());
        guild.write();
        GuildRunMatchingContainer.data.put(name, new GuildRunMatchingContainer(name));
        GUILDS.put(uuid.toString(), guild);
    }

    public static List<Guild> getAll() {
        return GUILDS.values().stream().toList();
    }

    public static List<Guild> getApplied(OfflinePlayer player) {
        return GUILDS.values().stream().filter(guild -> guild.applyingPlayers.contains(player.getUniqueId().toString())).collect(Collectors.toList());
    }

    public static Rank getRank(OfflinePlayer player) {
        Guild guild = get(player);
        String uuid = player.getUniqueId().toString();
        if (!guild.members.containsKey(Rank.MEMBER)) {
            guild.members.put(Rank.MEMBER, new ArrayList<>());
        }
        if (!guild.members.containsKey(Rank.SUB_MASTER)) {
            guild.members.put(Rank.SUB_MASTER, new ArrayList<>());
        }
        if (guild.master.equals(uuid)) {
            return Rank.MASTER;
        }
        if (guild.members.get(Rank.SUB_MASTER).contains(uuid)) {
            return Rank.SUB_MASTER;
        }
        if (guild.members.get(Rank.MEMBER).contains(uuid)) {
            return Rank.MEMBER;
        }
        return null;
    }

    public static void readAll() {
        if (!DIRECTORY.exists()) {
            DIRECTORY.mkdirs();
            return;
        }
        if (DIRECTORY.listFiles() == null) {
            return;
        }
        for (File file : DIRECTORY.listFiles()) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            String uuid = yamlConfiguration.getString("uuid");
            String name = yamlConfiguration.getString("name");
            String master = yamlConfiguration.getString("master");
            Map<Rank, List<String>> members = new HashMap<>();
            if (yamlConfiguration.contains("members")) {
                members = YAMLUtil.readMap(yamlConfiguration.getConfigurationSection("members"));
            }
            int level = yamlConfiguration.getInt("level");
            long exp = yamlConfiguration.getLong("exp");
            String description = yamlConfiguration.getString("description");
            boolean freeJoin = yamlConfiguration.getBoolean("freeJoin");
            List<String> applyingPlayers = yamlConfiguration.getStringList("applyingPlayers");
            int maxPlayer = yamlConfiguration.getInt("maxPlayer");
            Map<String, List<String>> attendance = new HashMap<>();
            Map<String, List<Integer>> attendanceReward = new HashMap<>();
            LocalDate localDate = LocalDate.now();

            if (yamlConfiguration.contains("attendance." + localDate)) {
                attendance.put(localDate.toString(), yamlConfiguration.getStringList("attendance." + localDate));
            } else {
                attendance.put(localDate.toString(), new ArrayList<>());
            }
            if (yamlConfiguration.contains("attendanceReward." + localDate)) {
                attendanceReward.put(localDate.toString(), yamlConfiguration.getIntegerList("attendanceReward." + localDate));
            } else {
                attendanceReward.put(localDate.toString(), new ArrayList<>());
            }

            long point = yamlConfiguration.getLong("point");
            Map<String, Long> contribution = new HashMap<>();
            if (yamlConfiguration.contains("contribution")) {
                for (String pUUID : yamlConfiguration.getConfigurationSection("contribution").getKeys(false)) {
                    contribution.put(pUUID, yamlConfiguration.getLong("contribution." + pUUID));
                }
            }

            GuildRunMatchingContainer.data.put(name, new GuildRunMatchingContainer(name));
            GUILDS.put(uuid, new Guild(uuid, name, master, members, maxPlayer, level, exp, description, freeJoin, applyingPlayers, attendance, attendanceReward, point, contribution));
        }
    }

    public static void quit(Player player) {
        Guild guild = Guild.get(player);
        guild.getContribution().remove(player.getUniqueId().toString());
        for (String uuid : guild.getMembers().get(Rank.SUB_MASTER)) {
            if (uuid.equals(player.getUniqueId().toString())) {
                guild.getMembers().get(Rank.SUB_MASTER).remove(player.getUniqueId().toString());
                break;
            }
        }
        for (String uuid : guild.getMembers().get(Rank.MEMBER)) {
            if (uuid.equals(player.getUniqueId().toString())) {
                guild.getMembers().get(Rank.MEMBER).remove(player.getUniqueId().toString());
                break;
            }
        }
        guild.write();
    }


    private String uuid;
    private String name;
    private String master;
    private Map<Rank, List<String>> members;
    private int maxPlayer;
    private int level;
    private long exp;
    private String description;
    private boolean freeJoin;
    private List<String> applyingPlayers;
    private Map<String, List<String>> attendance;
    private Map<String, List<Integer>> attendanceReward;
    private long point;
    private Map<String, Long> contribution;

    public Guild(String uuid, String name, String master, Map<Rank, List<String>> members, int maxPlayer, int level, long exp, String description, boolean freeJoin, List<String> applyingPlayers, Map<String, List<String>> attendance, Map<String, List<Integer>> attendanceReward, long point, Map<String, Long> contribution) {
        this.uuid = uuid;
        this.name = name;
        this.master = master;
        this.members = members;
        this.maxPlayer = maxPlayer;
        this.level = level;
        this.exp = exp;
        this.description = description;
        this.freeJoin = freeJoin;
        this.applyingPlayers = applyingPlayers;
        this.attendance = attendance;
        this.attendanceReward = attendanceReward;
        this.point = point;
        this.contribution = contribution;
    }

    public void setStatus(Rank rank, String uuid) {
        if (!members.containsKey(Rank.MEMBER)) {
            members.put(Rank.MEMBER, new ArrayList<>());
        }
        if (!members.containsKey(Rank.SUB_MASTER)) {
            members.put(Rank.SUB_MASTER, new ArrayList<>());
        }

        if (members.get(Rank.SUB_MASTER).contains(uuid)) {
            members.get(Rank.SUB_MASTER).remove(uuid);
        } else if (members.get(Rank.MEMBER).contains(uuid)) {
            members.get(Rank.MEMBER).remove(uuid);
        } else if (master.equals(uuid)) {
            master = null;
        }
        if (rank == Rank.MASTER) {
            master = uuid;
        } else if (rank == Rank.SUB_MASTER) {
            members.get(Rank.SUB_MASTER).add(uuid);
        } else if (rank == Rank.MEMBER) {
            members.get(Rank.MEMBER).add(uuid);
        }
        write();
    }

    public void join(OfflinePlayer player) {
        String uuid = player.getUniqueId().toString();
        for (Guild appliedGuild : Guild.getApplied(player)) {
            appliedGuild.applyingPlayers.remove(uuid);
        }
        if (!members.containsKey(Rank.MEMBER)) {
            members.put(Rank.MEMBER, new ArrayList<>());
        }
        members.get(Rank.MEMBER).add(uuid);
        System.out.println(members.get(Rank.MEMBER).get(0));
        write();
    }

    public void expel(String uuid, Rank rank) {
        members.get(rank).remove(uuid);
        contribution.remove(uuid);
        write();
    }

    public void apply(Player player) {
        applyingPlayers.add(player.getUniqueId().toString());
        write();
    }

    public int getMemberCount() {
        int count = 1;
        for (List<String> a : members.values()) {
            count += a.size();
        }
        return count;
    }

    public ItemStack getMemberInfo(Rank rank, String uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
        return ItemUtil.getHeadItem(
                uuid,
                offlinePlayer.getName(),
                List.of(
                        "§7[" + rank.getName() + "§7]",
                        "§7[기여도 : " + contribution.getOrDefault(uuid, 0l) + "§7]",
                        "",
                        "§7[" + (offlinePlayer.isOnline() ? "§a온라인" : "§4오프라인") + "§7]",
                        "§7[최근 접속 : " + PlayerJoinTracker.getRecentlyJoined(offlinePlayer) + "§7]"
                )
        );
    }

    public ItemStack getMemberInfoInManagement(Rank rank, String uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
        return ItemUtil.getHeadItem(
                uuid,
                offlinePlayer.getName(),
                List.of(
                        "§7[" + rank.getName() + "§7]",
                        "§7[기여도 : " + contribution.getOrDefault(uuid, 0l) + "§7]",
                        "",
                        "§7[" + (offlinePlayer.isOnline() ? "§a온라인" : "§4오프라인") + "§7]",
                        "§7[최근 접속 : " + PlayerJoinTracker.getRecentlyJoined(offlinePlayer) + "§7]",
                        "",
                        "§7좌클릭 시 일반 길드원으로 변경합니다",
                        "§7우클릭 시 길드에서 추방합니다",
                        "§7쉬프트 좌클릭 시 일반 길드 부마스터로 임명합니다",
                        "§7쉬프트 우클릭 시 길드마스터로 양도합니다"
                )
        );
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;

        if (Config.EXP.containsKey(level)) {
            long require = Config.EXP.get(level);
            if (exp >= require) {
                this.exp = exp - require;
                level++;
                if (level % 2 == 0) {
                    maxPlayer++;
                }
            }
        }
        write();
    }

    public List<String> getApplyingPlayers() {
        return applyingPlayers;
    }

    public boolean isFreeJoin() {
        return freeJoin;
    }

    public void setFreeJoin(boolean freeJoin) {
        this.freeJoin = freeJoin;
        write();
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    public String getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public Map<Rank, List<String>> getMembers() {
        return members;
    }

    public void setMembers(Map<Rank, List<String>> members) {
        this.members = members;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        write();
    }

    public Map<String, List<String>> getAttendance() {
        return attendance;
    }

    public Map<String, List<Integer>> getAttendanceReward() {
        return attendanceReward;
    }

    public Map<String, Long> getContribution() {
        return contribution;
    }

    public long getPoint() {
        return point;
    }

    public void setPoint(long point) {
        this.point = point;
    }

    public void setContribution(Map<String, Long> contribution) {
        this.contribution = contribution;
    }

    public void breakGuild() {
        final File FILE = new File(DIRECTORY, uuid + ".yml");
        FILE.delete();
        GUILDS.remove(this.uuid);
    }

    @Override
    public void write() {
        final File FILE = new File(DIRECTORY, uuid + ".yml");
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("uuid", uuid);
        yamlConfiguration.set("name", name);
        yamlConfiguration.set("master", master);
        YAMLUtil.saveMap(yamlConfiguration.createSection("members"), members);
        yamlConfiguration.set("level", level);
        yamlConfiguration.set("exp", exp);
        yamlConfiguration.set("description", description);
        yamlConfiguration.set("freeJoin", freeJoin);
        yamlConfiguration.set("applyingPlayers", applyingPlayers);
        yamlConfiguration.set("maxPlayer", maxPlayer);
        LocalDate localDate = LocalDate.now();
        yamlConfiguration.set("attendance." + localDate, attendance.get(localDate.toString()));
        yamlConfiguration.set("attendanceReward." + localDate, attendanceReward.get(localDate.toString()));
        yamlConfiguration.set("point", point);
        for (Map.Entry<String, Long> entry : contribution.entrySet()) {
            yamlConfiguration.set("contribution." + entry.getKey(), entry.getValue());
        }
        try {
            yamlConfiguration.save(FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
