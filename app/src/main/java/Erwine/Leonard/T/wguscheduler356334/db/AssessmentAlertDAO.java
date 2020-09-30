package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlertEntity;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface AssessmentAlertDAO {

    @Insert
    Single<Long> insert(AssessmentAlertEntity alert);

    @Insert
    Long insertSynchronous(AssessmentAlertEntity alert);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable update(AssessmentAlertEntity course);

    @Insert
    Single<List<Long>> insertAll(List<AssessmentAlertEntity> list);

    @Insert
    List<Long> insertAllSynchronous(List<AssessmentAlertEntity> list);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable updateAll(List<AssessmentAlertEntity> list);

    @Delete
    Completable delete(AssessmentAlertEntity course);

    @Query("SELECT * FROM assessmentAlerts WHERE ROWID = :rowId")
    Single<AssessmentAlertEntity> getByRowId(int rowId);

    @Query("SELECT * FROM assessmentAlerts WHERE id = :id")
    Single<AssessmentAlertEntity> getById(long id);

    @Query("SELECT * FROM assessmentAlerts" +
            " WHERE assessmentId = :assessmentId ORDER BY [leadTime]")
    LiveData<List<AssessmentAlertEntity>> getByAssessmentId(long assessmentId);

    @Query("SELECT * FROM assessmentAlerts" +
            " WHERE assessmentId = :assessmentId ORDER BY [leadTime]")
    List<AssessmentAlertEntity> getByAssessmentIdSynchronous(long assessmentId);

    @Query("DELETE FROM assessmentAlerts WHERE assessmentId=:assessmentId")
    Single<Integer> deleteByAssessmentId(long assessmentId);
}
