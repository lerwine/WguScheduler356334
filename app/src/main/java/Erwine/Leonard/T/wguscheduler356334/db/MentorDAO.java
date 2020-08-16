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
    Completable insert(MentorEntity mentor);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable update(MentorEntity mentor);

    @Insert
    Completable insertAll(List<MentorEntity> mentors);

    @Insert
    void insertAllItems(MentorEntity... items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable updateAll(List<MentorEntity> mentors);

    @Delete
    Completable delete(MentorEntity mentor);

    @Query("SELECT * FROM mentors WHERE ROWID = :rowId")
    Single<MentorEntity> getByRowId(int rowId);

    @Query("SELECT * FROM mentors WHERE id = :id")
    Single<MentorEntity> getById(int id);

    @Query("SELECT * FROM mentors ORDER BY [name]")
    LiveData<List<MentorEntity>> getAll();

    @Query("SELECT * FROM mentors")
    List<MentorEntity> getAllItems();

    @Query("SELECT COUNT(*) FROM mentors")
    Single<Integer> getCount();

    @Query("DELETE FROM mentors")
    Single<Integer> deleteAll();

}
