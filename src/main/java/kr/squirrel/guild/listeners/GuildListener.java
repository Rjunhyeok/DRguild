package kr.squirrel.guild.listeners;

import kr.squirrel.guild.configurations.Config;
import kr.squirrel.guild.guis.management.GuildAdminManagementContainer;
import kr.squirrel.guild.langs.Lang;
import kr.squirrel.guild.systems.PlayerJoinTracker;
import kr.squirrel.guild.guis.Container;
import kr.squirrel.guild.guis.GuildMainContainer;
import kr.squirrel.guild.objects.Guild;
import kr.squirrel.guild.utils.VaultUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;

public class GuildListener implements Listener {

    public static Map<String, Integer> nameSettings = new HashMap<>();
    public static Map<String, String> typingData = new HashMap<>();
    public static Map<String, String> adminChange = new HashMap<>();

    private static int MAX_LENGTH = 30;


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerJoinTracker.register(event.getPlayer());
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) throws Exception {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        String message = event.getMessage();
        if (nameSettings.containsKey(uuid)) {
            event.setCancelled(true);
            int inputCase = nameSettings.get(uuid);
            switch (inputCase) {
                case 1 -> {
                    Lang.send(player, Lang.GUILD_NAME_CONFIRM, s -> s.replaceAll("<name>", message));
                    nameSettings.put(uuid, 2);
                    typingData.put(uuid, message);
                    return;
                }
                case 2 -> {
                    switch (message) {
                        case "예", "yes" -> {
                            String typedName = typingData.get(uuid);
                            if (Guild.exists(typedName)) {
                                Lang.send(player, Lang.ALREADY_EXISTS_GUILD);
                                clear(uuid);
                                return;
                            }
                            if (!VaultUtil.hasValue(player, Config.GUILD_CREATE_PRICE)) {
                                Lang.send(player, Lang.REQUIRE_MONEY);
                                clear(uuid);
                                return;
                            }
                            Guild.create(player, typedName);
                            Lang.send(player, Lang.CREATE_GUILD);
                            clear(uuid);
                            return;
                        }
                        case "아니요", "no" -> {
                            clear(uuid);
                            Lang.send(player, Lang.CANCEL_CREATE_GUILD);
                            return;
                        }
                    }
                }
                case 3 -> {
                    if (message.length() > MAX_LENGTH) {
                        Lang.send(player, Lang.OVER_LENGTH);
                        return;
                    }
                    if (adminChange.containsKey(uuid)) {
                        Guild guild = Guild.get(adminChange.get(uuid));
                        guild.setDescription(message);
                        clear(uuid);
                        adminChange.remove(uuid);
                        Container.open(player, new GuildAdminManagementContainer());
                        return;
                    }
                    Guild guild = Guild.get(player);
                    guild.setDescription(message);
                    clear(uuid);
                    Container.open(player, new GuildMainContainer(player));
                    return;
                }
            }
            return;
        }
    }

    private void clear(String uuid) {
        nameSettings.remove(uuid);
        typingData.remove(uuid);
    }

}
