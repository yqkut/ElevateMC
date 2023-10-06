package dev.apposed.prime.spigot.module.rank.menu;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.pagination.PaginatedMenu;
import com.google.common.collect.ImmutableList;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.rank.Rank;
import dev.apposed.prime.spigot.module.rank.RankHandler;
import dev.apposed.prime.spigot.module.rank.meta.RankMeta;
import dev.apposed.prime.spigot.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RankEditorMenu extends PaginatedMenu {

    private final Prime plugin = JavaPlugin.getPlugin(Prime.class);
    private final RankHandler rankHandler = plugin.getModuleHandler().getModule(RankHandler.class);

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Rank Editor";
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 27;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return Color.translate("&aCreate Rank");
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
                        return Color.translate("&ePlease type a name for this rank, or type &c\"cancel\" &eto cancel.");
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext cc, String name) {
                        if(name.equalsIgnoreCase("cancel")) {
                            cc.getForWhom().sendRawMessage(ChatColor.RED + "Creation cancelled.");
                            return END_OF_CONVERSATION;
                        }

                        (new BukkitRunnable() {
                            @Override
                            public void run() {
                                Rank rank = rankHandler.create(new Rank(name));
                                new RankEditMenu(rank).openMenu(player);
                                cc.getForWhom().sendRawMessage(Color.translate("&aCreated rank &r" + rank.getColoredDisplay() + "&a."));
                            }
                        }).runTask(plugin);
                        return END_OF_CONVERSATION;
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

        final AtomicInteger slot = new AtomicInteger(0);
        this.rankHandler.getCache()
                .stream()
                .sorted((a, b) -> b.getWeight() - a.getWeight())
                .forEach(rank -> {
                   buttons.put(slot.getAndIncrement(), new Button() {
                       @Override
                       public String getName(Player player) {
                           return rank.getColoredDisplay();
                       }

                       @Override
                       public List<String> getDescription(Player player) {
                           return Color.translate(ImmutableList.of(
                                   Color.SPACER_SHORT,
                                   "&7Name: &f" + rank.getName(),
                                   "&7Color: &f" + rank.getColor() + rank.getColor().replace("§", ""),
                                   "&7Weight: &f" + rank.getWeight(),
                                   "&7Prefix: &f" + rank.getPrefix() + rank.getColor() + player.getName(),
                                   "&7List Name: &f" + rank.getColor() + player.getName(),
                                   "&7Default: &f" + rank.hasMeta(RankMeta.DEFAULT, true),
                                   "&7Hidden: &f" + rank.hasMeta(RankMeta.HIDDEN, true),
                                   "&7Inheritances: &f" + rank.getInherits().size(),
                                   " ",
                                   "&a&lLeft Click &7to edit",
                                   "&b&lRight Click &7to show details",
                                   "&c&lShift Right Click &7to delete",
                                   Color.SPACER_SHORT
                           ));
                       }

                       @Override
                       public Material getMaterial(Player player) {
                           return rank.getWool().getType();
                       }

                       @Override
                       public byte getDamageValue(Player player) {
                           return (byte) rank.getWool().getDurability();
                       }

                       @Override
                       public void clicked(Player player, int slot, ClickType type) {
                           switch(type) {
                               case LEFT: {
                                   new RankEditMenu(rank).openMenu(player);
                                   break;
                               }
                               case RIGHT: {
                                   Color.translate(Arrays.asList(
                                           Color.SPACER_LONG,
                                           Color.translate("&e&lDetailed information of " + rank.getName()),
                                           Color.translate("&7Name: &f" + rank.getName()),
                                           Color.translate("&7Color: &f" + rank.getColor()) + rank.getColor().replace("§", "&"),
                                           Color.translate("&7Weight: &f" + rank.getWeight()),
                                           Color.translate("&7Prefix: &f" + rank.getPrefix() + rank.getColor() + player.getName()),
                                           Color.translate("&7List Name: &f" + rank.getColor() + player.getName()),
                                           Color.translate("&7Default: &f" + rank.hasMeta(RankMeta.DEFAULT, true)),
                                           Color.translate("&7Hidden: &f" + rank.hasMeta(RankMeta.HIDDEN, true)),
                                           " ",
                                           Color.translate("&e&lInherits: &f" + String.join(", ", rank.getInherits().stream().map(Rank::getName).collect(Collectors.toList()))),
                                           " ",
                                           Color.translate("&e&lPermissions: &f" + String.join(", ", rank.getPermissions())),
                                           Color.SPACER_LONG
                                   )).forEach(player::sendMessage);
                                   break;
                               }
                               case SHIFT_RIGHT: {
                                   player.closeInventory();
                                   rankHandler.delete(rank);
                                   player.sendMessage(Color.translate("&aSuccessfully deleted &r" + rank.getColoredDisplay() + "&a."));
                                   break;
                               }
                           }
                       }
                   });
                });


        return buttons;
    }
}
