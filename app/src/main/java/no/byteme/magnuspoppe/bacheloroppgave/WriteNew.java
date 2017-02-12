package no.byteme.magnuspoppe.bacheloroppgave;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;


import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class WriteNew extends Activity implements APIUrls
{

    private EditText headline;
    private EditText content;

    //Preview card:
    private CardView preview;
    private RelativeLayout previewOverlay;
    private TextView previewHeadline;
    private TextView previewContent;

    private Spinner owner;

    private boolean editMode;
    DagbokInnlegg innlegg;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_new);

        // Setting up inputfields:
        headline = (EditText) findViewById(R.id.write_headline_field);
        content  = (EditText) findViewById(R.id.write_content_field);
        preview = (CardView) findViewById(R.id.write_preview);
        previewOverlay = (RelativeLayout) findViewById(R.id.previewOverlay);
        previewHeadline = (TextView) findViewById(R.id.innlegg_header);
        previewContent  = (TextView) findViewById(R.id.innlegg_content);

        // Checking for edit mode:
        Intent intent = getIntent();
        if (intent.hasExtra(MainActivity.INNLEGG_TAG))
        {
            editMode = true;
            innlegg = unpackDagbokInnlegg(intent.getBundleExtra(MainActivity.INNLEGG_TAG));
            headline.setText(innlegg.getTitle());
            content.setText(innlegg.getContent());
        }
        else editMode = false;

        // Setting up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.write_new_toolbar);
        setActionBar(toolbar);

        headline.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                previewHeadline.setText(headline.getText());
            }
        });

        content.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                previewContent.setText(content.getText());
            }
        });
    }

    private DagbokInnlegg unpackDagbokInnlegg(Bundle bundle)
    {
        return new DagbokInnlegg(
                bundle.getInt(MainActivity.INNLEGG_ID),
                bundle.getString(MainActivity.INNLEGG_TITLE),
                bundle.getString(MainActivity.INNLEGG_DATE),
                bundle.getString(MainActivity.INNLEGG_OWNER),
                bundle.getString(MainActivity.INNLEGG_CONTENT)
        );
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    public void onUploadButtonClicked(View v)
    {
        promptUploadVisible(true);
    }

    private void promptUploadVisible(boolean visible)
    {
        if (visible)
            previewOverlay.setVisibility( View.VISIBLE );
        else
            previewOverlay.setVisibility( View.GONE );
    }

    public void cancelUpload(View v)
    {
        promptUploadVisible(false);
    }

    public void confirmUpload(View v)
    {
        if (editMode)
        {
            putContent(headline.getText().toString(), content.getText().toString(), getOwner());
            finish();
        }
        else {
            postContent(headline.getText().toString(), content.getText().toString(), getOwner());
            finish();
        }

    }
    private void putContent(String title, String content, String owner)
    {
        String url = BYTEME_API + DIARY_PUT + "?" +
                "title="    + title + "&" +
                "content="  + content  + "&" +
                "owner="    + owner;

        ASyncServiceDiaryPost service = new ASyncServiceDiaryPost();
        service.execute(url, innlegg.getId() + "");
    }

    private void postContent(String title, String content, String owner)
    {
        String url = BYTEME_API + DIARY_POST + "?" +
                "title="    + title + "&" +
                "content="  + content  + "&" +
                "owner="    + owner;

        ASyncServiceDiaryPost service = new ASyncServiceDiaryPost();
        service.execute(url);
    }

    private String getOwner()
    {
        return "1";
    }
    /**
     * Egendefinert asynkron oppkobling for å endre innlegg.
     * @author Magnus Poppe Wang
     */
    private class ASyncServiceDiaryPut extends AsyncTask<String, Void, Void>
    {
        // Konstant for ut-data
        final private static String LOG_TAG = "PUT_DIARY_DATA_TASK";

        /**
         * Selve oppkoblingen gjøres her.
         */
        @Override
        protected Void doInBackground(String... params)
        {
            // Disse må deklareres utenfor try-catch så de kan lukkes igjen
            // i den endelige "finally" blokken.
            HttpURLConnection connect = null;
            BufferedReader reader = null;

            try
            {
                // Lager url for oppkobling
                int postID = Integer.parseInt(params[1]);
                URL url = new URL(params[0] + "?id=" + postID);

                // Lager HTTP request og kobler opp:
                connect = (HttpURLConnection) url.openConnection();
                connect.setRequestMethod("POST");
                connect.connect();
                Log.v(LOG_TAG, "Connected to: " + url.toString());
            }
            catch (IOException e)
            {
                Log.v(LOG_TAG, "The code didn't successfully get the data.");
            }
            finally
            {
                if (connect != null) {
                    connect.disconnect();
                    Log.v(LOG_TAG, "Disconnected from: " + params[0]);
                }
            }
            return null;
        }
    }

    /**
     * Egendefinert asynkron oppkobling for å lagre nye innlegg
     * @author Magnus Poppe Wang
     */
    private class ASyncServiceDiaryPost extends AsyncTask<String, Void, Void>
    {
        // Konstant for ut-data
        final private static String LOG_TAG = "POST_DIARY_DATA_TASK";

        /**
         * Selve oppkoblingen gjøres her.
         */
        @Override
        protected Void doInBackground(String... params)
        {
            // Disse må deklareres utenfor try-catch så de kan lukkes igjen
            // i den endelige "finally" blokken.
            HttpURLConnection connect = null;
            BufferedReader reader = null;

            try
            {
                // Lager url for oppkobling
                URL url = new URL(params[0]);

                // Lager HTTP request og kobler opp:
                connect = (HttpURLConnection) url.openConnection();
                connect.setRequestMethod("POST");
                connect.connect();
                Log.v(LOG_TAG, "Connected to: " + url.toString());
            }
            catch (IOException e)
            {
                Log.v(LOG_TAG, "The code didn't successfully get the data.");
            }
            finally
            {
                if (connect != null) {
                    connect.disconnect();
                    Log.v(LOG_TAG, "Disconnected from: " + params[0]);
                }
            }
            return null;
        }
    }

}
