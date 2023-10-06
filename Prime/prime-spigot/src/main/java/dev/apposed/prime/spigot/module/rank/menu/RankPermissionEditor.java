package dev.apposed.prime.spigot.module.rank.menu;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.pagination.PaginatedMenu;
import com.google.common.collect.ImmutableList;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.rank.Rank;
import dev.apposed.prime.spigot.module.rank.RankHandler;
import dev.apposed.prime.spigot.util.Color;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RankPermissionEditor extends PaginatedMenu {

    private final Prime plugin = Prime.getInstance();
    private final RankHandler rankHandler = plugin.getModuleHandler().getModule(RankHandler.class);

    private final Rank rank;

    public RankPermissionEditor(Rank rank) {
        this.rank = rank;
        setUpdateAfterClick(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Rank Permissions Editor";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return Color.translate("&aAdd Permission");
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
                        return Color.translate("&ePlease type a new permission to add to the rank, or type &c\"cancel\" &eto cancel.");
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext cc, String permission) {
                        if(permission.equalsIgnoreCase("cancel")) {
                            return Prompt.END_OF_CONVERSATION;
                        }

                        rank.getPermissions().add(permission);
                        rankHandler.save(rank);

                        cc.getForWhom().sendRawMessage(Color.translate("&aSuccessfully added &f" + permission + " &ato &r" + rank.getColoredDisplay() + "'s &apermissions."));

                        (new BukkitRunnable() {
                            @Override
                            public void run() {
                                new RankPermissionEditor(rank).openMenu(player);
                            }
                        }).runTask(plugin);

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

        final AtomicInteger slot = new AtomicInteger(0);
        this.rank.getPermissions().forEach(permission -> {
            buttons.put(slot.getAndIncrement(), new Button() {
                @Override
                public String getName(Player player) {
                    return Color.translate("&e" + permission);
                }

                @Override
                public List<String> getDescription(Player player) {
                    return Color.translate(ImmutableList.of(
                            " ",
                            "&c&lRight Click &7to remove"
                    ));
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.PAPER;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    rank.getPermissions().remove(permission);
                    rankHandler.save(rank);
                    player.sendMessage(Color.translate("&aSuccessfully removed &f" + permission + " &afrom &r" + rank.getColoredDisplay() + "'s &apermissions."));
                }
            });
        });

        return buttons;
    }
}