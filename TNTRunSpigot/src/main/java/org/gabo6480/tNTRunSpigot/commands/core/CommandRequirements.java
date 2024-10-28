package org.gabo6480.tNTRunSpigot.commands.core;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.Nullable;

@Builder
public class CommandRequirements {

    // Permission required to execute command
    @Nullable @Getter
    private Permission permission;

    // Must be player
    private boolean playerOnly;

    public boolean ComputeRequirements(CommandContext context){
        if (playerOnly && context.player == null){
            return false;
        }

        // Did not modify CommandRequirements return true
        if (permission == null) {
            return true;
        }

        return context.sender.hasPermission(permission);
    }
}
