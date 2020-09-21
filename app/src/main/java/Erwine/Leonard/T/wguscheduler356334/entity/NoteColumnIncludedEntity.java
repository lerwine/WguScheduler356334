package Erwine.Leonard.T.wguscheduler356334.entity;

import android.os.Bundle;

import androidx.annotation.NonNull;

public interface NoteColumnIncludedEntity extends IdIndexedEntity {
    /**
     * The name of the {@code "notes"} database column, which contains user notes for the entity.
     */
    String COLNAME_NOTES = "notes";

    @NonNull
    String getNotes();

    void setNotes(String notes);

    @Override
    default void restoreState(@NonNull Bundle bundle, boolean isOriginal) {
        IdIndexedEntity.super.restoreState(bundle, isOriginal);
        setNotes(bundle.getString(stateKey(COLNAME_NOTES, isOriginal), ""));
    }

    @Override
    default void saveState(@NonNull Bundle bundle, boolean isOriginal) {
        IdIndexedEntity.super.saveState(bundle, isOriginal);
        bundle.putString(stateKey(COLNAME_NOTES, isOriginal), getNotes());
    }
}
