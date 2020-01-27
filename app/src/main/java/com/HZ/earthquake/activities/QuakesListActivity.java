package com.HZ.earthquake.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.HZ.earthquake.R;
import com.HZ.earthquake.model.EathQuake;
import com.HZ.earthquake.util.Constants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QuakesListActivity extends AppCompatActivity {
    private ArrayList<String>  arrayList;
    private ArrayList<Double> lonList, latList;
    private ListView listView;
    private RequestQueue queue;
    private ArrayAdapter arrayAdapter;
    private List<EathQuake> quakeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quakes_list);
        quakeList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);

        queue = Volley.newRequestQueue(this);
        arrayList = new ArrayList<>();
        lonList = new ArrayList<>();
        latList = new ArrayList<>();
        getAllQuakes(Constants.URL);
    }

    void getAllQuakes(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                final EathQuake earthQuake = new EathQuake();
                try {
                    JSONArray jsonArray = response.getJSONArray("features");
                    for (int i = 0; i < Constants.LIMIT; i++) {
                        JSONObject properties = jsonArray.getJSONObject(i).getJSONObject("properties");
                        //let's get coordinates
                        JSONObject geometry = jsonArray.getJSONObject(i).getJSONObject("geometry");
                        //get coordinates array
                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                        double lon = coordinates.getDouble(0);
                        double lat = coordinates.getDouble(1);
                        //Setup EarthQuake Object
                        earthQuake.setPlace(properties.getString("place"));
                        earthQuake.setType(properties.getString("type"));
                        earthQuake.setTime(properties.getLong("time"));
                        earthQuake.setLon(lon);
                        earthQuake.setLat(lat);

                        arrayList.add(earthQuake.getPlace());
                        lonList.add(earthQuake.getLon());
                        latList.add(earthQuake.getLat());
                    }
                    arrayAdapter = new ArrayAdapter<>(QuakesListActivity.this, android.R.layout.simple_list_item_1,
                            android.R.id.text1, arrayList);
                    listView.setAdapter(arrayAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(QuakesListActivity.this, MapsActivity.class);
                            double lat = latList.get(position);
                            double lon = lonList.get(position);
                            intent.putExtra("lat", lat);
                            intent.putExtra("lon", lon);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                    });
                    arrayAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.back_button) {
            setResult(Activity.RESULT_CANCELED, new Intent(QuakesListActivity.this, MapsActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
