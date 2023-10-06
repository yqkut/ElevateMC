package com.elevatemc.ehub.menu.cosmetics;

import com.elevatemc.ehub.eHub;
import com.elevatemc.ehub.profile.Profile;
import com.elevatemc.ehub.type.armor.ArmorType;
import com.elevatemc.ehub.type.particle.ParticleType;
import com.elevatemc.ehub.utils.CC;
import com.elevatemc.ehub.utils.ItemBuilder;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CosmeticsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.DARK_AQUA + "Cosmetics";
    }

    @Override
    public int size(Player player) {
        return 9;
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        Profile profile = eHub.getInstance().getProfileManager().getByUuid(player.getUniqueId());
        ArmorType armorType = profile.getArmorType();

        buttons.put(2, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.LEATHER_CHESTPLATE)
                        .setName(CC.AQUA + "Armor")
                        .addEnchantment(Enchantment.DURABILITY)
                        .setLore(Arrays.asList(
                                "",
                                CC.GRAY + "Change your armor design.",
                                "",
                                CC.AQUA + "Selected Armor" + CC.GRAY + ": " + CC.WHITE + (armorType == null ? "None" : armorType.getName()),
                                "",
                                CC.AQUA + "Click to view Armor Designs."))
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
                if (eHub.getInstance().getProfileManager().getByUuid(player.getUniqueId()).getArmorType() == null) {
                    new RanksMenu().openMenu(player);

                } else {

                    new EditorMenu().openMenu(player);
                }
                Button.playSuccess(player);
            }
        });

        ParticleType particleType = profile.getParticleType();

        buttons.put(6, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.NETHER_STAR)
                        .setName(CC.AQUA + "Particles")
                        .setLore(Arrays.asList(
                                "",
                                CC.GRAY + "A lot of different particles",
                                CC.GRAY + "are spawning around you.",
                                "",
                                CC.AQUA + "Selected Particle" + CC.GRAY + ": " + CC.WHITE + (particleType == null ? "None" : particleType.getName().replace(" Particle", "")),
                                "",
                                CC.AQUA + "Click to view list of Particles."))

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
                new ParticlesMenu().openMenu(player);
                Button.playSuccess(player);
            }
        });

        return buttons;
    }
}
