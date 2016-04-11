package com.thomas.movementnotation;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Thomas on 7/14/2015.
 */
public class TechniqueListCursorAdapter extends CursorAdapter {
    public TechniqueListCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }


    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameView = (TextView) view.findViewById(R.id.technique_Name_list);

        String name = cursor.getString(TechniquesDBAdapter.COL_NAME);

        nameView.setText(name);

    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.technique_item, parent, false);
    }


}
