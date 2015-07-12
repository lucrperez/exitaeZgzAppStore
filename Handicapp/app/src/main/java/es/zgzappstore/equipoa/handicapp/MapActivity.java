package es.zgzappstore.equipoa.handicapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class MapActivity extends ActionBarActivity {

    GoogleMap map;
    int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Button btn_add = (Button) findViewById(R.id.btn_maps_addpoint);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), AddPointActivity.class);
                intent.putExtra("Total", total);
                startActivity(intent);
            }
        });

        LatLng ll = new LatLng(41.647475,-0.885705);
        float zoom = (float) 16.0;
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, zoom));

        new DownloadParkings().execute();
        new DownloadLugares().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_home:
                Intent map_intent = new Intent();
                map_intent.setClass(getApplicationContext(), HomeActivity.class);
                startActivity(map_intent);
                finish();
                break;
            case R.id.action_lists:
                Intent list_intent = new Intent();
                list_intent.setClass(getApplicationContext(), ListsActivity.class);
                startActivity(list_intent);
                finish();
                break;
            case R.id.action_map:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final boolean[] exit = {false};
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle(R.string.lbl_exit);
        builder.setMessage(R.string.txt_sure_exit);
        builder.setPositiveButton(R.string.button_text_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                customBack();
            }
        });
        builder.setNegativeButton(R.string.button_text_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create();
        builder.show();
    }

    private void customBack() {
        super.onBackPressed();
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
                    String param1 = "SELECT * FROM lugares;";

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

                    total = array.length();

                    ArrayList<Lugares> items = new ArrayList<Lugares>();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        Lugares lugar = new Lugares();

                        try {
                            if (obj.has("title")) {lugar.setTitle(obj.getString("title"));}
                            if (obj.has("description")) {lugar.setDescription(obj.getString("description"));}
                            if (obj.has("type")) {lugar.setType(obj.getString("type"));}
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

            for (Lugares ll : Listll) {
                map.addMarker(new MarkerOptions()
                        .position(ll.getGeometry())
                        .draggable(false)
                        .visible(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .title(ll.getTitle()));
            }
        }
    }


    private class DownloadParkings extends AsyncTask<Void, Void, ArrayList<LatLng>> {

        @Override
        protected ArrayList<LatLng> doInBackground(Void... params) {

            InputStream is = null;
            String response = "";

            try {
                URL url = new URL("http://www.zaragoza.es/api/recurso/urbanismo-infraestructuras/equipamiento/aparcamiento-personas-discapacidad.json?start=0&rows=500&srsname=wgs84&fl=title,geometry");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                conn.connect();
                is = conn.getInputStream();

                response = readIt(is);

                is.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                JSONObject json = new JSONObject(response);

                JSONArray array = json.getJSONArray("result");

                ArrayList<LatLng> items = new ArrayList<LatLng>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);

                    LatLng ll;


                    if (obj.has("geometry")) {
                        JSONObject obj2 = obj.getJSONObject("geometry");
                        if (obj2.has("coordinates")) {
                            JSONArray jArray = obj2.getJSONArray("coordinates");
                            ll = new LatLng((Double) jArray.get(1), (Double) jArray.get(0));
                            items.add(ll);
                        }
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
        protected void onPostExecute(ArrayList<LatLng> Listll) {
            super.onPostExecute(Listll);

            for (LatLng ll : Listll) {
                map.addMarker(new MarkerOptions()
                        .position(ll)
                        .draggable(false)
                        .visible(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        .title("Estacionamiento para discapacitados"));
            }
        }
    }
}
