package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import Erwine.Leonard.T.wguscheduler356334.R;

public class ViewMentorActivity extends AppCompatActivity {

    public static final String EXTRAS_KEY_MENTOR_ID = "mentorId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_mentor_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ViewMentorFragment.newInstance())
                    .commitNow();
        }
    }
}