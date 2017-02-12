package no.byteme.magnuspoppe.bacheloroppgave;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DagbokAdapter extends BaseAdapter
{
    Context context;
    ArrayList<DagbokInnlegg> innleggene;
    LayoutInflater in;

    public DagbokAdapter(Context c, ArrayList<DagbokInnlegg> dagbokInnlegg)
    {
        context = c;
        innleggene = dagbokInnlegg;
        in = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
    }
    public int getCount()
    {
        return  innleggene.size();
    }

    public Object getItem(int position)
    {
        return innleggene.get(position);
    }

    public long getItemId(int position)
    {
        return innleggene.get(position).getId();
    }
    
    public View getView(int position, View view, ViewGroup parent)
    {
        ViewHolder vh;
        if (view == null)
        {
            view = in.inflate(R.layout.content_diary_card, null);

            vh = new ViewHolder();
            vh.title    = (TextView) view.findViewById(R.id.innlegg_header);
            vh.author   = (TextView) view.findViewById(R.id.innlegg_author);
            vh.date     = (TextView) view.findViewById(R.id.innlegg_date);
            vh.content  = (TextView) view.findViewById(R.id.innlegg_content);

            view.setTag(vh);
        } else
        {
            vh = (ViewHolder) view.getTag();
        }

        DagbokInnlegg innlegg = innleggene.get(position);
        vh.title.setText(innlegg.getTitle());
        vh.date.setText(innlegg.getFormattedDate()); // Kan byttes med innlegg.date for SQL format dato.
        vh.author.setText(innlegg.getAuthor());
        vh.content.setText(innlegg.getContent());

        return view;
    }

    private static class ViewHolder
    {
        public TextView title;
        public TextView date;
        public TextView author;
        public TextView content;
    }
}