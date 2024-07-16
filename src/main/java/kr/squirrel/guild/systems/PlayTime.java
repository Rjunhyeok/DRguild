package kr.squirrel.guild.systems;

import kr.squirrel.guild.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayTime implements Listener {

    private static final File DIRECTORY = new File(Main.getInstance().getDataFolder(), "date_playtime");
    private static Map<UUID, Long> previousData = new HashMap<>();
    private static Map<UUID, PlayTime> datePlayTime = new HashMap<>();

    public static void readAll() {
        if (!DIRECTORY.exists()) {
            DIRECTORY.mkdirs();
            return;
        }
        File today = new File(DIRECTORY, LocalDate.now() + ".yml");
        if (today.exists()) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(today);
            for (String uuid : yamlConfiguration.getKeys(false)) {
                previousData.put(UUID.fromString(uuid), yamlConfiguration.getLong(uuid));
            }
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            register(player);
        }
    }

    public static void saveAll(LocalDate localDate) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        for (Map.Entry<UUID, PlayTime> entry : datePlayTime.entrySet()) {
            yamlConfiguration.set(entry.getKey().toString(), entry.getValue().getTodayPlayTime());
        }
        try {
            yamlConfiguration.save(new File(DIRECTORY, localDate + ".yml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void register(Player player) {
        UUID uuid = player.getUniqueId();
        if (datePlayTime.containsKey(uuid)) {
            datePlayTime.get(uuid).setRecentlyJoined(LocalDateTime.now());
            datePlayTime.get(uuid).setRecentlyQuited(null);
            return;
        }
        if (!previousData.containsKey(uuid)) {
            datePlayTime.put(uuid, new PlayTime(uuid, LocalDateTime.now(), 0l));
            return;
        }
        datePlayTime.put(uuid, new PlayTime(uuid, LocalDateTime.now(), previousData.get(uuid)));
    }

    public static void save(Player player) {
        datePlayTime.get(player.getUniqueId()).setRecentlyQuited(LocalDateTime.now());
    }

    public static long get(Player player) {
        return datePlayTime.get(player.getUniqueId()).getTodayPlayTime();
    }

    private UUID uuid;
    private LocalDateTime recentlyJoined;
    private LocalDateTime recentlyQuited;
    private Long todayPlayTime;

    public PlayTime(UUID uuid, LocalDateTime recentlyJoined, Long todayPlayTime) {
        this.uuid = uuid;
        this.recentlyJoined = recentlyJoined;
        this.todayPlayTime = todayPlayTime;
    }

    public void setRecentlyJoined(LocalDateTime recentlyJoined) {
        this.recentlyJoined = recentlyJoined;
    }

    public void setRecentlyQuited(LocalDateTime recentlyQuited) {
        this.recentlyQuited = recentlyQuited;
    }

    public Long getTodayPlayTime() {
        return recentlyQuited == null ? todayPlayTime + ChronoUnit.SECONDS.between(recentlyJoined, LocalDateTime.now()) : todayPlayTime + ChronoUnit.SECONDS.between(recentlyJoined, recentlyQuited);
    }

    public static class PlayTimeHandler implements Listener {
        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            register(event.getPlayer());
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent event) {
            save(event.getPlayer());
        }
    }

    public static class DateTracker {

        private static LocalDateTime localDateTime = LocalDateTime.now();

        public static void track() {
            new BukkitRunnable() {
                @Override
                public void run() {
                    LocalDateTime now = LocalDateTime.now();
                    if (now.getDayOfWeek().getValue() != localDateTime.getDayOfWeek().getValue()) {
                        saveAll(localDateTime.toLocalDate());
                        localDateTime = now;
                        previousData.clear();
                        datePlayTime.clear();
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            register(player);
                        }
                    }
                }
            }.runTaskTimer(Main.getInstance(), 20, 20);
        }
    }
}
