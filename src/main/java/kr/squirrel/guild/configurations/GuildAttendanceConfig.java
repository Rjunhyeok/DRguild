package kr.squirrel.guild.configurations;

import kr.squirrel.guild.Main;
import kr.squirrel.guild.utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuildAttendanceConfig {
    private static final File FILE = new File(Main.getInstance().getDataFolder(), "attendance/reward.yml");

    public static Map<Integer, List<Reward>> rewards = new HashMap<>();
    public static int ATTENDANCE_CONTRIBUTION;

    public static void load() {
        if (!FILE.exists()) {
            Main.getInstance().saveResource("attendance/reward.yml", false);
        }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(FILE);
        ATTENDANCE_CONTRIBUTION = yamlConfiguration.getInt("ATTENDANCE_CONTRIBUTION");
        for (String attendanceCount : yamlConfiguration.getConfigurationSection("reward").getKeys(false)) {
            int count = Integer.parseInt(attendanceCount);
            List<Reward> rewardList = new ArrayList<>();
            ConfigurationSection section = yamlConfiguration.getConfigurationSection("reward." + attendanceCount);
            for (String item : section.getKeys(false)) {
                String type = section.getString(item + ".type");
                String displayName = section.getString(item + ".displayName");
                List<String> lore = section.getStringList(item + ".lore");
                rewardList.add(new Reward(type, displayName, lore));
            }
            rewards.put(count, rewardList);
        }
    }

    public static class Reward {
        private String type;
        private String displayName;
        private List<String> lore;

        public Reward(String type, String displayName, List<String> lore) {
            this.type = type;
            this.displayName = displayName;
            this.lore = lore;
        }

        public ItemStack getItem() {
            return ItemUtil.getItemClearly(Material.getMaterial(type), displayName, lore);
        }
    }

}
