package ru.geekbrains.gb_android_2;

public class SettingsPresenter {
    //Внутреннее поле, будет хранить единственный экземпляр
    private static SettingsPresenter instance = null;

    // Поле для синхронизации
    private static final Object syncObj = new Object();
    private boolean isNightModeSwitchOn;
    private boolean isPressureSwitchOn;
    private boolean isFeelsLikeSwitchOn;

    private SettingsPresenter(){}

    public void changeFeelsLikeSwitchStatus(){
       isFeelsLikeSwitchOn = !isFeelsLikeSwitchOn;
    }

    public boolean getIsNightModeSwitchOn(){return isNightModeSwitchOn;}
    public boolean getIsPressureSwitchOn(){return isPressureSwitchOn;}
    public boolean getIsFeelsLikeSwitchOn(){return isFeelsLikeSwitchOn;}

    public void changeNightModeSwitchStatus(){
        isNightModeSwitchOn = !isNightModeSwitchOn;
    }

    public void changePressureSwitchStatus(){
        isPressureSwitchOn = !isPressureSwitchOn;
    }

    // Метод, который возвращает экземпляр объекта.
    // Если объекта нет, то создаем его.
    public static SettingsPresenter getInstance(){
        // Здесь реализована «ленивая» инициализация объекта,
        // то есть, пока объект не нужен, не создаем его.
        synchronized (syncObj) {
            if (instance == null) {
                instance = new SettingsPresenter();
            }
            return instance;
        }
    }
}
