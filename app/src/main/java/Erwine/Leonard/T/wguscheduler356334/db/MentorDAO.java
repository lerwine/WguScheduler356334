package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable updateAll(List<MentorEntity> mentors);

    @Delete
    Completable delete(MentorEntity mentor);

    @Delete
    void deleteSynchronous(MentorEntity mentor);

    @Query("SELECT * FROM mentors WHERE ROWID = :rowId")
    Single<MentorEntity> getByRowId(int rowId);

    @Query("SELECT * FROM mentors WHERE id = :id")
    Single<MentorEntity> getById(long id);

    @Query("SELECT * FROM mentors WHERE id = :id")
    MentorEntity getByIdSynchronous(long id);

    @Query("SELECT * FROM mentors ORDER BY [name]")
    LiveData<List<MentorEntity>> getAll();

    @Query("SELECT * FROM mentors")
    List<MentorEntity> getAllSynchronous();

    @Query("SELECT COUNT(*) FROM mentors")
    Single<Integer> getCount();

    @Query("DELETE FROM mentors")
    Single<Integer> deleteAll();

}
