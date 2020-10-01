package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface AssessmentDAO {

    @Insert
    Single<Long> insert(AssessmentEntity assessment);

    @Insert
    long insertSynchronous(AssessmentEntity item);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable update(AssessmentEntity assessment);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateSynchronous(AssessmentEntity assessment);

    @Insert
    Single<List<Long>> insertAll(List<AssessmentEntity> list);

    @Insert
    List<Long> insertAllSynchronous(List<AssessmentEntity> list);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable updateAll(List<AssessmentEntity> list);

    @Delete
    Completable delete(AssessmentEntity assessment);

    @Delete
    void deleteSynchronous(AssessmentEntity assessment);

    @Query("SELECT * FROM assessments WHERE ROWID = :rowId")
    Single<AssessmentEntity> getByRowId(int rowId);

    @Query("SELECT * FROM assessmentDetailView WHERE id = :id")
    Single<AssessmentDetails> getById(long id);

    @Query("SELECT * FROM assessments WHERE courseId = :courseId ORDER BY [goalDate], completionDate")
    LiveData<List<AssessmentEntity>> getByCourseId(long courseId);

    @Query("SELECT * FROM assessments WHERE courseId = :courseId")
    List<AssessmentEntity> getByCourseIdSynchronous(long courseId);

    @Query("SELECT * FROM assessments ORDER BY [goalDate], completionDate")
    LiveData<List<AssessmentEntity>> getAll();

    @Query("SELECT * FROM assessments ORDER BY [goalDate], completionDate")
    List<AssessmentEntity> getAllSynchronous();

    @Query("SELECT COUNT(*) FROM assessments WHERE courseId = :courseId")
    int getCountByCourseIdSynchronous(long courseId);

    @Query("SELECT COUNT(*) FROM assessments")
    Single<Integer> getCount();

    @Query("DELETE FROM assessments WHERE courseId=:courseId")
    Single<Integer> deleteByCourseId(long courseId);

}
