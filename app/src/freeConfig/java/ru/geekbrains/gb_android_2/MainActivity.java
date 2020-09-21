package ru.geekbrains.gb_android_2;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.squareup.otto.Subscribe;

import ru.geekbrains.gb_android_2.broadcastReceiver.InternetConnectionReceiver;
import ru.geekbrains.gb_android_2.broadcastReceiver.WifiConnectionReceiver;
import ru.geekbrains.gb_android_2.events.OpenChooseCityFragmentEvent;
import ru.geekbrains.gb_android_2.events.OpenSettingsFragmentEvent;
import ru.geekbrains.gb_android_2.events.OpenWeatherMainFragmentEvent;
import ru.geekbrains.gb_android_2.events.ShowCurrLocationItemEvent;
import ru.geekbrains.gb_android_2.events.ShowCurrentLocationWeatherEvent;

public class MainActivity extends AppCompatActivity {

    public NavigationView navigationView;
    private DrawerLayout drawer;
    public static final String SETTINGS = "settings";
    WifiConnectionReceiver wifiConnectionReceiver = new WifiConnectionReceiver();
    InternetConnectionReceiver internetConnectionReceiver = new InternetConnectionReceiver();
    private MenuItem currCityLocation;
    private GoogleSignInClient googleSignInClient;
    private final int RC_SIGN_IN = 5123;
    SimpleDraweeView userPhotoSimpleDraweeView;
    TextView userNameTextView;
    TextView userEmailTextView;
    View headerView;
    MenuItem navAuthMenuItem;
    GoogleSignInAccount account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализируем библиотеку для работы с картинками:
        Fresco.initialize(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViews();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(CurrentDataContainer.isFirstEnter) {
            setChooseCityFragment();
            navigationView.setCheckedItem(R.id.nav_choose_city);
        }
        else setHomeFragment();

        setOnClickForSideMenuItems();

        // Программная регистрация ресиверов
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        IntentFilter wifiFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiConnectionReceiver, wifiFilter);
        registerReceiver(internetConnectionReceiver, intentFilter);
        initNotificationChannel();

        // Добавим аутентификацию Google:
        String serverClientId = "116115044550-8ciggkdmab75skg7ar90t5doomdb73gl.apps.googleusercontent.com";
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(serverClientId)
                .requestServerAuthCode(serverClientId, false)
                .requestEmail()
                .requestProfile()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        if(account == null) {
            navigationView.removeHeaderView(headerView);
        }
    }

    private void findViews(){
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        userPhotoSimpleDraweeView = (SimpleDraweeView) headerView.findViewById(R.id.userPhoto);
        userNameTextView = (TextView) headerView.findViewById(R.id.userName);
        userEmailTextView = (TextView) headerView.findViewById(R.id.userEmail);
        navAuthMenuItem = (MenuItem) navigationView.getMenu().findItem(R.id.nav_auth);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Log.d("Google SignIn", "onActivityResult -> getSignedInAccountFromIntent");
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            String token = account.getIdToken();
            Log.d("Google SignIn", "signInResult:success code=" + token);

            // here we can send token to server

            // Signed in successfully, show authenticated UI.
            if(!TextUtils.isEmpty(token)) {
                Toast.makeText(getApplicationContext(), "Received not empty token!",
                        Toast.LENGTH_SHORT).show();
                updateUI(account);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("Google SignIn", "signInResult:failed code=" + e.getStatusCode() + "; message: " + e.getMessage());
            e.printStackTrace();
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        // Убираем выделение из бокового меню:
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
        if(account != null) {
            navigationView.addHeaderView(headerView);

            String userEmail = account.getEmail();
            Uri photoUri = account.getPhotoUrl();
            String userName = account.getDisplayName();
            userNameTextView.setText(userName);
            userEmailTextView.setText(userEmail);
            userPhotoSimpleDraweeView.setImageURI(photoUri);
            navAuthMenuItem.setTitle(R.string.exit);
        }
        if (account == null) {
            navigationView.removeHeaderView(headerView);
            navAuthMenuItem.setTitle(R.string.login_with_google);
        }
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
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) Log.d("Google SignIn", "onStart -> account name = " + account.getDisplayName());
        updateUI(account);
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

    @Subscribe
    @SuppressWarnings("unused")
    public void onShowCurrLocationItemEvent(ShowCurrLocationItemEvent event) {
        currCityLocation.setVisible(true);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onOpenChooseCityFragmentEvent(OpenChooseCityFragmentEvent event) {
        setChooseCityFragment();
        navigationView.setCheckedItem(R.id.nav_choose_city);
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
                case R.id.nav_auth: {
                    if(account == null) {
                        Intent signInIntent = googleSignInClient.getSignInIntent();
                        MainActivity.this.startActivityForResult(signInIntent, RC_SIGN_IN);
                    } else {
                        googleSignInClient.signOut()
                                .addOnCompleteListener(this, (OnCompleteListener<Void>) task -> {
                                    updateUI(null);
                                    account = null;
                                });
                    }
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
        if(item.getItemId() == R.id.action_curr_location){
           EventBus.getBus().post(new ShowCurrentLocationWeatherEvent());
        }
        return false;
    }
}
