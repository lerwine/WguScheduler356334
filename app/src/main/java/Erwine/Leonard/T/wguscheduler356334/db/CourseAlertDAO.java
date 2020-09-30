package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlertLink;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface CourseAlertDAO {

    @Insert
    Single<Long> insert(CourseAlertLink alert);

    @Insert
    Long insertSynchronous(CourseAlertLink alert);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable update(CourseAlertLink course);

    @Insert
    Single<List<Long>> insertAll(List<CourseAlertLink> list);

    @Insert
    List<Long> insertAllSynchronous(List<CourseAlertLink> list);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable updateAll(List<CourseAlertLink> list);

    @Delete
    Completable delete(CourseAlertLink course);

    @Query("SELECT * FROM courseAlerts WHERE ROWID = :rowId")
    Single<CourseAlertLink> getByRowId(int rowId);

    @Query("SELECT * FROM courseAlerts WHERE alertId = :alertId")
    Single<CourseAlertLink> getByAlertId(long alertId);

    @Query("SELECT * FROM courseAlerts" +
            " WHERE targetId = :courseId")
    LiveData<List<CourseAlertLink>> getByCourseId(long courseId);

    @Query("SELECT * FROM courseAlerts" +
            " WHERE targetId = :courseId")
    List<CourseAlertLink> getByCourseIdSynchronous(long courseId);

    @Query("DELETE FROM courseAlerts WHERE targetId=:courseId")
    Single<Integer> deleteByCourseId(long courseId);

}
