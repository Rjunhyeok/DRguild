package kr.squirrel.guild.objects.run;

import kr.squirrel.guild.Main;
import kr.squirrel.guild.objects.LocationDTO;
import kr.squirrel.guild.objects.Writable;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class GuildRun implements Writable {

    private static final File DIRECTORY = new File(Main.getInstance().getDataFolder(), "run/maps");
    private final static Map<String, GuildRun> GUILD_RUN_DATA = new HashMap<>();
    private final static List<String> PLAYING_GUILDS = new ArrayList<>();

    public static boolean exists(String name) {
        return GUILD_RUN_DATA.containsKey(name);
    }

    public static GuildRun getMap(String name) {
        return GUILD_RUN_DATA.get(name);
    }

    public static void create(String name) {
        LocationDTO locationDTO = new LocationDTO("world", 0, 0, 0, 0, 0);
        GuildRun guildRun = new GuildRun(name, locationDTO, locationDTO);
        GUILD_RUN_DATA.put(name, guildRun);
        guildRun.write();
    }

    public static String getAvailableMap() {
        List<String> mapList = new ArrayList<>();
        for (String name : GUILD_RUN_DATA.keySet()) {
            if (GuildRunInGame.isGaming(name)) {
                continue;
            }
            mapList.add(name);
        }
        if (mapList.size() == 0) {
            return null;
        }
        return mapList.get(new Random().nextInt(mapList.size()));
    }

    public static void startGame(String map, MatchingGuild... guilds) {
        GuildRunInGame.makeGuildGame(map, guilds);
    }

    public static boolean isPlaying(String guild) {
        return PLAYING_GUILDS.contains(guild);
    }

    public static List<String> getPlayingGuilds() {
        return PLAYING_GUILDS;
    }

    public static List<String> getList() {
        return GUILD_RUN_DATA.keySet().stream().toList();
    }

    public static void readAll() {
        if (!DIRECTORY.exists()) {
            DIRECTORY.mkdirs();
            return;
        }
        if (DIRECTORY.listFiles() == null) {
            return;
        }
        for (File file : DIRECTORY.listFiles()) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            String name = yamlConfiguration.getString("name");
            LocationDTO startingPoint = LocationDTO.readYAML(yamlConfiguration.getConfigurationSection("startingPoint"));
            LocationDTO endPoint = LocationDTO.readYAML(yamlConfiguration.getConfigurationSection("endPoint"));
            GUILD_RUN_DATA.put(name, new GuildRun(name, startingPoint, endPoint));
        }
    }

    private String name;
    private LocationDTO startingPoint;
    private LocationDTO endPoint;

    public GuildRun(String name, LocationDTO startingPoint, LocationDTO endPoint) {
        this.name = name;
        this.startingPoint = startingPoint;
        this.endPoint = endPoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocationDTO getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(LocationDTO startingPoint) {
        this.startingPoint = startingPoint;
    }

    public LocationDTO getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(LocationDTO endPoint) {
        this.endPoint = endPoint;
    }

    @Override
    public void write() {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("name", name);
        startingPoint.writeYAML(yamlConfiguration.createSection("startingPoint"));
        endPoint.writeYAML(yamlConfiguration.createSection("endPoint"));
        try {
            yamlConfiguration.save(new File(DIRECTORY, name + ".yml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
