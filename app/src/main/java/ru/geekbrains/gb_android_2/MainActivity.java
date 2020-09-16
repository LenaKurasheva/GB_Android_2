package ru.geekbrains.gb_android_2;//package ru.geekbrains.gb_android_2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.navigation.NavigationView;
import com.squareup.otto.Subscribe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import ru.geekbrains.gb_android_2.broadcastReceiver.InternetConnectionReceiver;
import ru.geekbrains.gb_android_2.broadcastReceiver.WifiConnectionReceiver;
import ru.geekbrains.gb_android_2.events.OpenSettingsFragmentEvent;
import ru.geekbrains.gb_android_2.events.OpenWeatherMainFragmentEvent;

public class MainActivity extends AppCompatActivity {

    public NavigationView navigationView;
    private DrawerLayout drawer;
    public static final String SETTINGS = "settings";
    WifiConnectionReceiver wifiConnectionReceiver = new WifiConnectionReceiver();
    InternetConnectionReceiver internetConnectionReceiver = new InternetConnectionReceiver();


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
        // Инициализируем библиотеку для работы с картинками:
        Fresco.initialize(this);

        // Программная регистрация ресиверов
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        IntentFilter wifiFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiConnectionReceiver, wifiFilter);
        registerReceiver(internetConnectionReceiver, intentFilter);
        initNotificationChannel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(wifiConnectionReceiver  != null) unregisterReceiver(wifiConnectionReceiver);
        if(internetConnectionReceiver != null) unregisterReceiver(internetConnectionReceiver);
    }

    // инициализация канала нотификаций
    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("2", "wifi connection", importance);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
            NotificationChannel internetChannel = new NotificationChannel("1", "internet connection", importance);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(internetChannel);
            }
        }
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

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getBus().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getBus().unregister(this);
        super.onStop();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onOpenWeatherMainFragmentEvent(OpenWeatherMainFragmentEvent event) {
        setHomeFragment();
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onOpenSettingsFragmentEvent(OpenSettingsFragmentEvent event) {
        setSettingsFragment();
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
        String currCityName = getSharedPreferences(MainActivity.SETTINGS, MODE_PRIVATE)
                .getString("current city", "Saint Petersburg");

        if (item.getItemId() == R.id.action_settings) {
            setSettingsFragment();
        }
        if (item.getItemId() == R.id.action_read_more){
            String wiki = "https://ru.wikipedia.org/wiki/" + currCityName;
            Uri uri = Uri.parse(wiki);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        return false;
    }
}
