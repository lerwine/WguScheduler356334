package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorListItem;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface MentorDAO {

    @Insert
    Single<Long> insert(MentorEntity mentor);

    @Insert
    long insertSynchronous(MentorEntity mentor);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable update(MentorEntity mentor);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateSynchronous(MentorEntity mentor);

    @Insert
    Single<List<Long>> insertAll(List<MentorEntity> mentors);

    @Insert
    List<Long> insertAllSynchronous(List<MentorEntity> items);

    @Delete
    Single<Integer> delete(MentorEntity mentor);

    @Delete
    int deleteSynchronous(MentorEntity mentor);

    @Query("SELECT * FROM mentors WHERE id = :id LIMIT 1")
    Single<MentorEntity> getById(long id);

    @Query("SELECT * FROM mentors WHERE id = :id LIMIT 1")
    MentorEntity getByIdSynchronous(long id);

    @Query("SELECT * FROM mentorListView")
    LiveData<List<MentorListItem>> getAll();

    @Query("SELECT * FROM mentorListView")
    List<MentorListItem> getAllSynchronous();

    @Query("SELECT COUNT(*) FROM mentors")
    Single<Integer> getCount();

}
