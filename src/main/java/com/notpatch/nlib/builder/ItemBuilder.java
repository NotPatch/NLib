package com.notpatch.nlib.builder;

import lombok.Builder;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemBuilder {

    private Material material;
    private int amount;
    private String displayName;
    private List<String> lore;
    private Map<Enchantment, Integer> enchantments;
    private List<ItemFlag> itemFlags;
    private boolean unbreakable;
    private boolean glow;
    private int customModelData;

    public ItemStack build() {
        ItemStack item = new ItemStack(material != null ? material : Material.BARRIER,
                amount > 0 ? amount : 1);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            if (displayName != null) {
                meta.setDisplayName(displayName);
            }

            if (lore != null && !lore.isEmpty()) {
                meta.setLore(lore);
            }

            meta.setUnbreakable(unbreakable);

            if (customModelData > 0) {
                meta.setCustomModelData(customModelData);
            }

            if (itemFlags != null) {
                for (ItemFlag flag : itemFlags) {
                    meta.addItemFlags(flag);
                }
            }

            if(glow){
                meta.addEnchant(Enchantment.FLAME, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            item.setItemMeta(meta);
        }

        if (enchantments != null) {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                item.addUnsafeEnchantment(entry.getKey(), entry.getValue());
            }
        }

        return item;
    }

    public ItemBuilder addLore(String loreLine) {
        if (this.lore == null) {
            this.lore = new ArrayList<>();
        }
        this.lore.add(loreLine);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        if (this.enchantments == null) {
            this.enchantments = new HashMap<>();
        }
        this.enchantments.put(enchantment, level);
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag flag) {
        if (this.itemFlags == null) {
            this.itemFlags = new ArrayList<>();
        }
        this.itemFlags.add(flag);
        return this;
    }

    public static ItemStack getItemFromSection(ConfigurationSection section) {
        ItemBuilder builder = new ItemBuilder();
        builder.material = Material.getMaterial(section.getString("material", "BARRIER").toUpperCase());
        builder.amount = section.getInt("amount", 1);
        builder.displayName = section.getString("name", null);
        builder.lore = section.getStringList("lore");
        builder.unbreakable = section.getBoolean("unbreakable", false);
        builder.customModelData = section.getInt("custom-model-data", 0);
        builder.glow = section.getBoolean("glow", false);

        if (section.isConfigurationSection("enchantments")) {
            ConfigurationSection enchantSection = section.getConfigurationSection("enchantments");
            if (enchantSection != null) {
                for (String key : enchantSection.getKeys(false)) {
                    Enchantment enchantment = Enchantment.getByName(key.toUpperCase());
                    int level = enchantSection.getInt(key, 1);
                    if (enchantment != null) {
                        builder.addEnchantment(enchantment, level);
                    }
                }
            }
        }

        if (section.isList("item-flags")) {
            List<String> flags = section.getStringList("item-flags");
            for (String flagStr : flags) {
                try {
                    ItemFlag flag = ItemFlag.valueOf(flagStr.toUpperCase());
                    builder.addItemFlag(flag);
                } catch (IllegalArgumentException e) {
                }
            }
        }

        return builder.build();
    }

}