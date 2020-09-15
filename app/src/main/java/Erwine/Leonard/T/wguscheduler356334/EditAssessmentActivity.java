package Erwine.Leonard.T.wguscheduler356334;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.disposables.CompositeDisposable;

public class EditAssessmentActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddTermActivity.class.getName();

    private final CompositeDisposable compositeDisposable;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EditAssessmentActivity() {
        Log.d(LOG_TAG, "Constructing Erwine.Leonard.T.wguscheduler356334.EditAssessmentActivity");
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assessment);
    }
}