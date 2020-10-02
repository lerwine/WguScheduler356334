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

import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.MentorCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.course.TermCourseListItem;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface CourseDAO {

    @Insert
    Single<Long> insert(CourseEntity course);

    @Insert
    Long insertSynchronous(CourseEntity course);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable update(CourseEntity course);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable updateSynchronous(CourseEntity course);

    @Insert
    Single<List<Long>> insertAll(List<CourseEntity> list);

    @Insert
    List<Long> insertAllSynchronous(List<CourseEntity> list);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable updateAll(List<CourseEntity> list);

    @Delete
    Completable delete(CourseEntity course);

    @Delete
    void deleteSynchronous(CourseEntity course);

    @Query("SELECT * FROM courses WHERE ROWID = :rowId")
    Single<CourseEntity> getByRowId(int rowId);

    @Query("SELECT * FROM courseDetailView WHERE id = :id")
    Single<CourseDetails> getById(long id);

    @Query("SELECT * FROM courses WHERE id = :id")
    CourseEntity getByIdSynchronous(long id);

    @Query("SELECT * FROM courses ORDER BY [actualStart], [expectedStart], [actualEnd], [expectedEnd]")
    LiveData<List<CourseEntity>> getAll();

    @Query("SELECT * FROM courses")
    List<CourseEntity> getAllSynchronous();

    @Query("SELECT * FROM termCourseListView WHERE termId = :termId ORDER BY [actualStart], [expectedStart], [actualEnd], [expectedEnd]")
    LiveData<List<TermCourseListItem>> getByTermId(long termId);

    @Query("SELECT * FROM termCourseListView WHERE termId = :termId")
    List<TermCourseListItem> getByTermIdSynchronous(long termId);

    @Query("SELECT * FROM mentorCourseView WHERE mentorId = :mentorId ORDER BY [actualStart], [expectedStart], [actualEnd], [expectedEnd]")
    LiveData<List<MentorCourseListItem>> getByMentorId(long mentorId);

    @Query("SELECT * FROM courses WHERE actualEnd IS NULL AND expectedStart IS NOT NULL AND expectedStart <= :date ORDER BY [actualStart], [expectedStart], [actualEnd], [expectedEnd]")
    LiveData<List<CourseEntity>> getUnterminatedOnOrBefore(LocalDate date);

    @Query("SELECT COUNT(*) FROM courses")
    Single<Integer> getCount();

    @Query("SELECT COUNT(*) FROM courses WHERE mentorId=:mentorId")
    int getCountByMentorIdSynchronous(long mentorId);

    @Query("SELECT COUNT(*) FROM courses WHERE termId=:termId")
    int getCountByTermIdSynchronous(long termId);

    @Query("DELETE FROM courses WHERE termId=:termId")
    Single<Integer> deleteByTermId(long termId);

}
