package uk.co.drnaylor.minecraft.hammer.core.commands;

import java.text.Format;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.ArgumentParseException;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.ArgumentMap;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.FlagParser;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.IParser;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;

public abstract class CommandCore {

    static final Format dateFormatter;
    static final ResourceBundle messageBundle = ResourceBundle.getBundle("messages", Locale.getDefault());

    Collection<String> permissionNodes = new ArrayList<>();
    private final boolean isAsync;
    private final List<ParserEntry> parsersList;

    final HammerCore core;

    static {
        dateFormatter = new SimpleDateFormat(messageBundle.getString("hammer.display.date"));
    }

    CommandCore(HammerCore core) {
        this.core = core;
        RunAsync a = this.getClass().getAnnotation(RunAsync.class);
        this.isAsync = a != null && a.isAsync();
        this.parsersList = createArgumentParserList();
    }

    protected abstract List<ParserEntry> createArgumentParserList();

    private Optional<ArgumentMap> getArguments(List<String> arguments) throws HammerException, ArgumentParseException {
        ListIterator<String> lis = arguments.listIterator();
        ArgumentMap am = new ArgumentMap();
        for (ParserEntry pe : parsersList) {
            Optional o;
            try {
                o = pe.parser.parseArgument(lis);
            } catch (ArgumentParseException ape) {
                if (!ape.isDontSkip() && pe.isOptional) {
                    o = Optional.empty();
                } else {
                    // Re-throw
                    throw new ArgumentParseException(pe.name, ape);
                }
            }

            if (o.isPresent()) {
                am.put(pe.name, o.get());
            } else {
                // Is it optional?
                if (pe.isOptional) {
                    pe.parser.onFailedButOptional(lis);
                } else {
                    return Optional.empty();
                }
            }
        }

        return Optional.of(am);
    }

    protected abstract boolean requiresDatabase();

    /**
     * Executes the specific routines in this command core with the specified source.
     *
     * @param source The {@link WrappedCommandSource} that is executing the command.
     * @param arguments The arguments of the command
     * @param conn If the command requires database access, holds a {@link DatabaseConnection} object. Otherwise, null.
     * @return Whether the command succeeded
     * @throws HammerException Thrown if an exception is thrown in the command core.
     */
    protected abstract boolean executeCommand(WrappedCommandSource source, ArgumentMap arguments, DatabaseConnection conn) throws HammerException;

    protected abstract String commandName();

    /**
     * Gets the usage of this command
     *
     * @return The {@link HammerText}
     */
    public final HammerText getUsageMessage() {
        // Get the command name.
        StringBuilder args = new StringBuilder("/").append(commandName());
        for (ParserEntry parserEntry : this.parsersList) {
            args.append(" ");
            String n = parserEntry.name;
            if (parserEntry.parser instanceof FlagParser) {
                n = ((FlagParser) parserEntry.parser).getFlagEntries();
            }

            if (parserEntry.isOptional) {
                args.append("[").append(n).append("]");
            } else {
                args.append("<").append(n).append(">");
            }
        }

        return new HammerTextBuilder().add(args.toString(), HammerTextColours.YELLOW).build();
    }

    public final Collection<String> getRequiredPermissions() {
        return permissionNodes;
    }

    /**
     * Entry point into any command core
     *
     * @param source The {@link WrappedCommandSource} that is executing the command
     * @param arguments The arguments of the command
     * @return Whether the command succeeded
     * @throws HammerException Thrown if an exception is thrown in the command core
     */
    public final boolean executeCommand(final WrappedCommandSource source, final List<String> arguments) throws HammerException {
        // Permission check
        for (String p : this.getRequiredPermissions()) {
            if (!source.hasPermission(p)) {
                sendNoPermsMessage(source);
                return true;
            }
        }

        /**
         * This runner allows us to run commands async, if the class is marked with the {@link RunAsync} annotation
         */
        CommandRunner r = new CommandRunner() {
            @Override
            public boolean runCommand(ArgumentMap args) throws HammerException {
                // Command execution.
                if (requiresDatabase()) {
                    try (DatabaseConnection conn = core.getDatabaseConnection()) {
                        return executeCommand(source, args, conn);
                    } catch (HammerException ex) {
                        throw ex;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw new HammerException("An unspecified error occurred", ex);
                    }
                } else {
                    return executeCommand(source, args, null);
                }
            }

            @Override
            public void run() {
                try {
                    Optional<ArgumentMap> arg = constructArgumentMap(source, arguments);
                    if (arg.isPresent()) {
                        runCommand(arg.get());
                    } else {
                        sendUsageMessage(source);
                    }
                } catch (HammerException ex) {
                    source.sendMessage(ex.getMessage());
                }
            }
        };

        // Get the class and check for the RunAsync annotation.
        if (isAsync) {
            core.getWrappedServer().getScheduler().runAsyncNow(r);
            return true;
        }

        Optional<ArgumentMap> arg;
        try {
            arg = constructArgumentMap(source, arguments);
            if (arg.isPresent()) {
                return r.runCommand(arg.get());
            } else {
                sendUsageMessage(source);
                return true;
            }
        } catch (HammerException ex) {
            source.sendMessage(ex.getMessage());
        }

        return true;
    }

    private Optional<ArgumentMap> constructArgumentMap(final WrappedCommandSource source, List<String> args) throws HammerException {
        try {
            return getArguments(args);
        } catch (ArgumentParseException e) {
            source.sendMessage(new HammerTextBuilder().add("[Hammer] ", HammerTextColours.RED).add(e.getHammerTextMessage()).build());
            return Optional.empty();
        }
    }

    /**
     * Creates and sends a templated message
     *
     * @param player The {@link WrappedCommandSource} to send the message to
     * @param messageKey The message key in the resource bundle
     * @param isError Whether to create an error message or not
     * @param useStub Whether to use the [Hammer] tag
     * @param replacements The replacements in the templated message
     */
    final void sendTemplatedMessage(WrappedCommandSource player, String messageKey, boolean isError, boolean useStub, String... replacements) {
        sendMessage(player, MessageFormat.format(messageBundle.getString(messageKey), (Object[]) replacements), isError, useStub);
    }

    /**
     * Sends a message to the {@link WrappedCommandSource}
     *
     * @param player The player to send a message to
     * @param message The message to send
     * @param isError Whether the message is an error
     * @param useStub Whether to use the [Hammer] tag
     */
    final void sendMessage(WrappedCommandSource player, String message, boolean isError, boolean useStub) {
        HammerTextBuilder hb;
        if (useStub) {
            hb = isError ? createErrorMessageStub() : createNormalMessageStub();
        } else {
            hb = new HammerTextBuilder();
        }

        hb.add(" " + message, isError ? HammerTextColours.RED : HammerTextColours.GREEN);
        player.sendMessage(hb.build());
    }

    /**
     * Sends the usage message to the {@link WrappedCommandSource}.
     *
     * @param source The source.
     */
    final void sendUsageMessage(WrappedCommandSource source) {
        String f = String.format(" %s ", messageBundle.getString("hammer.player.commandUsage"));
        HammerTextBuilder hb = createErrorMessageStub().add(f, HammerTextColours.RED)
                .add(this.getUsageMessage());

        source.sendMessage(hb.build());
    }

    final void sendNoPermsMessage(WrappedCommandSource target) {
        sendTemplatedMessage(target, "hammer.player.noperms", true, true);
    }

    private HammerTextBuilder createErrorMessageStub() {
        return new HammerTextBuilder().add(HammerConstants.textTag, HammerTextColours.RED);
    }

    private HammerTextBuilder createNormalMessageStub() {
        return new HammerTextBuilder().add(HammerConstants.textTag, HammerTextColours.GREEN);
    }

    private interface CommandRunner extends Runnable {
        boolean runCommand(ArgumentMap args) throws HammerException;
    }

    protected final class ParserEntry {
        public final String name;
        public final IParser parser;
        public final boolean isOptional;

        public ParserEntry(String name, IParser parser, boolean isOptional) {
            this.name = name;
            this.parser = parser;
            this.isOptional = isOptional;
        }
    }
}
