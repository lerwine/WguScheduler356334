package Erwine.Leonard.T.wguscheduler356334;

import android.content.Context;

import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.TermDAO;
import Erwine.Leonard.T.wguscheduler356334.ui.terms.TermItemViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.SampleData;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class AppDbTest {
    private AppDb db;
    private TermDAO dao;

    @Before
    public void createDatabase() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDb.class).build();
        dao = db.termDAO();
    }

    @After
    public void closeDatabase() {
        db.close();
    }

    @Test
    public void addAllTermsTest() {
        List<TermItemViewModel> data = SampleData.getData();
        dao.insertAll(data);
        int expected = data.size();
        int actual = dao.getCount();
        assertEquals(expected, actual);
    }

}
