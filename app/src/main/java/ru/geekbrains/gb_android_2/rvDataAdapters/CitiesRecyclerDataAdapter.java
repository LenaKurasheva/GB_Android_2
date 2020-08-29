package ru.geekbrains.gb_android_2.rvDataAdapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.geekbrains.gb_android_2.R;


public class CitiesRecyclerDataAdapter extends RecyclerView.Adapter<CitiesRecyclerDataAdapter.ViewHolder> {
    private ArrayList<String> cities;
    private RVOnItemClick onItemClickCallback;

    public CitiesRecyclerDataAdapter(ArrayList<String> cities, RVOnItemClick onItemClickCallback) {
        this.cities = cities;
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
        String text = cities.get(position);

        holder.setTextToTextView(text);
        holder.setOnClickForItem(text);
    }

    @Override
    public int getItemCount() {
        return cities == null ? 0 : cities.size();
    }


    public void putChosenCityToTopInCitiesList(String chosenCity){
        cities.remove(chosenCity);
        cities.add(0, chosenCity);
        notifyDataSetChanged();
    }

    public void remove(String cityName) {
        cities.remove(cityName);
        notifyDataSetChanged();
    }

    public void addNewCity(String newElement) {
        cities.add(0, newElement);
        notifyItemInserted(0);
        Log.d("myLog", "CitiesRVDAtaAdapter: new city added to ArrayList cities : "  + newElement);
    }
    public ArrayList<String> getCitiesList(){
        Log.d("myLog", "CitiesRVDAtaAdapter: get cities to put to CDC: "  + cities.toString());
        return cities;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cityName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.cityNameTextView);
        }

        void setTextToTextView(String text) {
            cityName.setText(text);
        }

        void setOnClickForItem(final String text) {
            cityName.setOnClickListener(view -> {
                if(onItemClickCallback != null) {
                    onItemClickCallback.onItemClicked(view, text);
                }
            });
            cityName.setOnLongClickListener(view -> {
                if(onItemClickCallback != null) {
                    onItemClickCallback.onItemLongPressed(view);
                }
                return false;
            });
        }
    }
}
