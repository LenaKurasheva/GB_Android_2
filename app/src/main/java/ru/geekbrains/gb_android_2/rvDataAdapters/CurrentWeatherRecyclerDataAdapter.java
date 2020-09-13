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

public class CurrentWeatherRecyclerDataAdapter extends RecyclerView.Adapter<ru.geekbrains.gb_android_2.rvDataAdapters.CurrentWeatherRecyclerDataAdapter.ViewHolder> {
   private List<String> currentWeather;
    private RVOnItemClick onItemClickCallback;

   public CurrentWeatherRecyclerDataAdapter(List<String> currentWeather, RVOnItemClick onItemClickCallback){
       this.currentWeather = currentWeather;
       this.onItemClickCallback = onItemClickCallback;
   }



        @NonNull
        @Override
        public CurrentWeatherRecyclerDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_current_weather_recyclerview_layout, parent,
                    false);
            return new CurrentWeatherRecyclerDataAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ru.geekbrains.gb_android_2.rvDataAdapters.CurrentWeatherRecyclerDataAdapter.ViewHolder holder, int position) {
            String item = currentWeather.get(position);

            holder.setTextToItemTextView(item);
            holder.setOnClickForItem(item, position);
        }

        @Override
        public int getItemCount() {
            return currentWeather == null ? 0 : currentWeather.size();
        }//

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView item;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                item = itemView.findViewById(R.id.item1);
            }

            void setTextToItemTextView(String text) {
                item.setText(text);
            }

            void setOnClickForItem(final String text, int position) {
                item.setOnClickListener(view -> {
                    if(onItemClickCallback != null) {
                        onItemClickCallback.onItemClicked(view, text, position);
                    }
                });
            }
        }

    }
