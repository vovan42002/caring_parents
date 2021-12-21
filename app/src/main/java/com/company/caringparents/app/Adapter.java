package com.company.caringparents.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.company.caringparents.R;
import com.company.caringparents.model.App;

import java.text.DateFormat;
import java.util.List;

public class Adapter extends ArrayAdapter<App> {
    List<App> appList;

    public Adapter(@NonNull Context context, @NonNull List objects) {
        super(context, 0, objects);
        this.appList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.usage_stats_item, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.package_name);
        TextView lastTimeUsed = (TextView) convertView.findViewById(R.id.last_time_used);
        TextView usageTime = (TextView) convertView.findViewById(R.id.usage_time);
        ImageView appIcon = (ImageView) convertView.findViewById(R.id.app_icon_usage);

        name.setText(appList.get(position).getName());
        lastTimeUsed.setText(DateUtils.formatSameDayTime(appList.get(position).getLast_time_used(),
                System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM));
        usageTime.setText(
                DateUtils.formatElapsedTime(appList.get(position).getTotal_time() / 1000));
        Bitmap bitmap = BitmapFactory.decodeByteArray(appList.get(position).getIcon(), 0,
                appList.get(position).getIcon().length);
        System.out.println("ICON: "+appList.get(position).getIcon().toString());
        appIcon.setImageBitmap(bitmap);
        return convertView;
    }
}
