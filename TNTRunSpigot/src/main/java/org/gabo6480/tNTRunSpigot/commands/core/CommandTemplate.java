package org.gabo6480.tNTRunSpigot.commands.core;

import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
public abstract class CommandTemplate {
    // Command Aliases
    protected List<String> aliases;

    // Information on the args
    protected LinkedList<ArgumentProvider> requiredArgs;
    protected LinkedList<OptionalArgumentProvider> optionalArgs;

    // Requirements to execute this command
    protected CommandRequirements requirements;
    /*
        Subcommands
     */
    protected List<CommandTemplate> subCommands;
    /*
        Help
     */
    protected List<String> helpLong;
    protected CommandVisibility visibility;
    protected String helpShort;

    public CommandTemplate() {
        aliases = new ArrayList<>();

        requiredArgs = new LinkedList<>();
        optionalArgs = new LinkedList<>();

        requirements = CommandRequirements.builder().build();

        subCommands = new ArrayList<>();

        helpLong = new ArrayList<>();
        visibility = CommandVisibility.VISIBLE;
        helpShort = "";
    }

    protected abstract boolean Execute(CommandContext context);

    public boolean Evaluate(CommandContext context) {
        // Is there a matching sub command?
        if (!context.arguments.isEmpty()) {
            String firstArg = Objects.requireNonNull(context.arguments.get(0)).toString().toLowerCase();
            for (var subCommand : this.subCommands) {
                if (subCommand.aliases.contains(firstArg)) {
                    context.arguments.remove(0);
                    context.AddToCommandChain(this);

                    return subCommand.Evaluate(context);
                }
            }
        }

        if (!this.IsValidCall(context)) {
            return false;
        }

        return Execute(context);
    }

    public List<String> Complete(CommandContext context){
        List<String> completions = new ArrayList<>();
        int argCount = context.arguments.size();

        if (!context.arguments.isEmpty()) {
            String firstArg = Objects.requireNonNull(context.arguments.get(0)).toString().toLowerCase();

            if(firstArg.isEmpty()){ //Just show all subcommands
                for (var subCommand : this.subCommands) {
                    if (subCommand.IsVisible(context))
                        completions.addAll(subCommand.aliases);
                }
            }
            else if(argCount == 1) {
                // Is there a matching sub command?
                for (var subCommand : this.subCommands) {
                    // We check each alias to see if they match whatever is currently written
                    for (String s : subCommand.aliases) {
                        if (s.startsWith(firstArg)) {
                            completions.addAll(subCommand.aliases);
                            break;
                        }
                    }
                }
            }
            else {
                // If there is at least 2 arguments, then check if there is an exactly matching subcommand and drill down into it
                for (var subCommand : this.subCommands) {
                    if (subCommand.aliases.contains(firstArg)) {
                        context.arguments.remove(0); // Remove this argument from the context

                        return subCommand.Complete(context);
                    }
                }
            }

            int currentArgIndex = argCount - 1;
            // If there are no exactly matching subcommands, then we start processing the requiredArgs
            if(argCount <= requiredArgs.size()){
                if(Objects.requireNonNull(context.arguments.get(currentArgIndex)).toString().isEmpty()){
                    completions.add("<" + requiredArgs.get(currentArgIndex).GetName() + ">");
                }

                List<String> result = requiredArgs.get(currentArgIndex).GetCompletions(context, currentArgIndex);
                if(result != null) completions.addAll(result.stream().map(ChatColor::stripColor).toList());
            }
            else if(argCount <= requiredArgs.size() + optionalArgs.size()){ //If we are already over the requiredArgs, check for optionalArgs
                int optionalArgumentIndex = currentArgIndex - requiredArgs.size();
                if(Objects.requireNonNull(context.arguments.get(currentArgIndex)).toString().isEmpty()){
                    String optionalArgName = optionalArgs.get(optionalArgumentIndex).GetName();
                    String optionalArgDefault = optionalArgs.get(optionalArgumentIndex).GetDefaultValue(context);

                    if(optionalArgDefault != null)
                        optionalArgName += "=" + optionalArgDefault;

                    completions.add("[" + optionalArgName + "]");
                }

                List<String> result = optionalArgs.get(optionalArgumentIndex).GetCompletions(context, currentArgIndex);
                if(result != null) completions.addAll(result.stream().map(ChatColor::stripColor).toList());
            }
        }

        return completions;
    }

    ///
    //      VALIDATIONS
    ///

    private boolean IsValidCall(CommandContext context) {
        return requirements.ComputeRequirements(context) && AreArgsValid(context);
    }

    private boolean AreArgsValid(CommandContext context){
        if (context.arguments.size() < this.requiredArgs.size()) {
            //TODO: TOO FEW ARGS
            return false;
        }

        if (context.arguments.size() > this.requiredArgs.size() + this.optionalArgs.size()) {
            // Get the args that are over the limit
            //List<String> theToMany = context.args.subList(this.requiredArgs.size() + this.optionalArgs.size(), context.args.size());
            //TODO: TOO MANY ARGS
            return false;
        }

        return true;
    }

    private boolean IsVisible(CommandContext context) {
        return this.visibility != CommandVisibility.INVISIBLE && requirements.ComputeRequirements(context);
    }

}
