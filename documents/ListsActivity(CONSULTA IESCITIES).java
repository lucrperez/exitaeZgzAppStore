package es.zgzappstore.equipoa.handicapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ListsActivity extends ActionBarActivity implements ActionBar.TabListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }


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
                Intent list_intent = new Intent();
                list_intent.setClass(getApplicationContext(), HomeActivity.class);
                startActivity(list_intent);
                finish();
                break;
            case R.id.action_lists:
                break;
            case R.id.action_map:
                Intent map_intent = new Intent();
                map_intent.setClass(getApplicationContext(), MapActivity.class);
                startActivity(map_intent);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final boolean[] exit = {false};
        AlertDialog.Builder builder = new AlertDialog.Builder(ListsActivity.this);
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

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }





    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a TriviaFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new RestaurantsFragment();
                case 1:
                    return new HotelsFragment();
                case 2:
                    return new SuggestionsFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.tab_restaurants).toUpperCase(l);
                case 1:
                    return getString(R.string.tab_hotels).toUpperCase(l);
                case 2:
                    return getString(R.string.tab_suggestions).toUpperCase(l);
            }
            return null;
        }
    }

    public static class RestaurantsFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            //return super.onCreateView(inflater, container, savedInstanceState);
            View rootView = inflater.inflate(R.layout.fragment_restaurants, container, false);

            new DownloadRestaurants().execute();

            return rootView;
        }


        private class DownloadRestaurants extends AsyncTask<Void, Void, ArrayList<LatLng>> {

            @Override
            protected ArrayList<LatLng> doInBackground(Void... params) {

           /*     InputStream is = null;
                String response = "";

                try {
                    URL url = new URL("http://www.zaragoza.es/api/recurso/turismo/restaurante.json?start=0&rows=1000&fl=title,streetAddress,addressLocality,accesibilidad,tel,email,url,image,logo,comment,tenedores,capacidad,geometry,link");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 );
                    conn.setConnectTimeout(15000 );
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);

                    conn.connect();
                    is = conn.getInputStream();

                    response = readIt(is);

                    is.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }*/







                String response = null;

                try {
                 /*    String charset = "UTF-8";
                    String param1 = "INSERT INTO Lugares (id, title, description, type, long, lat) VALUES (null, 'PruebaMal', 'La prueba', 'Hotel', 2.555, 3.788);";
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

                    is.close(); */
                    String charset = "UTF-8";
                    String param1 = "select * , GROUP_CONCAT(result_geometry_coordinates.geometry) as coordinates from result, result_geometry, result_geometry_coordinates where result.id=14 and result_geometry.parent_id=result.id and result_geometry_coordinates.parent_id=result_geometry._id;";
                    //String param1 = "SELECT * FROM lugares;";

                     URLConnection conn = new URL("https://iescities.com:443/IESCities/api/data/query/279/sql?origin=original").openConnection();
                    //conn.setReadTimeout(10000);
                    //conn.setConnectTimeout(15000);
                    //conn.setRequestMethod("POST");
                    //conn.setDoInput(true);
                    conn.setDoOutput(true);
                    //conn.setDoOutput(false);
                    conn.setRequestProperty("Accept-Charset", charset);
                    conn.setRequestProperty("Content-Type", "text/plain");

                    //final String basicAuth = "Basic " + Base64.encodeToString("handicapp:handicapp1".getBytes(), android.util.Base64.NO_WRAP);
                    //conn.setRequestProperty("Authorization", basicAuth);

                    OutputStream output = conn.getOutputStream();
                    output.write(param1.getBytes());

                    conn.connect();

                    InputStream is = conn.getInputStream();

                    response = readIt(is);

                    is.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*try {
                    JSONObject json = new JSONObject(String.valueOf(response));
                    int total = json.getInt("count");

                    JSONArray array = json.getJSONArray("rows");

                    ArrayList<LatLng> items = new ArrayList<LatLng>();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);

                        try {
                            items.add(new LatLng(obj.getDouble("lat"), obj.getDouble("lng")));

                        } catch (JSONException e) {

                        }
                    }

                    return items;

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
            protected void onPostExecute(ArrayList<LatLng> Listll) {
                super.onPostExecute(Listll);
            }
        }

    }

    public static class HotelsFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            //return super.onCreateView(inflater, container, savedInstanceState);
            View rootView = inflater.inflate(R.layout.fragment_hotels, container, false);

            return rootView;
        }

    }

    public static class SuggestionsFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            //return super.onCreateView(inflater, container, savedInstanceState);
            View rootView = inflater.inflate(R.layout.fragment_suggestions, container, false);

            return rootView;
        }

    }
}
