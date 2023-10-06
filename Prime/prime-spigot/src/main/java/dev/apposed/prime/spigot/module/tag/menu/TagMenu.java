package dev.apposed.prime.spigot.module.tag.menu;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.pagination.PaginatedMenu;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import dev.apposed.prime.spigot.module.tag.TagHandler;
import dev.apposed.prime.spigot.util.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TagMenu extends PaginatedMenu {

    private final Prime plugin = JavaPlugin.getPlugin(Prime.class);
    private final ProfileHandler profileHandler = plugin.getModuleHandler().getModule(ProfileHandler.class);
    private final TagHandler tagHandler = plugin.getModuleHandler().getModule(TagHandler.class);

    public TagMenu() {
        this.setUpdateAfterClick(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Tags";
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
        tagHandler.getTags().values().stream().filter(tag -> player.hasPermission(TagHandler.BASE_PERMISSION + tag.getId())).forEach(tag -> {
            buttons.put(slot.getAndIncrement(), new Button() {

                @Override
                public String getName(Player player) {
                    return Color.translate(tag.getDisplay());
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> description = new ArrayList<>();
                    if(player.hasPermission(TagHandler.BASE_PERMISSION + tag.getId()))
                        description.add("&7Click to " + (profile.hasActiveTag() && profile.getActiveTag() == tag ? "&cunequip" : "&aequip") + " &7" + tag.getDisplayName());
                    else
                        description.add("&cYou do not have permission to use this tag");
                    return Color.translate(description);
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.NAME_TAG;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (clickType != ClickType.LEFT) return;
                    if (profile.hasActiveTag() && profile.getActiveTag() == tag) {
                        player.sendMessage(Color.translate("&aYou have unequipped the &r" + tag.getDisplayName() + "&a tag!"));
                        profile.setTag(null);
                    } else if (!player.hasPermission(TagHandler.BASE_PERMISSION + tag.getId())) {
                        player.sendMessage(Color.translate("&cYou do not have permission to equip this tag!"));
                        return;
                    } else {
                        player.sendMessage(Color.translate("&aYou have equipped the &r" + tag.getDisplayName() + " &atag!"));
                        profile.setTag(tag.getId());
                    }
                    profileHandler.sendSync(profile);
                }
            });
        });

        return buttons;
    }
}
