package kr.squirrel.guild.langs;

import kr.squirrel.guild.Main;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.function.Function;

public class Lang {

    private static final File FILE = new File(Main.getInstance().getDataFolder(), "lang.yml");

    public static String CHAT_GUILD_NAME;
    public static String ALREADY_EXISTS_GUILD;
    public static String REQUIRE_MONEY;
    public static String GUILD_NAME_CONFIRM;
    public static String CREATE_GUILD;
    public static String CANCEL_CREATE_GUILD;
    public static String ALREADY_FULL;
    public static String ALREADY_APPLIED_GUILD;
    public static String APPLYING_COMPLETE;
    public static String JOIN_GUILD;
    public static String REQUIRE_GUILD;
    public static String IS_MEMBER;
    public static String TYPE_TO_SET;
    public static String OVER_LENGTH;
    public static String GUILD_QUIT;

    public static void load() {
        if (!FILE.exists()) {
            Main.getInstance().saveResource("lang.yml", false);
        }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(FILE);
        CHAT_GUILD_NAME = yamlConfiguration.getString("CHAT_GUILD_NAME");
        ALREADY_EXISTS_GUILD = yamlConfiguration.getString("ALREADY_EXISTS_GUILD");
        REQUIRE_MONEY = yamlConfiguration.getString("REQUIRE_MONEY");
        GUILD_NAME_CONFIRM = yamlConfiguration.getString("GUILD_NAME_CONFIRM");
        CREATE_GUILD = yamlConfiguration.getString("CREATE_GUILD");
        CANCEL_CREATE_GUILD = yamlConfiguration.getString("CANCEL_CREATE_GUILD");
        ALREADY_FULL = yamlConfiguration.getString("ALREADY_FULL");
        ALREADY_APPLIED_GUILD = yamlConfiguration.getString("ALREADY_APPLIED_GUILD");
        APPLYING_COMPLETE = yamlConfiguration.getString("APPLYING_COMPLETE");
        JOIN_GUILD = yamlConfiguration.getString("JOIN_GUILD");
        REQUIRE_GUILD = yamlConfiguration.getString("REQUIRE_GUILD");
        IS_MEMBER = yamlConfiguration.getString("IS_MEMBER");
        TYPE_TO_SET = yamlConfiguration.getString("TYPE_TO_SET");
        OVER_LENGTH = yamlConfiguration.getString("OVER_LENGTH");
        GUILD_QUIT = yamlConfiguration.getString("GUILD_QUIT");
    }

    public static void send(Player player, String message) {
        player.sendMessage(message);
    }

    public static void send(Player player, String message, Function<String, String> filter) {
        player.sendMessage(filter.apply(message));
    }

}
