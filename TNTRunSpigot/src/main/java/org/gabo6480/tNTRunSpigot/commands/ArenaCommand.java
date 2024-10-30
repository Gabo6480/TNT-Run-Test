package org.gabo6480.tNTRunSpigot.commands;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.gabo6480.tNTRunSpigot.TNTRunSpigot;
import org.gabo6480.tNTRunSpigot.commands.core.CommandContext;
import org.gabo6480.tNTRunSpigot.commands.core.CommandRequirements;
import org.gabo6480.tNTRunSpigot.commands.core.CommandTemplate;
import org.gabo6480.tNTRunSpigot.commands.core.arguments.BooleanArgumentProvider;
import org.gabo6480.tNTRunSpigot.commands.core.arguments.FileArgumentProvider;
import org.gabo6480.tNTRunSpigot.commands.core.arguments.ArenaArgumentProvider;
import org.gabo6480.tNTRunSpigot.commands.core.arguments.SingleWordArgumentProvider;
import org.gabo6480.tNTRunSpigot.entities.ArenaEntity;
import org.gabo6480.tNTRunSpigot.repositories.ArenaRepository;
import org.gabo6480.tNTRunSpigot.repositories.ArenaRepository_;


public class ArenaCommand extends CommandTemplate {

    public static class ArenaCreateSubCommand extends CommandTemplate{
        public ArenaCreateSubCommand() {
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

            var session = TNTRunSpigot.instance.getSessionFactory().openStatelessSession();

            ArenaRepository repo = new ArenaRepository_(session);

            var arena = repo.findByName(nameAsString);

            if(arena != null && !arena.isEmpty()) {
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


            var world = new WorldCreator(worldPathString).createWorld();

            assert world != null;

            world.setAutoSave(false);


            System.out.println(nameAsString + " // " + worldPathString + " // " + world.getUID());

            ArenaEntity newArena = new ArenaEntity();
            newArena.setName(nameAsString);
            newArena.setWorldPath(worldPathString);
            newArena.setUUID(world.getUID());

            repo.insert(newArena);

            context.getSender().sendMessage("§2Arena \"" + nameAsString + "\" created successfully!");

            session.close();

            return true;
        }
    }

    public static class ArenaTeleportSubCommand extends CommandTemplate{
        public ArenaTeleportSubCommand() {
            this.aliases.add( "teleport" );

            this.requiredArgs.add(new ArenaArgumentProvider());
            this.optionalArgs.add(new BooleanArgumentProvider("force load", false));

            this.requirements = CommandRequirements.builder().playerOnly(true).build();
        }

        @Override
        protected boolean Execute(CommandContext context) {
            var name = context.getArguments().get(0);
            if(name == null) return false;

            var nameAsString = name.toString();

            if(nameAsString.isEmpty()) return false;

            var session = TNTRunSpigot.instance.getSessionFactory().openStatelessSession();

            ArenaRepository repo = new ArenaRepository_(session);

            var lobbies = repo.findByName(nameAsString);

            if(lobbies == null || lobbies.isEmpty()) {
                context.getSender().sendMessage("§4There is no such arena as \"" + nameAsString + "\"");
                return true;
            }

            var arena = lobbies.get(0);


            var world = Bukkit.getWorld(arena.getUUID());

            if(world == null){
                var forceLoad = context.getArguments().get(1);

                if(forceLoad == null || !forceLoad.toBoolean()) {

                    context.getSender().sendMessage("§4The arena \"" + nameAsString + "\" is unloaded, try again later. Or set [force load] to true. ");
                    return true;
                }
                else if(forceLoad.toBoolean()){
                    context.getSender().sendMessage("§1Loading \"" + nameAsString + "\" arena before teleport..." );
                    arena.LoadArena(repo);
                    context.getSender().sendMessage("§2The arena \"" + nameAsString + "\" is now loaded. ");
                    world = Bukkit.getWorld(arena.getUUID());
                }
            }


            assert context.getPlayer() != null;
            assert world != null;

            context.getPlayer().teleport(world.getSpawnLocation());

            session.close();
            return true;
        }
    }

    public static class ArenaLoadSubCommand extends CommandTemplate{
        public ArenaLoadSubCommand() {
            this.aliases.add( "load" );

            this.requiredArgs.add(new ArenaArgumentProvider());
        }

        @Override
        protected boolean Execute(CommandContext context) {
            var name = context.getArguments().get(0);
            if(name == null) return false;

            var nameAsString = name.toString();

            if(nameAsString.isEmpty()) return false;

            var session = TNTRunSpigot.instance.getSessionFactory().openStatelessSession();

            ArenaRepository repo = new ArenaRepository_(session);

            var lobbies = repo.findByName(nameAsString);

            if(lobbies == null || lobbies.isEmpty()) {
                context.getSender().sendMessage("§4There is no such Lobby as \"" + nameAsString + "\"");
                return true;
            }

            var arena = lobbies.get(0);


            var world = Bukkit.getWorld(arena.getUUID());

            if(world != null){

                context.getSender().sendMessage("§1The arena \"" + nameAsString + "\" is already loaded. ");
                return true;
            }

            var res = arena.LoadArena(repo);

            if(res) {
                context.getSender().sendMessage("§2The arena \"" + nameAsString + "\" is now loaded. ");
            }
            else {
                context.getSender().sendMessage("§4The arena \"" + nameAsString + "\" could not be loaded. ");
            }

            session.close();
            return true;
        }
    }

    public static class ArenaUnloadSubCommand extends CommandTemplate{
        public ArenaUnloadSubCommand() {
            this.aliases.add( "unload" );

            this.requiredArgs.add(new ArenaArgumentProvider());
        }

        @Override
        protected boolean Execute(CommandContext context) {
            var name = context.getArguments().get(0);
            if(name == null) return false;

            var nameAsString = name.toString();

            if(nameAsString.isEmpty()) return false;

            var session = TNTRunSpigot.instance.getSessionFactory().openStatelessSession();

            ArenaRepository repo = new ArenaRepository_(session);

            var lobbies = repo.findByName(nameAsString);

            if(lobbies == null || lobbies.isEmpty()) {
                context.getSender().sendMessage("§4There is no such Lobby as \"" + nameAsString + "\"");
                return true;
            }

            var arena = lobbies.get(0);

            var world = Bukkit.getWorld(arena.getUUID());

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

            session.close();
            return true;
        }
    }

    public ArenaCommand() {
        super();

        this.aliases.add( "arena" );

        this.subCommands.add(new ArenaCreateSubCommand());
        this.subCommands.add(new ArenaTeleportSubCommand());
        this.subCommands.add(new ArenaLoadSubCommand());
        this.subCommands.add(new ArenaUnloadSubCommand());
    }

    @Override
    protected boolean Execute(CommandContext context) {
        return false;
    }
}
