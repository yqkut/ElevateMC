package com.elevatemc.ehub.menu.cosmetics;

import com.elevatemc.ehub.eHub;
import com.elevatemc.ehub.profile.Profile;
import com.elevatemc.ehub.type.particle.ParticleType;
import com.elevatemc.ehub.utils.CC;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import com.elevatemc.elib.util.cosmetics.ArmorUtil;
import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ParticlesMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.DARK_AQUA + "Choose Your Particle";
    }

    @Override
    public int size(Player player) {
        return 27;
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

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
                new CosmeticsMenu().openMenu(player);
                Button.playSuccess(player);
            }
        });

        Profile profile = eHub.getInstance().getProfileManager().getByUuid(player.getUniqueId());

        int count = 10;
        for (ParticleType type : ParticleType.values()) {
            buttons.put(count, new Button() {

                @Override
                public String getName(Player player) {
                    return type.getDisplayColor() + type.getName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    return Arrays.asList("",
                            CC.GRAY + "Changes your particle",
                            CC.GRAY + "to " + type.getDisplayColor() + type.getName() + CC.GRAY + " particle.",
                            CC.GRAY + "Left click to select.",
                            "",
                            profile.getParticleType() == type ? CC.GREEN + "That effect is already selected." :
                                    (type.hasPermission(player) ? CC.GRAY + "Click here to select " + CC.B_AQUA + type.getName() + CC.GRAY + "." :
                                            CC.RED + "You don't own this particle."));
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.WOOL;
                }

                @Override
                public byte getDamageValue(Player player) {
                    return (byte) (ArmorUtil.parseColor(type.getColor()));
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    ParticleType type = ParticleType.getByName(ChatColor.stripColor(getName(player)));

                    if (profile.getParticleType() == type) {
                        Button.playFail(player);
                        player.sendMessage(CC.B_RED + type.getName() + CC.RED + " effect is already selected!");
                        return;
                    }

                    if (!type.hasPermission(player)) {
                        Button.playFail(player);
                        player.sendMessage(CC.RED + "You don't have " + CC.B + type.getName() + CC.RED + " particle.");
                        return;
                    }

                    Button.playSuccess(player);

                    profile.setParticleType(type);
                    player.sendMessage(type.getDisplayColor() + type.getName() + CC.YELLOW + " is now set as your particle effect.");
                    player.closeInventory();
                }
            });

            count += 2;
        }

        if (profile.getParticleType() != null) {
            buttons.put(4, new Button() {


                @Override
                public String getName(Player player) {
                    return CC.B_GREEN + "Remove your Particle";
                }

                @Override
                public List<String> getDescription(Player player) {
                    return Arrays.asList("",
                            CC.GRAY + "By clicking this item you will",
                            CC.GRAY + "remove your current particle.",
                            "",
                            CC.RED + "Click here to remove your " + profile.getParticleType().getDisplayColor() + profile.getParticleType().getName() + CC.RED + ".");
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.LEVER;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    player.sendMessage(CC.GREEN + "You have deactivated your " + profile.getParticleType().getDisplayColor() + profile.getParticleType().getName() + CC.GREEN + ".");
                    profile.setParticleType(null);

                    Button.playSuccess(player);
                }
            });
        }

        return buttons;
    }
}
