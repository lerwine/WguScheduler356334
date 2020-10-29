package Erwine.Leonard.T.wguscheduler356334;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import Erwine.Leonard.T.wguscheduler356334.ui.alert.AssessmentAlertBroadcastReceiver;
import Erwine.Leonard.T.wguscheduler356334.ui.alert.CourseAlertBroadcastReceiver;

public class MainActivity extends AppCompatActivity {

    private static ViewModelProvider.AndroidViewModelFactory viewModelFactory;

    public static ViewModelProvider.AndroidViewModelFactory getViewModelFactory(@NonNull Application application) {
        if (null == viewModelFactory) {
            viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(application);
        }
        return viewModelFactory;
    }


    public static String getLogTag(Class<?> c) {
        String n = c.getName();
        int l;
        return (n.startsWith(BuildConfig.APPLICATION_ID) && n.length() > (l = BuildConfig.APPLICATION_ID.length())) ? n.substring(l) : n;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new AssessmentAlertBroadcastReceiver().createNotificationChannel(this);
        new CourseAlertBroadcastReceiver().createNotificationChannel(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_terms, R.id.navigation_mentors, R.id.navigation_alerts)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_manage_data) {
            Intent intent = new Intent(this, ManageDataActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_show_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}