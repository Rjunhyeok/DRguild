package kr.squirrel.guild.configurations;

import kr.squirrel.guild.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GuildRunConfig {

    private static final File FILE = new File(Main.getInstance().getDataFolder(), "run/game.yml");
    public static Map<Integer, Integer> POINT = new HashMap<>();
    public static int WIN_POINT;

    public static void load() {
        Main instance = Main.getInstance();
        instance.saveResource("run/game.yml", false);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(FILE);
        for (int i = 1; i <= 8; i++) {
            POINT.put(i, config.getInt("point." + i));
        }
        POINT.put(9, config.getInt("point.R"));
        WIN_POINT = config.getInt("winPoint");
    }
}
