package ru.geekbrains.gb_android_2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;


public class SettingsFragment extends Fragment {
    private SwitchCompat nightModeSwitch;
    private SwitchCompat pressureSwitch;
    private SwitchCompat feelsLikeSwitch;
    SettingsPresenter settingsActivityPresenter = SettingsPresenter.getInstance();

    static SettingsFragment create(CurrentDataContainer container) {
        SettingsFragment fragment = new SettingsFragment();    // создание
        // Передача параметра
        Bundle args = new Bundle();
        args.putSerializable("currCity", container);
        fragment.setArguments(args);
        Log.d("myLog", "WeatherMainFragment CREATE");
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("myLog", "onCreate - fragment SettingsFragment");
        super.onCreate(savedInstanceState);
    }

    // При создании фрагмента укажем его макет
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("myLog", "onCreateView - fragment SettingsFragment");
        return getView() != null ? getView() :
                inflater.inflate(R.layout.settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews(view);
        setCurrentSwitchState();
        setOnNightModeSwitchClickListener();
        setOnFeelsLikeSwitchClickListener();
        setOnPressureSwitchClickListener();
        super.onViewCreated(view, savedInstanceState);
    }

    private void initViews(View view) {
        nightModeSwitch = view.findViewById(R.id.night_mode_switch);
        pressureSwitch = view.findViewById(R.id.pressure_switch);
        feelsLikeSwitch = view.findViewById(R.id.feelsLikeSwitch);
    }

    private void setOnNightModeSwitchClickListener(){
        nightModeSwitch.setOnClickListener(view -> {
            Toast.makeText(getContext(), "nightmode is in dev", Toast.LENGTH_SHORT).show();
            settingsActivityPresenter.changeNightModeSwitchStatus();
            saveToPreference(requireActivity().getSharedPreferences(MainActivity.SETTINGS, Context.MODE_PRIVATE),
                    settingsActivityPresenter.getIsNightModeSwitchOn(), "night mode");
            requireActivity().recreate();
        });
    }

    private void setOnPressureSwitchClickListener(){
        pressureSwitch.setOnClickListener(view -> {
            settingsActivityPresenter.changePressureSwitchStatus();
            saveToPreference(requireActivity().getSharedPreferences(MainActivity.SETTINGS, Context.MODE_PRIVATE),
                    settingsActivityPresenter.getIsPressureSwitchOn(), "pressure");
        });
    }

    private void setOnFeelsLikeSwitchClickListener(){
        feelsLikeSwitch.setOnClickListener(view -> {
            settingsActivityPresenter.changeFeelsLikeSwitchStatus();
            saveToPreference(requireActivity().getSharedPreferences(MainActivity.SETTINGS, Context.MODE_PRIVATE),
                    settingsActivityPresenter.getIsFeelsLikeSwitchOn(), "feels like");
        });
    }

    public void setCurrentSwitchState(){
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(MainActivity.SETTINGS, Context.MODE_PRIVATE);
        nightModeSwitch.setChecked(sharedPreferences.getBoolean("night mode" , false));
        pressureSwitch.setChecked(sharedPreferences.getBoolean("pressure" , false));
        feelsLikeSwitch.setChecked(sharedPreferences.getBoolean("feels like" , false));
    }

    private void saveToPreference(SharedPreferences preferences, boolean isChecked, String switchName) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(switchName, isChecked);
        editor.commit();
    }
}
