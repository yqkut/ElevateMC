package dev.apposed.prime.spigot.module.server.filter.menu;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.pagination.PaginatedMenu;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.profile.grant.menu.GrantMenu;
import dev.apposed.prime.spigot.module.rank.Rank;
import dev.apposed.prime.spigot.module.rank.menu.RankEditMenu;
import dev.apposed.prime.spigot.module.server.filter.ChatFilter;
import dev.apposed.prime.spigot.module.server.filter.ChatFilterHandler;
import dev.apposed.prime.spigot.util.Color;
import dev.apposed.prime.spigot.util.time.DurationUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class ChatFilterEditor extends PaginatedMenu {

    private final Prime plugin = Prime.getInstance();
    private final ChatFilterHandler filterHandler = plugin.getModuleHandler().getModule(ChatFilterHandler.class);

    public ChatFilterEditor() {
        setUpdateAfterClick(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Chat Filters";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new AddChatFilterButton());

        return buttons;
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 36;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        final AtomicInteger slot = new AtomicInteger(0);
        filterHandler.getFilters().forEach(filter -> buttons.put(slot.getAndIncrement(), new ChatFilterButton(filter)));

        return buttons;
    }

    private class AddChatFilterButton extends Button {

        @Override
        public String getName(Player player) {
            return Color.translate("&aCreate Chat Filter");
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
            if(!clickType.isLeftClick()) return;
            player.closeInventory();
            ConversationFactory factory = new ConversationFactory(plugin).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

                @Override
                public String getPromptText(ConversationContext conversationContext) {
                    return Color.translate("&ePlease type a new chat filter regex pattern, or type &c\"cancel\" &eto cancel.");
                }

                @Override
                public Prompt acceptInput(ConversationContext cc, String pattern) {
                    if(pattern.equalsIgnoreCase("cancel")) {
                        cc.getForWhom().sendRawMessage(ChatColor.RED + "Creation cancelled.");
                        return END_OF_CONVERSATION;
                    }

                    (new BukkitRunnable() {
                        @Override
                        public void run() {
                            ConversationFactory factory = new ConversationFactory(plugin).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

                                @Override
                                public String getPromptText(ConversationContext conversationContext) {
                                    return Color.translate("&ePlease type the chat filter's description, or type &c\"cancel\" &eto cancel.");
                                }

                                @Override
                                public Prompt acceptInput(ConversationContext cc, String description) {
                                    if(description.equalsIgnoreCase("cancel")) {
                                        cc.getForWhom().sendRawMessage(ChatColor.RED + "Creation cancelled.");
                                        return END_OF_CONVERSATION;
                                    }

                                    final ChatFilter filter = new ChatFilter(description, pattern);

                                    filterHandler.trackFilter(filter);

                                    (new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            filterHandler.saveFilter(filter);
                                        }
                                    }).runTaskAsynchronously(plugin);

                                    (new BukkitRunnable(){
                                        @Override
                                        public void run() {
                                            ChatFilterEditor.this.openMenu(player);
                                        }
                                    }).runTask(plugin);

                                    return END_OF_CONVERSATION;
                                }
                            }).withLocalEcho(false).withEscapeSequence("/cancel").withTimeout(60).thatExcludesNonPlayersWithMessage("Player's only.");

                            Conversation conversation = factory.buildConversation(player);
                            player.beginConversation(conversation);
                        }
                    }).runTask(plugin);
                    return END_OF_CONVERSATION;
                }
            }).withLocalEcho(false).withEscapeSequence("/cancel").withTimeout(60).thatExcludesNonPlayersWithMessage("Player's only.");

            Conversation conversation = factory.buildConversation(player);
            player.beginConversation(conversation);
        }
    }

    private class ChatFilterButton extends Button {

        private final ChatFilter filter;

        public ChatFilterButton(ChatFilter filter) {
            this.filter = filter;
        }

        @Override
        public String getName(Player player) {
            return Color.translate("&e&l" + filter.getDescription());
        }

        @Override
        public List<String> getDescription(Player player) {
            return Color.translate(Arrays.asList(
                "&7Pattern: " + filter.getPattern().pattern(),
                " ",
                "&a&lLeft Click &7to edit pattern",
                "&c&lRight Click &7to delete filter"
            ));
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.PAPER;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            if(clickType.isLeftClick()) {
                ConversationFactory factory = new ConversationFactory(plugin).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

                    @Override
                    public String getPromptText(ConversationContext conversationContext) {
                        return Color.translate("&ePlease type a new regex pattern, or type &c\"cancel\" &eto cancel.");
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext cc, String pattern) {
                        if(pattern.equalsIgnoreCase("cancel")) {
                            cc.getForWhom().sendRawMessage(ChatColor.RED + "Editing cancelled.");
                            return END_OF_CONVERSATION;
                        }

                        filter.setRegex(pattern);

                        (new BukkitRunnable() {
                            @Override
                            public void run() {
                                filterHandler.saveFilter(filter);
                            }
                        }).runTaskAsynchronously(plugin);

                        (new BukkitRunnable() {
                            @Override
                            public void run() {
                                ChatFilterEditor.this.openMenu(player);
                            }
                        }).runTask(plugin);
                        return END_OF_CONVERSATION;
                    }
                }).withLocalEcho(false).withEscapeSequence("/cancel").withTimeout(60).thatExcludesNonPlayersWithMessage("Player's only.");

                Conversation conversation = factory.buildConversation(player);
                player.beginConversation(conversation);
                return;
            }

            if(clickType.isRightClick()) {
                (new BukkitRunnable() {
                    @Override
                    public void run() {
                        filterHandler.deleteFilter(filter);
                    }
                }).runTaskAsynchronously(plugin);

                (new BukkitRunnable() {
                    @Override
                    public void run() {
                        ChatFilterEditor.this.openMenu(player);
                    }
                }).runTaskLater(plugin, 10L);
                return;
            }
        }
    }
}
