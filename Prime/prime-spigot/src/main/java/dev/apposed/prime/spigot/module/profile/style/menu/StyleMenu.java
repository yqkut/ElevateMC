package dev.apposed.prime.spigot.module.profile.style.menu;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.pagination.PaginatedMenu;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import dev.apposed.prime.spigot.module.server.ServerHandler;
import dev.apposed.prime.spigot.util.Color;
import dev.apposed.prime.spigot.util.ItemBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class StyleMenu extends PaginatedMenu {

    private final Prime plugin = JavaPlugin.getPlugin(Prime.class);
    private final ProfileHandler profileHandler = plugin.getModuleHandler().getModule(ProfileHandler.class);
    private final ServerHandler serverHandler = plugin.getModuleHandler().getModule(ServerHandler.class);

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Select Style";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        final Profile profile = profileHandler.getProfile(player.getUniqueId()).orElse(null);
        if(profile == null) {
            buttons.put(0, new Button() {
                @Override
                public String getName(Player player) {
                    return Color.translate("&cError");
                }

                @Override
                public List<String> getDescription(Player player) {
                    return Collections.singletonList("Failed to fetch user's profile");
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.PAPER;
                }
            });
            return buttons;
        }

        serverHandler.getStyles().forEach((id, style) -> buttons.put(buttons.size(), new Button() {
            @Override
            public String getName(Player player) {
                return style.getKey().toString() + ChatColor.BOLD + id;
            }

            @Override
            public List<String> getDescription(Player player) {
                return Color.translate(Arrays.asList(
                        " ",
                        "&7Main: &r" + style.getKey().toString() + StringUtils.capitalize(style.getKey().name().toLowerCase().replace("_", " ")),
                        "&7Sub: &r" + style.getValue().toString() + StringUtils.capitalize(style.getValue().name().toLowerCase().replace("_", " ")),
                        " ",
                        "&7Click to select " + style.getKey().toString() + id
                ));
            }

            @Override
            public Material getMaterial(Player player) {
                return getWool(style.getKey()).getType();
            }

            @Override
            public byte getDamageValue(Player player) {
                return (byte) getWool(style.getKey()).getDurability();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if(!clickType.isLeftClick()) return;

                if(profile.hasStyle() && profile.getStyle().equalsIgnoreCase(id)) {
                    player.sendMessage(Color.translate("&cYou already have that style selected!"));
                    return;
                }

                profile.setStyle(id.toUpperCase());
                player.sendMessage(Color.translate("&aYou have selected the " + style.getKey() + id + " Style&a!"));
                profileHandler.sendSync(profile);
            }
        }));

        return buttons;
    }

    public ItemStack getWool(ChatColor color) {
        final ItemBuilder itemBuilder = new ItemBuilder(Material.WOOL);

        if(color == ChatColor.DARK_GREEN) {
            itemBuilder.dur(13);
        } else if (color == ChatColor.DARK_AQUA) {
            itemBuilder.dur(9);
        }else if(color == ChatColor.DARK_RED){
            itemBuilder.dur(14);
        }else if(color == ChatColor.DARK_PURPLE){
            itemBuilder.dur(10);
        }else if(color == ChatColor.GOLD){
            itemBuilder.dur(1);
        }else if(color == ChatColor.GRAY){
            itemBuilder.dur(8);
        }else if(color == ChatColor.DARK_GRAY){
            itemBuilder.dur(7);
        }else if(color == ChatColor.BLUE){
            itemBuilder.dur(11);
        }else if(color == ChatColor.GREEN){
            itemBuilder.dur(5);
        }else if(color == ChatColor.AQUA){
            itemBuilder.dur(3);
        }else if(color == ChatColor.RED){
            itemBuilder.dur(14);
        }else if(color == ChatColor.LIGHT_PURPLE){
            itemBuilder.dur(2);
        }else if(color == ChatColor.YELLOW){
            itemBuilder.dur(4);
        }

        return itemBuilder.build();
    }
}
