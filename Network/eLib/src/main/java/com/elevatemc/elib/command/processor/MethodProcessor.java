package com.elevatemc.elib.command.processor;

import com.elevatemc.elib.command.command.CommandNode;
import com.elevatemc.elib.eLib;
import com.elevatemc.elib.command.flag.Data;
import com.elevatemc.elib.command.flag.Flag;
import com.elevatemc.elib.command.flag.FlagData;
import com.elevatemc.elib.command.param.ParameterData;
import com.google.common.collect.Sets;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.CommandHandler;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class MethodProcessor implements Processor<Method, Set<CommandNode>> {

    public Set<CommandNode> process(Method value) {

        if (value.isAnnotationPresent(Command.class) && value.getParameterCount() >= 1 && CommandSender.class.isAssignableFrom(value.getParameterTypes()[0])) {

            final Command command = value.getAnnotation(Command.class);
            final Class<?> owningClass = value.getDeclaringClass();

            final List<Data> allParams = new ArrayList<>();
            final List<String> flagNames = new ArrayList<>();

            if (value.getParameterCount() > 1) {

                for (int i = 1; i < value.getParameterCount(); ++i) {

                    final Parameter parameter = value.getParameters()[i];

                    if (parameter.isAnnotationPresent(com.elevatemc.elib.command.param.Parameter.class)) {

                        final com.elevatemc.elib.command.param.Parameter param = parameter.getAnnotation(com.elevatemc.elib.command.param.Parameter.class);
                        final ParameterData data = new ParameterData(param.name(), param.defaultValue(), parameter.getType(), param.wildcard(), i, Sets.newHashSet(param.tabCompleteFlags()), parameter.isAnnotationPresent(Type.class) ? ((Type)parameter.getAnnotation(Type.class)).value() : null);

                        allParams.add(data);
                    } else {

                        if (!parameter.isAnnotationPresent(Flag.class)) {
                            throw new IllegalArgumentException("Every parameter, other than the sender, must have the Param or the Flag annotation! (" + value.getDeclaringClass().getName() + ":" + value.getName() + ")");
                        }

                        final Flag flag = parameter.getAnnotation(Flag.class);
                        final FlagData data = new FlagData(Arrays.asList(flag.value()), flag.description(), flag.defaultValue(), i);


                        allParams.add(data);
                        flagNames.addAll(Arrays.asList(flag.value()));
                    }
                }
            }

            final Set<CommandNode> registered = new HashSet<>();

            for (int i = 0; i < command.names().length; i++) {

                String name = command.names()[i];

                boolean change = true;
                boolean hadChild = false;

                name = name.toLowerCase().trim();

                String[] cmdNames;

                if (name.contains(" ")) {
                    cmdNames = name.split(" ");
                } else {
                    cmdNames = new String[]{name};
                }

                String primary = cmdNames[0];

                CommandNode workingNode = new CommandNode(owningClass);

                if (CommandHandler.ROOT_NODE.hasCommand(primary)) {
                    workingNode = eLib.getInstance().getCommandHandler().ROOT_NODE.getCommand(primary);
                    change = false;
                }

                if (change) {
                    workingNode.setName(cmdNames[0]);
                } else {
                    workingNode.getAliases().add(cmdNames[0]);
                }

                CommandNode parentNode = new CommandNode(owningClass);

                if (workingNode.hasCommand(cmdNames[0])) {
                    parentNode = workingNode.getCommand(cmdNames[0]);
                } else {
                    parentNode.setName(cmdNames[0]);
                    parentNode.setPermission("");
                }

                if (cmdNames.length > 1) {
                    hadChild = true;
                    workingNode.registerCommand(parentNode);
                    CommandNode childNode = new CommandNode(owningClass);

                    for(int i2 = 1; i2 < cmdNames.length; ++i2) {

                        String subName = cmdNames[i2];

                        childNode.setName(subName);

                        if (parentNode.hasCommand(subName)) {
                            childNode = parentNode.getCommand(subName);
                        }

                        parentNode.registerCommand(childNode);

                        if (i2 == cmdNames.length - 1) {
                            childNode.setMethod(value);
                            childNode.setAsync(command.async());
                            childNode.setHidden(command.hidden());
                            childNode.setPermission(command.permission());
                            childNode.setDescription(command.description());
                            childNode.setValidFlags(flagNames);
                            childNode.setParameters(allParams);
                            childNode.setLogToConsole(command.logToConsole());
                        } else {
                            parentNode = childNode;
                            childNode = new CommandNode(owningClass);
                        }
                    }
                }

                if (!hadChild) {
                    parentNode.setMethod(value);
                    parentNode.setAsync(command.async());
                    parentNode.setHidden(command.hidden());
                    parentNode.setPermission(command.permission());
                    parentNode.setDescription(command.description());
                    parentNode.setValidFlags(flagNames);
                    parentNode.setParameters(allParams);
                    parentNode.setLogToConsole(command.logToConsole());
                    workingNode.registerCommand(parentNode);
                }

                CommandHandler.ROOT_NODE.registerCommand(workingNode);
                registered.add(workingNode);
            }

            return registered;
        } else {

            return null;
        }
    }
}
