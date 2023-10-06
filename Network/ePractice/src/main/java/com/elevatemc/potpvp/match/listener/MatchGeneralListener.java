package com.elevatemc.potpvp.match.listener;

import com.elevatemc.elib.util.UUIDUtils;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.arena.Arena;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import com.elevatemc.potpvp.kit.Kit;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.match.MatchState;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.potpvp.nametag.PotPvPNametagProvider;
import com.elevatemc.elib.cuboid.Cuboid;
import com.elevatemc.elib.util.PlayerUtils;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketTeammates;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public final class MatchGeneralListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player player = event.getEntity();
        Match match = matchHandler.getMatchPlaying(player);

        if (match == null) {
            return;
        }

        MatchTeam team = match.getTeam(player.getUniqueId());
        if (team != null) {
            team.removeRallyForPlayer(player);
            LunarClientAPI.getInstance().sendTeammates(player, new LCPacketTeammates(null, 1, new HashMap<>()));

            if (match.getGameMode().equals(GameModes.PEARL_FIGHT)) {
                event.getDrops().clear();
                team.setLives(team.getLives() - 1);
                if (team.getLives() > 0) {
                    player.spigot().respawn();
                    if (team == match.getTeams().get(1)) {
                        player.teleport(match.getArena().getTeam2Spawn());
                    } else {
                        player.teleport(match.getArena().getTeam1Spawn());
                    }

                    if (team.getAllMembers().size() == 1) {
                        match.messageAll(ChatColor.DARK_AQUA + UUIDUtils.name(team.getFirstAliveMember()) + " has " + team.getLives() + " lives left.");
                    }

                    Kit appliedKit = MatchKitSelectionListener.appliedKits.get(player.getUniqueId());
                    if (appliedKit == null) {
                        Kit.ofDefaultKit(GameModeKit.byId("PEARL_FIGHT")).apply(player, false);
                    } else {
                        appliedKit.apply(player, false);
                    }
                } else {
                    if (team.getAllMembers().size() == 1) {
                    }
                    match.messageAll(ChatColor.DARK_AQUA + UUIDUtils.name(team.getFirstAliveMember()) + " has no lives left.");

                    team.forEachAlive(p -> {
                        match.markDead(p);
                        match.addSpectator(p, null, true);
                    });
                    match.getTeams().forEach(t -> t.getAliveMembers().forEach(p -> MatchKitSelectionListener.appliedKits.remove(p)));
                }
                return;
            }
        }

        // creates 'proper' player death animation (of the player falling over)
        // which we don't get due to our immediate respawn
        PlayerUtils.animateDeath(player);

        match.markDead(player);
        match.addSpectator(player, null, true);
        player.teleport(player.getLocation().add(0, 2, 0));

        // if we're ending the match we don't drop pots/bowls
        if (match.getState() == MatchState.ENDING) {
            event.getDrops().removeIf(i -> i.getType() == Material.POTION || i.getType() == Material.GLASS_BOTTLE || i.getType() == Material.MUSHROOM_SOUP || i.getType() == Material.BOWL);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player player = event.getPlayer();
        Match match = matchHandler.getMatchPlaying(player);

        if (match == null) {
            return;
        }

        MatchState state = match.getState();

        if (state == MatchState.COUNTDOWN || state == MatchState.IN_PROGRESS) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                UUID onlinePlayerUuid = onlinePlayer.getUniqueId();

                // if this player has no relation to the match skip
                if (match.getTeam(onlinePlayerUuid) == null && !match.isSpectator(onlinePlayerUuid)) {
                    continue;
                }

                ChatColor playerColor = PotPvPNametagProvider.getNameColor(player, onlinePlayer);
                String playerFormatted = playerColor + player.getName();

                onlinePlayer.sendMessage(playerFormatted + ChatColor.GRAY + " disconnected.");
            }
        }

        // run this regardless of match state
        match.markDead(player);
    }

    // "natural" teleports (like enderpearls) are forwarded down and
    // treated as a move event, plugin teleports (specifically
    // those originating in this plugin) are ignored.
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        switch (event.getCause()) {
            case PLUGIN:
            case COMMAND:
            case UNKNOWN:
                return;
            default:
                break;
        }

        onPlayerMove(event);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (
            from.getBlockX() == to.getBlockX() &&
            from.getBlockY() == to.getBlockY() &&
            from.getBlockZ() == to.getBlockZ()
        ) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlayingOrSpectating(player);

        if (match == null) {
            return;
        }

        Arena arena = match.getArena();
        Cuboid bounds = arena.getBounds();

        // pretend the vertical bounds of the arena are 2 blocks lower than they
        // are to avoid issues with players hitting their heads on the glass (Jon said to do this)
        // looks kind of funny but in a high frequency event this is by far the fastest
        if (!bounds.contains(to) || !bounds.contains(to.getBlockX(), to.getBlockY() + 2, to.getBlockZ())) {
            // spectators get a nice message, players just get cancelled
            if (match.isSpectator(player.getUniqueId())) {
                player.teleport(arena.getSpectatorSpawn());
            } else if (to.getBlockY() >= bounds.getUpperY() || to.getBlockY() <= bounds.getLowerY()) { // if left vertically
                if (!match.getGameMode().isVoidTeleport()) {
                    if (to.getBlockY() <= bounds.getLowerY() && bounds.getLowerY() - to.getBlockY() <= 20) return; // let the player fall 10 blocks
                    match.markDead(player);
                    match.addSpectator(player, null, true);
                }

                if (match.getGameMode().equals(GameModes.PEARL_FIGHT)) {
                    if (to.getBlockY() <= bounds.getLowerY() - 30) {
                        MatchTeam team = match.getTeam(player.getUniqueId());
                        if (team == null) {
                            player.teleport(arena.getSpectatorSpawn());
                        } else if (team == match.getTeams().get(1)) {
                            player.teleport(arena.getTeam1Spawn());
                            player.setHealth(0);
                        } else {
                            player.teleport(arena.getTeam2Spawn());
                            player.setHealth(0);
                        }
                    }

                } else {
                    player.teleport(arena.getSpectatorSpawn());
                }
            } else {
                if (!match.getGameMode().isVoidTeleport()) { // if they left horizontally
                    match.markDead(player);
                    match.addSpectator(player, null, true);
                    player.teleport(arena.getSpectatorSpawn());
                }

                event.setCancelled(true);
            }
        } else if (to.getBlockY() + 5 < arena.getSpectatorSpawn().getBlockY()) { // if the player is still in the arena bounds but fell down from the spawn point
            if (match.getGameMode().equals(GameModes.SUMO)) {
                match.markDead(player);
                match.addSpectator(player, null, true);
                player.teleport(arena.getSpectatorSpawn());
            }
        }
    }

    /**
     * Prevents (non-fall) damage between ANY two players not on opposing {@link MatchTeam}s.
     * This includes cancelling damage from a player not in a match attacking a player in a match.
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        // in the context of an EntityDamageByEntityEvent, DamageCause.FALL
        // is the 0 hearts of damage and knockback applied when hitting
        // another player with a thrown enderpearl. We allow this damage
        // in order to be consistent with HCTeams
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player victim = (Player) event.getEntity();
        Player damager = PlayerUtils.getDamageSource(event.getDamager());

        if (damager == null) {
            return;
        }

        Match match = matchHandler.getMatchPlaying(damager);
        boolean isSpleef = match != null && match.getGameMode().getId().equals("SPLEEF");
        boolean isSumo = match != null && match.getGameMode().equals(GameModes.SUMO);
        boolean isInvaded = match != null && match.getGameMode().getId().equals("INVADED");

        // we only specifically allow damage where both players are in a match together
        // and not on the same team, everything else is cancelled.
        // Match is not null
        if (match != null) {
            MatchTeam victimTeam = match.getTeam(victim.getUniqueId());
            MatchTeam damagerTeam = match.getTeam(damager.getUniqueId());

            // allow snowballs
            if (isSpleef && event.getDamager() instanceof Snowball) return;

            // set zero damage on boxing & sumo
            if (isSumo && victimTeam != null && victimTeam != damagerTeam) {
                // Ugly hack because people actually lose health & hunger in sumo somehow
                event.setDamage(0);
                return;
            }

            // allow opponent hit opponent && spleef should be false
            if (victimTeam != null && victimTeam != damagerTeam && !isSpleef) {
                return;
            }

            // allow boosting in invaded
            if (victim.getUniqueId().equals(damager.getUniqueId()) && isInvaded) {
                return;
            }
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            Match match = PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(player);
            if (match != null) {
                MatchTeam team = match.getTeam(player.getUniqueId());
                if (team != null) {
                    team.setHits(team.getHits() + 1);
                }
            }
        }

    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player player = event.getPlayer();

        if (!matchHandler.isPlayingMatch(player)) {
            return;
        }

        ItemStack itemStack = event.getItemDrop().getItemStack();
        Material itemType = itemStack.getType();
        String itemTypeName = itemType.name().toLowerCase();
        int heldSlot = player.getInventory().getHeldItemSlot();

        // don't let players drop swords, axes, and bows in the first slot
        if (!PlayerUtils.hasOwnInventoryOpen(player) && heldSlot == 0 && (itemTypeName.contains("sword") || itemTypeName.contains("axe") || itemType == Material.BOW)) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You can't drop that while you're holding it in slot 1.");
            event.setCancelled(true);
        }

        // glass bottles and bowls are removed from inventories but
        // don't spawn items on the ground
        if (itemType == Material.GLASS_BOTTLE || itemType == Material.BOWL) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player player = event.getPlayer();

        if (!matchHandler.isPlayingMatch(player)) {
            return;
        }

        Match match = matchHandler.getMatchPlaying(event.getPlayer());
        if (match == null) return;

        if (match.getState() == MatchState.ENDING || match.getState() == MatchState.TERMINATED) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack stack = event.getItem();
        if (stack == null || stack.getType() != Material.POTION) return;

        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> {
            //event.getPlayer().setItemInHand(null);
        }, 1L);
    }

    private final List<Material> disallowedMaterial = Arrays.asList(Material.CHEST, Material.TRAPPED_CHEST, Material.ENDER_CHEST, Material.SIGN, Material.SIGN_POST, Material.HOPPER, Material.ENCHANTMENT_TABLE, Material.DROPPER, Material.DISPENSER, Material.BEACON, Material.ANVIL);

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player player = event.getPlayer();

        if (!matchHandler.isPlayingMatch(player)) {
            return;
        }

        GameMode gameMode = matchHandler.getMatchPlaying(player).getGameMode();
        if (gameMode.equals(GameModes.TEAMFIGHT) || gameMode.equals(GameModes.TEAMFIGHT_DEBUFF)) {
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) {
                return;
            }

            if (this.disallowedMaterial.contains(event.getClickedBlock().getType())) {
                event.setCancelled(true);
                event.setUseInteractedBlock(Event.Result.DENY);
            }
        }
    }
}