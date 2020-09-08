package Erwine.Leonard.T.wguscheduler356334.entity;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;
import java.util.function.Function;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.util.PropertyChangeSupported;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringNormalizationOption;

@Entity(tableName = AppDb.TABLE_NAME_MENTORS, indices = {
        @Index(value = MentorEntity.COLNAME_NAME, name = MentorEntity.INDEX_NAME, unique = true)
})
public class MentorEntity extends PropertyChangeSupported {

    //<editor-fold defaultstate="collapsed" desc="Static Members" >

    public static final String INDEX_NAME = "IDX_MENTOR_NAME";
    public static final String COLNAME_ID = "id";
    public static final String COLNAME_NAME = "name";
    public static final String COLNAME_PHONE_NUMBERS = "phoneNumbers";
    public static final String COLNAME_EMAIL_ADDRESSES = "emailAddresses";
    public static final String COLNAME_NOTES = "notes";

    private static final Function<String, String> SINGLE_LINE_NORMALIZER = StringHelper.getNormalizer(StringNormalizationOption.SINGLE_LINE);
    private static final Function<String, String> MULTI_LINE_NORMALIZER = StringHelper.getNormalizer();

    public static void applyInsertedId(MentorEntity source, long id) {
        if (null != source.getId()) {
            throw new IllegalStateException();
        }
        source.id = id;
        source.firePropertyChange(COLNAME_ID, null, id);
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Fields">

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLNAME_ID)
    private Long id;

    @ColumnInfo(name = COLNAME_NAME)
    private String name;

    @Ignore
    private String primaryPhone;

    @ColumnInfo(name = COLNAME_PHONE_NUMBERS)
    private String phoneNumbers;

    @Ignore
    private String primaryEmail;

    @ColumnInfo(name = COLNAME_EMAIL_ADDRESSES)
    private String emailAddresses;

    @ColumnInfo(name = COLNAME_NOTES)
    private String notes;

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">

    public MentorEntity(String name, String notes, String phoneNumbers, String emailAddresses, long id) {
        this(name, notes, phoneNumbers, emailAddresses);
        this.id = id;
    }

    @Ignore
    public MentorEntity(String name, String notes, String phoneNumbers, String emailAddresses) {
        this.name = SINGLE_LINE_NORMALIZER.apply(name);
        this.notes = MULTI_LINE_NORMALIZER.apply(notes);
        this.phoneNumbers = MULTI_LINE_NORMALIZER.apply(phoneNumbers);
        this.emailAddresses = MULTI_LINE_NORMALIZER.apply(emailAddresses);
    }

    @Ignore
    public MentorEntity(String name, String notes) {
        this(name, notes, null, null);
    }

    @Ignore
    public MentorEntity() {
        this(null, null, null, null);
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Properties">

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        String oldValue = applyName(SINGLE_LINE_NORMALIZER.apply(name));
        if (null != oldValue) {
            firePropertyChange(COLNAME_NAME, oldValue, this.name);
        }
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        String oldValue = applyNotes(MULTI_LINE_NORMALIZER.apply(notes));
        if (null != oldValue) {
            firePropertyChange(COLNAME_NOTES, oldValue, this.notes);
        }
    }

    public synchronized String getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(String phoneNumbers) {
        Pair<String, String> oldValues = applyPhoneNumbers(MULTI_LINE_NORMALIZER.apply(phoneNumbers));
        if (null != oldValues) {
            if (null != oldValues.first) {
                firePropertyChange("primaryPhone", oldValues.first, this.primaryPhone);
            }
            firePropertyChange(COLNAME_PHONE_NUMBERS, oldValues.second, this.phoneNumbers);
        }
    }

    public synchronized String getPrimaryPhone() {
        return primaryPhone;
    }

    public synchronized String getEmailAddresses() {
        return emailAddresses;
    }

    public synchronized void setEmailAddresses(String emailAddresses) {
        Pair<String, String> oldValues = applyPhoneNumbers(MULTI_LINE_NORMALIZER.apply(emailAddresses));
        if (null != oldValues) {
            if (null != oldValues.first) {
                firePropertyChange("primaryEmail", oldValues.first, this.primaryEmail);
            }
            firePropertyChange(COLNAME_EMAIL_ADDRESSES, oldValues.second, this.emailAddresses);
        }
    }

    public synchronized String getPrimaryEmail() {
        return primaryEmail;
    }

    //</editor-fold>

    @Nullable
    private synchronized String applyName(@NonNull String newValue) {
        String oldValue = name;
        if (newValue.equals(name)) {
            return null;
        }
        name = newValue;
        return oldValue;
    }

    @Nullable
    private synchronized String applyNotes(@NonNull String newValue) {
        String oldValue = notes;
        if (newValue.equals(notes)) {
            return null;
        }
        notes = newValue;
        return oldValue;
    }

    @Nullable
    private synchronized Pair<String, String> applyPhoneNumbers(@NonNull String newValue) {
        String oldValue = phoneNumbers;
        if (newValue.equals(phoneNumbers)) {
            return null;
        }
        phoneNumbers = newValue;
        int i = phoneNumbers.indexOf("\n");
        String oldPrimary = primaryPhone;
        primaryPhone = (i < 0) ? "" : phoneNumbers.substring(0, i);
        return new Pair<>((oldPrimary.equals(primaryPhone)) ? null : oldPrimary, oldValue);
    }

    @Nullable
    private synchronized Pair<String, String> applyEmailAddresses(@NonNull String newValue) {
        String oldValue = emailAddresses;
        if (newValue.equals(emailAddresses)) {
            return null;
        }
        emailAddresses = newValue;
        int i = emailAddresses.indexOf("\n");
        String oldPrimary = primaryEmail;
        primaryEmail = (i < 0) ? "" : emailAddresses.substring(0, i);
        return new Pair<>((oldPrimary.equals(primaryEmail)) ? null : oldPrimary, oldValue);
    }

    //<editor-fold desc="Overrides">

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MentorEntity that = (MentorEntity) o;
        if (null != id) {
            return id.equals(that.id);
        }
        return null == that.id &&
                name.equals(that.name) &&
                phoneNumbers.equals(that.phoneNumbers) &&
                emailAddresses.equals(that.emailAddresses) &&
                notes.equals(that.notes);
    }

    @Override
    public int hashCode() {
        if (null != id) {
            return id.hashCode();
        }
        return Objects.hash(id, name, phoneNumbers, emailAddresses, notes);
    }

    @NonNull
    @Override
    public String toString() {
        return "MentorEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", primaryPhone=" + phoneNumbers +
                ", primaryEmail=" + emailAddresses +
                ", notes='" + notes + '\'' +
                '}';
    }

    //</editor-fold>

}
