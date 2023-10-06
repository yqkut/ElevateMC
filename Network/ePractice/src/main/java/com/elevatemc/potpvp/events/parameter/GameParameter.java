package com.elevatemc.potpvp.events.parameter;

import java.util.List;

public interface GameParameter {
    String getDisplayName();
    List<GameParameterOption> getOptions();
}
