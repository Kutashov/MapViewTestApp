package ru.alexandrkutashov.mapviewtestapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ru.alexandrkutashov.mapviewtestapp.mapview.MapView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapView mapView = findViewById(R.id.mapView);
        mapView.setCentralTile(33198, 22539);
    }
}
