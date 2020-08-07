package Erwine.Leonard.T.wguscheduler356334.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import Erwine.Leonard.T.wguscheduler356334.ui.terms.TermItemViewModel;

@Database(entities = {TermItemViewModel.class}, version = 1, exportSchema = false)
@TypeConverters(LocalDateConverter.class)
public abstract class AppDb extends RoomDatabase {
    public static final String DB_NAME = "WguScheduler.db";
    private static volatile AppDb instance;
    private static final Object SYNC_ROOT = new Object();

    public abstract TermDAO termDAO();

    public static AppDb getInstance(Context context) {
        if (null == instance) {
            synchronized (SYNC_ROOT) {
                if (null == instance) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), AppDb.class, DB_NAME).build();
                }
            }
        }
        return instance;
    }

}
