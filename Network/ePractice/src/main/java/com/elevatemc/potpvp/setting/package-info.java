/**
 * Handles accessing, saving, updating, and presentation of player settings.
 *
 * This includes the /settings command, a settings menu, persistence, etc.
 * Clients using the settings API should only concern themselves with {@link com.elevatemc.potpvp.setting.event.SettingUpdateEvent},
 * {@link com.elevatemc.potpvp.setting.SettingHandler#getSetting(java.util.UUID, com.elevatemc.potpvp.setting.Setting)} and
 * {@link com.elevatemc.potpvp.setting.SettingHandler#updateSetting(org.bukkit.entity.Player, com.elevatemc.potpvp.setting.Setting, boolean)},
 */
package com.elevatemc.potpvp.setting;