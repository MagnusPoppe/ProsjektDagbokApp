package no.byteme.magnuspoppe.bacheloroppgave;

import android.os.Bundle;
import static no.byteme.magnuspoppe.bacheloroppgave.MainActivity.INNLEGG_CONTENT;
import static no.byteme.magnuspoppe.bacheloroppgave.MainActivity.INNLEGG_DATE;
import static no.byteme.magnuspoppe.bacheloroppgave.MainActivity.INNLEGG_ID;
import static no.byteme.magnuspoppe.bacheloroppgave.MainActivity.INNLEGG_OWNER;
import static no.byteme.magnuspoppe.bacheloroppgave.MainActivity.INNLEGG_TITLE;

/**
 * Created by MagnusPoppe on 04/02/2017.
 */

public class DagbokInnlegg
{
    // PRIVATE CONSTANTS FOR DATE FORMATTING
    final static private int FULL_DATE = 0;
    final static private int TIMESTAMP = 1;
    final static private int YEAR   = 0;
    final static private int MONTH  = 1;
    final static private int DAY    = 2;

    private int     id;
    private String  title;
    private String  date;
    private String  formattedDate;
    private String  author;
    private String  content;

    // GETTERS:
    public int getId()
    {
        return id;
    }
    public String getTitle()
    {
        return title;
    }
    public String getDate()
    {
        return date;
    }
    public String getAuthor()
    {
        return author;
    }
    public String getContent()
    {
        return content;
    }
    public String getFormattedDate()
    {
        return formattedDate;
    }

    /**
     * Default constructor.
     * @param id of post
     * @param title of post
     * @param date posted
     * @param author of post
     * @param content in post
     */
    public DagbokInnlegg( int id, String title, String  date, String author, String content)
    {
        this.id         = id;
        this.title      = title;
        this.author     = author;
        this.content    = content;
        this.date       = date;
        this.formattedDate = formatDate(date);
    }

    /**
     * Unpacks a bundle to construct the object.
     * @param bundle
     * @return a formatted dagbokinnlegg.
     */
    public DagbokInnlegg(Bundle bundle)
    {
        this.id         = bundle.getInt(MainActivity.INNLEGG_ID);
        this.title      = bundle.getString(MainActivity.INNLEGG_TITLE);
        this.date       = bundle.getString(MainActivity.INNLEGG_DATE);
        this.author     = bundle.getString(MainActivity.INNLEGG_OWNER);
        this.content    = bundle.getString(MainActivity.INNLEGG_CONTENT);
    }

    /**
     * Packs a Object of the class DagbokInnlegg into a bundle
     * @return packed bundle.
     */
    public Bundle toBundle()
    {
        Bundle dagbokInnlegg = new Bundle();
        dagbokInnlegg.putInt(INNLEGG_ID, getId());
        dagbokInnlegg.putString(INNLEGG_DATE, getDate());
        dagbokInnlegg.putString(INNLEGG_OWNER, getAuthor());
        dagbokInnlegg.putString(INNLEGG_TITLE, getTitle());
        dagbokInnlegg.putString(INNLEGG_CONTENT, getContent());
        return dagbokInnlegg;
    }

    /**
     * Formats an SQL date to better fit the gui elements.
     * Only used with presentation.
     * @param date formatted in SQL YYYY-MM-DD
     * @return Date with format: "DD. monthname"
     */
    private String formatDate(String date)
    {
        String[] month = {
            "januar", "februar", "mars", "april",
            "mai", "juni", "juli", "juni", "august",
            "september", "oktober", "november", "desember"
        };

        String[] dateFragments = date.split(" ");
        dateFragments = dateFragments[FULL_DATE].split("-");
        int day =  Integer.parseInt(dateFragments[DAY]);
        int monthNumber = Integer.parseInt(dateFragments[MONTH] ) - 1;

        return  day + ". " + month[monthNumber];
    }

    public String toString()
    {
        return toJSONString();
    }

    /**
     * @return preformatted JSON string.
     */
    public String toJSONString()
    {
        return "{" +
                    "id :" + id + "," +
                    "date : "+ date + "," +
                    "title :" + title + "," +
                    "author :" + author + "," +
                    "content :" + content +
                "}";
    }
}
