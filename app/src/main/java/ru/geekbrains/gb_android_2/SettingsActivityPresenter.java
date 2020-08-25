package ru.geekbrains.gb_android_2;

public class SettingsActivityPresenter {
    //Внутреннее поле, будет хранить единственный экземпляр
    private static SettingsActivityPresenter instance = null;

    // Поле для синхронизации
    private static final Object syncObj = new Object();
    private boolean isNightModeSwitchOn;
    private boolean isPressureSwitchOn;
    private boolean isFeelsLikeSwitchOn;
    private boolean[] settingsArray;

    private SettingsActivityPresenter(){}

    public boolean[] getSettingsArray(){return settingsArray;}

    public void changeFeelsLikeSwitchStatus(){
       isFeelsLikeSwitchOn = !isFeelsLikeSwitchOn;
    }

    public boolean getIsNightModeSwitchOn(){return isNightModeSwitchOn;}

    public void changeNightModeSwitchStatus(){
        isNightModeSwitchOn = !isNightModeSwitchOn;
    }

    public void changePressureSwitchStatus(){
        isPressureSwitchOn = !isPressureSwitchOn;
    }

    public boolean[] createSettingsSwitchArray(){
        settingsArray =  new boolean[]{isNightModeSwitchOn, isFeelsLikeSwitchOn, isPressureSwitchOn};
        return  settingsArray;
    }

    // Метод, который возвращает экземпляр объекта.
    // Если объекта нет, то создаем его.
    public static SettingsActivityPresenter getInstance(){
        // Здесь реализована «ленивая» инициализация объекта,
        // то есть, пока объект не нужен, не создаем его.
        synchronized (syncObj) {
            if (instance == null) {
                instance = new SettingsActivityPresenter();
            }
            return instance;
        }
    }
}
