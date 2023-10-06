package com.elevatemc.elib.command;

import com.elevatemc.elib.command.bukkit.CommandMap;
import com.elevatemc.elib.command.defaults.BuildCommand;
import com.elevatemc.elib.eLib;
import com.elevatemc.elib.command.bukkit.Command;
import com.elevatemc.elib.command.bukkit.HelpTopic;
import com.elevatemc.elib.command.command.CommandConfiguration;
import com.elevatemc.elib.command.command.CommandNode;
import com.elevatemc.elib.command.defaults.CommandInfoCommand;
import com.elevatemc.elib.command.defaults.EvalCommand;
import com.elevatemc.elib.command.param.ParameterType;
import com.elevatemc.elib.command.param.defaults.*;
import com.elevatemc.elib.command.param.defaults.filter.NormalFilter;
import com.elevatemc.elib.command.param.defaults.filter.StrictFilter;
import com.elevatemc.elib.command.param.defaults.offlineplayer.OfflinePlayerWrapper;
import com.elevatemc.elib.command.param.defaults.offlineplayer.OfflinePlayerWrapperParameterType;
import com.elevatemc.elib.command.processor.MethodProcessor;
import com.elevatemc.elib.command.utils.EasyClass;
import com.elevatemc.elib.util.ClassUtils;
import lombok.Getter;


import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.*;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.*;

public class CommandHandler implements Listener {

    public static CommandNode ROOT_NODE = new CommandNode();

    private static Map<Class<?>, ParameterType<?>> PARAMETER_TYPE_MAP = new HashMap<>();
    private static org.bukkit.command.CommandMap commandMap = getCommandMap();
    protected static Map<String,org.bukkit.command.Command> knownCommands = getKnownCommands();

    @Getter private CommandConfiguration commandConfiguration;

    public CommandHandler() {

        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    swapCommandMap();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.runTaskLater(eLib.getInstance(), 5L);

        this.commandConfiguration = new CommandConfiguration("&cNo permission.");

        this.registerParameterType(Boolean.class, new BooleanParameterType());
        this.registerParameterType(boolean.class, new BooleanParameterType());
        this.registerParameterType(Integer.class, new IntegerParameterType());
        this.registerParameterType(int.class, new IntegerParameterType());
        this.registerParameterType(Double.class, new DoubleParameterType());
        this.registerParameterType(double.class, new DoubleParameterType());
        this.registerParameterType(Float.class, new FloatParameterType());
        this.registerParameterType(float.class, new FloatParameterType());
        this.registerParameterType(Long.class,new LongParameterType());
        this.registerParameterType(long.class,new LongParameterType());
        this.registerParameterType(String.class, new StringParameterType());
        this.registerParameterType(Player.class, new PlayerParameterType());
        this.registerParameterType(World.class, new WorldParameterType());
        this.registerParameterType(ItemStack.class, new ItemStackParameterType());
        this.registerParameterType(OfflinePlayer.class, new OfflinePlayerParameterType());
        this.registerParameterType(UUID.class, new UUIDParameterType());
        this.registerParameterType(Enchantment.class,new EnchantmentParameterType());
        this.registerParameterType(GameMode.class,new GameModeParameterType());
        this.registerParameterType(EntityType.class,new EntityTypeParameterType());
        this.registerParameterType(OfflinePlayerWrapper.class,new OfflinePlayerWrapperParameterType());
        registerParameterType(NormalFilter.class, new NormalFilter());
        registerParameterType(StrictFilter.class, new StrictFilter());

        this.registerClass(BuildCommand.class);
        this.registerClass(EvalCommand.class);
        this.registerClass(CommandInfoCommand.class);
    }

    protected static org.bukkit.command.CommandMap getCommandMap() {
        return MinecraftServer.getServer().server.getCommandMap();
    }

    protected static Map<String, org.bukkit.command.Command> getKnownCommands() {
        return (Map)(new EasyClass(commandMap)).getField("knownCommands").get();
    }

    public void registerMethod(Method method) {
        method.setAccessible(true);

        Set<CommandNode> nodes = new MethodProcessor().process(method);

        if (nodes != null) {

            nodes.forEach((node) -> {

                if (node != null) {

                    com.elevatemc.elib.command.bukkit.Command command = new com.elevatemc.elib.command.bukkit.Command(node, JavaPlugin.getProvidingPlugin(method.getDeclaringClass()));

                    register(command);

                    node.getChildren().values().forEach((n) -> registerHelpTopic(n, node.getAliases()));
                }

            });
        }

    }

    protected void registerHelpTopic(CommandNode node, Set<String> aliases) {

        if (node.getMethod() != null) {
            eLib.getInstance().getServer().getHelpMap().addTopic(new HelpTopic(node, aliases));
        }

        if (node.hasCommands()) {
            node.getChildren().values().forEach((n) -> registerHelpTopic(n, null));
        }

    }

    private void register(com.elevatemc.elib.command.bukkit.Command command) {
        try {

            Map<String,org.bukkit.command.Command> knownCommands = getKnownCommands();
            Iterator iterator = knownCommands.entrySet().iterator();

            while(iterator.hasNext()) {

                Map.Entry<String, org.bukkit.command.Command> entry = (Map.Entry)iterator.next();

                if (entry.getValue().getName().equalsIgnoreCase(command.getName())) {
                    (entry.getValue()).unregister(commandMap);
                    iterator.remove();
                }
            }

            for (String alias : command.getAliases()) {
                knownCommands.put(alias,command);
            }

            command.register(commandMap);
            knownCommands.put(command.getName(), command);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void registerClass(Class<?> clazz) {

        Method[] methods = clazz.getMethods();

        for (int i = 0; i < methods.length; i++) {

            Method method = methods[i];

            registerMethod(method);
        }

    }

    public void unregisterClass(Class<?> clazz) {

        Map<String,org.bukkit.command.Command> knownCommands = getKnownCommands();
        Iterator iterator = knownCommands.values().iterator();

        while(iterator.hasNext()) {
            org.bukkit.command.Command command = (org.bukkit.command.Command)iterator.next();
            if (command instanceof com.elevatemc.elib.command.bukkit.Command) {
                CommandNode node = ((Command)command).getNode();
                if (node.getOwningClass() == clazz) {
                    command.unregister(commandMap);
                    iterator.remove();
                }
            }
        }

    }

    public void registerPackage(Plugin plugin, String packageName) {
        ClassUtils.getClassesInPackage(plugin, packageName).forEach(this::registerClass);
    }

    public void registerAll(Plugin plugin) {
        registerPackage(plugin, plugin.getClass().getPackage().getName());
    }

    private void swapCommandMap() throws Exception {

        Field commandMapField = eLib.getInstance().getServer().getClass().getDeclaredField("commandMap");

        commandMapField.setAccessible(true);

        Object oldCommandMap = commandMapField.get(eLib.getInstance().getServer());
        Object newCommandMap = new CommandMap(Bukkit.getServer());
        Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");

        knownCommandsField.setAccessible(true);

        knownCommandsField.set(newCommandMap, knownCommandsField.get(oldCommandMap));

        commandMapField.set(eLib.getInstance().getServer(), newCommandMap);

    }

    public void registerParameterType(Class<?> clazz, ParameterType<?> type) {
        PARAMETER_TYPE_MAP.put(clazz, type);
    }

    public ParameterType getParameterType(Class<?> clazz) {
        return PARAMETER_TYPE_MAP.get(clazz);
    }

}