package com.ozsaat.sunshine.ui.forecast;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ozsaat.sunshine.R;
import com.ozsaat.sunshine.ui.BaseFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ForecastFragment extends BaseFragment {
    public static final String[] TEMP = new String[]{
            "Today - Sunny - 88/63",
            "Tomorrow - Foggy - 70/4",
            "Weds - Cloudy - 72/63",
            "Thurs - Asteroids - 75/65",
            "Fri - Heavy Rain - 65/56",
            "Sat - HELP TRAPPED IN A WEATHERSTATION - 60/51",
            "Sun - Sunny - 80/68"
    };

    public static ForecastFragment newInstance() {
        return new ForecastFragment();
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forecast, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        final ListView listView = (ListView) view.findViewById(R.id.listview_forecast);

        // Setup
        final List<String> weekForecast = new ArrayList<String>(Arrays.asList(TEMP));
        ArrayAdapter<String> forecastAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.li_forecast,
                        R.id.list_item_forecast_textview,
                        weekForecast);
        listView.setAdapter(forecastAdapter);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        // TODO Implement
    }

    public static class FetchWeatherTask extends AsyncTask<Void, Void, Void> {
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr = null;

            try {
                urlConnection = connect();
                reader = getReader(urlConnection);
                forecastJsonStr = read(reader);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        private HttpURLConnection connect() throws IOException {
            final URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

            // Create the request to OpenWeatherMap, and open the connection
            final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            return urlConnection;
        }

        private BufferedReader getReader(HttpURLConnection connection) throws IOException {
            final InputStream inputStream = connection.getInputStream();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            return new BufferedReader(new InputStreamReader(inputStream));
        }

        private String read(BufferedReader reader) throws IOException {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            if (sb.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            return sb.toString();
        }
    }
}
