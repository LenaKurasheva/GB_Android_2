package ru.geekbrains.gb_android_2.rvDataAdapters;

import android.view.View;

public interface RVOnItemClick {
    void onItemClicked(View view, String itemText, int position);
    void onItemLongPressed(View itemText, int position);
}

