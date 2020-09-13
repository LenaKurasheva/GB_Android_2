package ru.geekbrains.gb_android_2.rvDataAdapters;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import ru.geekbrains.gb_android_2.R;
import ru.geekbrains.gb_android_2.database.CitiesList;
import ru.geekbrains.gb_android_2.database.CitiesListSource;


public class CitiesRecyclerDataAdapter extends RecyclerView.Adapter<CitiesRecyclerDataAdapter.ViewHolder> {
    private RVOnItemClick onItemClickCallback;

    // Источник данных
    private CitiesListSource dataSource;
    android.os.Handler handler = new Handler();

    public CitiesRecyclerDataAdapter(CitiesListSource dataSource, RVOnItemClick onItemClickCallback) {
        this.dataSource = dataSource;
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cities_recyclerview_layout, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Заполняем данными записи на экране
        new Thread(()->{
            List<CitiesList> cities = dataSource.getCitiesList();
            handler.post(()->{
                CitiesList city = cities.get(position);
                holder.cityName.setText(city.name);
                holder.setOnClickForItem(city.name, position);
            });
        }).start();
    }

    @Override
    public int getItemCount() {
        int count = 0;
        Callable<Integer> task = ()-> (int) dataSource.getCountCities();
        FutureTask<Integer> future = new FutureTask<>(task);
        new Thread(future).start();
        try {
            count =  (future.get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return count;
    }

    public void putChosenCityToTopInCitiesList(String cityName){
       Thread thread = new Thread(()-> dataSource.updateCityCreatedTime(cityName));
       thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void remove(Integer cityId) {
        new Thread(()->{
        dataSource.removeCity(cityId);
            handler.post(this::notifyDataSetChanged);
        }).start();
    }

    public void sortByName(){
        new Thread(()->{
            dataSource.loadCitiesListSortedByName();
            handler.post(this::notifyDataSetChanged);
        }).start();
    }

    public void sortByCreatedTime(){
        new Thread(()->{
            dataSource.loadCitiesListSortedByCreated();
            handler.post(this::notifyDataSetChanged);
        }).start();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cityName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.cityNameTextView);
        }

        void setOnClickForItem(final String text, int position) {
            cityName.setOnClickListener(view -> {
                if(onItemClickCallback != null) {
                    onItemClickCallback.onItemClicked(view, text, position);
                }
            });
            cityName.setOnLongClickListener(view -> {

                if(onItemClickCallback != null) {
                    onItemClickCallback.onItemLongPressed(view, position);
                }
                return false;
            });
        }
    }
}
