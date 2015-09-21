package uk.co.drnaylor.minecraft.hammer.sponge.commands;

import com.google.common.base.Optional;
import org.spongepowered.api.Game;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.*;

import java.util.ArrayList;
import java.util.List;

public class SpongeAlias implements CommandCallable {

    private final String targetCommand;
    private final List<String> argumentsPrepend;
    private final Game game;

    public SpongeAlias(Game game, String targetCommand, List<String> argumentsPrepend) {
        this.game = game;
        this.targetCommand = targetCommand;
        this.argumentsPrepend = argumentsPrepend;
    }

    /**
     * Execute the command based on input arguments.
     * <p/>
     * <p>The implementing class must perform the necessary permission
     * checks.</p>
     *
     * @param source    The caller of the command
     * @param arguments The raw arguments for this command
     * @return The result of a command being processed
     * @throws CommandException Thrown on a command error
     */
    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        return game.getCommandDispatcher().process(source, String.format("%s %s %s", targetCommand, argumentsPrepend, arguments));
    }

    /**
     * Get a list of suggestions based on input.
     * <p/>
     * <p>If a suggestion is chosen by the user, it will replace the last
     * word.</p>
     *
     * @param source    The command source
     * @param arguments The arguments entered up to this point
     * @return A list of suggestions
     * @throws CommandException Thrown if there was a parsing error
     */
    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        return new ArrayList<>();
    }

    /**
     * Test whether this command can probably be executed by the given source.
     * <p/>
     * <p>If implementations are unsure if the command can be executed by
     * the source, {@code true} should be returned. Return values of this method
     * may be used to determine whether this command is listed in command
     * listings.</p>
     *
     * @param source The caller of the command
     * @return Whether permission is (probably) granted
     */
    @Override
    public boolean testPermission(CommandSource source) {
        // This returns true because the check will be exected by the called command.
        return true;
    }

    /**
     * Get a short one-line description of this command.
     * <p/>
     * <p>The help system may display the description in the command list.</p>
     *
     * @param source The source of the help request
     * @return A description
     */
    @Override
    public Optional<? extends Text> getShortDescription(CommandSource source) {
        return Optional.absent();
    }

    /**
     * Get a longer formatted help message about this command.
     * <p/>
     * <p>It is recommended to use the default text color and style. Sections
     * with text actions (e.g. hyperlinks) should be underlined.</p>
     * <p/>
     * <p>Multi-line messages can be created by separating the lines with
     * {@code \n}.</p>
     * <p/>
     * <p>The help system may display this message when a source requests
     * detailed information about a command.</p>
     *
     * @param source The source of the help request
     * @return A help text
     */
    @Override
    public Optional<? extends Text> getHelp(CommandSource source) {
        return Optional.absent();
    }

    /**
     * Get the usage string of this command.
     * <p/>
     * <p>A usage string may look like
     * {@code [-w &lt;world&gt;] &lt;var1&gt; &lt;var2&gt;}.</p>
     * <p/>
     * <p>The string must not contain the command alias.</p>
     *
     * @param source The source of the help request
     * @return A usage string
     */
    @Override
    public Text getUsage(CommandSource source) {
        return Texts.of();
    }
}
