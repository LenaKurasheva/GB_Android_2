package ru.geekbrains.gb_android_2;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class WeatherMainFragment extends Fragment implements RVOnItemClick {
    private boolean isLandscape;  // Можно ли расположить рядом фрагмент с выбором города
    public static String currentCity = "";
    private ImageButton settingsButton, locationButton, readMoreButton;
    private TextView cityTextView;
    private TextView degrees;
    private TextView feelsLikeTextView, pressureInfoTextView;
    final String myLog = "myLog";
    private RecyclerView weatherRecyclerView;
    private List<Integer> weatherIcon = new ArrayList<>();
    private List<String> days = new ArrayList<>();
    private List<String> daysTemp = new ArrayList<>();
    private TextView windInfoTextView;
    private TextView currTime;
    private TextView weatherStatusTextView;
    private ArrayList<String> citiesListFromRes;
    private ArrayList<String> citiesList;
    private ArrayList<WeatherData> weekWeatherData;


    static WeatherMainFragment create(CurrentDataContainer container) {
        WeatherMainFragment fragment = new WeatherMainFragment();    // создание
        // Передача параметра
        Bundle args = new Bundle();
        args.putSerializable("currCity", container);
        fragment.setArguments(args);
        Log.d("myLog", "WeatherMainFragment CREATE");
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
//        Log.d("Theme", "onCreate - fragment WeatherMainFragment");
        Log.d("myLog", "onCreate - fragment WeatherMainFragment");
        // В этот блок мы заходим, только когда пересоздаем активити
        isLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        if (isLandscape) {
            Log.d("Theme", " it IsLandscape");
            if (CurrentDataContainer.isNightModeOn) {
                Objects.requireNonNull(getActivity()).setTheme(R.style.NoToolbarDarkTheme);
            }
            if (!CurrentDataContainer.isNightModeOn) {
                Log.d("Theme", "NoToolbarTheme");
                Objects.requireNonNull(getActivity()).setTheme(R.style.NoToolbarTheme);
            }

        } else {
            if (CurrentDataContainer.isNightModeOn) {
                Objects.requireNonNull(getActivity()).setTheme(R.style.AppThemeDark);
            } else {
                Log.d("Theme", "AppTheme");
                Objects.requireNonNull(getActivity()).setTheme(R.style.AppTheme);
            }
        }
        super.onCreate(savedInstanceState);
    }

    // При создании фрагмента укажем его макет
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("myLog", "onCreateView - fragment WeatherMainFragment");
        return getView() != null ? getView() :
                inflater.inflate(R.layout.fragment_weather_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews(view);
        // Проверяем ориентацию экрана и в случае альбомной, меняем расположение элементов:
        moveViewsIfLandscapeOrientation(view);
        setOnLocationBtnOnClick();
        setOnSettingsBtnOnClick();
        setOnReadMoreBtnOnClick();
        takeCitiesListFromResources(getResources());
        generateDaysList();
        addDataToWeatherIconsIdFromRes();
        addDefaultDataToDaysTempFromRes(getResources());
        updateWeatherInfo(getResources()); //здесь забрали citiesList
        setupRecyclerView();
        super.onViewCreated(view, savedInstanceState);
    }

    // activity создана, можно к ней обращаться. Выполним начальные действия
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Определение, можно ли будет расположить рядом выбор города в другом фрагменте
        isLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        // Если можно нарисовать рядом выбор города, то сделаем это
        Log.d(myLog, "WeatherMainFragment: onActivityCreated !BEFORE updateChosenCity, currentCity: " + currentCity);
        isLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        Log.d("myLog", "Orientation land? : " + isLandscape);

        if (isLandscape) showChooseCityFragment(getCurrentDataContainer());

        Log.d(myLog, "WeatherMainFragment - savedInstanceState exists = " + (savedInstanceState != null));
        updateChosenCity(savedInstanceState);
        takeWeatherInfoForFirstEnter();
        Log.d(myLog, "WeatherMainFragment: onActivityCreated !AFTER updateChosenCity, currentCity: " + currentCity);
    }

    private void takeWeatherInfoForFirstEnter(){
        if(CurrentDataContainer.isFirstEnter){
            Log.d(myLog, "*FIRST ENTER*");
            ChooseCityPresenter chooseCityPresenter = ChooseCityPresenter.getInstance();
            chooseCityPresenter.getFiveDaysWeatherFromServer(currentCity, getResources());
            this.weekWeatherData = chooseCityPresenter.getWeekWeatherData();
            updateWeatherInfo(getResources());
            Log.d(myLog, "takeWeatherInfoForFirstEnter - after updateWeatherInfo;  CITIES LIST = "+ citiesList.toString());
            setupRecyclerView();
        } else {
            Log.d(myLog, "*NOT FIRST ENTER*");
        }
    }

    private void moveViewsIfLandscapeOrientation( View view){
        // Проверяем ориентацию экрана и в случае альбомной, меняем расположение элементов:
        isLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        if (isLandscape) {
            ConstraintLayout constraintLayout = view.findViewById(R.id.full_screen_constraintlayout);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.setVerticalBias(R.id.center, 0.67f);
            constraintSet.connect(R.id.degrees,ConstraintSet.BOTTOM,R.id.center,ConstraintSet.TOP,0);
            constraintSet.setVisibility(R.id.weekWeatherRV, View.GONE);
            constraintSet.setVisibility(R.id.locationButton, View.INVISIBLE);
            constraintSet.setHorizontalBias(R.id.readMoreButton, 0.1f);
            constraintSet.applyTo(constraintLayout);
        }
    }

    private void initViews(View view) {
        settingsButton = view.findViewById(R.id.settingsBottom);
        locationButton = view.findViewById(R.id.locationButton);
        cityTextView = view.findViewById(R.id.city);
        degrees = view.findViewById(R.id.degrees);
        feelsLikeTextView = view.findViewById(R.id.feelsLikeTextView);
        pressureInfoTextView = view.findViewById(R.id.pressureInfoTextView);
        readMoreButton = view.findViewById(R.id.readMoreButton);
        weatherRecyclerView = view.findViewById(R.id.weekWeatherRV);
        windInfoTextView = view.findViewById(R.id.windSpeed);
        currTime = view.findViewById(R.id.currTime);
        weatherStatusTextView = view.findViewById(R.id.cloudyInfoTextView);
    }

    private void setOnLocationBtnOnClick(){
        locationButton.setOnClickListener(view -> {
            showChooseCityFragment(getCurrentDataContainer());
            Objects.requireNonNull(getActivity()).finish();
        });
    }

    private void setOnSettingsBtnOnClick() {
        settingsButton.setOnClickListener(view -> {
            Intent intent = new Intent(Objects.requireNonNull(getActivity()), SettingsActivity.class);
            intent.putExtra("currCity", getCurrentDataContainer());
            startActivity(intent);
            getActivity().finish();
        });
    }

    private void setOnReadMoreBtnOnClick() {
        readMoreButton.setOnClickListener(view -> {
            String wiki = "https://ru.wikipedia.org/wiki/" + currentCity;
            Uri uri = Uri.parse(wiki);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
    }

    // Показать выбор города. Ecли возможно, то показать рядом с погодой,
    // если нет, то открыть вторую activity
    private void showChooseCityFragment(CurrentDataContainer cdc) {
        if (isLandscape) {
            Log.d("Theme", "WeatherMainFr - showChooseCityFragment - isLandscape");
            // Проверим, что фрагмент с выбором города существует в activity
            ChooseCityFragment chooseCityFragment = (ChooseCityFragment)
                    Objects.requireNonNull(getFragmentManager()).findFragmentById(R.id.chooseCity);

            // Если есть необходимость, то выведем выбор города
            if (chooseCityFragment == null || !chooseCityFragment.getCurrentCity().equals(currentCity)) {
                Log.d("Theme", "WeatherMainFr - showChooseCityFragment - isLandscape -> создаем фрагмент ChoseCityFragment рядом в гориз.режиме");

                Log.d(myLog, "WeatherMainFragment -> showChoseCityFRagment -> создаем фрагмент ChoseCityFragment рядом в гориз.режиме");
                // Создаем новый фрагмент с текущей позицией для города
                chooseCityFragment = ChooseCityFragment.create(cdc);

                // Выполняем транзакцию по замене фрагмента
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.chooseCity, chooseCityFragment);  // замена фрагмента
                ft.commit();
            }
        } else {
            // Если нельзя вывести выбор города рядом, откроем вторую activity
            Intent intent = new Intent();
            intent.setClass(Objects.requireNonNull(getActivity()), ChooseCityActivity.class);
            // и передадим туда параметры
            intent.putExtra("currCity", cdc);
            startActivity(intent);
        }
    }

    public CurrentDataContainer getCurrentDataContainer() {
        Log.d(myLog, "WeatherMainFragment - getCurrentDataContainer() ");
        CurrentDataContainer container = new CurrentDataContainer();
        container.currCityName = currentCity;
        container.citiesList = this.citiesList;
        Log.d(myLog, "WeatherMainFragment - getCurrentDataContainer() - положили в citiesList:" + this.citiesList.toString());
        if (!isLandscape) {
            CurrentDataContainer cdc = (CurrentDataContainer) Objects.requireNonNull(getActivity()).getIntent().getSerializableExtra("currCity");
            if(cdc != null) {
                boolean[] switchSettingsArray = cdc.switchSettingsArray;
                if (switchSettingsArray != null) {
                    container.switchSettingsArray = switchSettingsArray;
                    Log.d(myLog, "WeatherMainFragment: getCurrentDataContainer(); !isLandscape; switchSettingsArray != null");
                }
                weekWeatherData = cdc.weekWeatherData;
                if (weekWeatherData != null && weekWeatherData.size() != 0) container.weekWeatherData = weekWeatherData;
            }
            CurrentDataContainer.NightIsAlreadySettedInMain = false;
        } else {
            if (getArguments() != null && getArguments().getSerializable("currCity") != null) {
                CurrentDataContainer currentCityContainer = (CurrentDataContainer) getArguments().getSerializable("currCity");
                if (currentCityContainer != null) {
                    container.switchSettingsArray = currentCityContainer.switchSettingsArray;
                    weekWeatherData = currentCityContainer.weekWeatherData;
                    if (weekWeatherData != null && weekWeatherData.size() != 0) container.weekWeatherData = weekWeatherData;
                }
                Log.d(myLog, "WeatherMainFragment: getCurrentDataContainer(); else; currentCityContainer != null");
            }
        }
        if(getArguments() == null && Objects.requireNonNull(getActivity()).getIntent().getSerializableExtra("currCity") == null) container.weekWeatherData = weekWeatherData;
        return container;
    }

    private String getCityName() {
        currentCity = citiesListFromRes.get(0);
        Log.d(myLog, "WeatherMainFragment - updateChosenCity() -> getCityName() fromRes: " + currentCity);

        if (getArguments() != null) {
            CurrentDataContainer currentCityContainer = (CurrentDataContainer) getArguments().getSerializable("currCity");
            if (currentCityContainer != null) {
                currentCity = currentCityContainer.currCityName;
                Log.d(myLog, "WeatherMainFragment - updateChosenCity() -> getCityName() fromArguments: " + currentCity);
            }
        } else {

            if (Objects.requireNonNull(getActivity()).getIntent().getSerializableExtra("currCity") != null) {
                CurrentDataContainer currentCityContainer = (CurrentDataContainer) getActivity().getIntent().getSerializableExtra("currCity");
                currentCity = Objects.requireNonNull(currentCityContainer).currCityName;
                Log.d(myLog, "WeatherMainFragment - updateChosenCity() -> getCityName() fromIntent: " + currentCity);
            }
        }
        return currentCity;
    }

    private void updateChosenCity(Bundle savedInstanceState) {
        if (savedInstanceState == null) cityTextView.setText(getCityName());
        cityTextView.setText(currentCity);
    }

    private  void updateWeatherInfo(Resources resources){
        this.citiesList = citiesListFromRes;
        if(CurrentDataContainer.isFirstEnter) {
            if(ChooseCityPresenter.responseCode != 200) {
                Log.d(myLog, "updateWeatherInfo from resources");

                degrees.setText("+0°");

                String windInfoFromRes = resources.getString(R.string.windInfo);
                windInfoTextView.setText(String.format(windInfoFromRes, "0"));

                Date currentDate = new Date();
                DateFormat timeFormat = new SimpleDateFormat("E, HH:mm", Locale.getDefault());
                String timeText = timeFormat.format(currentDate);
                currTime.setText(timeText);
                Log.d(myLog, "WEatherMainFragment - updateWeatherInfo - FIRSTENTER; responseCode != 200; CITIES LIST = " + citiesList.toString());
            } else {
                setNewWeatherData(weekWeatherData);
                Log.d(myLog, "WEatherMainFragment - updateWeatherInfo - FIRSTENTER; responseCode == 200; CITIES LIST = " + citiesList.toString());
            }
        }

        boolean[] settingsSwitchArray;
        if (getArguments() != null) {
            CurrentDataContainer currentDataContainer = (CurrentDataContainer) getArguments().getSerializable("currCity");
            if (currentDataContainer != null) {
                currentCity = currentDataContainer.currCityName;
                settingsSwitchArray = currentDataContainer.switchSettingsArray;
                weekWeatherData = currentDataContainer.weekWeatherData;
                citiesList = currentDataContainer.citiesList;
                isSettingsSwitchArrayTransferred(settingsSwitchArray);
                if(weekWeatherData != null) Log.d(myLog, "WeatherMainFragment - updateWeatherInfo -> from Arguments; curr temp = "+weekWeatherData.get(0).degrees);
                setNewWeatherData(weekWeatherData);
            }
        } else {
            if (Objects.requireNonNull(getActivity()).getIntent() != null) {
                CurrentDataContainer cdc = (CurrentDataContainer) getActivity().getIntent().getSerializableExtra("currCity");
                if (cdc != null) {
                    Log.d(myLog, "WeatherMainFragment - updateWeatherInfo -> from Intent");
                    currentCity = cdc.currCityName;
                    settingsSwitchArray = cdc.switchSettingsArray;
                    weekWeatherData = cdc.weekWeatherData;
                    citiesList = cdc.citiesList;
                    isSettingsSwitchArrayTransferred(settingsSwitchArray);
                    setNewWeatherData(weekWeatherData);
                }
            }
        }
    }

    private void isSettingsSwitchArrayTransferred(boolean[] settingsSwitchArray){
        Log.d(myLog, "NightIsAlreadySettedInMain " + CurrentDataContainer.NightIsAlreadySettedInMain );
        Log.d(myLog, "NightMode " + CurrentDataContainer.isNightModeOn);
        if(settingsSwitchArray != null) {
            if (settingsSwitchArray[0] && !CurrentDataContainer.NightIsAlreadySettedInMain) {
                CurrentDataContainer.NightIsAlreadySettedInMain = true;
                CurrentDataContainer.isNightModeOn = true;
                Objects.requireNonNull(getActivity()).recreate();
                Log.d(myLog, " RECREATE weather main fragment");
            }
            if (!settingsSwitchArray[0]) CurrentDataContainer.isNightModeOn = false;
            if (settingsSwitchArray[1]) feelsLikeTextView.setVisibility(View.VISIBLE);
            if (settingsSwitchArray[2]) pressureInfoTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setNewWeatherData(ArrayList<WeatherData> weekWeatherData) {
        if (weekWeatherData != null && weekWeatherData.size() != 0) {
            WeatherData wd = weekWeatherData.get(0);
            degrees.setText(wd.degrees);
            windInfoTextView.setText(wd.windInfo);

            Date currentDate = new Date();
            DateFormat timeFormat = new SimpleDateFormat("E, HH:mm", Locale.getDefault());
            String timeText = timeFormat.format(currentDate);
            currTime.setText(timeText);

            weatherStatusTextView.setText(wd.weatherStateInfo);
            pressureInfoTextView.setText(wd.pressure);
            feelsLikeTextView.setText(wd.feelLike);

            for (int i = 0; i < ChooseCityPresenter.FORECAST_DAYS; i++) {
               WeatherData weatherData = weekWeatherData.get(i);
               daysTemp.set(i, weatherData.degrees);
                String imageName =weatherData.weatherIcon;
                Log.d(myLog, "ICON " + i + " " +  imageName);
                Integer resID = getResources().getIdentifier(imageName , "drawable", Objects.requireNonNull(getActivity()).getPackageName());
                weatherIcon.set(i, resID);
            }
        }
    }

    public void takeCitiesListFromResources(android.content.res.Resources resources){
        String[] cities = resources.getStringArray(R.array.cities);
        List<String> cit = Arrays.asList(cities);
        citiesListFromRes = new ArrayList<>(cit);
    }

    public void generateDaysList(){
        Date currentDate = new Date();
        DateFormat timeFormat = new SimpleDateFormat("E", Locale.getDefault());
        String curDay = timeFormat.format(currentDate);
        Log.d(myLog, "CURDAY = " + curDay);
        ArrayList<String> daysList = new ArrayList<>();
        daysList.add(curDay);
        for (int i = 1; i <ChooseCityPresenter.FORECAST_DAYS ; i++) {
            Calendar instance = Calendar.getInstance(Locale.getDefault());
            instance.add(Calendar.DAY_OF_MONTH, i); // прибавляем 1 день к установленной дате
            Date nextDate = instance.getTime(); // получаем измененную дату
            String nextDay = timeFormat.format(nextDate);
            daysList.add(nextDay);
        }
        Log.d(myLog, "WEEK: "+ daysList.toString());
        days = daysList;
    }

    public void addDefaultDataToDaysTempFromRes(android.content.res.Resources resources){
        String[] daysTempStringArr = resources.getStringArray(R.array.daysTemp);
        daysTemp  = Arrays.asList(daysTempStringArr);
    }

    public void addDataToWeatherIconsIdFromRes(){
        weatherIcon.add(R.drawable.clear_sky_day);
        weatherIcon.add(R.drawable.few_clouds_day);
        weatherIcon.add(R.drawable.scattered_clouds);
        weatherIcon.add(R.drawable.broken_clouds);
        weatherIcon.add(R.drawable.shower_rain);
        weatherIcon.add(R.drawable.rain_day);
        weatherIcon.add(R.drawable.thunderstorm);
        weatherIcon.add(R.drawable.snow);
        weatherIcon.add(R.drawable.mist);
    }

    @Override
    public void onItemClicked(View view, String itemText) {

    }

    @Override
    public void onItemLongPressed(View itemText) {
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(Objects.requireNonNull(getActivity()).getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
        WeekWeatherRecyclerDataAdapter weekWeatherAdapter = new WeekWeatherRecyclerDataAdapter(days, daysTemp, weatherIcon, this);

        weatherRecyclerView.setLayoutManager(layoutManager);
        weatherRecyclerView.setAdapter(weekWeatherAdapter);
    }
}