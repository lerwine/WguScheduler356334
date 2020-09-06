package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface TermDAO {

    @Insert
    Completable insert(TermEntity term);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable update(TermEntity term);

    @Insert
    Completable insertAll(List<TermEntity> terms);

    @Insert
    void insertAllItems(List<TermEntity> items);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable updateAll(List<TermEntity> terms);

    @Delete
    Completable delete(TermEntity term);

    @Query("SELECT * FROM terms WHERE ROWID = :rowId")
    Single<TermEntity> getByRowId(int rowId);

    @Query("SELECT * FROM terms WHERE id = :id")
    Single<TermEntity> getById(int id);

    @Query("SELECT * FROM terms ORDER BY [start], [end]")
    LiveData<List<TermEntity>> getAll();

    @Query("SELECT * FROM terms")
    List<TermEntity> getAllItems();

    @Query("SELECT COUNT(*) FROM terms")
    Single<Integer> getCount();

    @Query("DELETE FROM courses")
    Single<Integer> deleteAll();

}
