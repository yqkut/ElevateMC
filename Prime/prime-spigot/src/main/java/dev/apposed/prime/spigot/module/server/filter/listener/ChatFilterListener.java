package dev.apposed.prime.spigot.module.server.filter.listener;

import com.elevatemc.elib.util.Pair;
import dev.apposed.prime.spigot.module.listener.ListenerModule;
import dev.apposed.prime.spigot.module.server.filter.ChatFilter;
import dev.apposed.prime.spigot.module.server.filter.ChatFilterHandler;
import dev.apposed.prime.spigot.util.Color;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatFilterListener extends ListenerModule {

    private final Map<UUID, Pair<String, ChatFilter>> badMessages = new HashMap<>();

    private final ChatFilterHandler filterHandler = getModuleHandler().getModule(ChatFilterHandler.class);

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChatLowest(AsyncPlayerChatEvent event) {
        if(event.getPlayer().hasPermission("prime.filter.bypass")) return;
        final ChatFilter filter = filterHandler.filterMessage(event.getMessage());
        if(filter == null) return;
        badMessages.put(event.getPlayer().getUniqueId(), new Pair(event.getMessage(), filter));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChatMonitor(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        if(!badMessages.containsKey(player.getUniqueId())) return;

        event.getRecipients().clear();
        event.getRecipients().add(player);

        final TextComponent component = new TextComponent(Color.translate("&c&l[Filtered] "));
        component.addExtra(event.getFormat());
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("§eThis message was hidden from public chat.\n§cFilter: " + badMessages.get(player.getUniqueId()).getValue().getDescription()).create()));

        final ChatFilter filter = badMessages.get(player.getUniqueId()).getValue();
        if(filter.getDescription().contains("Racism")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("mute %s Racism", player.getName()));
        }
        if(filter.getDescription().contains("Offensive")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("mute %s Homophobia", player.getName()));
        }

        Bukkit.getOnlinePlayers().stream().filter(staff -> staff.hasPermission("prime.staff")).forEach(staff -> {
            staff.spigot().sendMessage(component);
        });

        badMessages.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        badMessages.remove(event.getPlayer().getUniqueId());
        final Player player = event.getPlayer();
        if(player.hasMetadata("frozen"))
            Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(staff -> staff.hasPermission("prime.staff"))
                    .forEach(staff -> staff.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + player.getName() + " logged out whilst frozen."));
    }
}
