package Erwine.Leonard.T.wguscheduler356334.entity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuildable;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;

public interface IdIndexedEntity extends ToStringBuildable {
    /**
     * The name of the {@code "id"} database column, which is the primary key.
     * If this value is {@code null}, then the current {@code IdIndexedEntity} object represents a new row that has not yet been saved.
     */
    String COLNAME_ID = "id";

    long ID_NEW = Long.MIN_VALUE;

    /**
     * Gets the value of the primary key for the database row represented by this {@code IdIndexedEntity}.
     *
     * @return The value of the primary key or {@code null} if this represents a row of data that has not yet been saved to the database.
     */
    long getId();

    /**
     * Sets the primary key value.
     *
     * @param id The value from the {@link #COLNAME_ID "id"} database column
     * @throws IllegalArgumentException if the {@link #COLNAME_ID "id"} was already set.
     */
    void setId(long id);

    @NonNull
    String dbTableName();

    static Long assertNullOrNotNewId(@Nullable Long id) {
        if (null != id && id == ID_NEW) {
            throw new IllegalArgumentException();
        }
        return id;
    }

    static Long nullIfNewId(@Nullable Long id) {
        if (null != id && id == ID_NEW) {
            return null;
        }
        return id;
    }

    static long assertNotNewId(long id) {
        if (id == ID_NEW) {
            throw new IllegalArgumentException();
        }
        return id;
    }

    static String stateKey(@NonNull String tableName, @NonNull String columnName, boolean isOriginal) {
        if (isOriginal) {
            return "o:" + tableName + "." + columnName;
        }
        return tableName + "." + columnName;
    }

    default String stateKey(String columnName, boolean isOriginal) {
        return stateKey(dbTableName(), columnName, isOriginal);
    }

    default void restoreState(@NonNull Bundle bundle, boolean isOriginal) {
        String key = stateKey(COLNAME_ID, false);
        if (bundle.containsKey(key)) {
            long id = bundle.getLong(key);
            if (getId() != id) {
                setId(id);
            }
        }
    }

    default void saveState(@NonNull Bundle bundle, boolean isOriginal) {
        if (!isOriginal) {
            long id = getId();
            if (ID_NEW != id) {
                bundle.putLong(stateKey(COLNAME_ID, false), getId());
            }
        }
    }

    @Override
    default void appendPropertiesAsStrings(@NonNull ToStringBuilder sb) {
        sb.appendRaw(COLNAME_ID).append("=").append(getId());
    }
}
