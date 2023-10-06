package dev.apposed.prime.spigot.module.rank.menu;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import com.google.common.collect.ImmutableList;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.rank.Rank;
import dev.apposed.prime.spigot.module.rank.RankHandler;
import dev.apposed.prime.spigot.util.Color;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RankEditMenu extends Menu {

    private final Prime plugin = Prime.getInstance();
    private final RankHandler rankHandler = plugin.getModuleHandler().getModule(RankHandler.class);

    private final Rank rank;

    public RankEditMenu(Rank rank) {
        this.rank = rank;
        setUpdateAfterClick(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Editing Rank";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        final AtomicInteger slot = new AtomicInteger(0);
        buttons.put(slot.getAndIncrement(), new Button() {
            @Override
            public String getName(Player player) {
                return Color.translate("&6Weight: &f" + rank.getWeight());
            }

            @Override
            public List<String> getDescription(Player player) {
                return Color.translate(ImmutableList.of(
                        " ",
                        "&a&lShift Left Click &7to increase by +10",
                        "&c&lShift Left Click &7to decrease by -10"
                ));
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.FEATHER;
            }

            @Override
            public void clicked(Player player, int slot, ClickType type) {
                switch(type) {
                    // +1
                    case LEFT: {
                        int active = rank.addWeight(1);
                        player.sendMessage(Color.translate("&aSuccessfully added &l1&a weight. &e" + rank.getColoredDisplay() + "&a now has a weight value of &e" + active + "&a."));
                        break;
                    }

                    // -1
                    case RIGHT: {
                        int active = rank.addWeight(-1);
                        player.sendMessage(Color.translate("&aSuccessfully removed &l1&a weight. &e" + rank.getColoredDisplay() + "&a now has a weight value of &e" + active + "&a."));
                        break;
                    }

                    // +10
                    case SHIFT_LEFT: {
                        int active = rank.addWeight(10);
                        player.sendMessage(Color.translate("&aSuccessfully added &l10&a weight. &e" + rank.getColoredDisplay() + "&a now has a weight value of &e" + active + "&a."));
                        break;
                    }

                    // -10
                    case SHIFT_RIGHT: {
                        int active = rank.addWeight(-10);
                        player.sendMessage(Color.translate("&aSuccessfully removed &l10&a weight. &e" + rank.getColoredDisplay() + "&a now has a weight value of &e" + active + "&a."));
                        break;
                    }
                }

                rankHandler.save(rank);
            }
        });

        buttons.put(slot.getAndIncrement(), new Button() {
            @Override
            public String getName(Player player) {
                return Color.translate("&6Color: &f" + rank.getColor()) + rank.getColor().replace("§", "&");
            }

            @Override
            public List<String> getDescription(Player player) {
                return Color.translate(ImmutableList.of(
                        " ",
                        "&a&lLeft Click &7to edit color"
                ));
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NAME_TAG;
            }

            @Override
            public void clicked(Player player, int slot, ClickType type) {
                player.closeInventory();
                ConversationFactory factory = new ConversationFactory(plugin).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

                    @Override
                    public String getPromptText(ConversationContext conversationContext) {
                        return Color.translate("&ePlease type a new color for the rank to be displayed as, or type &c\"cancel\" &eto cancel.");
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext cc, String color) {
                        if(color.equalsIgnoreCase("cancel")) {
                            return Prompt.END_OF_CONVERSATION;
                        }

                        rank.setColor(Color.translate(color));
                        rankHandler.save(rank);

                        cc.getForWhom().sendRawMessage(Color.translate("&aSuccessfully changed the color of &r" + rank.getColoredDisplay() + "&a."));

                        return Prompt.END_OF_CONVERSATION;
                    }
                }).withLocalEcho(false).withEscapeSequence("/cancel").withTimeout(60).thatExcludesNonPlayersWithMessage("Player's only.");

                Conversation conversation = factory.buildConversation(player);
                player.beginConversation(conversation);
            }
        });

        buttons.put(slot.getAndIncrement(), new Button() {
            @Override
            public String getName(Player player) {
                return Color.translate("&6Prefix: &f" + rank.getPrefix() + rank.getColor() + player.getName());
            }

            @Override
            public List<String> getDescription(Player player) {
                return Color.translate(ImmutableList.of(
                        " ",
                        "&a&lLeft Click &7to edit prefix"
                ));
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SIGN;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                ConversationFactory factory = new ConversationFactory(plugin).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

                    @Override
                    public String getPromptText(ConversationContext conversationContext) {
                        return Color.translate("&ePlease type a new prefix for the rank as, or type &c\"cancel\" &eto cancel.");
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext cc, String prefix) {
                        if(prefix.equalsIgnoreCase("cancel")) {
                            return Prompt.END_OF_CONVERSATION;
                        }

                        rank.setPrefix(Color.translate(prefix));
                        rankHandler.save(rank);

                        cc.getForWhom().sendRawMessage(Color.translate("&aSuccessfully changed the prefix of &r" + rank.getColoredDisplay() + "&a."));

                        return Prompt.END_OF_CONVERSATION;
                    }
                }).withLocalEcho(false).withEscapeSequence("/cancel").withTimeout(60).thatExcludesNonPlayersWithMessage("Player's only.");

                Conversation conversation = factory.buildConversation(player);
                player.beginConversation(conversation);
            }
        });

        buttons.put(slot.getAndIncrement(), new Button() {
            @Override
            public String getName(Player player) {
                return Color.translate("&6Metadata: &f" + rank.getMeta().size());
            }

            @Override
            public List<String> getDescription(Player player) {
                return Color.translate(ImmutableList.of(
                        " ",
                        "&a&lLeft Click &7to edit meta"
                ));
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.ANVIL;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                new RankMetaEditor(rank).openMenu(player);
            }
        });

        buttons.put(slot.getAndIncrement(), new Button() {
            @Override
            public String getName(Player player) {
                return Color.translate("&6Permissions: &f" + rank.getPermissions().size());
            }

            @Override
            public List<String> getDescription(Player player) {
                return Color.translate(ImmutableList.of(
                        " ",
                        "&a&lLeft Click &7to edit permissions"
                ));
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.PAPER;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new RankPermissionEditor(rank).openMenu(player);
            }
        });

        buttons.put(slot.getAndIncrement(), new Button() {
            @Override
            public String getName(Player player) {
                return Color.translate("&6Inheritances: &f" + rank.getInherits().size());
            }

            @Override
            public List<String> getDescription(Player player) {
                return Color.translate(ImmutableList.of(
                        " ",
                        "&a&lLeft Click &7to edit inheritances"
                ));
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.EMPTY_MAP;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new RankInheritEditor(rank).openMenu(player);
            }
        });

        buttons.put(slot.getAndIncrement(), new Button() {
            @Override
            public String getName(Player player) {
                return Color.translate("&6Proof Meta: &f" + rank.getProofMeta().size());
            }

            @Override
            public List<String> getDescription(Player player) {
                return Color.translate(ImmutableList.of(
                        " ",
                        "&a&lLeft Click &7to edit proof meta"
                ));
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.DIAMOND_SWORD;
            }


            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new RankProofEditor(rank).openMenu(player);
            }
        });

        return buttons;
    }
}
