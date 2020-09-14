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
public abstract class AbstractEntity<T extends AbstractEntity<T>> implements HasIdProperty {
    /**
     * The name of the {@link #id "id"} database column, which is the primary key.
     * If this value is {@code null}, then the current {@code AbstractEntity} object represents a new row that has not yet been saved.
     */
    public static final String COLNAME_ID = "id";
    /**
     * Normalizes string values by trimming whitespace and converting non-space whitespace characters into spaces as well as condensing multiple consecutive whitespace characters into
     * a single space character.
     */
    public static final Function<String, String> SINGLE_LINE_NORMALIZER = StringHelper.getNormalizer(StringNormalizationOption.SINGLE_LINE);

    /**
     * Updates the primary key value if it has not already been set.
     *
     * @param target The {@code AbstractEntity} object to apply the primary key value to.
     * @param id     The value from the {@link #COLNAME_ID "id"} database column, which gets applied to the {@link #id} field.
     * @throws IllegalArgumentException if the {@link #id} field was already set.
     */
    public static void applyInsertedId(AbstractEntity target, long id) {
        if (null != target.getId()) {
            throw new IllegalStateException();
        }
        target.id = id;
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLNAME_ID)
    private Long id;

    /**
     * Initializes a new {@code AbstractEntity} object to represent a row of data in an {@link AppDb} database table.
     *
     * @param id The value of the database row primary key, which can be {@code null} if this represents a new row that has not yet been saved.
     */
    @Ignore
    protected AbstractEntity(Long id) {
        this.id = id;
    }

    /**
     * Gets the value of the primary key for the database row represented by this {@code AbstractEntity}.
     *
     * @return The value of the primary key or {@code null} if this represents a row of data that has not yet been saved to the database.
     */
    @Override
    public Long getId() {
        return id;
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
        Long thatId = ((AbstractEntity<?>) o).id;
        if (null != id) {
            return id.equals(thatId);
        }
        if (null != thatId) {
            return false;
        }
        //noinspection unchecked
        return equalsEntity((T) o);
    }

    @Override
    public synchronized int hashCode() {
        if (null != id) {
            return id.hashCode();
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
