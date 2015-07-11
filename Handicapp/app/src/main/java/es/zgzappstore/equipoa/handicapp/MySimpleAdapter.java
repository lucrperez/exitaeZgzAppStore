package es.zgzappstore.equipoa.handicapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Luis on 11/07/2015.
 */
public class MySimpleAdapter extends ArrayAdapter<SimpleItem> {
    private final Context context;
    private final ArrayList<SimpleItem> itemsArray;

    public MySimpleAdapter(Context context, ArrayList<SimpleItem> itemsArray) {
        super(context, R.layout.row_simple, itemsArray);
        this.context = context;
        this.itemsArray = itemsArray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.row_simple, parent, false);

        //3. Get the views from layout
        TextView idView = (TextView) rowView.findViewById(R.id.row_simple_id);
        TextView titleView = (TextView) rowView.findViewById(R.id.row_simple_text);

        //4. Set the text for the View
        idView.setText(Integer.toString(itemsArray.get(position).getID()));
        titleView.setText(itemsArray.get(position).getTitle());

        return rowView;
    }
}
