package ru.geekbrains.gb_android_2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            WeatherMainFragment wmf = new WeatherMainFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.weatherMain, wmf);
            fragmentTransaction.commit();
            Log.d("myLog", "MainActivity: onCreate; savedInstanceState == null");
        } else {
            Log.d("myLog", "MainActivity: onCreate; savedInstanceState != null");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.about) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int countOfFragmentInManager = getSupportFragmentManager().getBackStackEntryCount();
        if(countOfFragmentInManager > 0) {
            getSupportFragmentManager().popBackStack();
            /*getSupportFragmentManager().popBackStack("Some_Key",
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);*/
        }
    }
}
