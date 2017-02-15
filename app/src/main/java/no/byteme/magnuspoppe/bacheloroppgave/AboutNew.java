package no.byteme.magnuspoppe.bacheloroppgave;

import android.app.Activity;
import android.os.Bundle;

/**
 * DENNE KLASSEN EKSISTERER KUN FORDI DEN VANLIGE "ABOUT" SIDEN
 * HAR EN KJEMPE FEIL DER DEN REPITERER TEKST MÅ EN MERKELIG MÅTE.
 * DET SER UT TIL AT JEG HAR KLART Å GJØRE DETTE FOR ALLE SLIKE
 * "SCROLLING ACTIVITY" TYPER.
 */
public class AboutNew extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_new);
    }
}
