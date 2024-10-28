package org.gabo6480.tNTRunSpigot.commands.core;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
public class CommandContext {
    public class CommandArgumentHolder {
        public class CommandArgument {

            @NotNull
            String value;

            public CommandArgument(@NotNull String value) {
                this.value = value;
            }

            @Override
            public String toString() {
                return value;
            }

            public Integer toInteger(){
                return Integer.parseInt(value);
            }

            public Double toDouble(){
                return Double.parseDouble(value);
            }
        }

        public CommandArgumentHolder(@NotNull List<String> arguments) {
            this.arguments = new ArrayList<>(arguments.stream().map(CommandArgument::new).toList());
        }

        @NotNull
        List<CommandArgument> arguments;

        @Nullable
        public CommandArgument get(int idx){
            if(idx > arguments.size()) return null;

            return arguments.get(idx);
        }

        boolean isEmpty(){
            return arguments.isEmpty();
        }

        public CommandArgument remove(int idx){
            return arguments.remove(idx);
        }

        public int size(){
            return arguments.size();
        }
    }


    @NotNull
    CommandSender sender;

    @NotNull
    CommandArgumentHolder arguments;

    @NotNull
    String label;

    @Nullable
    Player player;

    private final List<CommandTemplate> commandChain = new LinkedList<>();

    public CommandContext(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> arguments) {
        this.arguments = new CommandArgumentHolder(arguments);
        this.sender = sender;
        this.label = label;

        if(sender instanceof Player){
            this.player = (Player) sender;
        }
    }

    public void AddToCommandChain(CommandTemplate command){
        this.commandChain.add(command);
    }

}
