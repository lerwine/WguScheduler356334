package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.entity.PhoneNumberEntity;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface PhoneNumberDAO {
    @Insert
    Single<Long> insert(PhoneNumberEntity term);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable update(PhoneNumberEntity term);

    @Insert
    Single<List<Long>> insertAll(List<PhoneNumberEntity> terms);

    @Insert
    List<Long> insertAllSynchronous(List<PhoneNumberEntity> items);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable updateAll(List<PhoneNumberEntity> terms);

    @Delete
    Completable delete(PhoneNumberEntity term);

    @Query("SELECT * FROM phone_numbers WHERE ROWID = :rowId")
    Single<PhoneNumberEntity> getByRowId(int rowId);

    @Query("SELECT * FROM phone_numbers WHERE id = :id")
    Single<PhoneNumberEntity> getById(int id);

//    @Query("SELECT * FROM email_addresses WHERE mentorId =:mentorId ORDER BY [sortOrder], [id]")
//    LiveData<List<PhoneNumberEntity>> getByMentorId(int mentorId);

    @Query("SELECT * FROM email_addresses ORDER BY [sortOrder], [id]")
    LiveData<List<PhoneNumberEntity>> getAll();

//    @Query("SELECT * FROM email_addresses WHERE mentorId =:mentorId")
//    List<PhoneNumberEntity> getItemsByMentorId(int mentorId);

    @Query("SELECT * FROM email_addresses")
    List<PhoneNumberEntity> getAllSynchronous();

//    @Query("SELECT COUNT(*) FROM phone_numbers WHERE mentorId =:mentorId")
//    Single<Integer> getCountByMentorId(int mentorId);

    @Query("SELECT COUNT(*) FROM phone_numbers")
    Single<Integer> getCount();

    @Query("DELETE FROM phone_numbers")
    Single<Integer> deleteAll();

    @Query("DELETE FROM phone_numbers")
    int deleteAllSynchronous();

}
