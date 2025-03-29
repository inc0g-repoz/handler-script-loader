package com.github.inc0grepoz.hsl.handler;

import java.util.List;
import java.util.Objects;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.inc0grepoz.ltse.Script;
import com.github.inc0grepoz.ltse.unit.UnitFunction;
import com.github.inc0grepoz.ltse.util.PrimitiveTester;

@SuppressWarnings("unchecked")
public class MappedCommand extends Command {

    private final UnitFunction fne, fnt;

    public MappedCommand(Script script, String fnExe, String fnTab,
            String name, String description, String permission, String usage,
            List<String> aliases) {

        super(name, description, usage, aliases);

        fne = Objects.requireNonNull(script.getFunction(fnExe, 3),
                "Function not found (" + fnExe + ")");

        if (fnTab == null) {
            fnt = null;
        } else {
            fnt = Objects.requireNonNull(script.getFunction(fnTab, 3),
                    "Function not found (" + fnTab + ")");
        }

        setPermission(permission);
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        return !PrimitiveTester.isDefaultValue(fne.call(sender, alias, args));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias,
            String[] args) throws IllegalArgumentException {
        return fnt == null ? null : (List<String>) fnt.call(sender, alias, args);
    }

}
