package ru.geekbrains.gb_android_2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;


public class ChooseCityFragment extends Fragment implements RVOnItemClick {

    private TextInputEditText enterCity;
    private Button okEnterCity;
    static String currentCity = "";
    private RecyclerView recyclerView;
    private CitiesRecyclerDataAdapter adapter;
    private ArrayList<String> citiesList = new ArrayList<>();
    private ArrayList<WeatherData> weekWeatherData = new ArrayList<>();
    final String myLog = "myLog";
    private boolean isLandscape;
    ChooseCityPresenter chooseCityPresenter = ChooseCityPresenter.getInstance();
    private boolean isErrorShown;
    // Паттерн для проверки, является ли введеное слово названием города.
    Pattern checkEnterCity = Pattern.compile("^[а-яА-ЯЁa-zA-Z]+(?:[\\s-][а-яА-ЯЁa-zA-Z]+)*$");
    private boolean weatherCreated;

    static ChooseCityFragment create(CurrentDataContainer container) {
        ChooseCityFragment fragment = new ChooseCityFragment();    // создание

        // Передача параметра
        Bundle args = new Bundle();
        args.putSerializable("currCity", container);
        fragment.setArguments(args);
        return fragment;
    }

    // Получить текущий город из параметра
    String getCurrentCity() {return currentCity; }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
//        Log.d("Theme", "ChooseCityFragment - onCreate");
        isLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        Log.d("myLog", "ChooseCityFragment - onCreate");
        if (isLandscape) {
            Log.d("Theme", " ChooseCity it IsLandscape");
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
                Log.d("Theme", " ChooseCity AppTheme");
                Objects.requireNonNull(getActivity()).setTheme(R.style.AppTheme);
            }
        }
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Указывает на то, что у фрагмента будет меню
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_choose_city, container, false);
        // Добавляем стрелку назад в меню:
        if(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar() != null)
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        return rootView;
    }

    // Создаем меню на основе макета
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_menu, menu);
    }

    // Устанавливаем слушатель для элементов меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            updateWeatherData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        checkIsPositionLandscape();
        changeThemeIfNecessary();
        checkEnterCityField();
        takeCitiesList();
        setupRecyclerView();// тут создается адаптер на основании citiesList из этого класса ChooseCityFragment (адаптер берет список городов из этого класса)
        setOnBtnOkEnterCityClickListener();
    }

    private void checkIsPositionLandscape(){
        isLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }

    private void changeThemeIfNecessary() {
        Log.d(myLog, "***********NightMode " + CurrentDataContainer.isNightModeOn);
        Log.d(myLog, "************NightIsAlreadySettedInChoose " + CurrentDataContainer.NightIsAlreadySettedInChoose );
        if (CurrentDataContainer.isNightModeOn && !CurrentDataContainer.NightIsAlreadySettedInChoose) {
            //TODO
            CurrentDataContainer.NightIsAlreadySettedInChoose = true;
            Objects.requireNonNull(getActivity()).recreate();
            Log.d(myLog, "RECREATE weather main fragment");
        }
    }

    private void initViews(View view) {
        enterCity = view.findViewById(R.id.enterCity);
        okEnterCity = view.findViewById(R.id.okEnterCity);
        recyclerView = view.findViewById(R.id.cities);
    }

    private void setOnBtnOkEnterCityClickListener() {
        View.OnClickListener btnOkClickListener = view -> {
            // "Выклчаем" editText, чтобы убрать с него фокус и дать возмоность показать ошибку:
            enterCity.setEnabled(false);
            // Если ошибка показалась, "включаем" его обратно, чтобы дать поьзователю исправить ошибку:
            if(isErrorShown) {
                enterCity.setEnabled(true);
                Toast.makeText(Objects.requireNonNull(getActivity()), R.string.setOnBtnOkEnterCityToast, Toast.LENGTH_SHORT).show();
            }
            if(!isErrorShown) {
                enterCity.setEnabled(true);
                if (!Objects.requireNonNull(enterCity.getText()).toString().equals("")) {
                    currentCity = enterCity.getText().toString();

                    //Создаем прогноз погоды на неделю для нового выбранного города:
                    takeWeatherInfoForFiveDays();
                    if (ChooseCityPresenter.responseCode == 404) {
                        Log.d(myLog, "RESPONSE COD = " + ChooseCityPresenter.responseCode + " CURR CITY = " + currentCity);
                        Toast.makeText(getContext(), "City not found", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(ChooseCityPresenter.responseCode == 200) {
                        Log.d(myLog, "RESPONSE COD = " + ChooseCityPresenter.responseCode + " CURR CITY = " + currentCity);

                        this.weekWeatherData = chooseCityPresenter.getWeekWeatherData();
                        weatherCreated = true;
                    } if (ChooseCityPresenter.responseCode != 200 ){
                        Log.d(myLog, "RESPONSE COD = " + ChooseCityPresenter.responseCode + " CURR CITY = " + currentCity);

                        Toast.makeText(getContext(), "Fail connection", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Log.d(myLog, "ChooseCityFragment - setOnBtnOkEnterCityClickListener -> BEFORE flag -> weatherCreated: " + weatherCreated);

                    //Добавляем новый город в RV
                    adapter.addNewCity(currentCity);

                    Toast.makeText(Objects.requireNonNull(getActivity()), currentCity, Toast.LENGTH_SHORT).show();
                    //Обновляем данные погоды, если положение горизонтальное или открываем новое активити, если вертикальное
                    updateWeatherData();
                }
                enterCity.setText("");
            }
        };
        okEnterCity.setOnClickListener(btnOkClickListener);
    }

    private void updateWeatherData(){
        if(isLandscape) {
            chooseCityPresenter.updateWeatherInLandscape(getCurrentDataContainer(), Objects.requireNonNull(getFragmentManager()));
        } else {
            Intent intent = new Intent();
            intent.setClass(Objects.requireNonNull(getActivity()), MainActivity.class);
            // и передадим туда параметры
            intent.putExtra("currCity", getCurrentDataContainer());
            startActivity(intent);
            getActivity().finish();
        }
    }

    public CurrentDataContainer getCurrentDataContainer() {
        CurrentDataContainer container = new CurrentDataContainer();
        container.currCityName = currentCity;

        container.citiesList = adapter.getCitiesList();
        if (!isLandscape) {
            CurrentDataContainer currentDataCont = (CurrentDataContainer) Objects.requireNonNull(getActivity()).getIntent().getSerializableExtra("currCity");
            boolean[] switchSettingsArray = Objects.requireNonNull(currentDataCont).switchSettingsArray;
            if (switchSettingsArray != null) {
                container.switchSettingsArray = switchSettingsArray;
                Log.d(myLog, "CHOOSE CITY FRAGMENT: getCurrentDataContainer(); !isLandscape; switchSettingsArray != null");
            }
            if (weatherCreated){
                container.weekWeatherData = weekWeatherData;
                weatherCreated = false;
            } else {
                ArrayList<WeatherData> list = Objects.requireNonNull(currentDataCont).weekWeatherData;
                if (list != null && list.size() != 0) container.weekWeatherData = list;
            }
            container.citiesList = this.citiesList;
        } else {
            CurrentDataContainer currentCityContainer = (CurrentDataContainer) Objects.requireNonNull(getArguments()).getSerializable("currCity");
            if (currentCityContainer != null) {
                container.switchSettingsArray = currentCityContainer.switchSettingsArray;
                if (weatherCreated) {
                    Log.d(myLog, "ChooseCityFragment - getCurrentDataContainer -> land -> weatherCreated true");
                    container.weekWeatherData = this.weekWeatherData;
                    weatherCreated = false;
                }
            }
            Log.d(myLog, "CHOOSE CITY FRAGMENT: getCurrentDataContainer(); else; currentCityContainer != null");
        }
        CurrentDataContainer.NightIsAlreadySettedInChoose = false;
        return container;
    }

//    private void updateCitiesList() {
    private void takeCitiesList(){
        if (!isLandscape) {
            Log.d(myLog, "ChooseCityFragment; updateCitiesList; !isLandscape");
            if(Objects.requireNonNull(getActivity()).getIntent().getSerializableExtra("currCity") != null) {
                CurrentDataContainer currentCityContainer = (CurrentDataContainer) Objects.requireNonNull(getActivity()).getIntent().getSerializableExtra("currCity");
                if(Objects.requireNonNull(currentCityContainer).citiesList != null && currentCityContainer.citiesList.size() > 0) {
                    this.citiesList = currentCityContainer.citiesList;
                    Log.d(myLog, "ChooseCityFragment; updateCitiesList; citieslist = " + citiesList.toString());
//                    adapter.refreshCitiesList(currentCityContainer.citiesList);
                }
                if(Objects.requireNonNull(currentCityContainer.citiesList).size() == 0 ){
                    this.citiesList = currentCityContainer.citiesList;
//                    adapter.refreshCitiesList(currentCityContainer.citiesList);
                }
            }
        } else {
            if (Objects.requireNonNull(getArguments()).getSerializable("currCity") != null) {
                CurrentDataContainer currentCityContainer = (CurrentDataContainer) Objects.requireNonNull(getArguments()).getSerializable("currCity");
                if (currentCityContainer != null && currentCityContainer.citiesList.size() > 0) {
                        this.citiesList = currentCityContainer.citiesList;
//                        adapter.refreshCitiesList(currentCityContainer.citiesList);
                }
                if(Objects.requireNonNull(currentCityContainer).citiesList.size() == 0 ){
                    this.citiesList = currentCityContainer.citiesList;
//                    adapter.refreshCitiesList(currentCityContainer.citiesList);
                }
            }
        }
        Log.d(myLog, "ChooseCityFragment - takeCitiesList() " + citiesList.toString());
    }

    // Обработчик нажатий на город из списка RV
    @Override
    public void onItemClicked(View view, String itemText) {
        Toast.makeText(Objects.requireNonNull(getActivity()).getBaseContext(), itemText, Toast.LENGTH_SHORT).show();
        currentCity = itemText;

//          Ставим выбранный город на первое место в коллекции:
        adapter.putChosenCityToTopInCitiesList(currentCity);

        //Создаем прогноз погоды на неделю для нового выбранного города:
        takeWeatherInfoForFiveDays();
        if (ChooseCityPresenter.responseCode == 404) {
            Log.d(myLog, "RESPONSE COD = " + ChooseCityPresenter.responseCode + " CURR CITY = " + currentCity);
            Toast.makeText(getContext(), "City not found", Toast.LENGTH_LONG).show();
            return;
        }
        if(ChooseCityPresenter.responseCode == 200) {
            Log.d(myLog, "RESPONSE COD = " + ChooseCityPresenter.responseCode + " CURR CITY = " + currentCity);
            this.weekWeatherData = chooseCityPresenter.getWeekWeatherData();
            weatherCreated = true;
        } else {
            Log.d(myLog, "RESPONSE COD = " + ChooseCityPresenter.responseCode + " CURR CITY = " + currentCity);
            Toast.makeText(getContext(), "Fail connection", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(myLog, "ChooseCityFragment - setOnBtnOkEnterCityClickListener -> BEFORE flag -> weatherCreated: " + weatherCreated);

        //Обновляем данные погоды, если положение горизонтальное или открываем новое активити, если вертикальное
        updateWeatherData();
    }

    @Override
    public void onItemLongPressed(View view) {
        TextView textView = (TextView) view;
        deleteItem(textView);
    }

    public void deleteItem(final TextView view) {
        Snackbar.make(view, R.string.delete_city, Snackbar.LENGTH_LONG)
                .setAction(R.string.delete, v -> {
                    String cityName = view.getText().toString();
                    adapter.remove(cityName);
                    citiesList.remove(cityName);
                }).show();
    }

    private void takeWeatherInfoForFiveDays(){
        chooseCityPresenter.getFiveDaysWeatherFromServer(currentCity, getResources());
        CurrentDataContainer.isFirstEnter = false;
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(Objects.requireNonNull(getActivity()).getBaseContext());
        adapter = new CitiesRecyclerDataAdapter(citiesList, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void checkEnterCityField() {
        final TextView[] tv = new TextView[1];
        enterCity.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                tv[0] = (TextView) v;
                // Валидация, почти точно такая же, как и в поле логина
                validate(tv[0], checkEnterCity, getString(R.string.HintTextInputEditText));
                hideSoftKeyboard(Objects.requireNonNull(getActivity()), enterCity);
            }
        });
    }

    public static void hideSoftKeyboard (Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    private void validate(TextView tv, Pattern check, String message){
        String value = tv.getText().toString();
        if (check.matcher(value).matches()) {    // Проверим на основе регулярных выражений
            hideError(tv);
            isErrorShown = false;
        } else {
            showError(tv, message);
            isErrorShown = true;
        }
    }

    // Показать ошибку
    private void showError(TextView view, String message) {
        view.setError(message);
    }

    // спрятать ошибку
    private void hideError(TextView view) {
        view.setError(null);
    }
}