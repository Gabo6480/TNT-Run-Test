package org.gabo6480.tNTRunSpigot.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.gabo6480.tNTRunSpigot.TNTRunSpigot;
import org.gabo6480.tNTRunSpigot.commands.core.CommandContext;
import org.gabo6480.tNTRunSpigot.commands.core.CommandTemplate;
import org.gabo6480.tNTRunSpigot.commands.core.CommandVisibility;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

//TODO: Reemplazar declaración con anotaciones
//TODO: Automatizar que los comandos se registren en getCommandMap, y se generen+registren los permisos
public class CommandRoot extends CommandTemplate implements TabExecutor {

    public static final String command = "tntrun";

    CommandTemplate cmdHelp;

    public CommandRoot(TNTRunSpigot plugin) {
        //this.cmdHelp = cmdHelp;

        this.visibility = CommandVisibility.VISIBLE;

        this.subCommands.add(new ArenaCommand(plugin.getArenaManager()));
        this.subCommands.add(new WorldCommand());
        this.subCommands.add(new LeaveCommand(plugin.getArenaManager()));
        this.subCommands.add(new JoinCommand(plugin.getArenaManager()));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return this.Evaluate(new CommandContext(sender, label, new ArrayList<>(Arrays.asList(args))));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return this.Complete(new CommandContext(sender, label, new LinkedList<>(Arrays.asList(args))));
    }

    @Override
    public boolean Execute(CommandContext context) {
        context.AddToCommandChain(this);
        return this.cmdHelp.Evaluate(context);
    }
}
