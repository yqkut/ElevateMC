package com.elevatemc.potpvp.ability.parameter;

import com.elevatemc.elib.command.param.ParameterType;
import com.mysql.jdbc.StringUtils;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.Ability;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AbilityParameter implements ParameterType<Ability> {

    @Override
    public Ability transform(CommandSender commandSender, String s) {

        final Ability toReturn = PotPvPSI.getInstance().getAbilityHandler().fromName(s);

        if (toReturn == null) {
            commandSender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "No ability with the name \"" + s + ChatColor.RED + "\" not found.");
            return null;
        }

        return toReturn;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String s) {
        List<String> completions = new ArrayList<>();

        for (Ability ability : PotPvPSI.getInstance().getAbilityHandler().getAbilities().values()) {
            String name = ability.getName();
            if(StringUtils.startsWithIgnoreCase(name, s)) {
                completions.add(name);
            }
        }

        return completions;
    }
}
