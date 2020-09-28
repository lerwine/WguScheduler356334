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
import Erwine.Leonard.T.wguscheduler356334.entity.TermListItem;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface TermDAO {

    @Insert
    Single<Long> insert(TermEntity term);

    @Insert
    Long insertSynchronous(TermEntity term);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable update(TermEntity term);

    @Insert
    Single<List<Long>> insertAll(List<TermEntity> terms);

    @Insert
    List<Long> insertAllSynchronous(List<TermEntity> items);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable updateAll(List<TermEntity> terms);

    @Delete
    Completable delete(TermEntity term);

    @Query("SELECT * FROM terms WHERE ROWID = :rowId")
    Single<TermEntity> getByRowId(int rowId);

    @Query("SELECT * FROM terms WHERE id = :id")
    Single<TermEntity> getById(long id);

    @Query("SELECT * FROM termListView")
    LiveData<List<TermListItem>> getAll();

    @Query("SELECT * FROM termListView")
    List<TermListItem> getAllSynchronous();

    @Query("SELECT COUNT(*) FROM terms")
    Single<Integer> getCount();

    @Query("DELETE FROM terms")
    Single<Integer> deleteAll();

}
