package kr.squirrel.guild.commands;

import kr.squirrel.guild.configurations.Config;
import kr.squirrel.guild.configurations.GuildAttendanceConfig;
import kr.squirrel.guild.configurations.GuildRunConfig;
import kr.squirrel.guild.langs.GuildAttendanceLang;
import kr.squirrel.guild.langs.GuildRunLang;
import kr.squirrel.guild.langs.Lang;
import kr.squirrel.guild.libraries.SimpleCommandBuilder;
import kr.squirrel.guild.objects.Guild;
import org.bukkit.entity.Player;

public class GuildManagementCommand {

    public static void register() {
        new SimpleCommandBuilder("길드관리")
                .aliases("guild-management")
                .permission("op")
                .commandExecutor((sender, command, label, args) -> {
                    if (sender instanceof Player player) {
                        if (args.length == 0) {
                            player.sendMessage("/길드관리 reload");
                            player.sendMessage("/길드관리 인원증가 [길드이름] [수치]");
                            player.sendMessage("/길드관리 인원감소 [길드이름] [수치]");
                            player.sendMessage("/길드관리 경험치추가 [길드이름] [수치]");
                            player.sendMessage("/길드관리 경험치감소 [길드이름] [수치]");
                            player.sendMessage("/길드관리 포인트증가 [길드이름] [수치]");
                            player.sendMessage("/길드관리 포인트감소 [길드이름] [수치]");
                            return false;
                        }
                        switch (args[0]) {
                            case "reload" -> {
                                Lang.load();
                                GuildAttendanceLang.load();
                                GuildRunLang.load();
                                Config.load();
                                GuildAttendanceConfig.load();
                                GuildRunConfig.load();
                                player.sendMessage("§a리로드 완료");
                                return false;
                            }
                            case "인원증가" -> {
                                String guildName = args[1];
                                Guild guild = Guild.get(guildName);
                                guild.setMaxPlayer(guild.getMaxPlayer() + Integer.parseInt(args[2]));
                                guild.write();
                                player.sendMessage("§a추가 완료");
                                return false;
                            }
                            case "인원감소" -> {
                                String guildName = args[1];
                                Guild guild = Guild.get(guildName);
                                guild.setMaxPlayer(guild.getMaxPlayer() - Integer.parseInt(args[2]));
                                guild.write();
                                player.sendMessage("§a감소 완료");
                                return false;
                            }
                            case "경험치추가" -> {
                                String guildName = args[1];
                                Guild guild = Guild.get(guildName);
                                guild.setExp(guild.getExp() + Integer.parseInt(args[2]));
                                player.sendMessage("§a추가 완료");
                                return false;
                            }
                            case "경험치감소" -> {
                                String guildName = args[1];
                                Guild guild = Guild.get(guildName);
                                guild.setExp(guild.getExp() - Integer.parseInt(args[2]));
                                player.sendMessage("§a감소 완료");
                                return false;
                            }
                            case "포인트증가" -> {
                                String guildName = args[1];
                                Guild guild = Guild.get(guildName);
                                guild.setPoint(guild.getPoint() + Integer.parseInt(args[2]));
                                guild.write();
                                player.sendMessage("§a추가 완료");
                                return false;
                            }
                            case "포인트감소" -> {
                                String guildName = args[1];
                                Guild guild = Guild.get(guildName);
                                guild.setPoint(guild.getPoint() - Integer.parseInt(args[2]));
                                guild.write();
                                player.sendMessage("§a감소 완료");
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
