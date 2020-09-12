package Erwine.Leonard.T.wguscheduler356334.db;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Erwine.Leonard.T.wguscheduler356334.ui.mentor.MentorEditState;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DbLoaderTest {

    private AppDb db;
    private DbLoader dbLoader;

    @Before
    public void setUp() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDb.class).build();
        dbLoader = new DbLoader(context, db);
    }

    @After
    public void tearDown() throws Exception {
        db.close();
    }

    @Test
    public void getEditedMentor() {
        final MentorEditState editedMentor = dbLoader.getEditedMentor();
        assertNotNull(editedMentor);
        assertNull(editedMentor.getLiveData().getValue());
    }

//    @Test
//    public void ensureEditedMentorId() {
//    }
//
//    @Test
//    public void ensureNewEditedMentor() {
//    }
//
//    @Test
//    public void saveEditedMentor() {
//    }
//
//    @Test
//    public void deletedEditedMentor() {
//    }
}