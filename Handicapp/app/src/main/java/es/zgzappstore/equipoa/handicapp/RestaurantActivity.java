package es.zgzappstore.equipoa.handicapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

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


public class RestaurantActivity extends ActionBarActivity {

    int itemID;
    TextView tvText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent thisIntent = getIntent();

        itemID = thisIntent.getIntExtra("LugarID", -1);
        tvText = (TextView) findViewById(R.id.details_text);
        tvText.setText("ID del lugar = " + itemID);

        new DownloadRestaurant().execute();
    }

    private class DownloadRestaurant extends AsyncTask<Void, Void, Restaurants> {

        @Override
        protected Restaurants doInBackground (Void... params){

            String response = null;

            try {
                String charset = "UTF-8";
                String param1 = "select * , GROUP_CONCAT(result_geometry_coordinates.geometry) as coordinates, \n" +
                        "GROUP_CONCAT(result_tel.tel) as telf\n" +
                        "from result, result_geometry, result_geometry_coordinates, result_tel \n" +
                        "where result.id = " + String.valueOf(itemID) + " and result_geometry.parent_id=result.id and result_geometry_coordinates.parent_id=result_geometry._id \n" +
                        "and result_tel.parent_id = result.id\n" +
                        "GROUP BY result.id;";
                URLConnection conn = new URL("https://iescities.com:443/IESCities/api/data/query/279/sql?origin=original").openConnection();
                //conn.setReadTimeout(10000);
                //conn.setConnectTimeout(15000);
                //conn.setRequestMethod("POST");
                //conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept-Charset", charset);
                conn.setRequestProperty("Content-Type", "text/plain");

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
                JSONObject all = new JSONObject(response);

                JSONArray array = all.getJSONArray("rows");

                Restaurants restaurant = new Restaurants();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = array.getJSONObject(i);

                    if (json.has("accesibilidad"))
                        restaurant.setAccesibilidad(json.getString("accesibilidad"));
                    if (json.has("id")) restaurant.setId(json.getInt("id"));
                    if (json.has("title")) restaurant.setTitle(json.getString("title"));
                    if (json.has("streetAddress"))
                        restaurant.setStreetAddress(json.getString("streetAddress"));
                    if (json.has("addressLocality"))
                        restaurant.setAddressLocality(json.getString("addressLocality"));
                    if (json.has("tel")) restaurant.setTel(json.getString("tel"));
                    if (json.has("email")) restaurant.setEmail(json.getString("email"));
                    if (json.has("url")) restaurant.setUrl(json.getString("url"));
                    if (json.has("image")) restaurant.setImage(json.getString("image"));
                    if (json.has("logo")) restaurant.setLogo(json.getString("logo"));
                    if (json.has("comment")) restaurant.setComment(json.getString("comment"));
                    if (json.has("link")) restaurant.setLink(json.getString("link"));
                    if (json.has("tenedores")) restaurant.setTenedores(json.getInt("tenedores"));
                    if (json.has("capacidad")) restaurant.setCapacidad(json.getInt("capacidad"));
                    if (json.has("coordinates")) {
                        String[] geo = json.getString("coordinates").split(",");
                        String lat = geo[0];
                        String lon = geo[1];
                        LatLng ll = new LatLng(Double.valueOf(geo[0]), Double.valueOf(geo[1]));
                        restaurant.setGeometry(ll);
                    }
                }
                return restaurant;
                
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
        protected void onPostExecute(Restaurants restaurant) {
            super.onPostExecute(restaurant);
            tvText.setText(restaurant.getTel() + " " + restaurant.getTitle() + " " + restaurant.getGeometry().toString());
        }
    }
}
