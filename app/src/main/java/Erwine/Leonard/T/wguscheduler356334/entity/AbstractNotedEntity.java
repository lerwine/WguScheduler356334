package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import java.util.function.Function;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringNormalizationOption;

/**
 * Abstract class to represent a row of data from the an {@link AppDb} database table that includes an {@link AbstractEntity#COLNAME_ID "id" database column}, which is the primary key
 * and a {@link #COLNAME_NOTES "notes" database column}.
 */
public abstract class AbstractNotedEntity<T extends AbstractNotedEntity<T>> extends AbstractEntity<T> implements NoteColumnIncludedEntity {

    /**
     * Normalizes string values by converting line break sequences into newline characters, trimming whitespace from the end of each line, converting non-space whitespace characters
     * into spaces as well as condensing multiple consecutive whitespace characters into a single space character.
     */
    public static final Function<String, String> MULTI_LINE_NORMALIZER = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM_START, StringNormalizationOption.LEAVE_BLANK_LINES);

    @ColumnInfo(name = COLNAME_NOTES)
    private String notes;

    /**
     * Initializes a new {@code AbstractNotedEntity} object to represent a row of data in an {@link AppDb} database table.
     *
     * @param id    The value of the database row primary key, which can be {@code null} if this represents a new row that has not yet been saved.
     * @param notes User notes to be associated with the term.
     */
    protected AbstractNotedEntity(Long id, String notes) {
        super(id);
        this.notes = MULTI_LINE_NORMALIZER.apply(notes);
    }

    @Ignore
    protected AbstractNotedEntity(@NonNull AbstractNotedEntity<?> source) {
        super(source.getId());
        this.notes = source.notes;
    }

    /**
     * Gets the notes that are associated with the entity.
     *
     * @return The notes associated with the entity, whose lines are always whitespace-normalized, with trailing whitespace removed.
     */
    @NonNull
    @Override
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the notes to be associated with the entity.
     *
     * @param notes The multi-line notes to be associated with the entity.
     */
    public void setNotes(String notes) {
        this.notes = MULTI_LINE_NORMALIZER.apply(notes);
    }

}
