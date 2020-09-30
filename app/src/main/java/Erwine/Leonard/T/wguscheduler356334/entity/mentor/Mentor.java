package Erwine.Leonard.T.wguscheduler356334.entity.mentor;

import android.os.Bundle;

import androidx.annotation.NonNull;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.entity.NoteColumnIncludedEntity;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

public interface Mentor extends NoteColumnIncludedEntity {

    /**
     * The name of the {@code "name"} database column, which contains the name of the course mentor.
     */
    String COLNAME_NAME = "name";
    /**
     * The name of the {@code "phoneNumber"} database column, which contains the course mentor's phone number.
     */
    String COLNAME_PHONE_NUMBER = "phoneNumber";
    /**
     * The name of the {@code "emailAddress"} database column, which contains the course mentor's email address.
     */
    String COLNAME_EMAIL_ADDRESS = "emailAddress";

    /**
     * Gets the name of the course mentor.
     *
     * @return The name of the course mentor.
     */
    @NonNull
    String getName();

    /**
     * Sets the name of the course mentor.
     *
     * @param name The name of the course mentor.
     */
    void setName(String name);

    /**
     * Gets the phone number for the course mentor.
     *
     * @return The phone number for the course mentor.
     */
    @NonNull
    String getPhoneNumber();

    /**
     * Sets the phone number for the course mentor.
     *
     * @param phoneNumber The phone number for the course mentor.
     */
    void setPhoneNumber(String phoneNumber);

    /**
     * Gets the email address for the course mentor.
     *
     * @return The email address for the course mentor.
     */
    @NonNull
    String getEmailAddress();

    /**
     * Sets the email address for the course mentor.
     *
     * @param emailAddress The email address for the course mentor.
     */
    void setEmailAddress(String emailAddress);

    @Override
    default String dbTableName() {
        return AppDb.TABLE_NAME_MENTORS;
    }

    @Override
    default void restoreState(@NonNull Bundle bundle, boolean isOriginal) {
        NoteColumnIncludedEntity.super.restoreState(bundle, isOriginal);
        setName(bundle.getString(stateKey(COLNAME_NAME, isOriginal), ""));
        setPhoneNumber(bundle.getString(stateKey(COLNAME_PHONE_NUMBER, isOriginal), ""));
        setEmailAddress(bundle.getString(stateKey(COLNAME_EMAIL_ADDRESS, isOriginal), ""));
    }

    @Override
    default void saveState(@NonNull Bundle bundle, boolean isOriginal) {
        NoteColumnIncludedEntity.super.saveState(bundle, isOriginal);
        bundle.putString(stateKey(COLNAME_NAME, isOriginal), getName());
        bundle.putString(stateKey(COLNAME_PHONE_NUMBER, isOriginal), getPhoneNumber());
        bundle.putString(stateKey(COLNAME_EMAIL_ADDRESS, isOriginal), getEmailAddress());
    }

    @Override
    default void appendPropertiesAsStrings(ToStringBuilder sb) {
        NoteColumnIncludedEntity.super.appendPropertiesAsStrings(sb);
        sb.append(COLNAME_NAME, getName())
                .append(COLNAME_PHONE_NUMBER, getPhoneNumber())
                .append(COLNAME_EMAIL_ADDRESS, getEmailAddress())
                .append(COLNAME_NOTES, getNotes());
    }

}
