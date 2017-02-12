package no.byteme.magnuspoppe.bacheloroppgave;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements APIUrls
{
    private Button aboutButton;
    private ArrayList<DagbokInnlegg> data;
    private DagbokAdapter adapter;
    private ListView listView;

    private final static String LOG_TAG         = "MAIN_ACTIVITY";

    // KONSTANTER FOR BRUK AV INTENT MED DAGBOK INNLEGG:
    public  final static String INNLEGG_TAG     = "UNIK ID FOR INNLEGG";
    public  final static String INNLEGG_ID      = "UNIK ID ID";
    public  final static String INNLEGG_TITLE   = "UNIK ID TITTEL";
    public  final static String INNLEGG_DATE    = "UNIK ID DATO";
    public  final static String INNLEGG_OWNER   = "UNIK ID EIER";
    public  final static String INNLEGG_CONTENT = "UNIK ID INNHOLD";
    /**
     * Man vil alltid ha den ferskeste versjonen av dataene fra tjeneren
     * når man åpner appen.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        refreshFeed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // LAGER NØDVENDIGE KOMPONENTER TIL VIEWET:
        adapter = new DagbokAdapter(this, GetDummyData());
        listView = (ListView) findViewById(R.id.dagbok_innlegg_list);
        listView.setAdapter(adapter);

        // SETTER OPP "TOOLBAR"
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                switch ( item.getItemId() )
                {
                    case R.id.menu_refresh : refreshFeedWithMessage();     return true;
                    case R.id.menu_about   : openAboutPage();   return true;
                }
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                DagbokInnlegg innlegg = data.get(position);
                Intent intent = new Intent(getApplicationContext(), WriteNew.class);
                intent.putExtra(INNLEGG_TAG, packDagbokInnlegg(innlegg));
                startActivity(intent);
            }
        });
    }

    private Bundle packDagbokInnlegg( DagbokInnlegg innlegg )
    {
        Bundle dagbokInnlegg = new Bundle();
        dagbokInnlegg.putInt(INNLEGG_ID, innlegg.getId());
        dagbokInnlegg.putString(INNLEGG_DATE, innlegg.getDate());
        dagbokInnlegg.putString(INNLEGG_OWNER, innlegg.getAuthor());
        dagbokInnlegg.putString(INNLEGG_TITLE, innlegg.getTitle());
        dagbokInnlegg.putString(INNLEGG_CONTENT, innlegg.getContent());
        return dagbokInnlegg;
    }

    /**
     * Fyller opp menyen inni toolbaren.
     * @param menu
     * @return always true.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Reacts to the floating action button being clikced.
     * Changes activity from main -> Write New window.
     * @param v
     */
    public void onWriteNewButtonClicked(View v)
    {
        startActivity(new Intent(getApplicationContext(), WriteNew.class));
    }

    /**
     * Opens the about page.
     */
    private void openAboutPage()
    {
        startActivity(new Intent(getApplicationContext(), About.class));
    }

    /**
     * Refreshes the feed with a message for the user.
     */
    private void refreshFeedWithMessage()
    {
        refreshFeed();
        Snackbar.make(getCurrentFocus(), "Refreshed feed.", Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Refreshes the feed for the diary entries.
     */
    private void refreshFeed()
    {
        ASyncServiceDiaryGet service = new ASyncServiceDiaryGet();
        service.execute(BYTEME_API + DIARY_GET);
    }

    /**
     * Egendefinert asynkron oppkobling for å hente JSON data.
     * @author Magnus Poppe Wang
     */
    private class ASyncServiceDiaryGet extends AsyncTask<String, Void, String>
    {
        // Konstant for ut-data
        final private static String LOG_TAG = "GET_DIARY_DATA_TASK";

        /**
         * Dette skjer etter oppkoblingen, når data har blitt hentet ut.
         * @param output data fra oppkobling
         */
        @Override
        protected void onPostExecute(String output)
        {
            if (output != null) // Ingen data fra oppkobling
            {
                try // "Parser" JSON-teksten:
                {
                    Log.v(LOG_TAG, "Connection was a success!");
                    JSONArray innlegg = new JSONArray(output);
                    data = new ArrayList<>();

                    for (int i = 0; i < innlegg.length(); i++)
                    {
                        JSONObject innleggJSON = innlegg.getJSONObject(i);
                        data.add(i, new DagbokInnlegg(
                                innleggJSON.getInt("id"),
                                innleggJSON.getString("title"),
                                innleggJSON.getString("date"),
                                innleggJSON.getString("owner"),
                                innleggJSON.getString("content")
                        ));
                    }
                    adapter = new DagbokAdapter(getApplicationContext(), data);
                    listView = (ListView) findViewById(R.id.dagbok_innlegg_list);
                    listView.setAdapter(adapter);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            else Log.v(LOG_TAG, "No data gotten.");
        }

        /**
         * Selve oppkoblingen gjøres her.
         */
        @Override
        protected String doInBackground(String... params)
        {
            // Disse må deklareres utenfor try-catch så de kan lukkes igjen
            // i den endelige "finally" blokken.
            HttpURLConnection connect = null;
            BufferedReader reader = null;

            // Returdata som inneholder JSON som streng.
            String DiaryEntries;

            try
            {
                // Lager url for oppkobling
                URL url = new URL(params[0]);

                // Lager HTTP request og kobler opp:
                connect = (HttpURLConnection) url.openConnection();
                connect.setRequestMethod("GET");
                connect.connect();

                Log.v(LOG_TAG, "Connected to: " + url.toString());

                // Leser dataene fra oppkoblingen inn i en string:
                InputStream stream = connect.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (stream == null) // ingen data funnet, ingenting å gjøre.
                    DiaryEntries = null;


                String line;
                reader = new BufferedReader(new InputStreamReader(stream));
                while ((line = reader.readLine()) != null) // Overfører data til en buffer
                    buffer.append(line);

                if (buffer.length() == 0) // Ingen data i buffer.
                    DiaryEntries = null;

                // Hvis vi har kommet hit er dataene kommet inn som en suksess!
                DiaryEntries = buffer.toString();
            }
            catch (IOException e)
            {
                Log.v(LOG_TAG, "If the code didn't successfully get the data, there's no point in attemping to parse");
                DiaryEntries = null;
            }
            finally
            {
                if (connect != null) {
                    connect.disconnect();
                    Log.v(LOG_TAG, "Disconnected from: " + params[0]);
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream: ", e);
                    }
                }
            }
            return DiaryEntries;
        }
    }

    /**
     * Creates some dummy data for the view in-case there
     * is no internet connection.
     * @return
     */
    private ArrayList<DagbokInnlegg> GetDummyData()
    {
        Resources res = getResources();
        data = new ArrayList<>();
        data.add(new DagbokInnlegg(
                1,
                res.getString(R.string.no_connection_header),
                "2000-01-01",
                res.getString(R.string.no_connection_owner),
                res.getString(R.string.no_connection_content)
        ));

        return data;
    }

}
