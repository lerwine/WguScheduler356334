package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.time.LocalDate;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.entity.AlertListItem;
import io.reactivex.Single;

@Dao
public interface AlertDAO {

    @Query("SELECT * FROM alertListItemView ORDER BY alertDate DESC, eventDate DESC, code, title")
    LiveData<List<AlertListItem>> getAll();

    @Query("SELECT * FROM alertListItemView")
    List<AlertListItem> getAllSynchronous();

    @Query("SELECT * FROM alertListItemView WHERE courseId=:courseId ORDER BY alertDate DESC, eventDate DESC, code, title")
    LiveData<List<AlertListItem>> getAllByCourse(long courseId);

    @Query("SELECT * FROM alertListItemView WHERE assessment=1 AND id=:assessmentId ORDER BY alertDate DESC, eventDate DESC, code, title")
    LiveData<List<AlertListItem>> getByAssessment(long assessmentId);

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

    @Query("SELECT * FROM alertListItemView WHERE courseId=:courseId AND NOT(eventDate IS NULL) AND alertDate < :date AND eventDate < :date ORDER BY alertDate DESC, eventDate DESC, code, title")
    LiveData<List<AlertListItem>> getActiveBeforeDateByCourse(LocalDate date, long courseId);

    @Query("SELECT * FROM alertListItemView WHERE courseId=:courseId AND NOT(eventDate IS NULL OR alertDate > :date OR eventDate < :date) ORDER BY alertDate, eventDate, code, title")
    LiveData<List<AlertListItem>> getActiveOnDateByCourse(LocalDate date, long courseId);

    @Query("SELECT * FROM alertListItemView WHERE courseId=:courseId AND NOT(eventDate IS NULL OR alertDate > :end OR eventDate < :start) ORDER BY alertDate, eventDate, code, title")
    LiveData<List<AlertListItem>> getActiveInRangeByCourse(LocalDate start, LocalDate end, long courseId);

    @Query("SELECT * FROM alertListItemView WHERE courseId=:courseId AND NOT(eventDate IS NULL) AND alertDate > :date AND eventDate > :date ORDER BY alertDate, eventDate, code, title")
    LiveData<List<AlertListItem>> getActiveAfterDateByCourse(LocalDate date, long courseId);

    @Query("SELECT * FROM alertListItemView WHERE courseId=:courseId AND eventDate IS NULL ORDER BY code, title")
    LiveData<List<AlertListItem>> getDatePendingAlertsByCourse(long courseId);

    @Query("SELECT COUNT(*) FROM alertListItemView")
    Single<Integer> getCount();

    @Query("SELECT COUNT(*) FROM alertListItemView WHERE courseId=:courseId")
    Single<Integer> getCountByCourse(long courseId);

}
