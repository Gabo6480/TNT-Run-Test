package org.gabo6480.tNTRunSpigot.commands;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.gabo6480.tNTRunSpigot.TNTRunSpigot;
import org.gabo6480.tNTRunSpigot.commands.core.CommandContext;
import org.gabo6480.tNTRunSpigot.commands.core.CommandRequirements;
import org.gabo6480.tNTRunSpigot.commands.core.CommandTemplate;
import org.gabo6480.tNTRunSpigot.commands.core.arguments.BooleanArgumentProvider;
import org.gabo6480.tNTRunSpigot.commands.core.arguments.FileArgumentProvider;
import org.gabo6480.tNTRunSpigot.commands.core.arguments.LobbyArgumentProvider;
import org.gabo6480.tNTRunSpigot.commands.core.arguments.SingleWordArgumentProvider;
import org.gabo6480.tNTRunSpigot.entities.LobbyEntity;
import org.gabo6480.tNTRunSpigot.repositories.LobbyRepository;
import org.gabo6480.tNTRunSpigot.repositories.LobbyRepository_;


public class LobbyCommand extends CommandTemplate {

    public static class LobbyCreateSubCommand extends CommandTemplate{
        public LobbyCreateSubCommand() {
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

            LobbyRepository repo = new LobbyRepository_(session);

            var lobby = repo.findByName(nameAsString);

            if(lobby != null && !lobby.isEmpty()) {
                context.getSender().sendMessage("§4The is already a Lobby named \"" + nameAsString + "\"");
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

            context.getSender().sendMessage("§2Creating Lobby \"" + nameAsString + "\"...");


            var world = new WorldCreator(worldPathString).createWorld();

            assert world != null;

            world.setAutoSave(false);


            System.out.println(nameAsString + " // " + worldPathString + " // " + world.getUID());

            LobbyEntity newLobby = new LobbyEntity();
            newLobby.setName(nameAsString);
            newLobby.setWorldPath(worldPathString);
            newLobby.setUUID(world.getUID());

            repo.insert(newLobby);

            context.getSender().sendMessage("§2Lobby \"" + nameAsString + "\" created successfully!");

            session.close();

            return true;
        }
    }

    public static class LobbyTeleportSubCommand extends CommandTemplate{
        public LobbyTeleportSubCommand() {
            this.aliases.add( "teleport" );

            this.requiredArgs.add(new LobbyArgumentProvider());
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

            LobbyRepository repo = new LobbyRepository_(session);

            var lobbies = repo.findByName(nameAsString);

            if(lobbies == null || lobbies.isEmpty()) {
                context.getSender().sendMessage("§4There is no such Lobby as \"" + nameAsString + "\"");
                return true;
            }

            var lobby = lobbies.get(0);


            var world = Bukkit.getWorld(lobby.getUUID());

            if(world == null){
                var forceLoad = context.getArguments().get(1);

                if(forceLoad == null || !forceLoad.toBoolean()) {

                    context.getSender().sendMessage("§4The lobby \"" + nameAsString + "\" is unloaded, try again later. Or set [force load] to true. ");
                    return true;
                }
                else if(forceLoad.toBoolean()){
                    context.getSender().sendMessage("§1Loading \"" + nameAsString + "\" lobby before teleport..." );
                    lobby.LoadLobby(repo);
                    context.getSender().sendMessage("§2The lobby \"" + nameAsString + "\" is now loaded. ");
                    world = Bukkit.getWorld(lobby.getUUID());
                }
            }


            assert context.getPlayer() != null;
            assert world != null;

            context.getPlayer().teleport(world.getSpawnLocation());

            session.close();
            return true;
        }
    }

    public static class LobbyLoadSubCommand extends CommandTemplate{
        public LobbyLoadSubCommand() {
            this.aliases.add( "load" );

            this.requiredArgs.add(new LobbyArgumentProvider());
        }

        @Override
        protected boolean Execute(CommandContext context) {
            var name = context.getArguments().get(0);
            if(name == null) return false;

            var nameAsString = name.toString();

            if(nameAsString.isEmpty()) return false;

            var session = TNTRunSpigot.instance.getSessionFactory().openStatelessSession();

            LobbyRepository repo = new LobbyRepository_(session);

            var lobbies = repo.findByName(nameAsString);

            if(lobbies == null || lobbies.isEmpty()) {
                context.getSender().sendMessage("§4There is no such Lobby as \"" + nameAsString + "\"");
                return true;
            }

            var lobby = lobbies.get(0);


            var world = Bukkit.getWorld(lobby.getUUID());

            if(world != null){

                context.getSender().sendMessage("§1The lobby \"" + nameAsString + "\" is already loaded. ");
                return true;
            }

            var res = lobby.LoadLobby(repo);

            if(res) {
                context.getSender().sendMessage("§2The lobby \"" + nameAsString + "\" is now loaded. ");
            }
            else {
                context.getSender().sendMessage("§4The lobby \"" + nameAsString + "\" could not be loaded. ");
            }

            session.close();
            return true;
        }
    }

    public static class LobbyUnloadSubCommand extends CommandTemplate{
        public LobbyUnloadSubCommand() {
            this.aliases.add( "unload" );

            this.requiredArgs.add(new LobbyArgumentProvider());
        }

        @Override
        protected boolean Execute(CommandContext context) {
            var name = context.getArguments().get(0);
            if(name == null) return false;

            var nameAsString = name.toString();

            if(nameAsString.isEmpty()) return false;

            var session = TNTRunSpigot.instance.getSessionFactory().openStatelessSession();

            LobbyRepository repo = new LobbyRepository_(session);

            var lobbies = repo.findByName(nameAsString);

            if(lobbies == null || lobbies.isEmpty()) {
                context.getSender().sendMessage("§4There is no such Lobby as \"" + nameAsString + "\"");
                return true;
            }

            var lobby = lobbies.get(0);

            var world = Bukkit.getWorld(lobby.getUUID());

            if(world == null){

                context.getSender().sendMessage("§1The lobby \"" + nameAsString + "\" is already unloaded. ");
                return true;
            }

            var res = lobby.UnloadLobby();

            if(res) {
                context.getSender().sendMessage("§3The lobby \"" + nameAsString + "\" is now unloaded. ");
            }
            else {
                context.getSender().sendMessage("§4The lobby \"" + nameAsString + "\" could not be unloaded. ");
            }

            session.close();
            return true;
        }
    }

    public LobbyCommand() {
        super();

        this.aliases.add( "lobby" );

        this.subCommands.add(new LobbyCreateSubCommand());
        this.subCommands.add(new LobbyTeleportSubCommand());
        this.subCommands.add(new LobbyLoadSubCommand());
        this.subCommands.add(new LobbyUnloadSubCommand());
    }

    @Override
    protected boolean Execute(CommandContext context) {
        return false;
    }
}
