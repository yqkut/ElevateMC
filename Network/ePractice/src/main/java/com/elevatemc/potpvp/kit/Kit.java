package com.elevatemc.potpvp.kit;

import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchTeam;
import lombok.Getter;
import lombok.Setter;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.pvpclasses.pvpclasses.BardClass;
import com.elevatemc.potpvp.util.ItemUtils;
import com.elevatemc.potpvp.util.PatchedPlayerUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public final class Kit {

    @Getter @Setter private String name;
    @Getter @Setter private int slot; // starts at 1, not 0
    @Getter @Setter private GameModeKit type;
    @Getter @Setter private ItemStack[] inventoryContents;

    public static Kit ofDefaultKitCustomName(GameModeKit gameMode, String name) {
        return ofDefaultKit(gameMode, name, 0);
    }

    public static Kit ofDefaultKit(GameModeKit gameMode) {
        return ofDefaultKit(gameMode, "Default Kit", 0);
    }

    public static Kit ofDefaultKit(GameModeKit gameModeKit, String name, int slot) {
        Kit kit = new Kit();

        kit.setName(name);
        kit.setType(gameModeKit);
        kit.setSlot(slot);
        kit.setInventoryContents(gameModeKit.getDefaultInventory());

        return kit;
    }

    public void apply(Player player, boolean replacePearlsWithPots) {
        PatchedPlayerUtils.resetInventory(player);

        // we don't let players actually customize their armor, we just apply default
        player.getInventory().setArmorContents(GameModeKit.byId(type.getId()).getDefaultArmor());
        player.getInventory().setContents(inventoryContents);

        if (replacePearlsWithPots) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType().equals(Material.ENDER_PEARL)) {
                    item.setAmount(1);
                    item.setType(Material.POTION);
                    item.setDurability((short)16421);
                }
            };
        }

        if (type.getId().startsWith("HCF_")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
        }

        if (type.getId().equals("PEARL_FIGHT")) {
            Match match = PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(player);
            List<MatchTeam> teams = match.getTeams();

            DyeColor white = DyeColor.WHITE;

            for (ItemStack item : player.getInventory().getArmorContents()) {
                if (item != null && item.getType().name().startsWith("LEATHER_")) {
                    LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                    if (teams.size() == 2) {
                        if (teams.get(0) == match.getTeam(player.getUniqueId())) {
                            meta.setColor(Color.BLUE);
                        } else {
                            meta.setColor(Color.RED);
                        }
                    } else {
                        meta.setColor(white.getColor());
                    }
                    item.setItemMeta(meta);
                }
            }

            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType().equals(Material.WOOL)) {
                    if (teams.size() == 2) {
                        if (teams.get(0) == match.getTeam(player.getUniqueId())) {
                            item.setDurability(DyeColor.BLUE.getWoolData());
                        } else {
                            item.setDurability(DyeColor.RED.getWoolData());
                        }
                    } else {
                        // Randomize color
                        item.setDurability(white.getWoolData());
                    }
                }
            }
        }

        if (type.getId().equals("BARD_HCF")) {
            Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> BardClass.getEnergy().put(player.getName(), 100), 1L);
        }

        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), player::updateInventory, 1L);
    }

    public int countHeals() {
        return ItemUtils.countStacksMatching(inventoryContents, ItemUtils.INSTANT_HEAL_POTION_PREDICATE);
    }

    public int countDebuffs() {
        return ItemUtils.countStacksMatching(inventoryContents, ItemUtils.DEBUFF_POTION_PREDICATE);
    }

    public int countFood() {
        return ItemUtils.countStacksMatching(inventoryContents, ItemUtils.EDIBLE_PREDICATE);
    }

    public int countPearls() {
        return ItemUtils.countStacksMatching(inventoryContents, v -> v.getType() == Material.ENDER_PEARL);
    }

    // we use this method instead of .toSelectableBook().isSimilar()
    // to avoid the slight performance overhead of constructing
    // that itemstack every time
    public boolean isSelectionItem(ItemStack itemStack) {
        if (itemStack.getType() != Material.ENCHANTED_BOOK) {
            return false;
        }

        ItemMeta meta = itemStack.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.YELLOW.toString() + ChatColor.BOLD + name);
    }

    public ItemStack createSelectionItem() {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(ChatColor.YELLOW.toString() + ChatColor.BOLD + name);

        item.setItemMeta(itemMeta);
        return item;
    }

}