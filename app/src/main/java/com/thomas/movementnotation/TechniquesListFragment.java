package com.thomas.movementnotation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.software.shell.fab.ActionButton;

/**
 * Created by Thomas on 5/31/2015.
 */
public class TechniquesListFragment extends Fragment {
    TechniquesDBAdapter myDB;
    FramesDBAdapter framesDBAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.techniquelistfragment, container, false);

        myDB = new TechniquesDBAdapter(getActivity());
        myDB.open();
        framesDBAdapter = new FramesDBAdapter(getActivity());
        framesDBAdapter.open();


        //populating listview
        final ListView listView = (ListView) view.findViewById(R.id.technique_list_view);
        Cursor allData = myDB.getAllRows();

        final TechniqueListCursorAdapter listCursor = new TechniqueListCursorAdapter(getActivity(), allData);


        listView.setAdapter(listCursor);


        //ACTION BUTTON SETUP
        ActionButton actionButton = (ActionButton) view.findViewById(R.id.add_technique_action_button);
        actionButton.setButtonColor(getResources().getColor(R.color.fab_material_green_500));
        actionButton.setImageResource(R.drawable.fab_plus_icon);
        actionButton.show();

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddTechnique.class);
                startActivity(intent);
            }
        });


        //SETTING DELETE OPTION FROM LISTVIEW
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                final Cursor item = myDB.getRow(listCursor.getItemId(position));
                String name = item.getString(TechniquesDBAdapter.COL_NAME);
                String description = item.getString(TechniquesDBAdapter.COL_DESCRIPTION);
                final int techniqueID = item.getInt(TechniquesDBAdapter.COL_ROWID);

                builder.setCancelable(true);
                builder.setTitle("Delete: " + name + " ?");
                builder.setMessage(description);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (framesDBAdapter.containsTechnique(techniqueID)) {
                            Toast.makeText(getActivity(), "Cannot Delete Technique in Use", Toast.LENGTH_LONG).show();
                        } else {
                            myDB.deleteRow(item.getLong(TechniquesDBAdapter.COL_ROWID));
                            Toast.makeText(getActivity(), "Technique Deleted!", Toast.LENGTH_LONG).show();
                            Cursor newCursor = myDB.getAllRows();
                            listCursor.changeCursor(newCursor);
                        }

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

                return true;
            }
        });

        //SETTING EDIT OPTION ON LISTVIEW
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor item = myDB.getRow(listCursor.getItemId(position));

                String idStr = item.getString(TechniquesDBAdapter.COL_ROWID);

                Bundle bundle = new Bundle();
                bundle.putString("id", idStr);

                Intent intent = new Intent(getActivity(), ViewTechnique.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myDB.close();

    }
}
