package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlertDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlertLink;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface CourseAlertDAO {

    @Insert
    Single<Long> insert(CourseAlertLink alert);

    @Insert
    Long insertSynchronous(CourseAlertLink alert);

    @Insert
    Single<List<Long>> insertAll(List<CourseAlertLink> list);

    @Insert
    List<Long> insertAllSynchronous(List<CourseAlertLink> list);

    @Delete
    Completable delete(CourseAlertLink course);

    @Transaction
    @Query("SELECT * FROM courseAlerts WHERE alertId = :alertId AND targetId=:courseId")
    Single<CourseAlert> getByAlertId(long alertId, long courseId);

    @Transaction
    @Query("SELECT * FROM courseAlerts WHERE alertId = :alertId AND targetId=:courseId")
    Single<CourseAlertDetails> getDetailsAlertId(long alertId, long courseId);

    @Transaction
    @Query("SELECT * FROM courseAlerts WHERE targetId = :courseId")
    LiveData<List<CourseAlert>> getByCourseId(long courseId);

    @Transaction
    @Query("SELECT * FROM courseAlerts WHERE targetId = :courseId")
    List<CourseAlert> getByCourseIdSynchronous(long courseId);

    @Transaction
    @Query("DELETE FROM courseAlerts WHERE targetId=:courseId")
    Single<Integer> deleteByCourseId(long courseId);
}
