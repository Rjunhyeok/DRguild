package kr.squirrel.guild.commands.shop;

import kr.squirrel.guild.guis.Container;
import kr.squirrel.guild.guis.shop.GuildShopManagementContainer;
import kr.squirrel.guild.libraries.SimpleCommandBuilder;
import kr.squirrel.guild.objects.shop.GuildShop;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GuildShopManagementCommand {

    public static void register() {
        new SimpleCommandBuilder("길드상점관리")
                .aliases("guild-shop-management")
                .permission("op")
                .commandExecutor((sender, command, label, args) -> {
                    if (sender instanceof Player player) {
                        if (args.length == 0) {
                            Container.open(player, new GuildShopManagementContainer());
                            return false;
                        }
                        if (args[0].equalsIgnoreCase("등록")) {
                            if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
                                player.sendMessage("§c아이템을 들고 입력해주세요");
                                return false;
                            }
                            ItemStack itemStack = player.getItemInHand();
                            long price = Long.parseLong(args[1]);
                            GuildShop.getContents().add(new GuildShop.ShopContent(itemStack, price));
                            player.sendMessage("추가 완료");
                            return false;
                        }
                    }
                    return false;
                })
                .tabCompleter((sender, command, label, args) -> null)
                .register();
    }

}
