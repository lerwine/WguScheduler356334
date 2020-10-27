package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.function.Function;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringNormalizationOption;

/**
 * Abstract class to represent a row of data from the an {@link AppDb} database table that includes an {@link AbstractEntity#COLNAME_ID "id" database column}, which is the primary key.
 */
public abstract class AbstractEntity<T extends AbstractEntity<T>> implements IdIndexedEntity {
    /**
     * Normalizes string values by trimming whitespace and converting non-space whitespace characters into spaces as well as condensing multiple consecutive whitespace characters into
     * a single space character.
     */
    public static final Function<String, String> SINGLE_LINE_NORMALIZER = StringHelper.getNormalizer(StringNormalizationOption.SINGLE_LINE);

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLNAME_ID)
    private long id;

    /**
     * Initializes a new {@code AbstractEntity} object to represent a row of data in an {@link AppDb} database table.
     *
     * @param id The value of the database row primary key, which can be {@code null} if this represents a new row that has not yet been saved.
     */
    @Ignore
    protected AbstractEntity(long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public synchronized void setId(long id) {
        if (ID_NEW == this.id) {
            this.id = id;
        } else if (id != this.id) {
            throw new IllegalStateException();
        }
    }

    /**
     * Compares 2 {@code AbstractEntity} objects by property values of inherited classes. This gets invoked by {@link #equals(Object)} when the {@link #id} is null on both the current
     * and the other {@code AbstractEntity}. Otherwise, the {@link #equals(Object)} method only checks the values of both {@link #id} fields for equality.
     *
     * @param other The {@code AbstractEntity} to compare to.
     * @return {@code true} if the properties from the current and the {@code other} object are equal; otherwise, {@code false}.
     */
    protected abstract boolean equalsEntity(@NonNull T other);

    @Override
    public synchronized boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (id != ((AbstractEntity<?>) o).id) {
            return false;
        }
        //noinspection unchecked
        return id == ID_NEW && equalsEntity((T) o);
    }

    @Override
    public synchronized int hashCode() {
        if (id != ID_NEW) {
            return Long.hashCode(id);
        }
        return hashCodeFromProperties();
    }

    /**
     * Returns a hash code from the property values of the inherited classes. this gets invoked by {@link #hashCode()} when the {@link #id} field is null. Otherwise, the
     * {@link #hashCode()} method used the hash code value from the {@link #id} field.
     *
     * @return a hash code value for this {@code AbstractEntity} according to its field values.
     */
    protected abstract int hashCodeFromProperties();

}
