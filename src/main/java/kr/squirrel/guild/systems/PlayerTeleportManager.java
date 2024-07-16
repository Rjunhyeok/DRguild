package kr.squirrel.guild.systems;

import kr.squirrel.guild.Main;
import kr.squirrel.guild.objects.LocationDTO;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerTeleportManager implements Listener {

    private static final File FILE = new File(Main.getInstance().getDataFolder(), "teleport-manager.yml");
    private static final Map<UUID, LocationDTO> OFFLINE_TO_TELEPORT_PLAYERS = new HashMap<>();

    public static void readAll() {
        if (!FILE.exists()) {
            return;
        }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(FILE);
        for (String uuid : yamlConfiguration.getKeys(false)) {
            OFFLINE_TO_TELEPORT_PLAYERS.put(UUID.fromString(uuid), LocationDTO.readYAML(yamlConfiguration.getConfigurationSection(uuid)));
        }
    }

    public static void writeAll() {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        for (Map.Entry<UUID, LocationDTO> entry : OFFLINE_TO_TELEPORT_PLAYERS.entrySet()) {
            entry.getValue().writeYAML(yamlConfiguration.createSection(entry.getKey().toString()));
        }
        try {
            yamlConfiguration.save(FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void teleport(UUID uuid, LocationDTO locationDTO) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer.isOnline()) {
            offlinePlayer.getPlayer().teleport(locationDTO.toLocation());
            return;
        }
        OFFLINE_TO_TELEPORT_PLAYERS.put(uuid, locationDTO);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (OFFLINE_TO_TELEPORT_PLAYERS.containsKey(uuid)) {
            player.teleport(OFFLINE_TO_TELEPORT_PLAYERS.get(uuid).toLocation());
            OFFLINE_TO_TELEPORT_PLAYERS.remove(uuid);
        }
    }

}
