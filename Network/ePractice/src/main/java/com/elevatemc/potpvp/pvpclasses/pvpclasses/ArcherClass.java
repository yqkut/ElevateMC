package com.elevatemc.potpvp.pvpclasses.pvpclasses;

import com.elevatemc.elib.util.Pair;
import com.elevatemc.potpvp.match.Match;
import lombok.Getter;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.potpvp.pvpclasses.PvPClass;
import com.elevatemc.potpvp.pvpclasses.PvPClassHandler;
import com.elevatemc.elib.eLib;
import com.elevatemc.elib.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ArcherClass extends PvPClass {

    public static final int MARK_SECONDS = 6;

    private static final Map<String, Long> lastSpeedUsage = new HashMap<>();
    private static final Map<String, Long> lastJumpUsage = new HashMap<>();
    @Getter private static final Map<String, Long> markedPlayers = new ConcurrentHashMap<>();

    @Getter private static Map<String, Set<Pair<String, Long>>> markedBy = new HashMap<>();

    private static final PotionEffect PERMANENT_SPEED_THREE = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2);
    private static final PotionEffect PERMANENT_RESISTANCE = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1);
    private static final PotionEffect SPEED_FOUR = new PotionEffect(PotionEffectType.SPEED, 20 * 10, 3);
    private static final PotionEffect JUMP = new PotionEffect(PotionEffectType.JUMP, 20 * 5, 6);

    public ArcherClass() {
        super("Archer", 15, "LEATHER_", Arrays.asList(Material.SUGAR, Material.FEATHER, Material.IRON_INGOT));
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(PERMANENT_SPEED_THREE, true);
        player.addPotionEffect(PERMANENT_RESISTANCE, true);
    }

    @Override
    public void tick(Player player) {
        if (!this.qualifies(player.getInventory())) { super.tick(player); return; }

        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(PERMANENT_SPEED_THREE, true);
        }

        if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            player.addPotionEffect(PERMANENT_RESISTANCE, true);
        }
        super.tick(player);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntityArrowHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            final Player victim = (Player) event.getEntity();

            if (!(arrow.getShooter() instanceof Player)) {
                return;
            }

            Player shooter = (Player) arrow.getShooter();
            float pullback = arrow.getMetadata("Pullback").get(0).asFloat();
            if (!PvPClassHandler.hasKitOn(shooter, this)) {
                return;
            }

            double damage = isMarked(victim) ? 3 : 2;

            if (pullback <= 0.75F) {
                damage = 1;
            }

            if (victim.getHealth() - damage <= 0D) {
                event.setCancelled(true);
            } else {
                event.setDamage(0D);
            }

            // The 'ShotFromDistance' metadata is applied in the deathmessage module.
            Location shotFrom = (Location) arrow.getMetadata("ShotFromDistance").get(0).value();
            double distance = shotFrom.distance(victim.getLocation());

            victim.setHealth(Math.max(0D, victim.getHealth() - damage));

            String arrowRangePrefix = ChatColor.YELLOW + "[" + ChatColor.DARK_AQUA + "Arrow Range" + ChatColor.YELLOW + " (" + ChatColor.RED + (int) distance + ChatColor.YELLOW + ")] ";
            String heartsSuffix = ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "(" + damage / 2 + " heart" + ((damage / 2 == 1) ? "" : "s") + ")";

            if (PvPClassHandler.hasKitOn(victim, this)) {
                shooter.sendMessage(arrowRangePrefix + ChatColor.RED + "Cannot mark other Archers. " + heartsSuffix);
            } else if (pullback >= 0.5F) {
                shooter.sendMessage(arrowRangePrefix + ChatColor.GOLD + "Marked player for " + MARK_SECONDS + " seconds. " + heartsSuffix);

                // Only send the message if they're not already marked.
                if (!isMarked(victim)) {
                    victim.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED.toString() + ChatColor.BOLD + "Marked! " + ChatColor.YELLOW + "An archer has shot you and marked you (+20% damage) for " + MARK_SECONDS + " seconds.");
                }

                PotionEffect invis = null;

                for (PotionEffect potionEffect : victim.getActivePotionEffects()) {
                    if (potionEffect.getType().equals(PotionEffectType.INVISIBILITY)) {
                        invis = potionEffect;
                        break;
                    }
                }

                if (invis != null) {
                    victim.removePotionEffect(invis.getType());
                }

                getMarkedPlayers().put(victim.getName(), System.currentTimeMillis() + (MARK_SECONDS * 1000));

                getMarkedBy().putIfAbsent(shooter.getName(), new HashSet<>());
                getMarkedBy().get(shooter.getName()).add(new Pair<>(victim.getName(), System.currentTimeMillis() + (MARK_SECONDS * 1000)));

                eLib.getInstance().getNameTagHandler().reloadPlayer(victim);

                Match match = PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(shooter);
                if (match != null) {
                    match.getTotalTags().put(shooter.getUniqueId(), match.getTotalTags().getOrDefault(shooter.getUniqueId(), 0) + 1);
                }

                new BukkitRunnable() {

                    public void run() {
                        eLib.getInstance().getNameTagHandler().reloadPlayer(victim);
                    }

                }.runTaskLater(PotPvPSI.getInstance(), (MARK_SECONDS * 20) + 5);
            } else {
                shooter.sendMessage(arrowRangePrefix + ChatColor.RED + "Bow wasn't fully drawn back. " + heartsSuffix);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (isMarked(player)) {
                Player damager = null;
                if (event.getDamager() instanceof Player) {
                    damager = (Player) event.getDamager();
                } else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
                    damager = (Player) ((Projectile) event.getDamager()).getShooter();
                }

                if (damager != null && !canUseMark(damager, player)) {
                    return;
                }

                // Apply 120% damage if they're 'marked'
                event.setDamage(event.getDamage() * 1.20D);
            }
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        event.getProjectile().setMetadata("ShotFromDistance", new FixedMetadataValue(PotPvPSI.getInstance(), event.getProjectile().getLocation()));
        event.getProjectile().setMetadata("Pullback", new FixedMetadataValue(PotPvPSI.getInstance(), event.getForce()));
    }

    @Override
    public boolean itemConsumed(Player player, Material material) {
        if (material == Material.SUGAR) {
            if (lastSpeedUsage.containsKey(player.getName()) && lastSpeedUsage.get(player.getName()) > System.currentTimeMillis()) {
                long millisLeft = lastSpeedUsage.get(player.getName()) - System.currentTimeMillis();
                String msg = TimeUtils.formatIntoDetailedString((int) millisLeft / 1000);

                player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
                return (false);
            }

            lastSpeedUsage.put(player.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30));
            player.addPotionEffect(SPEED_FOUR, true);
            return (true);
        } else {
            if (lastJumpUsage.containsKey(player.getName()) && lastJumpUsage.get(player.getName()) > System.currentTimeMillis()) {
                long millisLeft = lastJumpUsage.get(player.getName()) - System.currentTimeMillis();
                String msg = TimeUtils.formatIntoDetailedString((int) millisLeft / 1000);

                player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
                return (false);
            }

            lastJumpUsage.put(player.getName(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1));
            player.addPotionEffect(JUMP, true);
            return (true);
        }
    }

    @EventHandler
    public void onPotionEffectExpire(PotionEffectExpireEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (PvPClassHandler.hasKitOn(player, this)) {
                if (e.getEffect().getType().equals(PotionEffectType.SPEED)) {
                    player.addPotionEffect(PERMANENT_SPEED_THREE, true);
                }
                if (e.getEffect().getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
                    player.addPotionEffect(PERMANENT_RESISTANCE, true);
                }
            }
        }
    }


    public static boolean isMarked(Player player) {
        return (getMarkedPlayers().containsKey(player.getName()) && getMarkedPlayers().get(player.getName()) > System.currentTimeMillis());
    }

    private boolean canUseMark(Player player, Player victim) {
        if (PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(player) != null) {
            MatchTeam team = PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(player).getTeam(player.getUniqueId());

            if (team != null) {
                int amount = 0;
                for (UUID memberUUID : team.getAllMembers()) {
                    Player member = Bukkit.getPlayer(memberUUID);

                    if (member == null) continue;
                    if (PvPClassHandler.hasKitOn(member, this)) {
                        amount++;

                        if (amount > 3) {
                            break;
                        }
                    }
                }

                if (amount > 3) {
                    player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Your team has too many archers. Archer mark was not applied.");
                    return false;
                }
            }
        }

        if (markedBy.containsKey(player.getName())) {
            for (Pair<String, Long> pair : markedBy.get(player.getName())) {
                if (victim.getName().equals(pair.getKey()) && pair.getValue() > System.currentTimeMillis()) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

}
