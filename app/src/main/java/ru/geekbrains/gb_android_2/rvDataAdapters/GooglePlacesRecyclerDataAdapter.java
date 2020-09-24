package ru.geekbrains.gb_android_2.rvDataAdapters;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import ru.geekbrains.gb_android_2.R;
import ru.geekbrains.gb_android_2.database.CitiesList;
import ru.geekbrains.gb_android_2.database.CitiesListSource;

public class GooglePlacesRecyclerDataAdapter extends RecyclerView.Adapter<GooglePlacesRecyclerDataAdapter.ViewHolder> {
        private PlacesRVOnItemClick onItemClickCallback;

        // Источник данных
        private ArrayList<String> places;
        android.os.Handler handler = new Handler();

        public GooglePlacesRecyclerDataAdapter(ArrayList<String> places, PlacesRVOnItemClick onItemClickCallback) {
            this.places = places;
            this.onItemClickCallback = onItemClickCallback;
        }

        @NonNull
        @Override
        public GooglePlacesRecyclerDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_places_recyclerview_layout, parent,
                    false);
            return new GooglePlacesRecyclerDataAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GooglePlacesRecyclerDataAdapter.ViewHolder holder, int position) {
            // Заполняем данными записи на экране
                    holder.placeName.setText(places.get(position));
                    holder.setOnClickForItem(places.get(position), position);
        }

    @Override
    public int getItemCount() {
        int count = 0;

        if (places != null && places.size() > 0) return places.size();
        return count;
    }

    public void updatePlacesList(ArrayList<String> newPlaces) {
            this.places = new ArrayList<>(newPlaces);
            this.notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
            private TextView placeName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                placeName = itemView.findViewById(R.id.placeTextView);
            }

            void setOnClickForItem(final String text, int position) {
                placeName.setOnClickListener(view -> {
                    if(onItemClickCallback != null) {

                        onItemClickCallback.onPlaceItemClicked(view, text, position);
                    }
                });
            }
        }
    }


