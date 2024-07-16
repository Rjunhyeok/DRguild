package kr.squirrel.guild.utils;

import kr.squirrel.guild.objects.Rank;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YAMLUtil {

    public static void saveMap(ConfigurationSection section, Map<Rank, List<String>> objectMap) {
        for (Map.Entry<Rank, List<String>> entry : objectMap.entrySet()) {
            section.set(entry.getKey().toString(), entry.getValue());
        }
    }

    public static Map<Rank, List<String>> readMap(ConfigurationSection section) {
        Map<Rank, List<String>> objectMap = new HashMap<>();
        for (String key : section.getKeys(false)) {
            objectMap.put(Rank.valueOf(key), section.getStringList(key));
        }
        return objectMap;
    }

}
