package com.example.checkinnow.main_menu_fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.checkinnow.R;

public class Main_Menu_Activity extends AppCompatActivity {



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment = null;

            switch (item.getItemId()) {

                case R.id.navigation_home:

                    Log.i("check fragment :"," 2 activity");
                    selectedFragment = new DashBoardFragment();

                    break;
                case R.id.navigation_dashboard:

                    selectedFragment = new Score_Friend_Fragment();
                    break;
                case R.id.navigation_notifications:
                    selectedFragment = new Score_Friend_Fragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.containerFragmentID,
                    selectedFragment).commit();

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__menu_);

        Log.i("check fragment :"," 1 activity");
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.containerFragmentID,
                    new Score_Friend_Fragment()).commit();
        }
    }

}
