package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import java.util.Objects;

public abstract class AbstractMentorEntity<T extends AbstractMentorEntity<T>> extends AbstractNotedEntity<T> {

    /**
     * The name of the {@link #name "name"} database column, which contains the name of the course mentor.
     */
    public static final String COLNAME_NAME = "name";
    /**
     * The name of the {@link #phoneNumber "phoneNumber"} database column, which contains the course mentor's phone number.
     */
    public static final String COLNAME_PHONE_NUMBER = "phoneNumber";
    /**
     * The name of the {@link #emailAddress "emailAddress"} database column, which contains the course mentor's email address.
     */
    public static final String COLNAME_EMAIL_ADDRESS = "emailAddress";

    @ColumnInfo(name = COLNAME_NAME, collate = ColumnInfo.NOCASE)
    private String name;

    @ColumnInfo(name = COLNAME_PHONE_NUMBER)
    private String phoneNumber;

    @ColumnInfo(name = COLNAME_EMAIL_ADDRESS)
    private String emailAddress;

    @Ignore
    protected AbstractMentorEntity(Long id, String name, String notes, String phoneNumber, String emailAddress) {
        super(id, notes);
        this.name = SINGLE_LINE_NORMALIZER.apply(name);
        this.phoneNumber = SINGLE_LINE_NORMALIZER.apply(phoneNumber);
        this.emailAddress = SINGLE_LINE_NORMALIZER.apply(emailAddress);
    }

    protected AbstractMentorEntity(AbstractMentorEntity<?> source) {
        super(source);
        this.name = source.name;
        this.phoneNumber = source.phoneNumber;
        this.emailAddress = source.emailAddress;
    }

    /**
     * Gets the name of the course mentor.
     *
     * @return The name of the course mentor, which is always single-line, whitespace-normalized and trimmed.
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the course mentor.
     *
     * @param name The name of the course mentor.
     */
    public synchronized void setName(String name) {
        this.name = SINGLE_LINE_NORMALIZER.apply(name);
    }

    /**
     * Gets the phone number for the course mentor.
     *
     * @return The phone number for the course mentor, which is always single-line, whitespace-normalized and trimmed.
     */
    @NonNull
    public synchronized String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number for the course mentor.
     *
     * @param phoneNumber The phone number for the course mentor.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = SINGLE_LINE_NORMALIZER.apply(phoneNumber);
    }

    /**
     * Gets the email address for the course mentor.
     *
     * @return The email address for the course mentor, which is always single-line, whitespace-normalized and trimmed.
     */
    @NonNull
    public synchronized String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the email address for the course mentor.
     *
     * @param emailAddress The email address for the course mentor.
     */
    public synchronized void setEmailAddress(String emailAddress) {
        this.emailAddress = SINGLE_LINE_NORMALIZER.apply(emailAddress);
    }

    @Override
    protected boolean equalsEntity(@NonNull T other) {
        return name.equals(other.getName()) &&
                phoneNumber.equals(other.getPhoneNumber()) &&
                emailAddress.equals(other.getEmailAddress()) &&
                getNotes().equals(other.getNotes());
    }

    @Override
    protected int hashCodeFromProperties() {
        return Objects.hash(name, phoneNumber, emailAddress, getNotes());
    }

}
