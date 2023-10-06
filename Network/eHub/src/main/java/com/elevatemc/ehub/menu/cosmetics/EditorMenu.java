package com.elevatemc.ehub.menu.cosmetics;

import com.elevatemc.ehub.eHub;
import com.elevatemc.ehub.profile.Profile;
import com.elevatemc.ehub.type.armor.ArmorType;
import com.elevatemc.ehub.utils.CC;
import com.elevatemc.ehub.utils.ItemBuilder;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import com.google.common.collect.Maps;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EditorMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.DARK_AQUA + "Choose your Armor";
    }

    @Override
    public int size(Player player) {
        return 54;
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        Profile profile = eHub.getInstance().getProfileManager().getByUuid(player.getUniqueId());
        ArmorType type = profile.getArmorType();

        buttons.put(14, new Button() {
            @Override
            public String getName(Player player) {
                return CC.B_GREEN + "Choose your armor";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList(
                        "",
                        CC.GRAY + "Changes your armor design.",
                        "",
                        CC.AQUA + "Current Armor" + CC.GRAY + ": " + (type == null ? CC.WHITE + "None" : type.getDisplayColor() + type.getName()),
                        "",
                        CC.GRAY + "Click to change your " + CC.B_AQUA + "Armor Design.");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.PAPER;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new RanksMenu().openMenu(player);
                Button.playSuccess(player);
                };
        });

        buttons.put(45, new Button() {

            @Override
            public String getName(Player player) {
                return CC.B_RED + "Back";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList(
                        "",
                        CC.RED + "Click here to return to",
                        CC.RED + "the previous menu.");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.REDSTONE;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new CosmeticsMenu().openMenu(player);
                Button.playSuccess(player);
            }
        });

        if (type == null) {
            return buttons;
        }

        buttons.put(12, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(type.getItems()[3].clone())
                        .setName(CC.RED + "Helmet")
                        .setLore(Arrays.asList(
                                "",
                                CC.GRAY + "Do you want to have",
                                CC.GRAY + "helmet armor piece on you?",
                                "",
                                profile.getArmors()[3] ?
                                        CC.RED + "Click to turn it off." :
                                        CC.GREEN + "Click to turn it on."))
                        .setColor(type == ArmorType.ELEVATE ? Color.fromRGB(type.getR(), type.getG(), type.getB()) : type.getColor())
                        .get();
            }

            @Override
            public String getName(Player player) {
                return null;
            }

            @Override
            public List<String> getDescription(Player player) {
                return null;
            }

            @Override
            public Material getMaterial(Player player) {
                return null;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                profile.setAstronaut(false);

                profile.getArmors()[3] = !profile.getArmors()[3];
                Button.playFail(player);
            }
        });

        buttons.put(21, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(type.getItems()[2].clone())
                        .setName(CC.RED + "Chestplate")
                        .setLore(Arrays.asList(
                                "",
                                CC.GRAY + "Do you want to have",
                                CC.GRAY + "chestplate armor piece on you?",
                                "",
                                profile.getArmors()[2] ?
                                        CC.RED + "Click to turn it off." :
                                        CC.GREEN + "Click to turn it on."))
                        .setColor(type == ArmorType.ELEVATE ? Color.fromRGB(type.getR(), type.getG(), type.getB()) : type.getColor())
                        .get();
            }

            @Override
            public String getName(Player player) {
                return null;
            }

            @Override
            public List<String> getDescription(Player player) {
                return null;
            }

            @Override
            public Material getMaterial(Player player) {
                return null;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                profile.getArmors()[2] = !profile.getArmors()[2];
                Button.playFail(player);
            }
        });

        buttons.put(30, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(type.getItems()[1].clone())
                        .setName(CC.RED + "Leggings")
                        .setLore(Arrays.asList(
                                "",
                                CC.GRAY + "Do you want to have",
                                CC.GRAY + "leggings armor piece on you?",
                                "",
                                profile.getArmors()[1] ?
                                        CC.RED + "Click to turn it off." :
                                        CC.GREEN + "Click to turn it on."))
                        .setColor(type == ArmorType.ELEVATE ? Color.fromRGB(type.getR(), type.getG(), type.getB()) : type.getColor())
                        .get();
            }

            @Override
            public String getName(Player player) {
                return null;
            }

            @Override
            public List<String> getDescription(Player player) {
                return null;
            }

            @Override
            public Material getMaterial(Player player) {
                return null;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                profile.getArmors()[1] = !profile.getArmors()[1];
                Button.playFail(player);
            }
        });

        buttons.put(39, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(type.getItems()[0].clone())
                        .setName(CC.RED + "Boots")
                        .setLore(Arrays.asList(
                                "",
                                CC.GRAY + "Do you want to have",
                                CC.GRAY + "boots armor piece on you?",
                                "",
                                profile.getArmors()[0] ?
                                        CC.RED + "Click to turn it off." :
                                        CC.GREEN + "Click to turn it on."))
                        .setColor(type == ArmorType.ELEVATE ? Color.fromRGB(type.getR(), type.getG(), type.getB()) : type.getColor())
                        .get();
            }

            @Override
            public String getName(Player player) {
                return null;
            }

            @Override
            public List<String> getDescription(Player player) {
                return null;
            }

            @Override
            public Material getMaterial(Player player) {
                return null;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                profile.getArmors()[0] = !profile.getArmors()[0];
                Button.playFail(player);
            }
        });

        buttons.put(23, new Button() {
            @Override
            public String getName(Player player) {
                return (profile.isEnchanted() ? CC.B_RED : CC.B_GREEN) + "Enchantment";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList(
                        "",
                        CC.GRAY + "Do you want to have",
                        CC.GRAY + "your armor enchanted?",
                        "",
                        profile.isEnchanted() ?
                                CC.RED + "Click to turn it off." :
                                CC.GREEN + "Click to turn it on.");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.INK_SACK;
            }

            @Override
            public byte getDamageValue(Player player) {
                return (byte) (profile.isEnchanted() ? 1 : 10);
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                profile.setEnchanted(!profile.isEnchanted());
                Button.playFail(player);
            }
        });

        buttons.put(32, new Button() {

            @Override
            public String getName(Player player) {
                return (profile.isAstronaut() ? CC.B_RED : CC.B_GREEN) + "Astronaut Helmet";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList(
                        "",
                        CC.GRAY + "Do you want to have",
                        CC.GRAY + "Astronaut Helmet placed",
                        CC.GRAY + "on your head?",
                        "",
                        profile.isAstronaut() ?
                                CC.RED + "Click to turn it off." :
                                CC.GREEN + "Click to turn it on.");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.INK_SACK;
            }

            @Override
            public byte getDamageValue(Player player) {
                return (byte) (profile.isAstronaut() ? 1 : 10);
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                profile.setAstronaut(!profile.isAstronaut());
                profile.getArmors()[3] = !profile.isAstronaut();

                Button.playFail(player);
            }
        });

        buttons.put(41, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.INK_SACK)
                        .setName(check(profile) ? CC.B_RED + "All Armor Pieces" : CC.B_GREEN + "All Armor Pieces")
                        .setLore(Arrays.asList(
                                "",
                                CC.GRAY + "Do you want to have",
                                CC.GRAY + "all armor pieces",
                                CC.GRAY + "placed on you?",
                                "",
                                areAllTrue(profile.getArmors()) ?
                                        CC.RED + "Click to remove them." :
                                        CC.GREEN + "Click to add them."))
                        .setDurability(check(profile) ? 1 : 10)
                        .get();
            }

            @Override
            public String getName(Player player) {
                return null;
            }

            @Override
            public List<String> getDescription(Player player) {
                return null;
            }

            @Override
            public Material getMaterial(Player player) {
                return null;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if (profile.isAstronaut() && areAllTrue(profile.getArmors())) {
                    profile.setAstronaut(false);
                }

                if (areAllTrue(profile.getArmors())) {
                    for (int i = 0; i < 4; i++) {
                        profile.getArmors()[i] = false;
                    }
                } else {
                    for (int i = 0; i < 4; i++) {
                        profile.getArmors()[i] = true;
                    }
                }

                Button.playFail(player);
            }
        });

        return buttons;
    }

    private boolean check(Profile profile) {
        profile.getArmors()[3] = profile.isAstronaut() || profile.getArmors()[3];

        return areAllTrue(profile.getArmors());
    }

    private boolean areAllTrue(boolean[] array) {
        for (boolean value : array) {
            if (!value) {
                return false;
            }
        }

        return true;
    }
}
