package com.github.matt159.bottomgear.commands;
// largely based on:
// https://github.com/GTNewHorizons/Railcraft/blob/master/src/main/java/mods/railcraft/common/commands/SubCommand.java

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

import java.util.*;

public abstract class SubCommand extends CommandBase implements ICommand {
    public enum PermLevel {
        EVERYONE(0),
        ADMIN(2);
        
        int permLevel;

        PermLevel(int permLevel) {
            this.permLevel = permLevel;
        }
    }

    protected final String name;
    protected final List<String> aliases = new ArrayList<>();
    protected final Map<String, SubCommand> children = new HashMap<>();

    protected ICommand parent;
    protected PermLevel permLevel = PermLevel.EVERYONE;

    public SubCommand(String name) {
        this.name = name;
    }

    public void setParent(ICommand parent) {
        this.parent = parent;
    }
    
    public SubCommand addChildCommand(SubCommand child) {
        child.setParent(this);
        children.put(child.getCommandName(), child);
        return this;
    }

    public SubCommand setPermLevel(PermLevel permLevel) {
        this.permLevel = permLevel;
        return this;
    }

    @Override
    public String getCommandName() {
        return this.name;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + this.name + " help";
    }

    @Override
    public List<String> getCommandAliases() {
        return this.aliases;
    }

    public Map<String, SubCommand> getChildren() {
        return children;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(getChildren().keySet());
        } else {
            if (children.containsKey(args[0])) {
                String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, newArgs.length);
                return children.get(args[0]).addTabCompletionOptions(sender, newArgs);
            }
        }
        return null;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender.canCommandSenderUseCommand(this.permLevel.permLevel, this.name);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(Object command) {
        return this.getCommandName().compareTo(((ICommand) command).getCommandName());
    }
}
