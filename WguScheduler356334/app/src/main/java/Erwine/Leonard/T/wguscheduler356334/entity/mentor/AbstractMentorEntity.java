package Erwine.Leonard.T.wguscheduler356334.entity.mentor;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.entity.AbstractNotedEntity;

public abstract class AbstractMentorEntity<T extends AbstractMentorEntity<T>> extends AbstractNotedEntity<T> implements Mentor {

    @ColumnInfo(name = COLNAME_NAME, collate = ColumnInfo.NOCASE)
    @NonNull
    private String name;

    @ColumnInfo(name = COLNAME_PHONE_NUMBER)
    @NonNull
    private String phoneNumber;

    @ColumnInfo(name = COLNAME_EMAIL_ADDRESS)
    @NonNull
    private String emailAddress;

    @Ignore
    protected AbstractMentorEntity(long id, String name, String notes, String phoneNumber, String emailAddress) {
        super(id, notes);
        this.name = SINGLE_LINE_NORMALIZER.apply(name);
        this.phoneNumber = SINGLE_LINE_NORMALIZER.apply(phoneNumber);
        this.emailAddress = SINGLE_LINE_NORMALIZER.apply(emailAddress);
    }

    @Ignore
    protected AbstractMentorEntity(@NonNull AbstractMentorEntity<?> source) {
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
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the course mentor.
     *
     * @param name The name of the course mentor.
     */
    @Override
    public synchronized void setName(String name) {
        this.name = SINGLE_LINE_NORMALIZER.apply(name);
    }

    /**
     * Gets the phone number for the course mentor.
     *
     * @return The phone number for the course mentor, which is always single-line, whitespace-normalized and trimmed.
     */
    @NonNull
    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number for the course mentor.
     *
     * @param phoneNumber The phone number for the course mentor.
     */
    public synchronized void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = SINGLE_LINE_NORMALIZER.apply(phoneNumber);
    }

    /**
     * Gets the email address for the course mentor.
     *
     * @return The email address for the course mentor, which is always single-line, whitespace-normalized and trimmed.
     */
    @NonNull
    @Override
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the email address for the course mentor.
     *
     * @param emailAddress The email address for the course mentor.
     */
    @Override
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
