package no.byteme.magnuspoppe.bacheloroppgave;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

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
