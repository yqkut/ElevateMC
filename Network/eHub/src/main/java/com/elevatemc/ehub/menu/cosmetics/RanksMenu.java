package com.elevatemc.ehub.menu.cosmetics;

import com.elevatemc.ehub.eHub;
import com.elevatemc.ehub.profile.Profile;
import com.elevatemc.ehub.type.armor.ArmorType;
import com.elevatemc.ehub.utils.CC;
import com.elevatemc.ehub.utils.ItemBuilder;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.pagination.PaginatedMenu;
import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RanksMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return CC.DARK_AQUA + "Ranks";
    }

    public int getMaxItemsPerPage(Player player) {
        return 36;
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
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        if (!player.hasPermission("core.cosmetic.armor.mod")) {

            buttons.put(18, new Button() {

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
                    new EditorMenu().openMenu(player);
                    Button.playSuccess(player);
                }
            });

            return buttons;
        } else {

            buttons.put(27, new Button() {

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
                    new EditorMenu().openMenu(player);
                    Button.playSuccess(player);
                }
            });

            return buttons;
        }
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        Profile profile = eHub.getInstance().getProfileManager().getByUuid(player.getUniqueId());

        int slot = 0;
        for (ArmorType type : ArmorType.values()) {

            if (!type.hasPermission(player) && !type.isDonator()) {
                continue;
            }

            buttons.put(slot++, new Button() {

                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.LEATHER_CHESTPLATE)
                            .setColor(type == ArmorType.ELEVATE ? Color.fromRGB(type.getR(), type.getG(), type.getB()) : type.getColor())
                            .setName(type.getDisplayColor() + type.getName())
                            .setLore(Arrays.asList("",
                                    CC.AQUA + "Click here to select " + type.getDisplayColor() + type.getName() + CC.AQUA + "."))
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
                    String name = ChatColor.stripColor(getButtonItem(player).getItemMeta().getDisplayName());
                    ArmorType type = ArmorType.valueOf(name.replace(" ", "_").replace("+", "_PLUS").toUpperCase());

                    if (!type.hasPermission(player)) {
                        player.sendMessage(CC.RED + "No permission.");
                        return;
                    }

                    if (profile.getArmorType() == type) {
                        Button.playFail(player);

                        player.sendMessage(type.getDisplayColor() + type.getName() + CC.RED + " is already selected.");
                        return;
                    }

                    profile.setArmorType(type);

                    for (int i = 0; i < 4; i++) {
                        if (profile.isAstronaut() && i == 3) {
                            profile.getArmors()[i] = false;
                        }

                        profile.getArmors()[i] = true;
                    }

                    Button.playSuccess(player);

                    player.closeInventory();
                }
            });
        }

        Map<Integer, Button> formattedButtons = Maps.newHashMap();
        int slotfix = 0, nextCheck = 0;

        for (Button formattedButton : buttons.values()) {
            if (slotfix == nextCheck - 1) {
                slotfix++;
            }

            if (slotfix == nextCheck) {
                nextCheck += 9;

                formattedButtons.put(slotfix, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
                formattedButtons.put(slotfix + 8, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));

                slotfix++;
            }

            formattedButtons.put(slotfix++, formattedButton);
        }

        buttons = formattedButtons;

        return buttons;
    }
}
