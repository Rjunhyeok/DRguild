package kr.squirrel.guild.langs;

import kr.squirrel.guild.Main;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.function.Function;

public class GuildAttendanceLang {

    private static final File FILE = new File(Main.getInstance().getDataFolder(), "attendance/attendance_lang.yml");

    public static String REQUIRE_EMPTY_SPACE;
    public static String REQUIRE_ENOUGH_PLAYTIME;

    public static void load() {
        if (!FILE.exists()) {
            Main.getInstance().saveResource("attendance/attendance_lang.yml", false);
        }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(FILE);
        REQUIRE_EMPTY_SPACE = yamlConfiguration.getString("REQUIRE_EMPTY_SPACE");
        REQUIRE_ENOUGH_PLAYTIME = yamlConfiguration.getString("REQUIRE_ENOUGH_PLAYTIME");
    }

    public static void send(Player player, String message) {
        player.sendMessage(message);
    }

    public static void send(Player player, String message, Function<String, String> filter) {
        player.sendMessage(filter.apply(message));
    }
}
