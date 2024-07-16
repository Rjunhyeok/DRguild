package kr.squirrel.guild;

import com.earth2me.essentials.Essentials;
import kr.squirrel.guild.commands.GuildCommand;
import kr.squirrel.guild.commands.GuildManagementCommand;
import kr.squirrel.guild.commands.run.GuildRunCommand;
import kr.squirrel.guild.commands.shop.GuildShopManagementCommand;
import kr.squirrel.guild.configurations.Config;
import kr.squirrel.guild.configurations.GuildAttendanceConfig;
import kr.squirrel.guild.configurations.GuildRunConfig;
import kr.squirrel.guild.langs.GuildAttendanceLang;
import kr.squirrel.guild.langs.Lang;
import kr.squirrel.guild.langs.GuildRunLang;
import kr.squirrel.guild.libraries.SimpleInventoryHolder;
import kr.squirrel.guild.listeners.GuildListener;
import kr.squirrel.guild.listeners.run.GuildRunListener;
import kr.squirrel.guild.objects.Guild;
import kr.squirrel.guild.objects.run.GuildRun;
import kr.squirrel.guild.objects.run.GuildRunInGame;
import kr.squirrel.guild.objects.shop.GuildShop;
import kr.squirrel.guild.systems.PlayTime;
import kr.squirrel.guild.systems.PlayerJoinTracker;
import kr.squirrel.guild.systems.PlayerTeleportManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDate;

public final class Main extends JavaPlugin {

    private static Main instance;
    private static Essentials essentials;

    public static Main getInstance() {
        return instance;
    }

    public static Essentials getEssentials() {
        return essentials;
    }

    @Override
    public void onEnable() {
        instance = this;

        prepareLibraries();

        getServer().getPluginManager().registerEvents(new GuildListener(), this);
        getServer().getPluginManager().registerEvents(new GuildRunListener(), this);

        PlayerJoinTracker.readAll();
        PlayerTeleportManager.readAll();

        GuildCommand.register();
        GuildManagementCommand.register();
        Guild.readAll();
        Lang.load();
        Config.load();

        GuildRunCommand.register();

        GuildRunConfig.load();
        GuildRunLang.load();

        GuildRun.readAll();
        GuildRunInGame.readAll();

        GuildAttendanceLang.load();
        GuildAttendanceConfig.load();

        GuildShopManagementCommand.register();
        GuildShop.load();
    }

    @Override
    public void onDisable() {
        PlayerJoinTracker.writeAll();
        PlayerTeleportManager.writeAll();

        PlayTime.saveAll(LocalDate.now());

        GuildRunInGame.saveAll();
    }

    private void prepareLibraries() {
        getServer().getPluginManager().registerEvents(new SimpleInventoryHolder.InventoryHolderHandler(), this);
        getServer().getPluginManager().registerEvents(new PlayTime.PlayTimeHandler(), this);

        boolean enable = getServer().getPluginManager().getPlugin("Essentials") != null;

        if (enable) {
            this.essentials = (Essentials) Essentials.getProvidingPlugin(Essentials.class);
        }

        PlayTime.readAll();
        PlayTime.DateTracker.track();
    }

}
