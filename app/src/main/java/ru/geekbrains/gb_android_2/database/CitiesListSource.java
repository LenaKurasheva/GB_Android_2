package ru.geekbrains.gb_android_2.database;

import java.util.List;


// Вспомогательный класс, развязывающий
// зависимость между Room и CitiesRecyclerView
public class CitiesListSource {

    private final CitiesListDao citiesListDao;

    // Буфер с данными, сюда будем подкачивать данные из БД
    private List<CitiesList> citiesList;

    public CitiesListSource(CitiesListDao citiesListDao){
        this.citiesListDao = citiesListDao;
    }

    // Получить всех студентов
    public List<CitiesList> getCitiesList(){
        // Если объекты еще не загружены, то загружаем их.
        // Сделано для того, чтобы не делать запросы в БД каждый раз
        if (citiesList == null){
            loadCitiesList();
        }
        return citiesList;
    }

    // Загрузить города в буфер
    public void loadCitiesList(){
        citiesList = citiesListDao.getAllCities();
    }

    // Получить количество записей
    public long getCountCities(){
        return citiesListDao.getCountCities();
    }

    // Добавить город
    public void addCity(CitiesList city){
        citiesListDao.insertCity(city);
        // После изменения БД надо перечитать буфер
        loadCitiesList();
    }

    // Заменить город
    public void updateCity(CitiesList city){
        citiesListDao.updateCity(city);
        loadCitiesList();
    }

    // Удалить город из базы
    public void removeCity(long id){
        citiesListDao.deteleCityById(id);
        loadCitiesList();
    }

     // Пересоздаем грод, чтобы он отображался первым
    public void reCreateCity(String cityName){
        CitiesList city = citiesListDao.getCityByName(cityName);
        removeCity(city.id);
        addCity(new CitiesList(cityName));
    }
}

