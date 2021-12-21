package com.company.caringparents.app;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.company.caringparents.Global;
import com.company.caringparents.R;
import com.company.caringparents.model.App;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AppActivity extends AppCompatActivity {
    private List<App> list = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usage_stats);
        String urlGetApps = Global.ip + "/app/getApps?idChild=" + Global.child_id;
        try {
            list = fromJsonArrToListApp(Global.methodGet(urlGetApps));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ListView listView = findViewById(R.id.pkg_list);
        Adapter adapter = new Adapter(getApplicationContext(), list);
        listView.setAdapter(adapter);

    }

    public List<App> fromJsonArrToListApp(JSONArray jsonArray) throws JSONException {
        final JSONArray result_array = jsonArray;
        List<App> list = new ArrayList<App>();
        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject joObject = result_array.getJSONObject(i);
            String name = joObject.get("name").toString();
            String last_time_used = joObject.get("last_time_used").toString();
            String total_time = joObject.get("total_time").toString();
            String icon = joObject.get("icon").toString();
            byte[] decodedBytes = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                decodedBytes = Base64.getDecoder().decode(icon);
            }
            App app = new App(name, Long.valueOf(last_time_used), Long.valueOf(total_time), decodedBytes);
            list.add(app);
        }
        return list;
    }
}