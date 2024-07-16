package kr.squirrel.guild.commands.run;

import kr.squirrel.guild.libraries.SimpleCommandBuilder;
import kr.squirrel.guild.objects.LocationDTO;
import kr.squirrel.guild.objects.run.GuildRun;
import org.bukkit.entity.Player;

public class GuildRunCommand {
    public static boolean guildRun = false;
    public static void register() {
        new SimpleCommandBuilder("길드런관리")
                .aliases("guild-run-management")
                .permission("op")
                .commandExecutor((sender, command, label, args) -> {
                    if (sender instanceof Player player) {
                        if (args.length == 0) {
                            player.sendMessage("/길드런관리 맵생성 [이름]");
                            player.sendMessage("/길드런관리 출발지점설정 [이름]");
                            player.sendMessage("/길드런관리 도착지점설정 [이름]");
                            player.sendMessage("/길드런관리 맵목록 [이름]");
                            player.sendMessage("/길드런관리 시작");
                            player.sendMessage("/길드런관리 잠금");
                            return false;
                        }
                        switch (args[0]) {
                            case "맵생성" -> {
                                String mapName = args[1];
                                if (GuildRun.exists(mapName)) {
                                    player.sendMessage("§c이미 존재하는 맵 이름입니다");
                                    return false;
                                }
                                GuildRun.create(mapName);
                                player.sendMessage("§a생성 완료");
                                return false;
                            }
                            case "출발지점설정" -> {
                                String mapName = args[1];
                                if (!GuildRun.exists(mapName)) {
                                    player.sendMessage("§c존재하지 않는 맵 이름입니다");
                                    return false;
                                }
                                GuildRun guildRun = GuildRun.getMap(mapName);
                                guildRun.setStartingPoint(LocationDTO.toLocationDTO(player.getLocation()));
                                guildRun.write();
                                player.sendMessage("§a설정 완료");
                                return false;
                            }
                            case "도착지점설정" -> {
                                String mapName = args[1];
                                if (!GuildRun.exists(mapName)) {
                                    player.sendMessage("§c존재하지 않는 맵 이름입니다");
                                    return false;
                                }
                                GuildRun guildRun = GuildRun.getMap(mapName);
                                guildRun.setEndPoint(LocationDTO.toLocationDTO(player.getLocation()));
                                guildRun.write();
                                player.sendMessage("§a설정 완료");
                                return false;
                            }
                            case "맵목록" -> {
                                for (String name : GuildRun.getList()) {
                                    player.sendMessage(name);
                                }
                                return false;
                            }
                            case "시작" -> {
                                guildRun = true;
                                player.sendMessage("§a길드런이 입장 가능하게 되었습니다");
                                return false;
                            }
                            case "잠금" -> {
                                guildRun = false;
                                player.sendMessage("§c길드런 입장이 불가능하게 되었습니다");
                                return false;
                            }
                        }
                    }
                    return false;
                })
                .tabCompleter((sender, command, label, args) -> null)
                .register();
    }

}
