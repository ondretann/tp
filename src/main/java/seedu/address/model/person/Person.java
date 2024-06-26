package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields: any of the fields can be used to identify a person
    private final Id id;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Name name;
    private final YearJoined yearJoined;
    private final Address address;
    private final ArrayList<Tag> tags = new ArrayList<>();

    /**
     * Every field must be present and not null.
     */
    public Person(Model model, Name name, Phone phone, Email email, Address address,
                  YearJoined yearJoined, ArrayList<Tag> tags) {
        requireAllNonNull(model, name, phone, email, address, yearJoined, tags);
        this.id = new Id(model, yearJoined);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.yearJoined = yearJoined;
        this.tags.addAll(tags);
    }

    /**
     * Every field must be present and not null.
     */
    public Person(Id id, Name name, Phone phone, Email email,
                  Address address, YearJoined yearJoined, ArrayList<Tag> tags) {
        requireAllNonNull(id, name, phone, email, address, yearJoined, tags);
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.yearJoined = yearJoined;
        this.tags.addAll(tags);
    }

    public Id getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public YearJoined getYearJoined() {
        return yearJoined;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public ArrayList<Tag> getTags() {
        return tags;
    }

    /**
     * Returns true if both persons have the same ID or phone number or email.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && (otherPerson.getId().equals(getId())
                || otherPerson.getPhone().equals(getPhone())
                || otherPerson.getEmail().equals(getEmail()));
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person)) {
            return false;
        }

        Person otherPerson = (Person) other;
        return id.equals(otherPerson.id)
                && name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && yearJoined.equals(otherPerson.yearJoined)
                && tags.equals(otherPerson.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, yearJoined, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("id", id)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("year joined", yearJoined)
                .add("tags", tags)
                .toString();
    }

}
