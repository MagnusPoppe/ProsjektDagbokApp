package no.byteme.magnuspoppe.bacheloroppgave;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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

    private static final String LOG_TAG = "WRITE_NEW_ACTIVITY";

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

        // Setting up spinner owner
        owner = (Spinner) findViewById(R.id.write_owner_spinner);;

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
            this,
            R.array.users,
            android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        owner.setAdapter(adapter);

        // Checking for edit mode:
        Intent intent = getIntent();
        if (intent.hasExtra(MainActivity.INNLEGG_TAG))
        {
            editMode = true;
            innlegg = new DagbokInnlegg(intent.getBundleExtra(MainActivity.INNLEGG_TAG));
            headline.setText(innlegg.getTitle());
            content.setText(innlegg.getContent());
        }
        else editMode = false;

        // Setting up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.write_new_toolbar);
        setActionBar(toolbar);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    /**
     * Event method. Happens when the FAB upload button is pressed.
     * Prompts the user with a preview of the post.
     * @param v
     */
    public void onUploadButtonClicked(View v)
    {
        promptUploadVisible(true);
    }

    /**
     * Shows or hides the preview window.
     * @param visible
     */
    private void promptUploadVisible(boolean visible)
    {
        previewContent.setText(content.getText());
        previewHeadline.setText(headline.getText());

        if (visible)
            previewOverlay.setVisibility( View.VISIBLE );
        else
            previewOverlay.setVisibility( View.GONE );
    }

    /**
     * Event method. Happens when the button "Cancel" is pressed.
     * Cancels the upload.
     * @param v
     */
    public void cancelUpload(View v)
    {
        promptUploadVisible(false);
    }

    /**
     * Event method. Happens when the button "upload" is pressed.
     * Executes the upload to server. Uses async task and finishes the
     * acitivty.
     * @param v
     */
    public void confirmUpload(View v)
    {
        String url = "";
        if (editMode)
        {
            url += BYTEME_API + DIARY_PUT + "?" +
                    "title="    + headline.getText().toString() + "&" +
                    "content="  + content.getText().toString()  + "&" +
                    "owner="    + getOwner() + "&" +
                    "id="       + innlegg.getId();
        }
        else
        {
            url += BYTEME_API + DIARY_POST + "?" +
                    "title="    + headline.getText().toString() + "&" +
                    "content="  + content.getText().toString()  + "&" +
                    "owner="    + getOwner();
        }

        ASyncServiceDiaryPost service = new ASyncServiceDiaryPost();
        service.execute(url);
        finish();
    }

    /**
     * @return owner id from spinner object "owner"
     */
    private String getOwner()
    {
        return ""+(owner.getSelectedItemPosition()+1);
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
                String safeUrl = params[0].replace(" ", "%20");
                URL url = new URL(safeUrl);
                // Lager HTTP request og kobler opp:
                connect = (HttpURLConnection) url.openConnection();
                connect.setRequestMethod("POST");
                connect.connect();
                Log.v(LOG_TAG, "Connected to: " + url.getContent());
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
