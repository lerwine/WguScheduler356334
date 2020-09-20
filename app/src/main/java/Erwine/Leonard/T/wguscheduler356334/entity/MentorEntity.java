package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;

/**
 * Represents a row of data from the {@link AppDb#TABLE_NAME_MENTORS "mentors"} database table.
 */
@Entity(tableName = AppDb.TABLE_NAME_MENTORS, indices = {
        @Index(value = MentorEntity.COLNAME_NAME, name = MentorEntity.INDEX_NAME, unique = true)
})
public final class MentorEntity extends AbstractMentorEntity<MentorEntity> {

    /**
     * The name of the unique index for the {@link #getName() "name"} database column.
     */
    public static final String INDEX_NAME = "IDX_MENTOR_NAME";

    /**
     * Initializes a new {@code MentorEntity} object to represent an existing row of data in the {@link AppDb#TABLE_NAME_MENTORS "mentors"} database table.
     *
     * @param name         the name of the course mentor.
     * @param notes        multi-line text containing notes about the course mentor.
     * @param phoneNumber  the course mentor's phone number.
     * @param emailAddress the course mentor's email address.
     * @param id           The value of the {@link #COLNAME_ID primary key column}.
     */
    public MentorEntity(String name, String notes, String phoneNumber, String emailAddress, long id) {
        super(id, name, notes, phoneNumber, emailAddress);
    }

    /**
     * Initializes a new {@code MentorEntity} object to represent a new row of data for the {@link AppDb#TABLE_NAME_MENTORS "mentors"} database table.
     *
     * @param name         the name of the course mentor.
     * @param notes        multi-line text containing notes about the course mentor.
     * @param phoneNumber  the course mentor's phone number.
     * @param emailAddress the course mentor's email address.
     */
    @Ignore
    public MentorEntity(String name, String notes, String phoneNumber, String emailAddress) {
        super((Long) null, name, notes, phoneNumber, emailAddress);
    }

    public MentorEntity(AbstractMentorEntity<?> source) {
        super(source);
    }

    /**
     * Initializes a new {@code MentorEntity} object with empty values to represent a new row of data for the {@link AppDb#TABLE_NAME_MENTORS "mentors"} database table.
     */
    @Ignore
    public MentorEntity() {
        super(null, null, null, null, null);
    }

}
