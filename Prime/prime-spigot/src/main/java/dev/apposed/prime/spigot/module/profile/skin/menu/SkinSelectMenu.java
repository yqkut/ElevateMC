package dev.apposed.prime.spigot.module.profile.skin.menu;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.pagination.PaginatedMenu;
import com.google.common.collect.ImmutableList;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.profile.skin.SkinHandler;
import dev.apposed.prime.spigot.util.Color;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkinSelectMenu extends PaginatedMenu {

    private final Prime plugin = Prime.getInstance();
    private final SkinHandler skinHandler = plugin.getModuleHandler().getModule(SkinHandler.class);

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Select Skin";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return Color.translate("&aAdd new skin");
            }

            @Override
            public List<String> getDescription(Player player) {
                return Collections.emptyList();
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NETHER_STAR;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                ConversationFactory factory = new ConversationFactory(plugin).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

                    @Override
                    public String getPromptText(ConversationContext conversationContext) {
                        return Color.translate("&eEnter the username of the skin you would like to apply, or type &c\"cancel\" &eto cancel.");
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext cc, String username) {
                        if(username.equalsIgnoreCase("cancel")) {
                            return Prompt.END_OF_CONVERSATION;
                        }

                        skinHandler.changeSkin(player, username);

                        return Prompt.END_OF_CONVERSATION;
                    }
                }).withLocalEcho(false).withEscapeSequence("/cancel").withTimeout(60).thatExcludesNonPlayersWithMessage("Player's only.");

                Conversation conversation = factory.buildConversation(player);
                player.beginConversation(conversation);
            }
        });
        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        skinHandler.getUsage(player.getUniqueId()).forEach(username -> buttons.put(buttons.size(), new Button() {
            @Override
            public String getName(Player player) {
                return Color.translate("&e&l" + username);
            }

            @Override
            public List<String> getDescription(Player player) {
                return ImmutableList.of(
                        "",
                        Color.translate("&a&lLeft Click &7to apply")
                );
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SKULL_ITEM;
            }

            @Override
            public byte getDamageValue(Player player) {
                return 3;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                if(!clickType.isLeftClick()) return;
                skinHandler.changeSkin(player, username);
            }
        }));

        return buttons;
    }
}
