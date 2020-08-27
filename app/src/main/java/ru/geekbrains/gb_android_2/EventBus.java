package ru.geekbrains.gb_android_2;

import com.squareup.otto.Bus;

public class EventBus {
    private static Bus bus;

    static Bus getBus() {
        if(bus == null) {
            bus = new Bus();
        }
        return bus;
    }
}

