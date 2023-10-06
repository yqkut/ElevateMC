package dev.apposed.prime.spigot.module.profile.punishment.menu;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.pagination.PaginatedMenu;
import com.google.common.collect.ImmutableList;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.PrimeConstants;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import dev.apposed.prime.spigot.module.profile.punishment.Punishment;
import dev.apposed.prime.spigot.module.profile.punishment.evidence.PunishmentEvidence;
import dev.apposed.prime.spigot.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PunishmentEvidenceMenu extends PaginatedMenu {

    private final Prime plugin = Prime.getInstance();
    private final ProfileHandler profileHandler = plugin.getModuleHandler().getModule(ProfileHandler.class);

    private final Profile profile;
    private final Punishment punishment;

    public PunishmentEvidenceMenu(Profile profile, Punishment punishment) {
        this.profile = profile;
        this.punishment = punishment;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Punishment Evidence";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return Color.translate("&aAdd Evidence");
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
                        return Color.translate("&ePlease enter the link of the evidence to add to the punishment, or type &c\"cancel\" &eto cancel.");
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext cc, String link) {
                        if(link.equalsIgnoreCase("cancel")) {
                            return Prompt.END_OF_CONVERSATION;
                        }

                        punishment.addEvidence(link, player.getUniqueId(), System.currentTimeMillis());
                        profileHandler.sendSync(profile);

                        cc.getForWhom().sendRawMessage(Color.translate("&aSuccessfully added the evidence"));

                        (new BukkitRunnable() {
                            @Override
                            public void run() {
                                new PunishmentEvidenceMenu(profile, punishment).openMenu(player);
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
        this.punishment.getEvidence().forEach(evidence -> {
            buttons.put(slot.getAndIncrement(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.GOLD + evidence.getLink();
                }

                @Override
                public List<String> getDescription(Player player) {
                    return Color.translate(ImmutableList.of(
                            Color.SPACER_SHORT,
                            "&eAdded By: &c" + (evidence.getAddedBy().equals(PrimeConstants.CONSOLE_UUID) ?
                                    "&4&lConsole" : profileHandler.getProfile(evidence.getAddedBy()).isPresent() ?
                                    profileHandler.getProfile(evidence.getAddedBy()).get().getColoredName() : "Unknown"),
                            Color.SPACER_SHORT
                    ));
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.PAPER;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    player.sendMessage(Color.translate("&a&lEvidence: &f" + evidence.getLink()));
                }
            });
        });

        return buttons;
    }
}
