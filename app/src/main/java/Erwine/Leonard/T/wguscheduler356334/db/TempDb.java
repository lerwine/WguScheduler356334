package Erwine.Leonard.T.wguscheduler356334.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import Erwine.Leonard.T.wguscheduler356334.entity.EmailAddressEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.PhoneNumberEntity;

@Database(entities = {PhoneNumberEntity.class, EmailAddressEntity.class}, version = 1, exportSchema = false)
public abstract class TempDb extends RoomDatabase {
    public static final String TABLE_NAME_PHONE_NUMBERS = "phone_numbers";
    public static final String TABLE_NAME_EMAIL_ADDRESSES = "email_addresses";
    private static final Object SYNC_ROOT = new Object();
    private static volatile TempDb instance;

    static TempDb getInstance(Context context) {
        if (null == instance) {
            synchronized (SYNC_ROOT) {
                if (null == instance) {
                    instance = Room.inMemoryDatabaseBuilder(context.getApplicationContext(), TempDb.class).build();
                }
            }
        }
        return instance;
    }

    public abstract PhoneNumberDAO phoneNumberDAO();

    public abstract EmailAddressDAO emailAddressDAO();
}
