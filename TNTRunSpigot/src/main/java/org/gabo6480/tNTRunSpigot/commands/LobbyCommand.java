package org.gabo6480.tNTRunSpigot.commands;

import org.gabo6480.tNTRunSpigot.commands.core.CommandContext;
import org.gabo6480.tNTRunSpigot.commands.core.CommandTemplate;
import org.gabo6480.tNTRunSpigot.commands.core.arguments.FileArgumentProvider;
import org.gabo6480.tNTRunSpigot.commands.core.arguments.SingleWordArgumentProvider;

import java.util.List;

public class LobbyCommand extends CommandTemplate {

    public class LobbyCreateSubCommand extends CommandTemplate{
        public LobbyCreateSubCommand() {
            this.aliases.add( "create" );

            this.requiredArgs.add(new SingleWordArgumentProvider("name"));
            this.optionalArgs.add(new FileArgumentProvider("worldFile"));
        }

        @Override
        protected boolean Execute(CommandContext context) {
            return false;
        }
    }


    public LobbyCommand() {
        super();

        this.aliases.add( "lobby" );

        this.subCommands.add(new LobbyCreateSubCommand());
    }

    @Override
    protected boolean Execute(CommandContext context) {
        return false;
    }
}
