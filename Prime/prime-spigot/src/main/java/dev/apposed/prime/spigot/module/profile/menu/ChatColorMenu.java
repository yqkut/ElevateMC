package dev.apposed.prime.spigot.module.profile.menu;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.pagination.PaginatedMenu;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import dev.apposed.prime.spigot.util.Color;
import dev.apposed.prime.spigot.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatColorMenu extends PaginatedMenu {

    private final Prime plugin = JavaPlugin.getPlugin(Prime.class);
    private final ProfileHandler profileHandler = plugin.getModuleHandler().getModule(ProfileHandler.class);
    private final List<ChatColor> colors = Arrays.asList(
            ChatColor.DARK_GREEN,
            ChatColor.DARK_AQUA,
            ChatColor.DARK_RED,
            ChatColor.DARK_PURPLE,
            ChatColor.GOLD,
            ChatColor.GRAY,
            ChatColor.DARK_GRAY,
            ChatColor.BLUE,
            ChatColor.GREEN,
            ChatColor.AQUA,
            ChatColor.RED,
            ChatColor.LIGHT_PURPLE,
            ChatColor.YELLOW,
            ChatColor.WHITE
    );

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Select a Color";
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

        final AtomicInteger slot = new AtomicInteger(0);
        for(ChatColor color : colors) {
            buttons.put(slot.getAndIncrement(), new Button() {
                @Override
                public String getName(Player player) {
                    return Color.translate(color + color.name());
                }

                @Override
                public List<String> getDescription(Player player) {
                    if(player.hasPermission("prime.color." + color.name().toLowerCase())) {
                        return Color.translate(Collections.singletonList("&7Click to select " + color + color.name()));
                    } else {
                        return Color.translate(Collections.singletonList("&cYou do not have access to this color."));
                    }
                }

                @Override
                public Material getMaterial(Player player) {
                    return getWool(color).getType();
                }

                @Override
                public byte getDamageValue(Player player) {
                    return (byte) getWool(color).getDurability();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (clickType != ClickType.LEFT) return;
                    if(!player.hasPermission("prime.color." + color.name().toLowerCase())) {
                        player.sendMessage(Color.translate("&cYou do not have access to this color."));
                        return;
                    }

                    profile.setChatColor(color);
                    profileHandler.sendSync(profile);
                    player.sendMessage(Color.translate("&aYou have selected " + color + color.name()));
                }
            });
        }

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
