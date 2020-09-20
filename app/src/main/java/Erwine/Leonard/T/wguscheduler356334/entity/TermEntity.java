package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import java.time.LocalDate;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;

/**
 * Represents a row of data from the {@link AppDb#TABLE_NAME_TERMS "terms"} database table.
 */
@Entity(tableName = AppDb.TABLE_NAME_TERMS, indices = {
        @Index(value = TermEntity.COLNAME_NAME, name = TermEntity.INDEX_NAME, unique = true)
})
public final class TermEntity extends AbstractTermEntity<TermEntity> {

    /**
     * The name of the unique index for the {@link #getName() "name"} database column.
     */
    public static final String INDEX_NAME = "IDX_TERM_NAME";

    /**
     * Initializes a new {@code TermEntity} object to represent an existing row of data in the {@link AppDb#TABLE_NAME_TERMS "terms"} database table.
     *
     * @param name  The name of the term.
     * @param start The inclusive start date of the term, which can be {@code null} if no start date has been determined.
     * @param end   The inclusive end date of the term, which can be {@code null} if no end date has been determined.
     * @param notes User notes to be associated with the term.
     * @param id    The value of the {@link #COLNAME_ID primary key column}.
     */
    public TermEntity(String name, LocalDate start, LocalDate end, String notes, long id) {
        super(id, name, start, end, notes);
    }

    /**
     * Initializes a new {@code TermEntity} object to represent a new row of data for the {@link AppDb#TABLE_NAME_TERMS "terms"} database table.
     *
     * @param name  The name of the term.
     * @param start The inclusive start date of the term, which can be {@code null} if no start date has been determined.
     * @param end   The inclusive end date of the term, which can be {@code null} if no end date has been determined.
     * @param notes User notes to be associated with the term.
     */
    @Ignore
    public TermEntity(String name, LocalDate start, LocalDate end, String notes) {
        super(null, name, start, end, notes);
    }

    public TermEntity(@NonNull AbstractTermEntity<?> source) {
        super(source);
    }

    /**
     * Initializes a new {@code TermEntity} object with empty values to represent a new row of data for the {@link AppDb#TABLE_NAME_TERMS "terms"} database table.
     */
    @Ignore
    public TermEntity() {
        super(null, null, null, null, null);
    }

}
