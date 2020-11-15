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

import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermListItem;
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

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateSynchronous(TermEntity term);

    @Insert
    Single<List<Long>> insertAll(List<TermEntity> terms);

    @Insert
    List<Long> insertAllSynchronous(List<TermEntity> items);

    @Delete
    Single<Integer> delete(TermEntity term);

    @Delete
    int deleteSynchronous(TermEntity term);

    @Query("SELECT * FROM terms WHERE id = :id LIMIT 1")
    Single<TermEntity> getById(long id);

    @Query("SELECT * FROM terms WHERE id = :id LIMIT 1")
    TermEntity getByIdSynchronous(long id);

    @Query("SELECT * FROM termListView ORDER BY start, `end`")
    LiveData<List<TermListItem>> getAll();

    @Query("SELECT * FROM termListView WHERE null != start AND start >= :date AND (null == `end` || `end` >= :date)")
    LiveData<List<TermListItem>> getOnOrAfterDate(LocalDate date);

    @Query("SELECT * FROM termListView ORDER BY start, `end`")
    List<TermListItem> getAllSynchronous();

    @Query("SELECT COUNT(*) FROM terms")
    Single<Integer> getCount();

}
