package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.EDIT_SAME_FIELD;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_ID;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.TAG_DUPLICATE_TAG;
import static seedu.address.logic.Messages.TAG_NO_TAG_PRESENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javafx.util.Pair;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Id;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.YearJoined;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "/edit";

    public static final String FOLLOW_MESSAGE = "Format: " + COMMAND_WORD + " ID "
            + "[" + PREFIX_NAME + " NAME] "
            + "[" + PREFIX_PHONE + " PHONE] "
            + "[" + PREFIX_EMAIL + " EMAIL] "
            + "[" + PREFIX_ADDRESS + " ADDRESS] "
            + "[" + PREFIX_TAG + " TAG_INDEX NEW_TAG]\n"
            + "Note: Remove all the employee’s tags by typing " + PREFIX_TAG + " -1.";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the details of the person with the ID provided.\n"
            + "Existing values will be overwritten by the input values.\n"
            + "Format: " + COMMAND_WORD + " ID "
            + "[" + PREFIX_NAME + " NAME] "
            + "[" + PREFIX_PHONE + " PHONE] "
            + "[" + PREFIX_EMAIL + " EMAIL] "
            + "[" + PREFIX_ADDRESS + " ADDRESS] "
            + "[" + PREFIX_TAG + " TAG_INDEX NEW_TAG]\n"
            + "Note: Remove all the employee’s tags by typing " + PREFIX_TAG + " -1.\n"
            + "Examples:\n"
            + "• " + COMMAND_WORD + " 240001 "
            + PREFIX_PHONE + " 91234567 "
            + PREFIX_EMAIL + " johndoe@example.com "
            + PREFIX_TAG + " 1 friend\n"
            + "• " + COMMAND_WORD + " 240001 " + PREFIX_TAG + " -1";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final Id id;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param id of the person stored in the data file to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Id id, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(id);
        requireNonNull(editPersonDescriptor);

        this.id = id;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        Person personToEdit = lastShownList
                .stream()
                .filter(person -> person.getId().value == (this.id.value))
                .findFirst()
                .orElse(null);
        if (personToEdit == null) {
            throw new CommandException(MESSAGE_INVALID_PERSON_DISPLAYED_ID);
        }
        editPersonDescriptor.setTags(personToEdit.getTags());
        editPersonDescriptor.setUpdatedTags();
        if (editPersonDescriptor.getName().orElse(new Name("swdwdadwx")).equals(personToEdit.getName())
            || editPersonDescriptor.getPhone().orElse(new Phone("1312421")).equals(personToEdit.getPhone())
            || editPersonDescriptor.getEmail().orElse(new Email("adw@asdw")).equals(personToEdit.getEmail())
            || editPersonDescriptor.getAddress().orElse(new Address("wxesc")).equals(personToEdit.getAddress())) {
            throw new CommandException(EDIT_SAME_FIELD);
        }
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        boolean isDuplicate = model.getDuplicatePersons(editedPerson).stream()
                .anyMatch(person -> !person.getId().equals(editedPerson.getId()));
        if (isDuplicate) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Id id = personToEdit.getId();
        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        YearJoined yearJoined = personToEdit.getYearJoined();
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        ArrayList<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());

        return new Person(id, updatedName, updatedPhone, updatedEmail, updatedAddress, yearJoined, updatedTags);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        EditCommand otherEditCommand = (EditCommand) other;
        return id.equals(otherEditCommand.id)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("id", id)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Id id;
        private Name name;
        private Phone phone;
        private Email email;
        private YearJoined yearJoined;
        private Address address;
        private ArrayList<Tag> tags = new ArrayList<>();
        private Pair<Integer, String> updateTagInfo;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setId(toCopy.id);
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setYearJoined(toCopy.yearJoined);
            setAddress(toCopy.address);
            if (toCopy.tags == null) {
                setTags(new ArrayList<Tag>());
            } else {
                setTags(toCopy.tags);
            }
            setUpdateTagInfo(toCopy.updateTagInfo);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address);
        }


        public void setName(Name name) {
            this.name = name;
        }

        public void setId(Id id) {
            this.id = id;
        }

        public void setYearJoined(YearJoined yearJoined) {
            this.yearJoined = yearJoined;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        public Optional<YearJoined> getYearJoined() {
            return Optional.ofNullable(yearJoined);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(ArrayList<Tag> tags) {
            this.tags = (tags != null) ? new ArrayList<>(tags) : null;
        }

        /**
         * Stores tag info to be updated later.
         * @param tagInfo A pair of index and new tag name.
         */
        public void setUpdateTagInfo(Pair<Integer, String> tagInfo) {
            updateTagInfo = tagInfo;
        }

        /**
         * Sets {@code tagInfo} to this object's {@code tags}.
         */
        public void setUpdatedTags() throws CommandException {
            if (updateTagInfo == null) {
                return;
            }
            if (updateTagInfo.getKey() > this.tags.size()) {
                throw new CommandException(MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            if (updateTagInfo.getKey() == 0 && this.tags.size() == 0) {
                throw new CommandException(TAG_NO_TAG_PRESENT);
            }
            if (updateTagInfo.getKey() == -1) {
                this.tags = new ArrayList<>();
            } else {
                Tag newTag = new Tag(updateTagInfo.getValue());
                for (int i = 0; i < tags.size(); i++) {
                    if (tags.get(i).equals(newTag)) {
                        throw new CommandException(TAG_DUPLICATE_TAG);
                    }
                }
                tags.set(updateTagInfo.getKey() - 1, newTag);
            }
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<ArrayList<Tag>> getTags() {
            return (tags != null) ? Optional.of(tags) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            EditPersonDescriptor otherEditPersonDescriptor = (EditPersonDescriptor) other;
            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(yearJoined, otherEditPersonDescriptor.yearJoined)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(address, otherEditPersonDescriptor.address)
                    && Objects.equals(tags, otherEditPersonDescriptor.tags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("year joined", yearJoined)
                    .add("address", address)
                    .add("tags", tags)
                    .toString();
        }
    }
}
