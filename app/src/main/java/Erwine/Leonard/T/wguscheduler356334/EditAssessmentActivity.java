package Erwine.Leonard.T.wguscheduler356334;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.AddTermActivity.onOptionsItemSelected");
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            confirmSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        confirmSave();
    }

    private void confirmSave() {
//        if (viewModel.isChanged()) {
//            new AlertHelper(R.drawable.dialog_warning, R.string.title_discard_changes, R.string.message_discard_changes, this).showYesNoCancelDialog(this::finish, () -> {
//                compositeDisposable.clear();
//                compositeDisposable.add(viewModel.save().subscribe(this::onSaveOperationFinished, this::onSaveFailed));
//            }, null);
//        } else {
        finish();
//        }
    }

}