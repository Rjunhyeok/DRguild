package kr.squirrel.guild.configurations;

import kr.squirrel.guild.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class Config {

    public static int GUILD_CREATE_PRICE;
    public static Map<Integer, Long> EXP = new HashMap<>();

    public static void load() {
        Main instance = Main.getInstance();
        instance.saveDefaultConfig();
        instance.reloadConfig();
        FileConfiguration config = instance.getConfig();
        GUILD_CREATE_PRICE = config.getInt("GUILD_CREATE_PRICE");
        EXP.clear();
        for (String level : config.getConfigurationSection("EXP").getKeys(false)) {
            EXP.put(Integer.parseInt(level), config.getLong("EXP." + level));
        }
    }

}
