package org.gabo6480.tNTRunSpigot.commands;

import org.gabo6480.tNTRunSpigot.commands.core.CommandContext;
import org.gabo6480.tNTRunSpigot.commands.core.CommandRequirements;
import org.gabo6480.tNTRunSpigot.commands.core.CommandTemplate;
import org.gabo6480.tNTRunSpigot.commands.core.arguments.ArenaArgumentProvider;
import org.gabo6480.tNTRunSpigot.entities.arena.ArenaState;
import org.gabo6480.tNTRunSpigot.managers.ArenaManager;

public class LeaveCommand extends CommandTemplate {
    final ArenaManager arenaManager;

    public LeaveCommand(ArenaManager arenaManager) {
        super();

        this.arenaManager = arenaManager;

        this.aliases.add( "leave" );

        this.requirements = CommandRequirements.builder().playerOnly(true).build();
    }

    @Override
    protected boolean Execute(CommandContext context) {
        var player = context.getPlayer();

        assert player != null;

        if(!arenaManager.IsInArena(player)){
            context.getSender().sendMessage("§4You are not currently in an arena.");
            return true;
        }

        var arena = arenaManager.GetArena(player.getWorld());

        assert arena != null;

        var arenaPlayer = arena.GetArenaPlayer(context.getPlayer());

        assert arenaPlayer != null;

        context.getSender().sendMessage("§3Leaving arena \"" + arena.getName() + "\"...");

        arena.TeleportToLobby(arenaPlayer);

        return true;
    }
}
