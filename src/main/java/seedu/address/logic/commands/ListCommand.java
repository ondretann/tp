package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import seedu.address.model.Model;

/**
 * Lists all persons in PayBack to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "/list";

    public static final String FOLLOW_MESSAGE = "Shows list of all employee";

    public static final String MESSAGE_SUCCESS = "Listed and Refreshed the workers recorded in the system";


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
