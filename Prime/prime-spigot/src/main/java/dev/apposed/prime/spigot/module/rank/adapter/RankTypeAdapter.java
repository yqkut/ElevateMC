package dev.apposed.prime.spigot.module.rank.adapter;

import com.elevatemc.elib.command.param.ParameterType;
import dev.apposed.prime.spigot.module.rank.Rank;
import dev.apposed.prime.spigot.module.rank.RankHandler;
import dev.apposed.prime.spigot.util.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RankTypeAdapter implements ParameterType<Rank> {

    private final RankHandler rankHandler;

    public RankTypeAdapter(RankHandler rankHandler) {
        this.rankHandler = rankHandler;
    }

    @Override
    public Rank transform(CommandSender sender, String s) {
        Optional<Rank> rank = this.rankHandler.getRank(s);
        if(!rank.isPresent()) {
            sender.sendMessage(Color.translate("&cUnable to find a rank with the name " + s + "."));
            return null;
        }

        return rank.get();
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> set, String s) {
        return this.rankHandler.getCache().stream()
                .map(Rank::getName)
                .filter(name -> name.toLowerCase().startsWith(s))
                .collect(Collectors.toList());
    }
}
