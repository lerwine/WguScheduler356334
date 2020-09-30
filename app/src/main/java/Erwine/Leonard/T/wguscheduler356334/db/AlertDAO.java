package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.time.LocalDate;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlert;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface AlertDAO {

    @Insert
    Single<Long> insert(AlertEntity assessment);

    @Insert
    long insertSynchronous(AlertEntity item);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable update(AlertEntity assessment);

    @Insert
    Single<List<Long>> insertAll(List<AlertEntity> list);

    @Insert
    List<Long> insertAllSynchronous(List<AlertEntity> list);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable updateAll(List<AlertEntity> list);

    @Delete
    Completable delete(AlertEntity assessment);

    @Query("SELECT * FROM alertListItemView ORDER BY alertDate DESC, eventDate DESC, code, title")
    LiveData<List<AlertListItem>> getAll();

    @Query("SELECT * FROM alertListItemView")
    List<AlertListItem> getAllSynchronous();

    @Query("SELECT * FROM alertListItemView WHERE NOT(eventDate IS NULL) AND alertDate < :date AND eventDate < :date ORDER BY alertDate DESC, eventDate DESC, code, title")
    LiveData<List<AlertListItem>> getActiveBeforeDate(LocalDate date);

    @Query("SELECT * FROM alertListItemView WHERE NOT(eventDate IS NULL OR alertDate > :date OR eventDate < :date) ORDER BY alertDate, eventDate, code, title")
    LiveData<List<AlertListItem>> getActiveOnDate(LocalDate date);

    @Query("SELECT * FROM alertListItemView WHERE NOT(eventDate IS NULL OR alertDate > :end OR eventDate < :start) ORDER BY alertDate, eventDate, code, title")
    LiveData<List<AlertListItem>> getActiveInRange(LocalDate start, LocalDate end);

    @Query("SELECT * FROM alertListItemView WHERE NOT(eventDate IS NULL) AND alertDate > :date AND eventDate > :date ORDER BY alertDate, eventDate, code, title")
    LiveData<List<AlertListItem>> getActiveAfterDate(LocalDate date);

    @Query("SELECT * FROM alertListItemView WHERE eventDate IS NULL ORDER BY code, title")
    LiveData<List<AlertListItem>> getDatePendingAlerts();

    @Query("SELECT * FROM courseAlerts WHERE targetId=:courseId")
    LiveData<List<CourseAlert>> getAllByCourse(long courseId);

    @Query("SELECT * FROM assessmentAlerts WHERE targetId=:assessmentId")
    LiveData<List<AssessmentAlert>> getAllByAssessment(long assessmentId);

//    @Query("SELECT COUNT(*) FROM alertListItemView")
//    Single<Integer> getCount();
//
//    @Query("SELECT COUNT(*) FROM alertListItemView WHERE courseId=:courseId")
//    Single<Integer> getCountByCourse(long courseId);

}
