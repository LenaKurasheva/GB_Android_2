package ru.geekbrains.gb_android_2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeekWeatherRecyclerDataAdapter extends RecyclerView.Adapter<WeekWeatherRecyclerDataAdapter.ViewHolder> {
    private List<Integer> weatherIcons ;
    private List<String> days;
    private List<String> daysTemp;
    private RVOnItemClick onItemClickCallback;

    public WeekWeatherRecyclerDataAdapter(List<String> days, List<String> daysTemp, List<Integer> weatherIcons, RVOnItemClick onItemClickCallback) {
        this.weatherIcons = weatherIcons;
        this.days = days;
        this.daysTemp = daysTemp;
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_recyclerview_layout, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String day = days.get(position);
        String dayTemp = daysTemp.get(position);
        Integer weatherIconId = weatherIcons.get(position);

        holder.setTextToDayTextView(day);
        holder.setTextToDayTemperatureTextView(dayTemp);
        holder.setImageToWeatherIconImageView(weatherIconId);
        holder.setOnClickForItem(day);
    }

    @Override
    public int getItemCount() {
        return days == null ? 0 : days.size();
    }//

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dayTextView;
        private TextView dayTemperatureTextView;
        private ImageView weatherIconImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.date);
            dayTemperatureTextView = itemView.findViewById(R.id.dayTemperature);
            weatherIconImageView = itemView.findViewById(R.id.weatherIcon);
        }

        void setTextToDayTextView(String text) {
            dayTextView.setText(text);
        }
        void setTextToDayTemperatureTextView(String text) { dayTemperatureTextView.setText(text);}
        void setImageToWeatherIconImageView(int resourceId) { weatherIconImageView.setImageResource(resourceId);}

        void setOnClickForItem(final String day) {
            weatherIconImageView.setOnClickListener(view -> {
                if(onItemClickCallback != null) {
                    onItemClickCallback.onItemClicked(view, day);
                }
            });

        }
    }
}
