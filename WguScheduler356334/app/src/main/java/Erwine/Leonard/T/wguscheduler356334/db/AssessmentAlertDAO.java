package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlertDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlertLink;
import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface AssessmentAlertDAO {

    @Insert
    Single<Long> insert(AssessmentAlertLink alert);

    @Insert
    Long insertSynchronous(AssessmentAlertLink alert);

    @Update
    void updateSynchronous(AssessmentAlertLink alert);

    @Insert
    Single<List<Long>> insertAll(List<AssessmentAlertLink> list);

    @Insert
    List<Long> insertAllSynchronous(List<AssessmentAlertLink> list);

    @Delete
    Single<Integer> delete(AssessmentAlertLink course);

    @Delete
    int deleteSynchronous(AssessmentAlertLink course);

    @Transaction
    @Query("SELECT * FROM assessmentAlerts WHERE alertId = :alertId AND targetId=:assessmentId")
    Single<AssessmentAlert> getByAlertId(long alertId, long assessmentId);

    @Transaction
    @Query("SELECT * FROM assessmentAlerts WHERE alertId = :alertId AND targetId=:assessmentId")
    Single<AssessmentAlertDetails> getByDetailByAlertId(long alertId, long assessmentId);

    @Transaction
    @Query("SELECT * FROM assessmentAlerts WHERE targetId = :assessmentId")
    LiveData<List<AssessmentAlert>> getLiveDataByAssessmentId(long assessmentId);

    @Transaction
    @Query("SELECT * FROM assessmentAlerts WHERE targetId = :assessmentId")
    Observable<List<AssessmentAlert>> getObservableByAssessmentId(long assessmentId);

    @Transaction
    @Query("SELECT * FROM assessmentAlerts WHERE targetId = :assessmentId")
    List<AssessmentAlert> getByAssessmentIdSynchronous(long assessmentId);

    @Query("DELETE FROM assessmentAlerts WHERE targetId=:assessmentId")
    Single<Integer> deleteByAssessmentId(long assessmentId);

    @Query("SELECT COUNT(targetId) FROM assessmentAlerts WHERE alertId=:alertId")
    int countByAlertIdSynchronous(long alertId);

}
