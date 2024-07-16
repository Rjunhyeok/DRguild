package kr.squirrel.guild.systems;

import kr.squirrel.guild.Main;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class PlayerJoinTracker {

    private static final File FILE = new File(Main.getInstance().getDataFolder(), "data.yml");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Map<String, LocalDateTime> PLAYER_JOIN_MAP = new HashMap<>();

    public static void register(Player player) {
        PLAYER_JOIN_MAP.put(player.getUniqueId().toString(), LocalDateTime.now());
    }

    public static void writeAll() {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        for (Map.Entry<String, LocalDateTime> entry : PLAYER_JOIN_MAP.entrySet()) {
            yamlConfiguration.set(entry.getKey(), entry.getValue().format(DATE_FORMAT));
        }
        try {
            yamlConfiguration.save(FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readAll() {
        if (!FILE.exists()) {
            return;
        }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(FILE);
        for (String uuid : yamlConfiguration.getKeys(false)) {
            LocalDateTime localDateTime = LocalDateTime.parse(yamlConfiguration.getString(uuid), DATE_FORMAT);
            PLAYER_JOIN_MAP.put(uuid, localDateTime);
        }
    }

    public static String getRecentlyJoined(OfflinePlayer player) {
        return PLAYER_JOIN_MAP.get(player.getUniqueId().toString()).format(DATE_FORMAT);
    }
}
