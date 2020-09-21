package Erwine.Leonard.T.wguscheduler356334.entity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;

public interface Course extends NoteColumnIncludedEntity {
    /**
     * The name of the {@code "termId"} database column, which is the value of the {@link TermEntity#COLNAME_ID primary key} for the {@link TermEntity course's term}.
     */
    String COLNAME_TERM_ID = "termId";
    /**
     * The name of the {@code "mentorId"} database column, which is the value of the {@link MentorEntity#COLNAME_ID primary key} for the {@link MentorEntity course mentor}.
     */
    String COLNAME_MENTOR_ID = "mentorId";
    /**
     * The name of the {@code "number"} database column, which contains the WGU-proprietary number/code that is used to refer to the course.
     */
    String COLNAME_NUMBER = "number";
    /**
     * The name of the {@code "title"} database column, which contains the course title.
     */
    String COLNAME_TITLE = "title";
    /**
     * The name of the {@code "expectedStart"} database column, which contains the date that the user expected to start teh course.
     */
    String COLNAME_EXPECTED_START = "expectedStart";
    /**
     * The name of the {@code "actualStart"} database column, which contains the actual start date of the course.
     */
    String COLNAME_ACTUAL_START = "actualStart";
    /**
     * The name of the {@code "expectedEnd"} database column, which contains the date the user expected to finish the course.
     */
    String COLNAME_EXPECTED_END = "expectedEnd";
    /**
     * The name of the {@code "actualEnd"} database column, which contains the date that the course actually ended.
     */
    String COLNAME_ACTUAL_END = "actualEnd";
    /**
     * The name of the {@code "status"} database column, which contains the current or final status of the course.
     */
    String COLNAME_STATUS = "status";
    /**
     * The name of the {@code "competencyUnits"} database column, which contains the competencyUnits attributed to the course.
     */
    String COLNAME_COMPETENCY_UNITS = "competencyUnits";

    /**
     * Gets the value of the {@link TermEntity#COLNAME_ID primary key} for the {@link TermEntity term} associated with the course.
     *
     * @return The value of the {@link TermEntity#COLNAME_ID primary key} for the {@link TermEntity term} associated with the course.
     */
    @Nullable
    Long getTermId();

    /**
     * Sets the {@link TermEntity#COLNAME_ID primary key} value for the {@link TermEntity term} to be associated with the course.
     *
     * @param termId The new {@link TermEntity#COLNAME_ID primary key} value for the {@link TermEntity term} to be associated with the course.
     */
    void setTermId(long termId);

    /**
     * Gets the value of the {@link MentorEntity#COLNAME_ID primary key} for the student's {@link MentorEntity course mentor} for the course.
     *
     * @return The value of the {@link MentorEntity#COLNAME_ID primary key} for the student's {@link MentorEntity course mentor} for the course or {@code null} if no mentor
     * has been associated with the course.
     */
    @Nullable
    Long getMentorId();

    /**
     * Sets the {@link MentorEntity#COLNAME_ID primary key} value for the {@link MentorEntity course mentor} to be associated with the course.
     *
     * @param mentorId The new {@link MentorEntity#COLNAME_ID primary key} value for the {@link MentorEntity course mentor} to be associated with the course.
     */
    void setMentorId(@Nullable Long mentorId);

    /**
     * Gets the WGU-proprietary number/code for the course.
     *
     * @return The WGU-proprietary number/code that is used to refer to the course.
     */
    @NonNull
    String getNumber();

    /**
     * Sets the WGU-proprietary number/code for the course.
     *
     * @param number The new WGU-proprietary number/code that will refer to the course.
     */
    void setNumber(String number);

    /**
     * Gets the title for the course.
     *
     * @return The course title.
     */
    @NonNull
    String getTitle();

    /**
     * Sets the course title.
     *
     * @param title The new title for the course.
     */
    void setTitle(String title);

    /**
     * Gets the current or final status of the course.
     *
     * @return The current or final status of the course.
     */
    @NonNull
    CourseStatus getStatus();

    /**
     * Sets the course status.
     *
     * @param status The new course status value.
     */
    void setStatus(CourseStatus status);

    /**
     * Gets the date that the user expects to start the course.
     *
     * @return The date that the user expects to start the course or {@code null} if the expected start date has not been determined.
     */
    @Nullable
    LocalDate getExpectedStart();

    /**
     * Sets the date that the user expects to start the course.
     *
     * @param expectedStart The date that the user expects to start the course or {@code null} if the expected start date has not been determined.
     */
    void setExpectedStart(@Nullable LocalDate expectedStart);

    /**
     * Gets the date that the user actually started the course.
     *
     * @return The date that the user actually started the course or {@code null} if the course hasn't been started, yet.
     */
    @Nullable
    LocalDate getActualStart();

    /**
     * Sets the date that the user actually started the course.
     *
     * @param actualStart The date that the user actually started the course or {@code null} if the course hasn't been started, yet.
     */
    void setActualStart(@Nullable LocalDate actualStart);

    /**
     * Gets the date that the user expects to finish the course.
     *
     * @return The date that the user expects to finish the course or {@code null} if the expected finish date has not yet been determined.
     */
    @Nullable
    LocalDate getExpectedEnd();

    /**
     * Sets the date that the user expects to finish the course.
     *
     * @param expectedEnd The date that the user expects to finish the course or {@code null} if the expected finish date has not yet been determined.
     */
    void setExpectedEnd(@Nullable LocalDate expectedEnd);

    /**
     * Gets the date that the course was actually concluded.
     *
     * @return The date that the course was actually concluded or {@code null} if the course hasn't yet concluded.
     */
    @Nullable
    LocalDate getActualEnd();

    /**
     * Sets the date that the course was actually concluded.
     *
     * @param actualEnd The date that the course was actually concluded or {@code null} if the course hasn't yet concluded.
     */
    void setActualEnd(@Nullable LocalDate actualEnd);

    /**
     * Gets the number of competency units attributed to the course.
     *
     * @return The number of competency units attributed to the course or {@code 0} if the competency units value is unknown.
     */
    int getCompetencyUnits();

    /**
     * Sets the number of competency units attributed to the course.
     *
     * @param competencyUnits The number of competency units attributed to the course or {@code 0} if the competency units value is unknown.
     */
    void setCompetencyUnits(int competencyUnits);

    @Override
    default String dbTableName() {
        return AppDb.TABLE_NAME_COURSES;
    }

    @Override
    default void restoreState(@NonNull Bundle bundle, boolean isOriginal) {
        NoteColumnIncludedEntity.super.restoreState(bundle, isOriginal);
        String key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_ID, isOriginal);
        if (bundle.containsKey(key)) {
            setTermId(bundle.getLong(key));
        }
        key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_ID, isOriginal);
        if (bundle.containsKey(key)) {
            setMentorId(bundle.getLong(key));
        }
        setNumber(bundle.getString(stateKey(COLNAME_NUMBER, isOriginal), ""));
        setTitle(bundle.getString(stateKey(COLNAME_TITLE, isOriginal), ""));
        key = stateKey(COLNAME_STATUS, isOriginal);
        setStatus((bundle.containsKey(key)) ? CourseStatus.valueOf(bundle.getString(key)) : CourseStatus.UNPLANNED);
        key = stateKey(COLNAME_EXPECTED_START, isOriginal);
        if (bundle.containsKey(key)) {
            setExpectedStart(LocalDate.ofEpochDay(bundle.getLong(key)));
        } else {
            setExpectedStart(null);
        }
        key = stateKey(COLNAME_ACTUAL_START, isOriginal);
        if (bundle.containsKey(key)) {
            setActualStart(LocalDate.ofEpochDay(bundle.getLong(key)));
        } else {
            setActualStart(null);
        }
        key = stateKey(COLNAME_EXPECTED_END, isOriginal);
        if (bundle.containsKey(key)) {
            setExpectedEnd(LocalDate.ofEpochDay(bundle.getLong(key)));
        } else {
            setExpectedEnd(null);
        }
        key = stateKey(COLNAME_ACTUAL_END, isOriginal);
        if (bundle.containsKey(key)) {
            setActualEnd(LocalDate.ofEpochDay(bundle.getLong(key)));
        } else {
            setActualEnd(null);
        }
        setCompetencyUnits(bundle.getInt(stateKey(COLNAME_COMPETENCY_UNITS, isOriginal), 0));
    }

    @Override
    default void saveState(@NonNull Bundle bundle, boolean isOriginal) {
        NoteColumnIncludedEntity.super.saveState(bundle, isOriginal);
        Long id = getTermId();
        if (null != id) {
            bundle.putLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_ID, isOriginal), id);
        }
        id = getMentorId();
        if (null != id) {
            bundle.putLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_ID, isOriginal), id);
        }
        bundle.putString(stateKey(COLNAME_NUMBER, isOriginal), getNumber());
        bundle.putString(stateKey(COLNAME_TITLE, isOriginal), getTitle());
        bundle.putString(stateKey(COLNAME_STATUS, isOriginal), getStatus().name());
        LocalDate d = getExpectedStart();
        if (null != d) {
            bundle.putLong(stateKey(COLNAME_EXPECTED_START, isOriginal), d.toEpochDay());
        }
        d = getActualStart();
        if (null != d) {
            bundle.putLong(stateKey(COLNAME_ACTUAL_START, isOriginal), d.toEpochDay());
        }
        d = getExpectedEnd();
        if (null != d) {
            bundle.putLong(stateKey(COLNAME_EXPECTED_END, isOriginal), d.toEpochDay());
        }
        d = getActualEnd();
        if (null != d) {
            bundle.putLong(stateKey(COLNAME_ACTUAL_END, isOriginal), d.toEpochDay());
        }
        bundle.putInt(stateKey(COLNAME_COMPETENCY_UNITS, isOriginal), getCompetencyUnits());
    }
}
