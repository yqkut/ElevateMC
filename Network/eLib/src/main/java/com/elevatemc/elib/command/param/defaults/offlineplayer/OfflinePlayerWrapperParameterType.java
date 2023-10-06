package com.elevatemc.elib.command.param.defaults.offlineplayer;

import com.elevatemc.elib.command.param.ParameterType;

import org.bukkit.command.CommandSender;

public class OfflinePlayerWrapperParameterType implements ParameterType<OfflinePlayerWrapper> {

    @Override
    public OfflinePlayerWrapper transform(final CommandSender sender,final String source) {
        return new OfflinePlayerWrapper(source);
    }

}
