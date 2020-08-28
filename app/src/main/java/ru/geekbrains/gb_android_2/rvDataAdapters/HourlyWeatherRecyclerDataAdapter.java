package ru.geekbrains.gb_android_2.rvDataAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.geekbrains.gb_android_2.R;

public class HourlyWeatherRecyclerDataAdapter extends RecyclerView.Adapter<HourlyWeatherRecyclerDataAdapter.ViewHolder> {
    private List<Integer> hourlyWeatherIcon ;
    private List<String> hourlyTime;
    private List<String> hourlyTemperature;
    private RVOnItemClick onItemClickCallback;

    public HourlyWeatherRecyclerDataAdapter(List<String> hourlyTime, List<Integer> hourlyWeatherIcon, List<String> hourlyTemperature, RVOnItemClick onItemClickCallback) {
        this.hourlyTime = hourlyTime;
        this.hourlyWeatherIcon = hourlyWeatherIcon;
        this.hourlyTemperature = hourlyTemperature;
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public HourlyWeatherRecyclerDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hourly_weather_recyclerview_layout, parent,
                false);
        return new HourlyWeatherRecyclerDataAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyWeatherRecyclerDataAdapter.ViewHolder holder, int position) {
        String time = hourlyTime.get(position);
        Integer weatherIcon = hourlyWeatherIcon.get(position);
        String temperature = hourlyTemperature.get(position);

        holder.setTextToTimeTextView(time);
        holder.setTextToHourlyTemperatureTextView(temperature);
        holder.setImageToHourlyWeatherIconImageView(weatherIcon);
        holder.setOnClickForItem(time);
    }

    @Override
    public int getItemCount() {
        return hourlyTime == null ? 0 : hourlyTime.size();
    }//

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView timeTextView;
        private TextView hourlyTemperatureTextView;
        private ImageView hourlyWeatherIconImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.time);
            hourlyTemperatureTextView = itemView.findViewById(R.id.hourlyTemperature);
            hourlyWeatherIconImageView = itemView.findViewById(R.id.hourlyWeatherIcon);
        }

        void setTextToTimeTextView(String text) {
            timeTextView.setText(text);
        }
        void setTextToHourlyTemperatureTextView(String text) { hourlyTemperatureTextView.setText(text);}
        void setImageToHourlyWeatherIconImageView(int resourceId) { hourlyWeatherIconImageView.setImageResource(resourceId);}

        void setOnClickForItem(final String day) {
            hourlyWeatherIconImageView.setOnClickListener(view -> {
                if(onItemClickCallback != null) {
                    onItemClickCallback.onItemClicked(view, day);
                }
            });
        }
    }

}
