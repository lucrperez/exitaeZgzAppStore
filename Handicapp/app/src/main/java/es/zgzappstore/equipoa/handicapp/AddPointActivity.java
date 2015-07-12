package es.zgzappstore.equipoa.handicapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


public class AddPointActivity extends ActionBarActivity {

    GoogleMap map;
    Spinner spType;
    EditText tvTitle;
    EditText tvDescription;
    String strSpinner = "";
    LatLng insertLL = null;
    int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_point);

        Intent thisIntent = getIntent();
        total = thisIntent.getIntExtra("Total", -1);

        LatLng ll = new LatLng(41.6532341,-0.8870108);
        float zoom = (float) 12.0;
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, zoom));

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                insertLL = latLng;
                map.addMarker(new MarkerOptions()
                    .position(insertLL)
                    .draggable(false)
                    .visible(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
        });

        spType = (Spinner) findViewById(R.id.tb_add_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.suggestion_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strSpinner = spType.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                strSpinner = "-1";
            }
        });

        tvTitle = (EditText) findViewById(R.id.tb_add_name);
        tvDescription = (EditText) findViewById(R.id.tb_add_description);

        Button btn_accept = (Button) findViewById(R.id.btn_add_accept);
        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (strSpinner == "-1") {
                    Toast t = Toast.makeText(getApplicationContext(), R.string.txt_select_type, Toast.LENGTH_LONG);
                    t.show();
                    return;
                }
                if (insertLL == null) {
                    Toast t = Toast.makeText(getApplicationContext(), R.string.txt_select_point, Toast.LENGTH_LONG);
                    t.show();
                    return;
                }
                if (tvTitle.getText().toString() == "") {
                    Toast t = Toast.makeText(getApplicationContext(), R.string.txt_select_title, Toast.LENGTH_LONG);
                    t.show();
                    return;
                }
                if (tvDescription.getText().toString() == "") {
                    Toast t = Toast.makeText(getApplicationContext(), R.string.txt_select_description, Toast.LENGTH_LONG);
                    t.show();
                    return;
                }

                new UploadPoint().execute();
                finish();

            }
        });
    }

    private class UploadPoint extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            String response = "";

            try {
                String charset = "UTF-8";
                String param1 = "INSERT INTO Lugares (id, title, description, type, long, lat) VALUES (" + String.valueOf(total + 1) + ", '" +
                        tvTitle.getText().toString() + "', '" + tvDescription.getText().toString() + "', '" + strSpinner + "', " +
                        insertLL.latitude + ", " + insertLL.longitude + ");";
                //String param1 = "SELECT * FROM lugares;";

                URLConnection conn = new URL("https://iescities.com:443/IESCities/api/data/update/287/sql").openConnection();
                //conn.setReadTimeout(10000);
                //conn.setConnectTimeout(15000);
                //conn.setRequestMethod("POST");
                //conn.setDoInput(true);
                conn.setDoOutput(true);
                //conn.setDoOutput(false);
                conn.setRequestProperty("Accept-Charset", charset);
                conn.setRequestProperty("Content-Type", "text/plain");

                final String basicAuth = "Basic " + Base64.encodeToString("handicapp:handicapp1".getBytes(), android.util.Base64.NO_WRAP);
                conn.setRequestProperty("Authorization", basicAuth);

                OutputStream output = conn.getOutputStream();
                output.write(param1.getBytes());

                conn.connect();

                InputStream is = conn.getInputStream();

                response = readIt(is);

                is.close();


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        public String readIt(InputStream stream) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "n");
            }
            return sb.toString();
        }
    }
}
