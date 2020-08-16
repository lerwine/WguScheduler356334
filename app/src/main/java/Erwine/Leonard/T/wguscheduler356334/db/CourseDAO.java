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

import Erwine.Leonard.T.wguscheduler356334.entity.CourseEntity;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface CourseDAO {

    @Insert
    Completable insert(CourseEntity course);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable update(CourseEntity course);

    @Insert
    Completable insertAll(List<CourseEntity> list);

    @Insert
    void insertAllItems(CourseEntity... items);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable updateAll(List<CourseEntity> list);

    @Delete
    Completable delete(CourseEntity course);

    @Query("SELECT * FROM courses WHERE ROWID = :rowId")
    Single<CourseEntity> getByRowId(int rowId);

    @Query("SELECT * FROM courses WHERE id = :id")
    Single<CourseEntity> getById(int id);

    @Query("SELECT * FROM courses ORDER BY [expectedStart], [actualStart]")
    LiveData<List<CourseEntity>> getAll();

    @Query("SELECT * FROM courses WHERE termId = :termId ORDER BY [expectedStart], [actualStart]")
    LiveData<List<CourseEntity>> getByTermId(int termId);

    @Query("SELECT * FROM courses WHERE termId = :termId")
    List<CourseEntity> getItemsByTermId(int termId);

    @Query("SELECT * FROM courses WHERE mentorId = :mentorId ORDER BY [expectedStart], [actualStart]")
    LiveData<List<CourseEntity>> getByMentorId(int mentorId);

    @Query("SELECT * FROM courses WHERE actualEnd IS NULL AND expectedStart IS NOT NULL AND expectedStart <= :date ORDER BY [expectedStart], [actualStart]")
    LiveData<List<CourseEntity>> getUnterminatedOnOrBefore(LocalDate date);

    @Query("SELECT COUNT(*) FROM courses")
    Single<Integer> getCount();

    @Query("DELETE FROM courses")
    Single<Integer> deleteAll();

}
