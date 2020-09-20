package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;

public interface NoteColumnIncludedEntity extends IdIndexedEntity {
    @NonNull
    String getNotes();

    void setNotes(String notes);
}
