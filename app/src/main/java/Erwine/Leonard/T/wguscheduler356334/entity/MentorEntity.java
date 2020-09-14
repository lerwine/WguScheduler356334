package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import java.util.Objects;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;

/**
 * Represents a row of data from the {@link AppDb#TABLE_NAME_MENTORS "mentors"} database table.
 */
@Entity(tableName = AppDb.TABLE_NAME_MENTORS, indices = {
        @Index(value = MentorEntity.COLNAME_NAME, name = MentorEntity.INDEX_NAME, unique = true)
})
public final class MentorEntity extends AbstractNotedEntity<MentorEntity> {

    /**
     * The name of the unique index for the {@link #name "name"} database column.
     */
    public static final String INDEX_NAME = "IDX_MENTOR_NAME";
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
    private MentorEntity(Long id, String name, String notes, String phoneNumber, String emailAddress) {
        super(id, notes);
        this.name = SINGLE_LINE_NORMALIZER.apply(name);
        this.phoneNumber = SINGLE_LINE_NORMALIZER.apply(phoneNumber);
        this.emailAddress = SINGLE_LINE_NORMALIZER.apply(emailAddress);
    }

    /**
     * Initializes a new {@code MentorEntity} object to represent an existing row of data in the {@link AppDb#TABLE_NAME_MENTORS "mentors"} database table.
     *
     * @param name         the name of the course mentor.
     * @param notes        multi-line text containing notes about the course mentor.
     * @param phoneNumber  the course mentor's phone number.
     * @param emailAddress the course mentor's email address.
     * @param id           The value of the {@link #COLNAME_ID primary key column}.
     */
    public MentorEntity(String name, String notes, String phoneNumber, String emailAddress, long id) {
        this(id, name, notes, phoneNumber, emailAddress);
    }

    /**
     * Initializes a new {@code MentorEntity} object to represent a new row of data for the {@link AppDb#TABLE_NAME_MENTORS "mentors"} database table.
     *
     * @param name         the name of the course mentor.
     * @param notes        multi-line text containing notes about the course mentor.
     * @param phoneNumber  the course mentor's phone number.
     * @param emailAddress the course mentor's email address.
     */
    @Ignore
    public MentorEntity(String name, String notes, String phoneNumber, String emailAddress) {
        this((Long) null, name, notes, phoneNumber, emailAddress);
    }

    /**
     * Initializes a new {@code MentorEntity} object with empty values to represent a new row of data for the {@link AppDb#TABLE_NAME_MENTORS "mentors"} database table.
     */
    @Ignore
    public MentorEntity() {
        this(null, null, null, null, null);
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
    protected boolean equalsEntity(@NonNull MentorEntity other) {
        return name.equals(other.name) &&
                phoneNumber.equals(other.phoneNumber) &&
                emailAddress.equals(other.emailAddress) &&
                getNotes().equals(other.getNotes());
    }

    @Override
    protected int hashCodeFromProperties() {
        return Objects.hash(name, phoneNumber, emailAddress, getNotes());
    }

    @NonNull
    @Override
    public String toString() {
        return "MentorEntity{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", emailAddress=" + emailAddress +
                ", notes='" + getNotes() + '\'' +
                '}';
    }

}
