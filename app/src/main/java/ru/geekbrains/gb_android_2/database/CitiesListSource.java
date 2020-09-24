package ru.geekbrains.gb_android_2.database;

import java.util.ArrayList;
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
            loadCitiesListSortedByCreated();
        }
        return citiesList;
    }

    // Загрузить города в буфер
    public void loadCitiesListSortedByCreated(){
        citiesList = citiesListDao.getAllCities();
    }

    // Получить количество записей
    public long getCountCities(){
        return citiesListDao.getCountCities();
    }

    // Добавить город
    public void addCity(CitiesList city){
        CitiesList cityFromDB = citiesListDao.getCityByName(city.name);
        if (cityFromDB != null){
            updateCityCreatedTime(cityFromDB.name);
        } else citiesListDao.insertCity(city);
        // После изменения БД надо перечитать буфер
    }

    // Заменить город
    public void updateCity(CitiesList city){
        citiesListDao.updateCity(city);
        loadCitiesListSortedByCreated();
    }

    // Удалить город из базы
    public void removeCity(long id){
        citiesListDao.deteleCityById(id);
        loadCitiesListSortedByCreated();
    }

     // Меняем время добавления города, чтобы он отображался первым
    public void updateCityCreatedTime(String cityName) {
        long currentTime = System.currentTimeMillis() / 1000L;
        citiesListDao.updateCreatedTime(cityName, currentTime);
    }

    // Сортируем города по имени
    public void loadCitiesListSortedByName(){
        citiesList = citiesListDao.sortByName();
    }

    // Get coordinates by name
    public List<Double> getCoordinatesFromDB(String name){
        Double latitude = citiesListDao.getLatitudeByName(name);
        Double longitude = citiesListDao.getLongitudeByName(name);
        List<Double> coordinates = new ArrayList<>();
        coordinates.add(latitude);
        coordinates.add(longitude);
        loadCitiesListSortedByCreated();
        return coordinates;
    }
}

