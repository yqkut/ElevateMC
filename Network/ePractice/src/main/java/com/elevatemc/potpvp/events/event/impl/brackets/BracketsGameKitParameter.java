package com.elevatemc.potpvp.events.event.impl.brackets;

import com.elevatemc.potpvp.events.parameter.GameParameter;
import com.elevatemc.potpvp.events.parameter.GameParameterOption;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.stream.Collectors;

public class BracketsGameKitParameter implements GameParameter {

    private static final String DISPLAY_NAME = "Kit";
    private static final List<GameParameterOption> OPTIONS = ImmutableList.of(
            new BracketsGameKitOption(GameModeKit.byId("NO_DEBUFF")),
            new BracketsGameKitOption(GameModeKit.byId("VANILLA")),
            new BracketsGameKitOption(GameModeKit.byId("SOUP"))
    );

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public List<GameParameterOption> getOptions() {
        return OPTIONS;
    }

    public static final class BracketsGameKitOption implements GameParameterOption {

        private final GameModeKit kit;

        public BracketsGameKitOption(GameModeKit kit) {
            this.kit = kit;
        }

        @Override
        public String getDisplayName() {
            return kit.getDisplayName();
        }

        @Override
        public ItemStack getIcon() {
            final ItemStack icon = new ItemStack(kit.getIcon().getItemType());

            icon.setData(kit.getIcon());

            return icon;
        }

        private ItemStack[] getItems() {
            return kit.getDefaultInventory();
        }

        private ItemStack[] getArmor() {
            return kit.getDefaultArmor();
        }

        public void apply(Player player) {
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.getInventory().setArmorContents(getArmor());
            final ItemStack[] items = getItems();

            ItemStack filler = items[7];
            if(filler != null && filler.getType() != Material.POTION && filler.getType() != Material.MUSHROOM_SOUP) {
                filler = new ItemStack(Material.AIR);
            }

//            Bukkit.broadcastMessage(player.getName() + " items size is " + items.length);
//            int potCount = 0, nonSplash = 0;

//            for(int i=0; i<items.length; i++) {
//                final ItemStack item = items[i];
//
//                if(item == null) continue;
//
//                if(item.getType() == Material.ENDER_PEARL) {
//                    items[i] = filler;
//                    continue;
//                }
//
//                if(item.getType() == Material.POTION) {
//                    potCount++;
//                    final Potion potion = Potion.fromItemStack(item);
//
//                    if(potion.isSplash()) continue;
//                    nonSplash++;
//                    Bukkit.broadcastMessage("found " + potion.getEffects()
//                            .stream()
//                            .map(PotionEffect::getType)
//                            .map(PotionEffectType::toString)
//                            .collect(Collectors.joining(", ")) + " in " + player.getName());
//
//                    potion.apply(player);
//                    items[i] = filler;
//                    continue;
//                }
//
//                if(item.getType().isEdible() && item.getType() != Material.MUSHROOM_SOUP && item.getType() != Material.GOLDEN_APPLE) {
//                    items[i] = filler;
//                    continue;
//                }
//
//                if(items[7] != filler && items[8] != filler) {
//                    items[8] = item;
//                    items[7] = filler;
//                }
//            }

            player.getInventory().setContents(getItems());

            if(player.getInventory().contains(Material.BOW) && !player.getInventory().contains(Material.ARROW)) {
                player.getInventory().setItem(17, new ItemStack(Material.ARROW, 10));
            }

//            Bukkit.broadcastMessage(String.format("%s | inventory info | P: %s, NS: %s", player.getName(), potCount, nonSplash));

            player.updateInventory();
        }
    }
}
