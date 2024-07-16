package kr.squirrel.guild.objects.run;

import kr.squirrel.guild.Main;
import kr.squirrel.guild.configurations.GuildRunConfig;
import kr.squirrel.guild.langs.GuildRunLang;
import kr.squirrel.guild.logs.GuildRunLog;
import kr.squirrel.guild.objects.Guild;
import kr.squirrel.guild.objects.LocationDTO;
import kr.squirrel.guild.objects.Writable;
import kr.squirrel.guild.systems.PlayerTeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class GuildRunInGame implements Writable {

    private static final File DIRECTORY = new File(Main.getInstance().getDataFolder(), "run/ingame");
    private static final Map<String, GuildRunInGame> currentGames = new HashMap<>();

    public static boolean isGaming(String name) {
        return currentGames.containsKey(name);
    }

    public static void makeGuildGame(String map, MatchingGuild... guilds) {
        GuildRunInGame guildRunInGame = new GuildRunInGame(map, Arrays.stream(guilds).toList());
        currentGames.put(map, guildRunInGame);
        guildRunInGame.prepare();
    }

    public static GuildRunInGame isGaming(Player player) {
        for (GuildRunInGame game : currentGames.values()) {
            List<UUID> players = game.getPlayersByUUID();
            if (players.contains(player.getUniqueId())) {
                return game;
            }
        }
        return null;
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
            int remainSecond = yamlConfiguration.getInt("remainSecond");
            List<MatchingGuild> guilds = new ArrayList<>();
            ConfigurationSection section = yamlConfiguration.getConfigurationSection("guilds");
            for (String guild : section.getKeys(false)) {
                GuildRun.getPlayingGuilds().add(guild);
                List<GuildRunUser> players = new ArrayList<>();
                for (String uuid : section.getConfigurationSection(guild).getKeys(false)) {
                    players.add(new GuildRunUser(UUID.fromString(uuid), LocationDTO.readYAML(section.getConfigurationSection(guild + "." + uuid))));
                }
                guilds.add(new MatchingGuild(guild, players));
            }
            List<String> completedPlayers = yamlConfiguration.getStringList("completedPlayers");
            List<UUID> completedPlayersParsed = new ArrayList<>();
            for (String uuid : completedPlayers) {
                completedPlayersParsed.add(UUID.fromString(uuid));
            }
            GuildRunInGame game = new GuildRunInGame(name, remainSecond, guilds, completedPlayersParsed);
            currentGames.put(name, game);
            game.waveTime();
        }
    }

    public static void saveAll() {
        for (GuildRunInGame inGame : currentGames.values()) {
            inGame.write();
        }
    }

    private String name;
    private int remainSecond;
    private List<MatchingGuild> guilds;
    private List<UUID> completedPlayer;
    private boolean joinLine;

    public GuildRunInGame(String name, List<MatchingGuild> guilds) {
        this.name = name;
        this.remainSecond = 300;
        this.guilds = guilds;
        this.completedPlayer = new ArrayList<>();
    }

    public GuildRunInGame(String name, int remainSecond, List<MatchingGuild> guilds, List<UUID> completedPlayer) {
        this.name = name;
        this.remainSecond = remainSecond;
        this.guilds = guilds;
        this.completedPlayer = completedPlayer;
    }

    public void end() {
        int retirePoint = GuildRunConfig.POINT.get(9);
        Map<String, Integer> teamPoint = new HashMap<>() {{
            GuildRun.getPlayingGuilds().remove(guilds.get(0).getGuild());
            GuildRun.getPlayingGuilds().remove(guilds.get(1).getGuild());
            put(guilds.get(0).getGuild(), retirePoint * 4);
            put(guilds.get(1).getGuild(), retirePoint * 4);
        }};
        for (int i = 1; i <= completedPlayer.size(); i++) {
            UUID uuid = completedPlayer.get(i - 1);
            for (MatchingGuild matchingGuild : guilds) {
                if (matchingGuild.getPlayers().stream().filter(guildRunUser -> guildRunUser.getUUID().toString().equals(uuid.toString())).count() == 0) {
                    continue;
                }
                teamPoint.put(matchingGuild.getGuild(), teamPoint.get(matchingGuild.getGuild()) + GuildRunConfig.POINT.get(i));
            }
        }

        String loser = teamPoint.keySet().stream().toList().get(1);
        String winner = null;
        int score = -999;
        for (Map.Entry<String, Integer> entry : teamPoint.entrySet()) {
            if (score < entry.getValue()) {
                score = entry.getValue();
                loser = winner;
                winner = entry.getKey();
            } else if (score == entry.getValue()) {
                winner = null;
                break;
            }
        }
        String finalWinner = winner;
        for (MatchingGuild matchingGuild : guilds) {
            for (GuildRunUser user : matchingGuild.getPlayers()) {
                PlayerTeleportManager.teleport(user.getUUID(), user.getPreviousLocation());
                OfflinePlayer player = Bukkit.getOfflinePlayer(user.getUUID());
                if (player.isOnline()) {
                    GuildRunLang.send(player.getPlayer(), winner == null ? GuildRunLang.END_GAME_WITH_DRAW : GuildRunLang.END_GAME, s -> s.replaceAll("<guild>", finalWinner));
                }
            }
        }

        currentGames.remove(name);

        if (winner == null) {
            List<String> names = teamPoint.keySet().stream().toList();
            GuildRunLog.add("(무승부) " + names.get(0) + " vs " + names.get(1));
            return;
        }

        GuildRunLog.add("(승) " + winner + " vs " + loser + " (패)");

        MatchingGuild winningGuild = null;
        for (MatchingGuild matchingGuild : guilds) {
            if (!matchingGuild.getGuild().equals(winner)) {
                continue;
            }
            winningGuild = matchingGuild;
        }

        Guild guild = Guild.get(winningGuild.getGuild());
        Map<String, Long> contribution = guild.getContribution();
        for (GuildRunUser user : winningGuild.getPlayers()) {
            contribution.put(user.getUUID().toString(), contribution.getOrDefault(user.getUUID().toString(), 0l) + GuildRunConfig.WIN_POINT);
        }
        guild.setContribution(contribution);
        guild.setPoint(guild.getPoint() + GuildRunConfig.WIN_POINT * 4);
        guild.write();
    }

    public void prepare() {
        GuildRun map = GuildRun.getMap(name);
        setGlass(map);
        List<Player> players = getPlayers();
        remainSecond = 300;
        for (Player player : players) {
            player.teleport(map.getStartingPoint().toLocation());
            player.sendTitle("이번 길드런 맵은 " + map.getName() + " 입니다", "5초 후 출발합니다", 0, 20, 0);
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendTitle("이번 길드런 맵은 " + map.getName() + " 입니다", "4초 후 출발합니다", 0, 20, 0);
                }
            }.runTaskLater(Main.getInstance(), 20 * 1);
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendTitle("이번 길드런 맵은 " + map.getName() + " 입니다", "3초 후 출발합니다", 0, 20, 0);
                }
            }.runTaskLater(Main.getInstance(), 20 * 2);
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendTitle("이번 길드런 맵은 " + map.getName() + " 입니다", "2초 후 출발합니다", 0, 20, 0);
                }
            }.runTaskLater(Main.getInstance(), 20 * 3);
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendTitle("이번 길드런 맵은 " + map.getName() + " 입니다", "1초 후 출발합니다", 0, 20, 0);
                }
            }.runTaskLater(Main.getInstance(), 20 * 4);
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendTitle("출발", "", 0, 20, 0);
                }
            }.runTaskLater(Main.getInstance(), 20 * 5);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                waveTime();
            }
        }.runTaskLater(Main.getInstance(), 20 * 5);
    }

    private void setGlass(GuildRun map) {
        Location origin = map.getStartingPoint().toLocation();
        Map<Location, Material> previousBlocks = new HashMap<>();

        double startX = origin.getBlockX() - 2;
        double startY = origin.getBlockY() - 1;
        double startZ = origin.getBlockZ() - 2;

        Material material = Material.GLASS;

        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                for (int y = 0; y <= 4; y++) {
                    Location location = new Location(origin.getWorld(), startX + x, startY + y, startZ + z);
                    if (y != 4 && y != 0) {
                        if ((x >= 0 && z == 0) || (x >= 0 && z == 4) || (x == 0 && z >= 0) || (x == 4 && z >= 0)) {
                            previousBlocks.put(location, location.getBlock().getType());
                            location.getBlock().setType(material);
                        }
                    } else {
                        previousBlocks.put(location, location.getBlock().getType());
                        location.getBlock().setType(material);
                    }
                }
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Location, Material> entry : previousBlocks.entrySet()) {
                    entry.getKey().getBlock().setType(entry.getValue());
                }
            }
        }.runTaskLater(Main.getInstance(), 20 * 5);
    }

    private void waveTime() {
        if (joinLine) {
            return;
        }
        remainSecond--;
        if (remainSecond == 0) {
            end();
            return;
        } else {
            List<Player> players = getPlayers();
            if (remainSecond <= 20) {
                for (Player player : players) {
                    player.sendTitle("", remainSecond + " 초 남았습니다", 0, 20, 0);
                }
            }
            if (remainSecond % 60 == 0) {
                for (Player player : players) {
                    player.sendTitle("", (remainSecond / 60) + " 분 남았습니다", 0, 20, 0);
                }
            }
            if (remainSecond == 30) {
                for (Player player : players) {
                    player.sendTitle("", remainSecond + " 초 남았습니다", 0, 20, 0);
                }
            }
        }
        if (remainSecond > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (joinLine) {
                        return;
                    }
                    waveTime();
                }
            }.runTaskLater(Main.getInstance(), 20);
        }
    }

    public void join(Player player) {
        for (Player t : getPlayers()) {
            t.sendTitle("", player.getName() + "님이 1등으로 도착하셨습니다", 5, 20, 5);
        }
        joinLine = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                joinLine = false;
                waveTime();
            }
        }.runTaskLater(Main.getInstance(), 20);
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        for (MatchingGuild matchingGuild : guilds) {
            for (GuildRunUser user : matchingGuild.getPlayers()) {
                players.add(Bukkit.getPlayer(user.getUUID()));
            }
        }
        return players;
    }

    public List<UUID> getPlayersByUUID() {
        List<UUID> players = new ArrayList<>();
        for (MatchingGuild matchingGuild : guilds) {
            for (GuildRunUser user : matchingGuild.getPlayers()) {
                players.add(user.getUUID());
            }
        }
        return players;
    }


    public String getName() {
        return name;
    }

    public int getRemainSecond() {
        return remainSecond;
    }

    public void setRemainSecond(int remainSecond) {
        this.remainSecond = remainSecond;
    }

    public List<MatchingGuild> getGuilds() {
        return guilds;
    }

    public void setGuilds(List<MatchingGuild> guilds) {
        this.guilds = guilds;
    }

    public List<UUID> getCompletedPlayer() {
        return completedPlayer;
    }

    @Override
    public void write() {
        File file = new File(DIRECTORY, name + ".yml");
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("name", name);
        yamlConfiguration.set("remainSecond", remainSecond);
        for (MatchingGuild matchingGuild : guilds) {
            ConfigurationSection section = yamlConfiguration.createSection("guilds." + matchingGuild.getGuild());
            for (GuildRunUser user : matchingGuild.getPlayers()) {
                user.getPreviousLocation().writeYAML(section.createSection(user.getUUID().toString()));
            }
        }
        yamlConfiguration.set("completedPlayer", completedPlayer);
        try {
            yamlConfiguration.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
