package ru.geekbrains.gb_android_2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class SettingsFragment extends Fragment {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch nightModeSwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch pressureSwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch feelsLikeSwitch;
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
            settingsActivityPresenter.createSettingsSwitchArray();
            CurrentDataContainer.getInstance().switchSettingsArray = settingsActivityPresenter.getSettingsArray();
            requireActivity().recreate();
        });
    }

    private void setOnPressureSwitchClickListener(){
        pressureSwitch.setOnClickListener(view -> {
            settingsActivityPresenter.changePressureSwitchStatus();
            settingsActivityPresenter.createSettingsSwitchArray();
            CurrentDataContainer.getInstance().switchSettingsArray = settingsActivityPresenter.getSettingsArray();
        });
    }

    private void setOnFeelsLikeSwitchClickListener(){
        feelsLikeSwitch.setOnClickListener(view -> {
            settingsActivityPresenter.changeFeelsLikeSwitchStatus();
            settingsActivityPresenter.createSettingsSwitchArray();
            CurrentDataContainer.getInstance().switchSettingsArray = settingsActivityPresenter.getSettingsArray();
        });
    }

    public void setCurrentSwitchState(){
       boolean[] switchArr =  settingsActivityPresenter.getSettingsArray();
       if(switchArr != null){
           nightModeSwitch.setChecked(switchArr[0]);
           feelsLikeSwitch.setChecked(switchArr[1]);
           pressureSwitch.setChecked(switchArr[2]);
       }
    }
}
