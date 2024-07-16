package kr.squirrel.guild.logs;

import kr.squirrel.guild.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GuildRunLog {

    private static final File FILE = new File(Main.getInstance().getDataFolder(), "run/log.yml");

    public static void add(String format) {
        if (!FILE.exists()) {
            Main.getInstance().saveResource("run/log.yml", false);
        }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(FILE);
        yamlConfiguration.set(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), format);
        try {
            yamlConfiguration.save(FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
