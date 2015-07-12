package es.zgzappstore.equipoa.handicapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class PlacesActivity extends ActionBarActivity {

    GoogleMap map;
    int id;
    TextView tvTitle;
    TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        Intent thisIntent = getIntent();
        id = thisIntent.getIntExtra("LugarID", -1);

        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.place_map)).getMap();
        tvTitle = (TextView) findViewById(R.id.place_title);
        tvDescription = (TextView) findViewById(R.id.place_description);

        new DownloadLugares().execute();

    }

    private class DownloadLugares extends AsyncTask<Void, Void, ArrayList<Lugares>> {

        @Override
        protected ArrayList<Lugares> doInBackground(Void... params) {

            //InputStream is = null;
            //String response = "";

            //protected ArrayList<LatLng> doInBackground(Void... params) {
            String response = null;

            try {
                String charset = "UTF-8";
                //String param1 = "INSERT INTO Lugares (id, title, description, type, long, lat) VALUES (null, 'PruebaMal', 'La prueba', 'Hotel', 2.555, 3.788);";
                String param1 = "SELECT * FROM lugares WHERE id = " + String.valueOf(id) + ";";

                URLConnection conn = new URL("https://iescities.com:443/IESCities/api/data/query/287/sql").openConnection();
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

            try {
                JSONObject json = new JSONObject(String.valueOf(response));

                JSONArray array = json.getJSONArray("rows");

                ArrayList<Lugares> items = new ArrayList<Lugares>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    Lugares lugar = new Lugares();

                    try {
                        if (obj.has("id")) {lugar.setId(obj.getInt("id"));}
                        if (obj.has("title")) {lugar.setTitle(obj.getString("title"));}
                        if (obj.has("description")) {lugar.setDescription(obj.getString("description"));}
                        if (obj.has("type")) {if (!obj.getString("type").trim().equals("Sugerencia")) {continue;}}
                        if (obj.has("long")) {lugar.setGeometry(new LatLng(obj.getDouble("long"),obj.getDouble("lat")));}

                        items.add(lugar);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                return items;

            } catch (JSONException e) {
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

        @Override
        protected void onPostExecute(ArrayList<Lugares> Listll) {
            super.onPostExecute(Listll);
            for (Lugares l : Listll) {
                map.addMarker(new MarkerOptions()
                        .position(l.getGeometry())
                        .draggable(false)
                        .visible(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                float zoom = (float) 15.0;
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(l.getGeometry(), zoom));

                //String title = getResources().getString(R.string.lbl_name);
                String comment = getResources().getString(R.string.lbl_comment);

                tvTitle.setText(l.getTitle());
                tvDescription.setText(l.getDescription());
            }
        }
    }
}
