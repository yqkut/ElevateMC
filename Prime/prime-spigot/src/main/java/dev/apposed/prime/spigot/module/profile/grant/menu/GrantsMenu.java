package dev.apposed.prime.spigot.module.profile.grant.menu;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.pagination.PaginatedMenu;
import com.elevatemc.elib.util.UUIDUtils;
import com.google.common.collect.ImmutableList;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.PrimeConstants;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import dev.apposed.prime.spigot.module.profile.grant.Grant;
import dev.apposed.prime.spigot.module.rank.meta.RankMeta;
import dev.apposed.prime.spigot.util.Color;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GrantsMenu extends PaginatedMenu {

    private final Prime plugin = JavaPlugin.getPlugin(Prime.class);
    private final ProfileHandler profileHandler = plugin.getModuleHandler().getModule(ProfileHandler.class);
    private final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm");

    private final Profile profile;
    private final List<Grant> grants;
    private boolean showReceived = false;

    public GrantsMenu(Profile profile, List<Grant> grants) {
        this.profile = profile;
        this.grants = grants;
        format.setTimeZone(TimeZone.getTimeZone("America/New_York"));
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return profile.getUsername() + "'s Grants";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        final AtomicInteger slot = new AtomicInteger(0);
        this.grants.stream()
                .sorted(Comparator.comparingLong(Grant::getAddedAt).reversed())
                .filter(grant -> !grant.getRank().hasMeta(RankMeta.DEFAULT, true))
                .forEach(grant -> {
                    buttons.put(slot.getAndIncrement(), new Button() {
                        @Override
                        public String getName(Player player) {
                            return ChatColor.GOLD + format.format(grant.getAddedAt());
                        }

                        @Override
                        public List<String> getDescription(Player player) {
                            final List<String> lore = new ArrayList<>();

                            lore.add(Color.SPACER_SHORT);

                            if(showReceived) {
                                final Optional<Profile> profileOptional = profileHandler.getProfile(grant.getPlayer());
                                if(!profileOptional.isPresent()) {
                                    lore.add("&ePlayer: &cUnknown");
                                } else {
                                    final Profile profile = profileOptional.get();
                                    lore.add("&ePlayer: &c" + profile.getColoredName());
                                }
                            }

                            lore.addAll(Arrays.asList(
                                    "&eBy: &c" + (grant.getAddedBy().equals(PrimeConstants.CONSOLE_UUID) ?
                                            "&4&lConsole" : UUIDUtils.name(grant.getAddedBy())),
                                    "&eRank: &r" + grant.getRank().getColoredDisplay(),
                                    "&eScopes: &c" + String.join(", ", grant.getScopes()),
                                    "&eReason: &c" + grant.getAddedReason(),
                                    "&eRemaining: &c" + grant.formatDuration(),
                                    Color.SPACER_SHORT
                            ));

                            if(grant.isRemoved()) {
                                lore.add("&c&lRemoved");
                                lore.add(" ");
                                lore.add("&eBy: &c" + (grant.getRemovedBy().equals(PrimeConstants.CONSOLE_UUID) ?
                                        "&4&lConsole" : UUIDUtils.name(grant.getRemovedBy())));
                                lore.add("&eReason: &c" + grant.getRemovedReason());
                                lore.add(" ");
                                lore.add(ChatColor.GOLD + format.format(grant.getRemovedAt()));
                                lore.add(Color.SPACER_SHORT);
                            }else if(player.hasPermission("prime.grants.remove." + grant.getRank().getName())) {
                                lore.add("&cClick to remove grant.");
                                lore.add(Color.SPACER_SHORT);
                            }

                            return Color.translate(lore);
                        }

                        @Override
                        public Material getMaterial(Player player) {
                            return grant.getItemStack().getType();
                        }

                        @Override
                        public byte getDamageValue(Player player) {
                            return (byte) grant.getItemStack().getDurability();
                        }

                        @Override
                        public void clicked(Player player, int slot, ClickType clickType) {
                            player.closeInventory();
                            ConversationFactory factory = new ConversationFactory(plugin).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

                                @Override
                                public String getPromptText(ConversationContext conversationContext) {
                                    return Color.translate("&ePlease type a reason for this grant to be removed, or type &c\"cancel\" &eto cancel.");
                                }

                                @Override
                                public Prompt acceptInput(ConversationContext cc, String reason) {
                                    if(reason.equalsIgnoreCase("cancel")) {
                                        cc.getForWhom().sendRawMessage(ChatColor.RED + "Granting cancelled.");
                                        return END_OF_CONVERSATION;
                                    }
                                    grant.setRemoved(true);
                                    grant.setRemovedBy(player.getUniqueId());
                                    grant.setRemovedAt(System.currentTimeMillis());
                                    grant.setRemovedReason(reason);

                                    profileHandler.sendSync(profile);

                                    cc.getForWhom().sendRawMessage(Color.translate("&aSuccessfully removed grant."));

                                    return Prompt.END_OF_CONVERSATION;
                                }
                            }).withLocalEcho(false).withEscapeSequence("/cancel").withTimeout(60).thatExcludesNonPlayersWithMessage("Player's only.");

                            Conversation conversation = factory.buildConversation(player);
                            player.beginConversation(conversation);
                        }
                    });
                });

        return buttons;
    }

    public GrantsMenu showWhoReceived() {
        this.showReceived = true;
        return this;
    }
}
