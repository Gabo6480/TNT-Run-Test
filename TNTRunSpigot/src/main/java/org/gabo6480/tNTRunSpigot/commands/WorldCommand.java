package org.gabo6480.tNTRunSpigot.commands;

import org.bukkit.Bukkit;
import org.gabo6480.tNTRunSpigot.TNTRunSpigot;
import org.gabo6480.tNTRunSpigot.commands.core.CommandContext;
import org.gabo6480.tNTRunSpigot.commands.core.CommandRequirements;
import org.gabo6480.tNTRunSpigot.commands.core.CommandTemplate;
import org.gabo6480.tNTRunSpigot.commands.core.arguments.BooleanArgumentProvider;
import org.gabo6480.tNTRunSpigot.commands.core.arguments.GameRuleArgumentProvider;
import org.gabo6480.tNTRunSpigot.commands.core.arguments.LobbyArgumentProvider;
import org.gabo6480.tNTRunSpigot.commands.core.arguments.WorldArgumentProvider;
import org.gabo6480.tNTRunSpigot.repositories.LobbyRepository;
import org.gabo6480.tNTRunSpigot.repositories.LobbyRepository_;

public class WorldCommand extends CommandTemplate {
    public static class WorldTeleportSubCommand extends CommandTemplate{
        public WorldTeleportSubCommand() {
            this.aliases.add( "teleport" );

            this.requiredArgs.add(new WorldArgumentProvider());

            this.requirements = CommandRequirements.builder().playerOnly(true).build();
        }

        @Override
        protected boolean Execute(CommandContext context) {
            var name = context.getArguments().get(0);
            if(name == null) return false;

            var nameAsString = name.toString();

            if(nameAsString.isEmpty()) return false;

            var world = Bukkit.getWorld(nameAsString);

            if(world == null){
                context.getSender().sendMessage("§4Couldn't find world \"" + nameAsString + "\". ");
                return true;
            }

            assert context.getPlayer() != null;

            context.getPlayer().teleport(world.getSpawnLocation());

            return true;
        }
    }


    public WorldCommand() {
        super();

        this.aliases.add( "world" );

        this.subCommands.add(new WorldTeleportSubCommand());
    }

    @Override
    protected boolean Execute(CommandContext context) {
        return false;
    }
}
