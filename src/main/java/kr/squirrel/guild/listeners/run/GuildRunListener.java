package kr.squirrel.guild.listeners.run;

import kr.squirrel.guild.objects.LocationDTO;
import kr.squirrel.guild.objects.run.GuildRun;
import kr.squirrel.guild.objects.run.GuildRunInGame;
import kr.squirrel.guild.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class GuildRunListener implements Listener {

    @EventHandler
    public void onJoin(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!LocationUtil.isMoved(event.getFrom(), event.getTo())) {
            return;
        }
        GuildRunInGame game = GuildRunInGame.isGaming(player);
        if (game == null) {
            return;
        }
        LocationDTO locationDTO = GuildRun.getMap(game.getName()).getEndPoint();
        Location to = event.getTo();
        Location blockLocation = locationDTO.toLocation();
        if (
                blockLocation.getWorld().getName().equals(to.getWorld().getName()) &&
                        blockLocation.getBlockX() == to.getBlockX() &&
                        blockLocation.getBlockY() == to.getBlockY() &&
                        blockLocation.getBlockZ() == to.getBlockZ()
        ) {
            if (game.getCompletedPlayer().contains(player.getUniqueId())) {
                return;
            }
            if (game.getCompletedPlayer().size() == 0) {
                game.setRemainSecond(21);
                game.join(player);
            } else {
                for (Player user : game.getPlayers()) {
                    if (game.getCompletedPlayer().contains(user.getUniqueId())) {
                        continue;
                    }
                    user.sendMessage(player.getName() + "님이 " + (game.getCompletedPlayer().size() + 1) + "등으로 도착하였습니다");
                }
            }
            game.getCompletedPlayer().add(player.getUniqueId());
        }
    }
}
