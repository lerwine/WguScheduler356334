package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.entity.AssessmentEntity;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface AssessmentDAO {

    @Insert
    Completable insert(AssessmentEntity assessment);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable update(AssessmentEntity assessment);

    @Insert
    Completable insertAll(List<AssessmentEntity> list);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable updateAll(List<AssessmentEntity> list);

    @Delete
    Completable delete(AssessmentEntity assessment);

    @Query("SELECT * FROM assessments WHERE ROWID = :rowId")
    Single<AssessmentEntity> getByRowId(int rowId);

    @Query("SELECT * FROM assessments WHERE id = :id")
    Single<AssessmentEntity> getById(int id);

    @Query("SELECT * FROM assessments WHERE courseId = :courseId ORDER BY [goalDate], [evaluationDate]")
    LiveData<List<AssessmentEntity>> getByCourseId(int courseId);

    @Query("SELECT * FROM assessments ORDER BY [goalDate], [evaluationDate]")
    LiveData<List<AssessmentEntity>> getAll();

    @Query("SELECT COUNT(*) FROM assessments")
    Single<Integer> getCount();

}
