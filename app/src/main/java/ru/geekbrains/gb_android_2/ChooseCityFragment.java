package ru.geekbrains.gb_android_2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
    private ArrayList<HourlyWeatherData> hourlyWeatherList = new ArrayList<>();
    final String myLog = "myLog";
    ChooseCityPresenter chooseCityPresenter = ChooseCityPresenter.getInstance();
    private boolean isErrorShown;
    // Паттерн для проверки, является ли введеное слово названием города.
    Pattern checkEnterCity = Pattern.compile("^[а-яА-ЯЁa-zA-Z]+(?:[\\s-][а-яА-ЯЁa-zA-Z]+)*$");

    static ChooseCityFragment create(CurrentDataContainer container) {
        ChooseCityFragment fragment = new ChooseCityFragment();    // создание

        // Передача параметра
        Bundle args = new Bundle();
        args.putSerializable("currCity", container);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
//        Log.d("Theme", "onCreate - fragment WeatherMainFragment");
        Log.d("myLog", "onCreate - fragment SettingsFragment");
        super.onCreate(savedInstanceState);
    }

    // При создании фрагмента укажем его макет
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("myLog", "onCreateView - fragment SettingsFragment");
        return getView() != null ? getView() :
                inflater.inflate(R.layout.fragment_choose_city, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        checkEnterCityField();
        takeCitiesList();
        setupRecyclerView();// тут создается адаптер на основании citiesList из этого класса ChooseCityFragment (адаптер берет список городов из этого класса)
        setOnBtnOkEnterCityClickListener();
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
                Toast.makeText(requireActivity(), R.string.setOnBtnOkEnterCityToast, Toast.LENGTH_SHORT).show();
            }
            if(!isErrorShown) {
                enterCity.setEnabled(true);
                if (!Objects.requireNonNull(enterCity.getText()).toString().equals("")) {
                    String previousCity = CurrentDataContainer.getInstance().currCityName;
                    currentCity = enterCity.getText().toString();
                    //Создаем прогноз погоды на неделю для нового выбранного города:
                    takeWeatherInfoForFiveDays();
                    if (ChooseCityPresenter.responseCode == 404) {
                        Log.d(myLog, "RESPONSE COD = " + ChooseCityPresenter.responseCode + " CURR CITY = " + currentCity);
                        Toast.makeText(getContext(), "City not found", Toast.LENGTH_LONG).show();
                        currentCity = previousCity;
                        return;
                    }
                    if(ChooseCityPresenter.responseCode == 200) {
                        CurrentDataContainer.getInstance().currCityName = currentCity;
                        Log.d(myLog, "RESPONSE COD = " + ChooseCityPresenter.responseCode + " CURR CITY = " + currentCity);
                        this.weekWeatherData = chooseCityPresenter.getWeekWeatherData();
                        this.hourlyWeatherList = chooseCityPresenter.getHourlyWeatherData();
                        CurrentDataContainer.getInstance().weekWeatherData = this.weekWeatherData;
                        CurrentDataContainer.getInstance().hourlyWeatherList = this.hourlyWeatherList;
                    } if (ChooseCityPresenter.responseCode != 200 ){
                        Log.d(myLog, "RESPONSE COD = " + ChooseCityPresenter.responseCode + " CURR CITY = " + currentCity);
                        Toast.makeText(getContext(), "Fail connection", Toast.LENGTH_LONG).show();
                        currentCity = previousCity;
                        return;
                    }
                    Log.d(myLog, "ChooseCityFragment - setOnBtnOkEnterCityClickListener -> BEFORE flag");
                    //Добавляем новый город в RV
                    adapter.addNewCity(currentCity);
                    Toast.makeText(requireActivity(), currentCity, Toast.LENGTH_SHORT).show();
                    //Обновляем данные погоды, если положение горизонтальное или открываем новое активити, если вертикальное
                    updateWeatherData();
                }
                enterCity.setText("");
            }
        };
        okEnterCity.setOnClickListener(btnOkClickListener);
    }

    private void updateWeatherData(){
        WeatherMainFragment weatherMainFragment = WeatherMainFragment.create(CurrentDataContainer.getInstance());
        FragmentTransaction ft = requireFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentContainer, weatherMainFragment);  // замена фрагмента
        ft.addToBackStack(WeatherMainFragment.class.getSimpleName());
        CurrentDataContainer.backStack.addElement(WeatherMainFragment.class.getSimpleName());
        ft.commit();
        }

    private void takeCitiesList(){
        if(CurrentDataContainer.getInstance().citiesList != null) this.citiesList = CurrentDataContainer.getInstance().citiesList;
    }

    // Обработчик нажатий на город из списка RV
    @Override
    public void onItemClicked(View view, String itemText) {
        Toast.makeText(requireActivity().getBaseContext(), itemText, Toast.LENGTH_SHORT).show();
        currentCity = itemText;
        CurrentDataContainer.getInstance().currCityName = currentCity;

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
            this.hourlyWeatherList = chooseCityPresenter.getHourlyWeatherData();
            CurrentDataContainer.getInstance().weekWeatherData = this.weekWeatherData;
            CurrentDataContainer.getInstance().hourlyWeatherList = this.hourlyWeatherList;
        } else {
            Log.d(myLog, "RESPONSE COD = " + ChooseCityPresenter.responseCode + " CURR CITY = " + currentCity);
            Toast.makeText(getContext(), "Fail connection", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(myLog, "ChooseCityFragment - setOnBtnOkEnterCityClickListener -> BEFORE flag");

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
        if(ChooseCityPresenter.responseCode == 200) CurrentDataContainer.isFirstEnter = false;
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity().getBaseContext());
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
                hideSoftKeyboard(requireActivity(), enterCity);
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