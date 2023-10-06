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
import dev.apposed.prime.spigot.module.rank.Rank;
import dev.apposed.prime.spigot.module.server.ServerHandler;
import dev.apposed.prime.spigot.module.webhook.DiscordWebhook;
import dev.apposed.prime.spigot.util.Color;
import dev.apposed.prime.spigot.util.time.DurationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ScopesMenu extends PaginatedMenu {

    private final Prime plugin = JavaPlugin.getPlugin(Prime.class);
    private final ProfileHandler profileHandler = plugin.getModuleHandler().getModule(ProfileHandler.class);
    private final ServerHandler serverHandler = plugin.getModuleHandler().getModule(ServerHandler.class);

    private final Profile profile;
    private final Rank rank;
    private final long duration;
    private final String reason;

    private final List<String> selectedScopes;
    private final String GRANT_WEBHOOK = "https://discord.com/api/webhooks/993224084060115004/Rpd7FcojkkgLlbWJQg938NkwlarCA0Kuw_9uNfxyVEMMwBzlbaxkNnA7gdPkHKcHm6P3";

    public ScopesMenu(Profile profile, Rank rank, long duration, String reason) {
        this.profile = profile;
        this.rank = rank;
        this.duration = duration;
        this.reason = reason;

        this.selectedScopes = new ArrayList<>();
        this.selectedScopes.add("Global");
        setUpdateAfterClick(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Select Scopes";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return Color.translate("&6Grant &r" + profile.getColoredName() + "&r " + rank.getColoredDisplay());
            }

            @Override
            public List<String> getDescription(Player player) {
                return Color.translate(ImmutableList.of(
                        Color.SPACER_SHORT,
                        "&eScopes: &c" + String.join(", ", selectedScopes),
                        "&eReason: &c" + reason,
                        "&eDuration: &c" + (duration == Long.MAX_VALUE ? "Permanent" : DurationUtils.toString(System.currentTimeMillis() + duration)),
                        Color.SPACER_SHORT
                ));
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.NETHER_STAR;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                UUID addedBy = player.getUniqueId();
                Grant grant = new Grant(
                        rank,
                        addedBy,
                        System.currentTimeMillis(),
                        reason,
                        duration,
                        selectedScopes
                );

                profile.getGrants().add(grant);

                profileHandler.save(profile);

                player.sendMessage(Color.translate("&aYou have granted &r" + profile.getColoredName() + " &athe &r" + rank.getColoredDisplay() + " &arank for &f" + (duration == Long.MAX_VALUE ? "forever" : DurationUtils.toString(System.currentTimeMillis() + duration)) + "&a."));

                Player targetPlayer = Bukkit.getPlayer(profile.getUuid());
                if(targetPlayer != null && targetPlayer.isOnline()) {
                    targetPlayer.sendMessage(Color.translate("&aYou have been granted &r" + rank.getColoredDisplay()+ " &afor &f" + (duration == Long.MAX_VALUE ? "forever" : DurationUtils.toString(System.currentTimeMillis() + duration)) + "&a."));
                }

                final DiscordWebhook webhook = new DiscordWebhook(GRANT_WEBHOOK);
                webhook.addEmbed(
                        new DiscordWebhook.EmbedObject()
                                .setTitle(profile.getUsername() + " has been granted " + rank.getName())
                                .addField("Added By", (grant.getAddedBy().equals(PrimeConstants.CONSOLE_UUID) ? "Console" : UUIDUtils.name(grant.getAddedBy())), false)
                                .addField("Reason", grant.getAddedReason(), false)
                                .addField("Duration", grant.formatDuration(), false)
                                .addField("Scopes", String.join(", ", grant.getScopes()), false)
                                .setColor(java.awt.Color.cyan)
                                .setFooter("Prime Grants", null)
                );
                new Thread(() -> {
                    try {
                        webhook.execute();
                    }catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }, "grant-log-" + addedBy).start();
            }
        });

        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        final AtomicInteger slot = new AtomicInteger(0);
        this.serverHandler.getServerGroups().forEach(group -> {
            buttons.put(slot.getAndIncrement(), new Button() {
                @Override
                public String getName(Player player) {
                    return Color.translate("&6" + group.getId());
                }

                @Override
                public List<String> getDescription(Player player) {
                    return Collections.emptyList();
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.BEDROCK;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if(selectedScopes.contains(group.getId())) {
                        selectedScopes.remove(group.getId());
                        return;
                    }

                    selectedScopes.add(group.getId());
                }
            });
        });

        return buttons;
    }
}
