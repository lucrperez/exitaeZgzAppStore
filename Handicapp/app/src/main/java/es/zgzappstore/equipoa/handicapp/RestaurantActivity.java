package es.zgzappstore.equipoa.handicapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class RestaurantActivity extends ActionBarActivity {

    int itemID;

    ImageView ivPhoto;
    ImageView ivLogo;
    TextView tvTitle;
    TextView tvAddress;
    TextView tvPhone;
    TextView tvEmail;
    TextView tvUrl;
    TextView tvComment;
    TextView tvCapacity;
    TextView tvForks;
    TextView tvAccessibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);

        Intent thisIntent = getIntent();

        itemID = thisIntent.getIntExtra("LugarID", -1);

        ivPhoto = (ImageView) findViewById(R.id.restaurant_photo);
        ivLogo = (ImageView) findViewById(R.id.restaurant_logo);
        tvTitle = (TextView) findViewById(R.id.restaurant_title);
        tvAddress = (TextView) findViewById(R.id.restaurant_address);
        tvPhone = (TextView) findViewById(R.id.restaurant_phone);
        tvEmail = (TextView) findViewById(R.id.restaurant_email);
        tvUrl = (TextView) findViewById(R.id.restaurant_url);
        tvComment = (TextView) findViewById(R.id.restaurant_comment);
        tvCapacity = (TextView) findViewById(R.id.restaurant_capacity);
        tvForks = (TextView) findViewById(R.id.restaurant_forks);
        tvAccessibility = (TextView) findViewById(R.id.restaurant_accessibility);

        new DownloadRestaurant().execute();
    }

    private class DownloadRestaurant extends AsyncTask<Void, Void, Restaurants> {

        @Override
        protected Restaurants doInBackground (Void... params){

            InputStream is = null;
            String response = "";

            try {
                URL url = new URL("http://www.zaragoza.es/api/recurso/turismo/restaurante/" + String.valueOf(itemID) + ".json?start=0&rows=1000&srsname=wgs84&fl=id,title,streetAddress,addressLocality,accesibilidad,tel,email,url,image,logo,comment,tenedores,capacidad,geometry,link");
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

                Restaurants restaurant = new Restaurants();

                String zgzEs = getResources().getString(R.string.txt_zgzes);

                if (json.has("accesibilidad"))
                    restaurant.setAccesibilidad(json.getString("accesibilidad"));
                if (json.has("id")) restaurant.setId(json.getInt("id"));
                if (json.has("title")) restaurant.setTitle(json.getString("title"));
                if (json.has("streetAddress"))
                    restaurant.setStreetAddress(json.getString("streetAddress"));
                if (json.has("addressLocality"))
                    restaurant.setAddressLocality(json.getString("addressLocality"));
                if (json.has("tel")) {
                    JSONObject telObj = json.getJSONObject("tel");
                    if (telObj.has("tel")) {
                        restaurant.setTel(telObj.getString("tel"));
                    }
                }
                if (json.has("email")) restaurant.setEmail(json.getString("email"));
                if (json.has("url")) restaurant.setUrl(json.getString("url"));
                if (json.has("image")) restaurant.setImage(zgzEs + json.getString("image"));
                if (json.has("logo")) restaurant.setLogo(zgzEs + json.getString("logo"));
                if (json.has("comment")) restaurant.setComment(json.getString("comment"));
                if (json.has("link")) restaurant.setLink(json.getString("link"));
                if (json.has("tenedores")) restaurant.setTenedores(json.getInt("tenedores"));
                if (json.has("capacidad")) restaurant.setCapacidad(json.getInt("capacidad"));
                if (json.has("geometry")) {
                    JSONObject obj = json.getJSONObject("geometry");
                    if (obj.has("coordinates")) {
                        JSONArray jArray = obj.getJSONArray("coordinates");
                        LatLng ll = new LatLng((Double) jArray.get(1), (Double) jArray.get(0));
                        restaurant.setGeometry(ll);
                    }
                }
                return restaurant;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            /*String response = null;

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
                    if (json.has("tel.tel")) restaurant.setTel(json.getString("tel.tel"));
                    if (json.has("email")) restaurant.setEmail(json.getString("email"));
                    if (json.has("url")) restaurant.setUrl(json.getString("url"));
                    if (json.has("image")) restaurant.setImage(json.getString("image"));
                    if (json.has("logo")) restaurant.setLogo(json.getString("logo"));
                    if (json.has("comment")) restaurant.setComment(json.getString("comment"));
                    if (json.has("link")) restaurant.setLink(json.getString("link"));
                    if (json.has("tenedores")) restaurant.setTenedores(json.getInt("tenedores"));
                    if (json.has("capacidad")) restaurant.setCapacidad(json.getInt("capacidad"));
                    if (json.has("geometry.coordinates")) {
                        //JSONObject geo = json.getJSONObject("geometry");
                        //if (geo.has("coordinates")) {
                            String[] strLl = json.getString("geometry.coordinates").split(",");
                            LatLng ll = new LatLng(Double.valueOf(strLl[0]), Double.valueOf(strLl[1]));
                            restaurant.setGeometry(ll);
                        //}
                    }
                }
                return restaurant;
                
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
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

            //String title = getResources().getString(R.string.lbl_name);
            //String address = getResources().getString(R.string.lbl_address);
            String phone = getResources().getString(R.string.lbl_phone);
            //String email = getResources().getString(R.string.lbl_email);
            //String comment = getResources().getString(R.string.lbl_comment);
            String capacity = getResources().getString(R.string.lbl_capacity);
            String forks = getResources().getString(R.string.lbl_fork);

            tvTitle.setText(restaurant.getTitle());
            tvAddress.setText(restaurant.getStreetAddress() + ", " + restaurant.getAddressLocality());
            tvPhone.setText(phone + restaurant.getTel());
            tvEmail.setText(restaurant.getEmail());
            tvUrl.setText(restaurant.getUrl());
            tvComment.setText(restaurant.getComment());
            tvCapacity.setText(capacity + restaurant.getCapacidad());
            tvForks.setText(forks + restaurant.getTenedores());
            tvAccessibility.setText(Html.fromHtml(restaurant.getAccesibilidad()));


            new DownloadImageTask(ivPhoto).execute(restaurant.getImage());
            if (restaurant.getLogo() != null && restaurant.getLogo() != getResources().getString(R.string.txt_zgzes)) {
                new DownloadImageTask(ivLogo).execute(restaurant.getLogo());
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
