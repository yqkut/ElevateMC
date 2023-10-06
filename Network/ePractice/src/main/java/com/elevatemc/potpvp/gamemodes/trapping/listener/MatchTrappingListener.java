package com.elevatemc.potpvp.gamemodes.trapping.listener;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.type.AntiBlockup;
import com.elevatemc.potpvp.ability.type.TimeWarp;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.elevatemc.potpvp.listener.PearlCooldownListener;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.potpvp.match.event.MatchEndEvent;
import com.elevatemc.potpvp.match.event.MatchTerminateEvent;
import com.elevatemc.potpvp.util.Elevator;
import com.elevatemc.spigot.event.PlayerPearlRefundEvent;
import net.minecraft.server.v1_8_R3.EntityEnderPearl;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

import static com.elevatemc.potpvp.listener.PearlCooldownListener.PEARL_COOLDOWN_MILLIS;

public class MatchTrappingListener implements Listener {
    public static int ticks = 6;


    public MatchTrappingListener() {
        EntityEnderPearl.pearlAbleType = Arrays.asList("STEP", "STAIR");
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (player.hasMetadata("TRAPPER")) {
            player.removeMetadata("TRAPPER", PotPvPSI.getInstance());
        }

        if (player.hasMetadata("ANTI_BUILD")) {
            player.removeMetadata("ANTI_BUILD", PotPvPSI.getInstance());
        }

        if (player.hasMetadata("DROPDOWN_COUNT")) {
            player.removeMetadata("DROPDOWN_COUNT", PotPvPSI.getInstance());
        }

        if (player.hasMetadata("ANTI_TRAP")) {
            player.removeMetadata("ANTI_TRAP", PotPvPSI.getInstance());
        }

        if (player.hasMetadata("SPIDER")) {
            player.removeMetadata("SPIDER", PotPvPSI.getInstance());
        }

        if (player.hasMetadata("NINJASTAR")) {
            player.removeMetadata("NINJASTAR", PotPvPSI.getInstance());
        }

        if (player.hasMetadata("CHAOS")) {
            player.removeMetadata("CHAOS", PotPvPSI.getInstance());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onMatchEnd(MatchEndEvent event) {
        final Match match = event.getMatch();

        if (match.getGameMode() != GameModes.TRAPPING) {
            return;
        }

        for (MatchTeam team : match.getTeams()) {
            for (UUID allMember : team.getAllMembers()) {
                final Player player = PotPvPSI.getInstance().getServer().getPlayer(allMember);

                if (player == null) {
                    continue;
                }

                if (player.hasMetadata("ANTI_BUILD")) {
                    player.removeMetadata("ANTI_BUILD", PotPvPSI.getInstance());
                }

                if (player.hasMetadata("DROPDOWN_COUNT")) {
                    player.removeMetadata("DROPDOWN_COUNT", PotPvPSI.getInstance());
                }

                if (player.hasMetadata("ANTI_TRAP")) {
                    player.removeMetadata("ANTI_TRAP", PotPvPSI.getInstance());
                }

                if (player.hasMetadata("SPIDER")) {
                    player.removeMetadata("SPIDER", PotPvPSI.getInstance());
                }

                if (player.hasMetadata("NINJASTAR")) {
                    player.removeMetadata("NINJASTAR", PotPvPSI.getInstance());
                }

                if (player.hasMetadata("CHAOS")) {
                    player.removeMetadata("CHAOS", PotPvPSI.getInstance());
                }

                if (player.hasMetadata("TRAPPER")) {
                    player.removeMetadata("TRAPPER", PotPvPSI.getInstance());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onHunger(FoodLevelChangeEvent event) {
        final Player player = (Player) event.getEntity();
        final MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (!matchHandler.isPlayingMatch(player)) {
            return;
        }

        final Match match = matchHandler.getMatchPlaying(player);

        if (match == null || match.getGameMode() != GameModes.TRAPPING) {
            return;
        }

        player.setFoodLevel(20);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRefund(PlayerPearlRefundEvent event) {
        final Player player = event.getPlayer();

        if (!player.isOnline()) {
            return;
        }

        final ItemStack itemStack = player.getItemInHand();

        if (itemStack != null && itemStack.getType() == Material.ENDER_PEARL && itemStack.getAmount() < 16) {
            itemStack.setAmount(itemStack.getAmount() + 1);
        } else {
            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
        }

        player.updateInventory();

        PearlCooldownListener.pearlCooldown.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !(event.getDamager() instanceof EnderPearl) || !(event.getEntity() instanceof Player)) {
            return;
        }

        final EnderPearl enderPearl = (EnderPearl) event.getDamager();
        if (!(enderPearl.getShooter() instanceof Player)) {
            return;
        }

        final Player damager = (Player) enderPearl.getShooter();
        final Player target = (Player) event.getEntity();

        if (damager == target) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (!matchHandler.isPlayingMatch(damager)) {
            // BasicPreventionListener handles this
            return;
        }

        if (matchHandler.getMatchPlaying(damager).getGameMode() != GameModes.TRAPPING) {
            return;
        }

        PearlCooldownListener.pearlCooldown.put(damager.getUniqueId(), System.currentTimeMillis() + PEARL_COOLDOWN_MILLIS);

        TimeWarp.pearlLocations.put(damager.getUniqueId(), damager.getLocation());

        PotPvPSI.getInstance().getServer().getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> {
            final Location targetLocation = target.getLocation().clone();
            final BlockFace blockFace = getDirection(damager);

            if (blockFace == BlockFace.NORTH) {
                targetLocation.setZ(targetLocation.getZ() + 0.5);
            }

            if (blockFace == BlockFace.SOUTH) {
                targetLocation.setZ(targetLocation.getZ() - 0.5);
            }

            if (blockFace == BlockFace.WEST) {
                targetLocation.setX(targetLocation.getX() + 0.5);
            }

            if (blockFace == BlockFace.EAST) {
                targetLocation.setX(targetLocation.getX() - 0.5);
            }

            if (targetLocation.getBlock().getType() != Material.AIR) {
                return;
            }

            targetLocation.setYaw(damager.getLocation().getYaw());
            targetLocation.setPitch(damager.getLocation().getPitch());

            damager.teleport(targetLocation);
        }, ticks);
    }

    private final List<String> elevatorDirections = Collections.singletonList(Arrays.stream(Elevator.values()).map(Elevator::name).map(String::toLowerCase).collect(Collectors.joining()));
    private final List<Material> signMaterials = Arrays.asList(Material.SIGN_POST, Material.WALL_SIGN);

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignInteract(PlayerInteractEvent event) {

        final Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !this.signMaterials.contains(event.getClickedBlock().getType())) {
            return;
        }

        final BlockState blockState = event.getClickedBlock().getState();

        if (!(blockState instanceof Sign)) {
            return;
        }

        final Sign sign = (Sign) blockState;

        if (!sign.getLine(0).contains("[Elevator]")) {
            return;
        }

        final Block block = player.getTargetBlock((HashSet<Material>) null,(int)sign.getLocation().distance(player.getLocation()));

        if (block != null && !(block.getState() instanceof Sign)) {
            return;
        }

        Elevator elevator;

        try {
            elevator = Elevator.valueOf(ChatColor.stripColor(sign.getLine(1).toUpperCase()));
        } catch (IllegalArgumentException ex) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Invalid elevator direction, try UP or DOWN.");
            ex.printStackTrace();
            return;
        }

        if (AntiBlockup.getCache().containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou may not use elevator signs whilst on &6&lAnti-Blockup&c!"));
            return;
        }

        if (elevator == null) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Invalid elevator direction, try UP or DOWN.");
            return;
        }

        final Location toTeleport = elevator.getCalculatedLocation(sign.getLocation(), Elevator.Type.SIGN);

        if (toTeleport == null) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "There was an issue trying to find a valid location!");
            return;
        }

        toTeleport.setYaw(player.getLocation().getYaw());
        toTeleport.setPitch(player.getLocation().getPitch());
        player.teleport(toTeleport.add(0.5,0,0.5));
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (event.getLine(0).equalsIgnoreCase("[Elevator]") && event.getLine(1).equalsIgnoreCase("Up")) {
            event.setLine(0, ChatColor.BLUE + "[Elevator]");
            event.setLine(1, "Up");
        }
        if (event.getLine(0).equalsIgnoreCase("[Elevator]") && event.getLine(1).equalsIgnoreCase("Down")) {
            event.setLine(0, ChatColor.BLUE + "[Elevator]");
            event.setLine(1, "Down");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (event.isCancelled() || !matchHandler.isPlayingMatch(player)) {
            return;
        }

        final Match match = matchHandler.getMatchPlaying(player);

        if (match == null || match.getGameMode() != GameModes.TRAPPING) {
            return;
        }

        if (event.getBlock().getType() == Material.COBBLESTONE) {
            player.getInventory().addItem(new ItemStack(Material.COBBLESTONE, 1));
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying((Player) event.getEntity());

        if (match != null && match.getGameMode().equals(GameModes.TRAPPING)) {
            event.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getEntity());

        if (match != null && match.getGameMode().equals(GameModes.TRAPPING)) {
            UUID uuid = event.getEntity().getUniqueId();
            TimeWarp.pearlLocations.remove(uuid);
            TimeWarp.oldPearlLocations.remove(uuid);
        }
    }

    @EventHandler
    public void onDeath(MatchTerminateEvent event) {
        Match match = event.getMatch();

        if (match != null && match.getGameMode().equals(GameModes.TRAPPING)) {
            match.getAllPlayers().forEach(uuid -> {
                TimeWarp.pearlLocations.remove(uuid);
                TimeWarp.oldPearlLocations.remove(uuid);
            });
        }
    }

    private BlockFace getDirection(Player player) {
        float yaw = player.getLocation().getYaw();
        if (yaw < 0) {
            yaw += 360;
        }
        if (yaw >= 315 || yaw < 45) {
            return BlockFace.SOUTH;
        } else if (yaw < 135) {
            return BlockFace.WEST;
        } else if (yaw < 225) {
            return BlockFace.NORTH;
        } else if (yaw < 315) {
            return BlockFace.EAST;
        }
        return BlockFace.NORTH;
    }
}
