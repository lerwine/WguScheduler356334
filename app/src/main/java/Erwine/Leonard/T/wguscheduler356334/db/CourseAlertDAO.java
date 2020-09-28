package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.entity.CourseAlertEntity;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface CourseAlertDAO {

    @Insert
    Single<Long> insert(CourseAlertEntity alert);

    @Insert
    Long insertSynchronous(CourseAlertEntity alert);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable update(CourseAlertEntity course);

    @Insert
    Single<List<Long>> insertAll(List<CourseAlertEntity> list);

    @Insert
    List<Long> insertAllSynchronous(List<CourseAlertEntity> list);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable updateAll(List<CourseAlertEntity> list);

    @Delete
    Completable delete(CourseAlertEntity course);

    @Query("SELECT * FROM courseAlerts WHERE ROWID = :rowId")
    Single<CourseAlertEntity> getByRowId(int rowId);

    @Query("SELECT * FROM courseAlerts WHERE id = :id")
    Single<CourseAlertEntity> getById(long id);

    @Query("SELECT * FROM courseAlerts" +
            " WHERE courseId = :courseId ORDER BY [leadTime]")
    LiveData<List<CourseAlertEntity>> getByCourseId(long courseId);

    @Query("SELECT * FROM courseAlerts" +
            " WHERE courseId = :courseId ORDER BY [leadTime]")
    List<CourseAlertEntity> getByCourseIdSynchronous(long courseId);

    @Query("DELETE FROM courseAlerts WHERE courseId=:courseId")
    Single<Integer> deleteByCourseId(long courseId);

}
