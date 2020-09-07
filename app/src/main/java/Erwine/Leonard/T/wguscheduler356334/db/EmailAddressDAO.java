package Erwine.Leonard.T.wguscheduler356334.db;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.entity.EmailAddressEntity;
import io.reactivex.Completable;
import io.reactivex.Single;

public interface EmailAddressDAO {
    @Insert
    Completable insert(EmailAddressEntity term);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable update(EmailAddressEntity term);

    @Insert
    Completable insertAll(List<EmailAddressEntity> terms);

    @Insert
    void insertAllItems(List<EmailAddressEntity> items);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable updateAll(List<EmailAddressEntity> terms);

    @Delete
    Completable delete(EmailAddressEntity term);

    @Query("SELECT * FROM email_addresses WHERE ROWID = :rowId")
    Single<EmailAddressEntity> getByRowId(int rowId);

    @Query("SELECT * FROM email_addresses WHERE id = :id")
    Single<EmailAddressEntity> getById(int id);

    @Query("SELECT * FROM email_addresses WHERE mentorId =:mentorId ORDER BY [sortOrder], [id]")
    LiveData<List<EmailAddressEntity>> getByMentorId(int mentorId);

    @Query("SELECT * FROM email_addresses WHERE mentorId =:mentorId")
    List<EmailAddressEntity> getItemsByMentorId(int mentorId);

    @Query("SELECT COUNT(*) FROM email_addresses WHERE mentorId =:mentorId")
    Single<Integer> getCountByMentorId(int mentorId);

    @Query("DELETE FROM email_addresses WHERE mentorId =:mentorId")
    Single<Integer> deleteByMentorId(int mentorId);
}
