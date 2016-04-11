package com.thomas.movementnotation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Thomas Oropeza on 6/19/2015.
 */
public class ViewTechnique extends Activity {
    FramesDBAdapter framesDBAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.view_technique);

        final TechniquesDBAdapter myDB = new TechniquesDBAdapter(ViewTechnique.this);
        myDB.open();

        final FramesDBAdapter framesDBAdapter = new FramesDBAdapter(ViewTechnique.this);
        framesDBAdapter.open();

        TextView nameView = (TextView) findViewById(R.id.technique_view_name);
        TextView descriptionView = (TextView) findViewById(R.id.technique_view_description);
        TextView bodyView = (TextView) findViewById(R.id.technique_view_body);

        Bundle techniqueInfo = getIntent().getExtras();


        final String id = techniqueInfo.getString("id");
        Cursor row = myDB.getRow(Integer.valueOf(id));
        String name = row.getString(TechniquesDBAdapter.COL_NAME);
        String description = row.getString(TechniquesDBAdapter.COL_DESCRIPTION);
        String body = row.getString(TechniquesDBAdapter.COL_BODY);

        nameView.setText(name);
        descriptionView.setText(description);
        bodyView.setText(body + " Technique");

        ImageButton backButton = (ImageButton) findViewById(R.id.view_technique_back);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(ViewTechnique.this, MainActivity.class);
                startActivity(backIntent);
            }
        });

        ImageButton deleteButton = (ImageButton) findViewById(R.id.view_technique_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewTechnique.this);

                final Cursor item = myDB.getRow(Integer.valueOf(id));
                String name = item.getString(TechniquesDBAdapter.COL_NAME);

                builder.setCancelable(true);
                builder.setTitle("Delete: " + name + " ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (framesDBAdapter.containsTechnique(Long.valueOf(item.getString(TechniquesDBAdapter.COL_ROWID)))) {
                            Toast.makeText(ViewTechnique.this, "Cannot Delete Technique in Use", Toast.LENGTH_LONG).show();
                        } else {
                            dialog.dismiss();
                            myDB.deleteRow(Long.valueOf(item.getString(TechniquesDBAdapter.COL_ROWID)));

                            Toast.makeText(ViewTechnique.this, "Technique Deleted!", Toast.LENGTH_LONG).show();

                            Intent backIntent = new Intent(ViewTechnique.this, MainActivity.class);
                            startActivity(backIntent);
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

            }
        });

        ImageButton editButton = (ImageButton) findViewById(R.id.view_technique_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewTechnique.this, EditTechnique.class);
                Bundle idInfo = new Bundle();
                idInfo.putString("id", id);
                intent.putExtras(idInfo);
                startActivity(intent);
            }
        });


    }
}
