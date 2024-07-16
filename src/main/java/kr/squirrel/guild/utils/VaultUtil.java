package kr.squirrel.guild.utils;

import com.earth2me.essentials.api.Economy;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class VaultUtil {

    public static void addValue(Player player, double value) throws Exception {
        Economy.add(player.getUniqueId(), BigDecimal.valueOf(value));
    }

    public static void subtractValue(Player player, double value) throws Exception {
        Economy.subtract(player.getUniqueId(), BigDecimal.valueOf(value));
    }

    public static void setValue(Player player, double value) throws Exception {
        Economy.setMoney(player.getUniqueId(), BigDecimal.valueOf(value));
    }

    public static boolean hasValue(Player player, double value) throws Exception {
        return Economy.getMoneyExact(player.getUniqueId()).doubleValue() >= value;
    }

    public static double getValue(Player player) throws Exception {
        return Economy.getMoneyExact(player.getUniqueId()).doubleValue();
    }

}
