package ru.geekbrains.gb_android_2;//package ru.geekbrains.gb_android_2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    public NavigationView navigationView;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setHomeFragment();
        setOnClickForSideMenuItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
            CurrentDataContainer.backStack.pop();
            String currFragmentName = CurrentDataContainer.backStack.peek();
            if(currFragmentName.equals("WeatherMainFragment")) navigationView.setCheckedItem(R.id.nav_home);
            if(currFragmentName.equals("ChooseCityFragment")) navigationView.setCheckedItem(R.id.nav_choose_city);
            if(currFragmentName.equals("AboutFragment")) navigationView.setCheckedItem(R.id.nav_about);
            // Если открыт фрагмент с настройками, кт. нет в Navigation drawer, убираем вделение со всех пунктов меню:
            if(currFragmentName.equals("SettingsFragment")) {
                int size = navigationView.getMenu().size();
                for (int i = 0; i < size; i++) {
                    navigationView.getMenu().getItem(i).setChecked(false);
                }
            }
        }
    }

    private void setOnClickForSideMenuItems() {
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home: {
                    setHomeFragment();
                    drawer.close();
                    break;
                }
                case R.id.nav_choose_city: {
                    setChooseCityFragment();
                    drawer.close();
                    break;
                }
                case R.id.nav_about: {
                    setAboutFragment();
                    drawer.close();
                    break;
                }
            }
            return true;
        });
    }

    public void setHomeFragment() {
        setFragment(WeatherMainFragment.create(CurrentDataContainer.getInstance()), WeatherMainFragment.class.getSimpleName());
    }

    private void setChooseCityFragment() {
        setFragment(ChooseCityFragment.create(CurrentDataContainer.getInstance()), ChooseCityFragment.class.getSimpleName());
    }

    private void setAboutFragment() {
        setFragment(AboutFragment.create(CurrentDataContainer.getInstance()), AboutFragment.class.getSimpleName());
        Log.d("BACKSTACK", AboutFragment.class.getSimpleName());
    }

    private void setSettingsFragment(){
        setFragment(SettingsFragment.create(CurrentDataContainer.getInstance()), SettingsFragment.class.getSimpleName());
    }

    public void setFragment(Fragment fragment, String fragmentName) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.addToBackStack(fragmentName);
        CurrentDataContainer.backStack.addElement(fragmentName);
        fragmentTransaction.commit();
    }


    @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            setSettingsFragment();
        }
        if (item.getItemId() == R.id.action_read_more){
            String wiki = "https://ru.wikipedia.org/wiki/" + CurrentDataContainer.getInstance().currCityName;
            Uri uri = Uri.parse(wiki);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        return false;
    }
}
