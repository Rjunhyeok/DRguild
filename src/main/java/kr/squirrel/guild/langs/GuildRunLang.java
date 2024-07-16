package kr.squirrel.guild.langs;

import kr.squirrel.guild.Main;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.function.Function;

public class GuildRunLang {

    private static final File FILE = new File(Main.getInstance().getDataFolder(), "run/run_lang.yml");

    public static String END_GAME;
    public static String END_GAME_WITH_DRAW;
    public static String NONE_SPACE;
    public static String ALREADY_PLAYING;
    public static String ALREADY_FULL;

    public static void load() {
        if (!FILE.exists()) {
            Main.getInstance().saveResource("run/run_lang.yml", false);
        }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(FILE);
        END_GAME = yamlConfiguration.getString("END_GAME");
        END_GAME_WITH_DRAW = yamlConfiguration.getString("END_GAME_WITH_DRAW");
        NONE_SPACE = yamlConfiguration.getString("NONE_SPACE");
        ALREADY_PLAYING = yamlConfiguration.getString("ALREADY_PLAYING");
        ALREADY_FULL = yamlConfiguration.getString("ALREADY_FULL");
    }

    public static void send(Player player, String message) {
        player.sendMessage(message);
    }

    public static void send(Player player, String message, Function<String, String> filter) {
        player.sendMessage(filter.apply(message));
    }
}
