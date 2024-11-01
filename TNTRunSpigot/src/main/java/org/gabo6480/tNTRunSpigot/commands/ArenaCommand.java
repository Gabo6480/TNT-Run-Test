package org.gabo6480.tNTRunSpigot.commands;

import org.bukkit.Location;
import org.gabo6480.tNTRunSpigot.TNTRunSpigot;
import org.gabo6480.tNTRunSpigot.commands.core.CommandContext;
import org.gabo6480.tNTRunSpigot.commands.core.CommandRequirements;
import org.gabo6480.tNTRunSpigot.commands.core.CommandTemplate;
import org.gabo6480.tNTRunSpigot.commands.core.arguments.*;
import org.gabo6480.tNTRunSpigot.entities.arena.ArenaState;
import org.gabo6480.tNTRunSpigot.managers.ArenaManager;


public class ArenaCommand extends CommandTemplate {

    public static class ArenaCreateSubCommand extends CommandTemplate{
        final ArenaManager arenaManager;

        public ArenaCreateSubCommand(ArenaManager arenaManager) {
            this.arenaManager = arenaManager;
            this.aliases.add( "create" );

            this.requiredArgs.add(new SingleWordArgumentProvider("name"));
            this.optionalArgs.add(new FileArgumentProvider("worldFile"));
        }

        @Override
        protected boolean Execute(CommandContext context) {
            var name = context.getArguments().get(0);
            if(name == null) return false;

            var nameAsString = name.toString();

            if(nameAsString.isEmpty()) return false;

            var arena = arenaManager.GetArena(nameAsString);

            if(arena != null) {
                context.getSender().sendMessage("§4The is already a arena named \"" + nameAsString + "\"");
                return true;
            }

            var worldPath = context.getArguments().get(1);
            String worldPathString;
            if(worldPath != null){
                worldPathString = worldPath.toString();
            }
            else {
                worldPathString = nameAsString;
            }

            context.getSender().sendMessage("§2Creating arena \"" + nameAsString + "\"...");

            var result = arenaManager.CreateArena(nameAsString, worldPathString);

            if(result) context.getSender().sendMessage("§2Arena \"" + nameAsString + "\" created successfully!");
            else context.getSender().sendMessage("§4Failed to create arena \"" + nameAsString + "\".");

            return true;
        }
    }

    public static class ArenaTeleportSubCommand extends CommandTemplate{
        final ArenaManager arenaManager;

        public ArenaTeleportSubCommand(ArenaManager arenaManager) {
            this.arenaManager = arenaManager;
            this.aliases.add( "teleport" );

            this.requiredArgs.add(new ArenaArgumentProvider());
            this.requiredArgs.add(new ListStringArgumentProvider("place", "waitingroom", "startpoint", "waitingroom"));
            this.optionalArgs.add(new BooleanArgumentProvider("force load", false));

            this.requirements = CommandRequirements.builder().playerOnly(true).build();
        }

        @Override
        protected boolean Execute(CommandContext context) {
            var name = context.getArguments().get(0);
            if(name == null) return false;

            var nameAsString = name.toString();

            if(nameAsString.isEmpty()) return false;

            var arena = arenaManager.GetArena(nameAsString);

            if(arena == null) {
                context.getSender().sendMessage("§4There is no such arena as \"" + nameAsString + "\"");
                return true;
            }

            var place = context.getArguments().get(1);

            Location teleportLocation;

            if(place == null || place.toString().equalsIgnoreCase("waitingroom"))  teleportLocation = arena.GetWaitingRoomLocation();
            else if(place.toString().equalsIgnoreCase("startpoint"))  teleportLocation = arena.GetStartPointLocation();
            else {
                context.getSender().sendMessage("§4\"" + place + "\" is an unknown place in arena \"" + nameAsString + "\"");
                return true;
            }

            var world = arena.GetWorld();

            if(world == null){
                var forceLoad = context.getArguments().get(2);

                if(forceLoad == null || !forceLoad.toBoolean()) {
                    context.getSender().sendMessage("§4The arena \"" + nameAsString + "\" is unloaded, try again later. Or set [force load] to true. ");
                    return true;
                }
                else if(forceLoad.toBoolean()){
                    context.getSender().sendMessage("§1Loading \"" + nameAsString + "\" arena before teleport..." );
                    arenaManager.LoadArena(arena);
                    context.getSender().sendMessage("§2The arena \"" + nameAsString + "\" is now loaded. ");
                    world = arena.GetWorld();
                }
            }

            assert context.getPlayer() != null;
            assert world != null;

            context.getPlayer().teleport(teleportLocation);

            return true;
        }
    }

    public static class ArenaSetWaitingRoomSubCommand extends CommandTemplate{
        final ArenaManager arenaManager;

        public ArenaSetWaitingRoomSubCommand(ArenaManager arenaManager) {
            this.arenaManager = arenaManager;
            this.aliases.add( "setwaitingroom" );

            this.requiredArgs.add(new ArenaArgumentProvider());

            this.requirements = CommandRequirements.builder().playerOnly(true).build();
        }

        @Override
        protected boolean Execute(CommandContext context) {
            var name = context.getArguments().get(0);
            if(name == null) return false;

            var nameAsString = name.toString();

            if(nameAsString.isEmpty()) return false;

            var arena = arenaManager.GetArena(nameAsString);

            if(arena == null) {
                context.getSender().sendMessage("§4There is no such arena as \"" + nameAsString + "\"");
                return true;
            }

            var world = arena.GetWorld();

            if(world == null){
                context.getSender().sendMessage("§4The arena \"" + nameAsString + "\" is unloaded. ");
                return true;
            }


            assert context.getPlayer() != null;

            if(!world.getPlayers().contains(context.getPlayer())){
                context.getSender().sendMessage("§4You are not in the arena \"" + nameAsString + "\". ");
                return true;
            }

            arena.SetWaitingRoomLocation(context.getPlayer().getLocation());

            context.getSender().sendMessage("§2You updated the Waiting Room in the arena \"" + nameAsString + "\". ");

            arenaManager.SaveArena(arena);

            return true;
        }
    }

    public static class ArenaSetStartPointSubCommand extends CommandTemplate{
        final ArenaManager arenaManager;

        public ArenaSetStartPointSubCommand(ArenaManager arenaManager) {
            this.arenaManager = arenaManager;
            this.aliases.add( "setstartpoint" );

            this.requiredArgs.add(new ArenaArgumentProvider());

            this.requirements = CommandRequirements.builder().playerOnly(true).build();
        }

        @Override
        protected boolean Execute(CommandContext context) {
            var name = context.getArguments().get(0);
            if(name == null) return false;

            var nameAsString = name.toString();

            if(nameAsString.isEmpty()) return false;

            var arena = arenaManager.GetArena(nameAsString);

            if(arena == null) {
                context.getSender().sendMessage("§4There is no such arena as \"" + nameAsString + "\"");
                return true;
            }

            var world = arena.GetWorld();

            if(world == null){
                context.getSender().sendMessage("§4The arena \"" + nameAsString + "\" is unloaded. ");
                return true;
            }


            assert context.getPlayer() != null;

            if(!world.getPlayers().contains(context.getPlayer())){
                context.getSender().sendMessage("§4You are not in the arena \"" + nameAsString + "\". ");
                return true;
            }

            arena.SetStartPointLocation(context.getPlayer().getLocation());

            context.getSender().sendMessage("§2You updated the Start Point in the arena \"" + nameAsString + "\". ");

            arenaManager.SaveArena(arena);

            return true;
        }
    }

    public static class ArenaLoadSubCommand extends CommandTemplate{
        final ArenaManager arenaManager;

        public ArenaLoadSubCommand(ArenaManager arenaManager) {
            this.arenaManager = arenaManager;
            this.aliases.add( "load" );

            this.requiredArgs.add(new ArenaArgumentProvider());
        }

        @Override
        protected boolean Execute(CommandContext context) {
            var name = context.getArguments().get(0);
            if(name == null) return false;

            var nameAsString = name.toString();

            if(nameAsString.isEmpty()) return false;

            var arena = arenaManager.GetArena(nameAsString);

            if(arena == null) {
                context.getSender().sendMessage("§4There is no such arena as \"" + nameAsString + "\"");
                return true;
            }


            var world = arena.GetWorld();

            if(world != null){

                context.getSender().sendMessage("§1The arena \"" + nameAsString + "\" is already loaded. ");
                return true;
            }

            var res = arenaManager.LoadArena(arena);

            if(res) {
                context.getSender().sendMessage("§2The arena \"" + nameAsString + "\" is now loaded. ");
            }
            else {
                context.getSender().sendMessage("§4The arena \"" + nameAsString + "\" could not be loaded. ");
            }

            return true;
        }
    }

    public static class ArenaUnloadSubCommand extends CommandTemplate{
        final ArenaManager arenaManager;
        
        public ArenaUnloadSubCommand(ArenaManager arenaManager) {
            this.arenaManager = arenaManager;
            this.aliases.add( "unload" );

            this.requiredArgs.add(new ArenaArgumentProvider());
        }

        @Override
        protected boolean Execute(CommandContext context) {
            var name = context.getArguments().get(0);
            if(name == null) return false;

            var nameAsString = name.toString();

            if(nameAsString.isEmpty()) return false;

            var arena = arenaManager.GetArena(nameAsString);

            if(arena == null) {
                context.getSender().sendMessage("§4There is no such arena as \"" + nameAsString + "\"");
                return true;
            }

            var world = arena.GetWorld();

            if(world == null){
                context.getSender().sendMessage("§1The arena \"" + nameAsString + "\" is already unloaded. ");
                return true;
            }

            var res = arena.UnloadArena();

            if(res) {
                context.getSender().sendMessage("§3The arena \"" + nameAsString + "\" is now unloaded. ");
            }
            else {
                context.getSender().sendMessage("§4The arena \"" + nameAsString + "\" could not be unloaded. ");
            }
            return true;
        }
    }

    public static class ArenaSaveChangesSubCommand extends CommandTemplate{
        final ArenaManager arenaManager;

        public ArenaSaveChangesSubCommand(ArenaManager arenaManager) {
            this.arenaManager = arenaManager;
            this.aliases.add( "savechanges" );

            this.requiredArgs.add(new ArenaArgumentProvider());
        }

        @Override
        protected boolean Execute(CommandContext context) {
            var name = context.getArguments().get(0);
            if(name == null) return false;

            var nameAsString = name.toString();

            if(nameAsString.isEmpty()) return false;

            var arena = arenaManager.GetArena(nameAsString);

            if(arena == null) {
                context.getSender().sendMessage("§4There is no such arena as \"" + nameAsString + "\"");
                return true;
            }

            var world = arena.GetWorld();

            if(world == null){
                context.getSender().sendMessage("§4The arena \"" + nameAsString + "\" is unloaded. ");
                return true;
            }

            world.save();

            return true;
        }
    }

    public static class ArenaForceStartSubCommand extends CommandTemplate{
        final ArenaManager arenaManager;

        public ArenaForceStartSubCommand(ArenaManager arenaManager) {
            this.arenaManager = arenaManager;
            this.aliases.add( "forcestart" );

            this.requiredArgs.add(new ArenaArgumentProvider(((arena, context) -> arena.IsArenaState(ArenaState.WAITING_FOR_PLAYERS))));
        }

        @Override
        protected boolean Execute(CommandContext context) {
            var name = context.getArguments().get(0);
            if(name == null) return false;

            var nameAsString = name.toString();

            if(nameAsString.isEmpty()) return false;

            var arena = arenaManager.GetArena(nameAsString);

            if(arena == null) {
                context.getSender().sendMessage("§4There is no such arena as \"" + nameAsString + "\"");
                return true;
            }

            if(!arena.IsArenaState(ArenaState.WAITING_FOR_PLAYERS)) {
                context.getSender().sendMessage("§4You cannot force start \"" + nameAsString + "\"");
                return true;
            }

            if(arena.getActivePlayers().isEmpty()) {
                context.getSender().sendMessage("§4There are no players in \"" + nameAsString + "\"");
                return true;
            }

            context.getSender().sendMessage("§3Forcing game to start in \"" + nameAsString + "\"...");
            arena.BeginStartingGame();

            return true;
        }
    }

    final ArenaManager arenaManager;
    public ArenaCommand(ArenaManager arenaManager) {
        super();
        this.arenaManager = arenaManager;

        this.aliases.add( "arena" );

        this.subCommands.add(new ArenaCreateSubCommand(arenaManager));
        this.subCommands.add(new ArenaTeleportSubCommand(arenaManager));
        this.subCommands.add(new ArenaLoadSubCommand(arenaManager));
        this.subCommands.add(new ArenaUnloadSubCommand(arenaManager));
        this.subCommands.add(new ArenaSetWaitingRoomSubCommand(arenaManager));
        this.subCommands.add(new ArenaSetStartPointSubCommand(arenaManager));
        this.subCommands.add(new ArenaSaveChangesSubCommand(arenaManager));
        this.subCommands.add(new ArenaForceStartSubCommand(arenaManager));
    }

    @Override
    protected boolean Execute(CommandContext context) {
        context.getSender().sendMessage("§3Current arenas:");
        int index = 0;
        for(var arena : this.arenaManager.getArenas().values()){
            context.getSender().sendMessage("§3  " + ++index + ".- " + arena.getName() + " - " + arena.getState().name() + " (" + arena.getActivePlayers().size() + "/" + arena.getMinPlayers() + ")" );
        }

        return true;
    }
}
