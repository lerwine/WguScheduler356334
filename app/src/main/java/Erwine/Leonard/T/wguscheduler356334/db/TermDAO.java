package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.ui.terms.TermItemViewModel;

@Dao
public interface TermDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTerm(TermItemViewModel term);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TermItemViewModel> terms);

    @Delete
    void deleteTerm(TermItemViewModel term);

    @Query("SELECT * FROM terms WHERE id = :id")
    TermItemViewModel getTermById(int id);

    @Query("SELECT * FROM terms ORDER BY [start], [end]")
    LiveData<List<TermItemViewModel>> getAll();

    @Query("SELECT COUNT(*) FROM terms")
    int getCount();

}
