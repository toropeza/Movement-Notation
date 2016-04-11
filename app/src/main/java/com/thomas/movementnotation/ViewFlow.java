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
 * Created by Thomas on 8/11/2015.
 */
public class ViewFlow extends Activity {
    FlowsDBAdapter flowsDBAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.view_flow);

        flowsDBAdapter = new FlowsDBAdapter(ViewFlow.this);
        flowsDBAdapter.open();

        Bundle bundle = getIntent().getExtras();
        final long flowID = bundle.getLong("flowID");


        TextView nameView = (TextView) findViewById(R.id.view_flow_name);
        final String name = bundle.getString("name");
        nameView.setText(name);

        TextView descriptionView = (TextView) findViewById(R.id.view_flow_description);
        final String description = bundle.getString("description");
        if (description.replaceAll(" ", "").length() > 0) {
            descriptionView.setText(description);
        } else {
            descriptionView.setText("No Description");
        }

        ImageButton backButton = (ImageButton) findViewById(R.id.button_back_view_flow);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewFlow.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ImageButton deleteButton = (ImageButton) findViewById(R.id.view_flow_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewFlow.this);


                builder.setCancelable(true);
                builder.setTitle("Delete: " + name + " ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FramesDBAdapter framesDBAdapter = new FramesDBAdapter(ViewFlow.this);
                        framesDBAdapter.open();
                        flowsDBAdapter.deleteFlow(flowID);
                        framesDBAdapter.deleteRow(flowID);
                        framesDBAdapter.close();
                        Toast.makeText(ViewFlow.this, "Flow Deleted!", Toast.LENGTH_LONG).show();

                        Intent backIntent = new Intent(ViewFlow.this, MainActivity.class);
                        startActivity(backIntent);
                        dialog.dismiss();
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

        Button startFlow = (Button) findViewById(R.id.view_flow_startButton);
        startFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewFlow.this, PracticeFlow.class);
                intent.putExtra("flowID", flowID);
                intent.putExtra("name", name);
                intent.putExtra("description", description);
                startActivity(intent);
            }
        });

        ImageButton edit = (ImageButton) findViewById(R.id.view_flow_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewFlow.this, EditFlowProperties.class);
                intent.putExtra("flowID", flowID);
                intent.putExtra("name", name);
                intent.putExtra("description", description);
                startActivity(intent);
            }
        });

    }
}
