package ru.geekbrains.gb_android_2;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class ChooseCityActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_city);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Если устройство перевернули в альбомную ориентацию, то надо эту activity закрыть
            finish();
            return;
        }
        if (savedInstanceState == null) {
            // Если эта activity запускается первый раз
            // то перенаправим параметр фрагменту
            ChooseCityFragment chooseCityFragment = new ChooseCityFragment();
            chooseCityFragment.setArguments(getIntent().getExtras());
            // Добавим фрагмент на activity
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.chooseCityFragmentContainer, chooseCityFragment)
                    .commit();
        }
    }
}
