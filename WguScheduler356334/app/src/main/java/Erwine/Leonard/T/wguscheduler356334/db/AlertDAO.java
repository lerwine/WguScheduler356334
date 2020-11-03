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
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface AlertDAO {

    @Insert
    Single<Long> insert(AlertEntity item);

    @Insert
    long insertSynchronous(AlertEntity item);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable update(AlertEntity item);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateSynchronous(AlertEntity item);

    @Insert
    Single<List<Long>> insertAll(List<AlertEntity> list);

    @Insert
    List<Long> insertAllSynchronous(List<AlertEntity> list);

    @Delete
    Single<Integer> delete(AlertEntity item);

    @Delete
    int deleteSynchronous(AlertEntity item);

    @Query("SELECT * FROM alertListView ORDER BY alertDate DESC, eventDate DESC, code, title")
    LiveData<List<AlertListItem>> getAll();

    @Query("SELECT * FROM alertListView WHERE courseId=:courseId ORDER BY alertDate DESC, eventDate DESC, code, title")
    Single<List<AlertListItem>> getAllByCourseId(long courseId);

    @Query("SELECT * FROM alertListView WHERE termId=:termId ORDER BY alertDate DESC, eventDate DESC, code, title")
    Single<List<AlertListItem>> getAllByTermId(long termId);

    @Query("SELECT * FROM alertListView WHERE mentorId=:mentorId ORDER BY alertDate DESC, eventDate DESC, code, title")
    Single<List<AlertListItem>> getAllByMentorId(long mentorId);

    @Query("SELECT * FROM alertListView")
    List<AlertListItem> getAllSynchronous();

    @Query("SELECT * FROM alertListView WHERE NOT(eventDate IS NULL) AND alertDate < :date AND eventDate < :date ORDER BY alertDate DESC, eventDate DESC, code, title")
    LiveData<List<AlertListItem>> getActiveBeforeDate(LocalDate date);

    @Query("SELECT * FROM alertListView WHERE NOT(eventDate IS NULL OR alertDate > :date OR eventDate < :date) ORDER BY alertDate, eventDate, code, title")
    LiveData<List<AlertListItem>> getActiveOnDate(LocalDate date);

    @Query("SELECT * FROM alertListView WHERE NOT(eventDate IS NULL OR alertDate > :end OR eventDate < :start) ORDER BY alertDate, eventDate, code, title")
    LiveData<List<AlertListItem>> getActiveInRange(LocalDate start, LocalDate end);

    @Query("SELECT * FROM alertListView WHERE NOT(eventDate IS NULL) AND alertDate > :date AND eventDate > :date ORDER BY alertDate, eventDate, code, title")
    LiveData<List<AlertListItem>> getActiveAfterDate(LocalDate date);

    @Query("SELECT * FROM alertListView WHERE eventDate IS NULL ORDER BY code, title")
    LiveData<List<AlertListItem>> getDatePendingAlerts();

    @Query("SELECT COUNT(id) FROM alerts WHERE  notificationId=:notificationId")
    int countByNotificationId(int notificationId);

}
