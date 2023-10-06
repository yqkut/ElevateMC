package com.elevatemc.elib.command.param.defaults;

import com.elevatemc.elib.command.param.ParameterType;
import org.bukkit.command.CommandSender;

public class StringParameterType implements ParameterType<String> {

    public String transform(CommandSender sender,String value) {
        return value;
    }

}
