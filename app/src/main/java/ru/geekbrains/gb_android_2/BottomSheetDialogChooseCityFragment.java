package ru.geekbrains.gb_android_2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.net.MalformedURLException;

import ru.geekbrains.gb_android_2.events.OpenWeatherMainFragmentEvent;
import ru.geekbrains.gb_android_2.forecastRequest.ForecastRequest;
import ru.geekbrains.gb_android_2.forecastRequest.OpenWeatherMap;

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
        try {
            ForecastRequest.getInstance().getForecastFromServer(cityName, openWeatherMap.getWeatherUrl(cityName));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if(ForecastRequest.responseCode == 200){
            CurrentDataContainer.isFirstEnter = false;
            CurrentDataContainer.getInstance().weekWeatherData = openWeatherMap.getWeekWeatherData(getResources());
            CurrentDataContainer.getInstance().hourlyWeatherList = openWeatherMap.getHourlyWeatherData();
            CurrentDataContainer.getInstance().currCityName = cityName;
            CurrentDataContainer.getInstance().citiesList.add(0, cityName);
            dismiss();
            EventBus.getBus().post(new OpenWeatherMainFragmentEvent());
        }
        if(ForecastRequest.responseCode == 404){
            enterCityEditText.setText("");
            chooseCityTextView.setText(R.string.city_not_found);
            chooseCityTextView.setTextColor(R.color.colorPrimary);
        }
        if(ForecastRequest.responseCode != 404 && ForecastRequest.responseCode != 200){
            enterCityEditText.setText("");
            chooseCityTextView.setText(R.string.connection_failed);
            chooseCityTextView.setTextColor(R.color.colorPrimary);
        }

    }
}
