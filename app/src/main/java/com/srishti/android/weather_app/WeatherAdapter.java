package com.srishti.android.weather_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherModel> weatherInfoList;

    public WeatherAdapter(Context context, ArrayList<WeatherModel> weatherInfoList) {
        this.context = context;
        this.weatherInfoList = weatherInfoList;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_item, parent, false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {

        WeatherModel weatherInfo = weatherInfoList.get(position);
        Picasso.get().load("http:".concat(weatherInfo.getIcon())).into(holder.conditionIV);
        holder.temperatureTV.setText(weatherInfo.getTemperature() + "°C");
        holder.windTV.setText(weatherInfo.getWindSpeed() + "Km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
            Date t = input.parse(weatherInfo.getTime());
            holder.timeTV.setText(output.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if(weatherInfoList == null)
            return 0;
        return weatherInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView windTV, temperatureTV, timeTV;
        private ImageView conditionIV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            windTV = itemView.findViewById(R.id.wind_speed_tv);
            temperatureTV = itemView.findViewById(R.id.temperature_TV);
            timeTV = itemView.findViewById(R.id.time_tv);
            conditionIV = itemView.findViewById(R.id.weather_condition_iv);
        }
    }
}
