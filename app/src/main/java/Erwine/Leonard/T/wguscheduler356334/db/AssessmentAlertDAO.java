package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlertLink;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface AssessmentAlertDAO {

    @Insert
    Single<Long> insert(AssessmentAlertLink alert);

    @Insert
    Long insertSynchronous(AssessmentAlertLink alert);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable update(AssessmentAlertLink course);

    @Insert
    Single<List<Long>> insertAll(List<AssessmentAlertLink> list);

    @Insert
    List<Long> insertAllSynchronous(List<AssessmentAlertLink> list);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable updateAll(List<AssessmentAlertLink> list);

    @Delete
    Completable delete(AssessmentAlertLink course);

    @Query("SELECT * FROM assessmentAlerts WHERE ROWID = :rowId")
    Single<AssessmentAlertLink> getByRowId(int rowId);

    @Query("SELECT * FROM assessmentAlerts WHERE alertId = :alertId")
    Single<AssessmentAlertLink> getByAlertId(long alertId);

    @Query("SELECT * FROM assessmentAlerts" +
            " WHERE targetId = :assessmentId")
    LiveData<List<AssessmentAlertLink>> getByAssessmentId(long assessmentId);

    @Query("SELECT * FROM assessmentAlerts" +
            " WHERE targetId = :assessmentId")
    List<AssessmentAlertLink> getByAssessmentIdSynchronous(long assessmentId);

    @Query("DELETE FROM assessmentAlerts WHERE targetId=:assessmentId")
    Single<Integer> deleteByAssessmentId(long assessmentId);
}
