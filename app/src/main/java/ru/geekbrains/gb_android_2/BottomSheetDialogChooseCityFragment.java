package ru.geekbrains.gb_android_2;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import ru.geekbrains.gb_android_2.database.CitiesList;
import ru.geekbrains.gb_android_2.database.CitiesListDao;
import ru.geekbrains.gb_android_2.database.CitiesListSource;
import ru.geekbrains.gb_android_2.events.OpenWeatherMainFragmentEvent;
import ru.geekbrains.gb_android_2.forecastRequest.ForecastRequest;
import ru.geekbrains.gb_android_2.forecastRequest.OpenWeatherMap;

import static android.content.Context.MODE_PRIVATE;

public class BottomSheetDialogChooseCityFragment extends BottomSheetDialogFragment {
    private EditText enterCityEditText;
    private TextView chooseCityTextView;

    static BottomSheetDialogChooseCityFragment newInstance() {
        return new BottomSheetDialogChooseCityFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_dialog, container,
                false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setCancelable(true);

        chooseCityTextView = view.findViewById(R.id.choose_city_textView);
        enterCityEditText = view.findViewById(R.id.enter_city_editText);
        enterCityEditText.setOnKeyListener((view1, keyCode, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                if (enterCityEditText.getText().toString().length() != 0) {
                    checkIsShowingWeatherPossible(enterCityEditText.getText().toString().trim());
                }
                return true;
            }
            return false;
        });
    }

    @SuppressLint("ResourceAsColor")
    private void checkIsShowingWeatherPossible(String cityName){

        OpenWeatherMap openWeatherMap = OpenWeatherMap.getInstance();
        ForecastRequest.getForecastFromServer(cityName);
        Log.d("retrofit", "countDownLatch = " + ForecastRequest.getForecastResponseReceived().getCount());
        Handler handler = new Handler();
        new Thread(() -> {
            try {
                // Ждем, пока не получим актуальный response code:
                ForecastRequest.getForecastResponseReceived().await();

                if(ForecastRequest.responseCode == 200) {
                    // Делаем первую букву заглавной:
                    String newCityName = cityName.substring(0, 1).toUpperCase() + cityName.substring(1);

                    CurrentDataContainer.isFirstEnter = false;
                    CurrentDataContainer.getInstance().weekWeatherData = openWeatherMap.getWeekWeatherData(getResources());
                    CurrentDataContainer.getInstance().hourlyWeatherList = openWeatherMap.getHourlyWeatherData();

                    // Добавляем город в бд:
                    CitiesListDao citiesListDao = App
                                .getInstance()
                                .getCitiesListDao();
                    CitiesListSource citiesListSource = new CitiesListSource(citiesListDao);
                    citiesListSource.addCity(new CitiesList(newCityName));

                    //Запоминаем выбранный город в SharedPreferences
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(MainActivity.SETTINGS, MODE_PRIVATE);
                    saveToPreference(sharedPreferences, newCityName);

                    requireActivity().runOnUiThread(() -> {
                    dismiss();
                    EventBus.getBus().post(new OpenWeatherMainFragmentEvent());
                });
            }
            if(ForecastRequest.responseCode == 404){
                handler.post(()->{
                    enterCityEditText.setText("");
                    chooseCityTextView.setText(R.string.city_not_found);
                    chooseCityTextView.setTextColor(R.color.colorPrimary);
                });
            }
            if(ForecastRequest.responseCode != 404 && ForecastRequest.responseCode != 200){
                handler.post(()->{
                enterCityEditText.setText("");
                chooseCityTextView.setText(R.string.connection_failed);
                chooseCityTextView.setTextColor(R.color.colorPrimary);
                });
            }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void saveToPreference(SharedPreferences preferences, String currentCity) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("current city", currentCity);
        editor.apply();
    }
}
