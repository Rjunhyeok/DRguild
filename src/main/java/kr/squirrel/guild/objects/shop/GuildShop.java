package kr.squirrel.guild.objects.shop;

import kr.squirrel.guild.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GuildShop {

    private static final File FILE = new File(Main.getInstance().getDataFolder(), "shop.yml");

    private static List<ShopContent> contents;

    public static void load() {
        contents = new ArrayList<>();
        if (!FILE.exists()) {
            return;
        }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(FILE);
        for (String key : yamlConfiguration.getConfigurationSection("contents").getKeys(false)) {
            ConfigurationSection section = yamlConfiguration.getConfigurationSection("contents." + key);
            ItemStack itemStack = section.getItemStack("item");
            long price = section.getLong("price");
            contents.add(new ShopContent(itemStack, price));
        }
    }

    public static void save() {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        for (int i = 0; i < contents.size(); i++) {
            yamlConfiguration.set("contents." + i + ".item", contents.get(i).getItemStack());
            yamlConfiguration.set("contents." + i + ".price", contents.get(i).getItemStack());
        }
        try {
            yamlConfiguration.save(FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<ShopContent> getContents() {
        return contents;
    }

    public static class ShopContent {
        private ItemStack itemStack;
        private long price;

        public ShopContent(ItemStack itemStack, long price) {
            this.itemStack = itemStack;
            this.price = price;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public void setItemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        public long getPrice() {
            return price;
        }

        public void setPrice(long price) {
            this.price = price;
        }
    }

}
