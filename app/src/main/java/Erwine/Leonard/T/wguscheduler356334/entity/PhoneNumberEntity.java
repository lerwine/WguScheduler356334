package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;
import java.util.function.Function;

import Erwine.Leonard.T.wguscheduler356334.db.TempDb;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringNormalizationOption;

@Entity(tableName = TempDb.TABLE_NAME_PHONE_NUMBERS)
public class PhoneNumberEntity implements Comparable<PhoneNumberEntity> {
    public static final String COLNAME_ID = "id";
    public static final String COLNAME_MENTOR_ID = "mentorId";
    public static final String COLNAME_SORT_ORDER = "sortOrder";
    public static final String COLNAME_VALUE = "value";
    private static final Function<String, String> SINGLE_LINE_NORMALIZER = StringHelper.getNormalizer(StringNormalizationOption.SINGLE_LINE);

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLNAME_ID)
    private Integer id;
    @ColumnInfo(name = COLNAME_MENTOR_ID)
    private int mentorId;
    @ColumnInfo(name = COLNAME_VALUE)
    private String value;
    @ColumnInfo(name = COLNAME_SORT_ORDER)
    private int sortOrder;

    @Ignore
    public PhoneNumberEntity(int mentorId, String value, int sortOrder) {
        this.mentorId = mentorId;
        this.value = SINGLE_LINE_NORMALIZER.apply(value);
        this.sortOrder = sortOrder;
    }

    @Ignore
    public PhoneNumberEntity(int mentorId, String value) {
        this(mentorId, value, Integer.MAX_VALUE);
    }

    public PhoneNumberEntity(int mentorId, String value, int sortOrder, int id) {
        this(mentorId, value, sortOrder);
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public int getMentorId() {
        return mentorId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String name) {
        this.value = SINGLE_LINE_NORMALIZER.apply(value);
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public synchronized boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneNumberEntity that = (PhoneNumberEntity) o;
        return (null == id) ? null == that.id && mentorId == that.mentorId && value.equals(that.value) && sortOrder == that.sortOrder : Objects.equals(id, that.id);
    }

    @Override
    public synchronized int hashCode() {
        return (null == id) ? Objects.hash(mentorId, value, sortOrder) : id;
    }

    @Override
    public synchronized int compareTo(PhoneNumberEntity o) {
        if (this == o) return 0;
        if (o == null || getClass() != o.getClass()) return -1;
        PhoneNumberEntity that = (PhoneNumberEntity) o;
        int result = sortOrder - that.sortOrder;
        if (result != 0) {
            return result;
        }
        Integer i = that.id;
        if (null == i) {
            return (null == id) ? value.compareTo(that.value) : -1;
        }
        return (null == id) ? 1 : id - i;
    }

    @NonNull
    @Override
    public String toString() {
        return "PhoneNumberEntity{" +
                "id=" + id +
                ", mentorId=" + mentorId +
                ", value='" + value + '\'' +
                ", sortOrder=" + sortOrder +
                '}';
    }
}
