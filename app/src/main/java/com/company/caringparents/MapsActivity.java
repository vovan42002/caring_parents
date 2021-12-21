package com.company.caringparents;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.company.caringparents.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener {
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        List<String> list;
        mMap = googleMap;
        final String urlListChild = Global.ip + "/parent/listChilds?idParent=" + Global.id;
        list = listChild(urlListChild);
        LatLng marker = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(marker).title("Default"));
        for (int i=0; i < list.size(); i++){
            String urlGetChildId = Global.ip+"/child/getChildId?idParent="+Global.id+"&name="+list.get(i);
            Long idChild = getChildId(urlGetChildId);
            String urlGetGeo = Global.ip+"/child/getGeo?idChild="+idChild;
            List<String> listGeo = listChild(urlGetGeo);
            marker = new LatLng(Double.valueOf(listGeo.get(0)), Double.valueOf(listGeo.get(1)));
            mMap.addMarker(new MarkerOptions().position(marker).title(list.get(i)));

            String urlGetLocation = Global.ip+"/child/getLocation?idChild="+idChild;
            String location = getLocation(urlGetLocation);
            List<String> listLoc = loc(location);
            PolylineOptions options = new PolylineOptions();
            for (int j = 0; j < listLoc.size(); j++){
                LatLng lng = new LatLng(Double.valueOf(listLoc.get(j)), Double.valueOf(listLoc.get(j+1)));
                j++;
                options.add(lng);
            }
            options.clickable(true);
            Polyline polyline = googleMap.addPolyline(options);
            polyline.setWidth(5);
            polyline.setColor(Color.BLUE);

        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker,15));

        googleMap.setOnPolylineClickListener(this);
        googleMap.setOnPolygonClickListener(this);

    }


    public static List<String> loc(String str) {
        List<String> list = new ArrayList<>();
        StringBuilder sbResult = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == 'I') {
                list.add(sb.toString());
                sbResult.append(sb.toString());
                sb = new StringBuilder();
            } else
                sb.append(str.charAt(i));
        }
        return list;
    }




    private List<String> listChild(String url) {
        List<String> list = new ArrayList<>();
        System.out.println(url);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            connection.connect();

            BufferedReader br;
            System.out.println("Response code = " + connection.getResponseCode() + " message = " + connection.getResponseMessage());
            if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output).append("\n");
                list.add(output);
            }
            if (sb != null)
                return list;
            else return list;

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Long getChildId(String url) {
        System.out.println(url);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            connection.connect();

            BufferedReader br;
            System.out.println("Response code = " + connection.getResponseCode() + " message = " + connection.getResponseMessage());
            if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
                System.out.println("Никнейм существует: " + sb.toString());
            }
            if (sb != null) {
                return Long.valueOf(sb.toString());
            } else return null;
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private String getLocation(String url) {
        System.out.println(url);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            connection.connect();

            BufferedReader br;
            System.out.println("Response code = " + connection.getResponseCode() + " message = " + connection.getResponseMessage());
            if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
                if (sb.toString() != null) {
                    return sb.toString();
                }
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPolygonClick(@NonNull Polygon polygon) {

    }
}