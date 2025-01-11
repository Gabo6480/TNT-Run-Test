package org.gabo6480.tNTRunSpigot.commands;

import org.gabo6480.tNTRunSpigot.commands.core.CommandContext;
import org.gabo6480.tNTRunSpigot.commands.core.CommandTemplate;
import org.gabo6480.tNTRunSpigot.commands.core.annotations.PlayerOnly;
import org.gabo6480.tNTRunSpigot.commands.core.arguments.ArenaArgumentProvider;
import org.gabo6480.tNTRunSpigot.managers.ArenaManager;

@PlayerOnly
public class JoinCommand extends CommandTemplate {

    final ArenaManager arenaManager;
    public JoinCommand(ArenaManager arenaManager) {
        super();

        this.arenaManager = arenaManager;

        this.aliases.add( "join" );

        this.requiredArgs.add(new ArenaArgumentProvider((arena, context) -> context.getPlayer() != null && arena.CanPlayerJoin(context.getPlayer())));
    }

    @Override
    protected boolean Execute(CommandContext context) {
        var player = context.getPlayer();

        assert player != null;

        var name = context.getArguments().get(0);
        if(name == null) return false;

        var nameAsString = name.toString();

        if(nameAsString.isEmpty()) return false;

        var arena = arenaManager.GetArena(nameAsString);

        if(arena == null) {
            context.getSender().sendMessage("§4There is no such arena as \"" + nameAsString + "\"");
            return true;
        }

        if(!arena.CanPlayerJoin(player)){
            context.getSender().sendMessage("§4You can't join \"" + nameAsString + "\"");
            return true;
        }

        var otherArena = arenaManager.GetArena(player.getWorld());

        if(otherArena != null && otherArena.IsInArena(player)){
            if(otherArena.getUUID().equals(arena.getUUID())){
                context.getSender().sendMessage("§3You are already in arena \"" + otherArena.getName() + "\"!");
                return true;
            }

            var arenaPlayer = otherArena.GetArenaPlayer(context.getPlayer());
            assert arenaPlayer != null;
            otherArena.PlayerLeft(arenaPlayer);
            context.getSender().sendMessage("§3Leaving arena \"" + otherArena.getName() + "\"...");
        }

        var result = arena.AddPlayer(player);

        if(result){
            context.getSender().sendMessage("§2 Joined \"" + nameAsString + "\"!");
        }
        else {
            context.getSender().sendMessage("§4Couldn't join \"" + nameAsString + "\"");
        }

        return true;
    }
}
