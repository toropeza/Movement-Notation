package com.thomas.movementnotation;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class EditFlowProperties extends Activity {

    FlowsDBAdapter flowsDBAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_flow_properties);

        flowsDBAdapter = new FlowsDBAdapter(EditFlowProperties.this);
        flowsDBAdapter.open();

        Bundle extras = getIntent().getExtras();
        final long flowID = extras.getLong("flowID");
        final String name = extras.getString("name");
        final String description = extras.getString("description");

        final EditText editNameView = (EditText) findViewById(R.id.editFlowProp_name);
        editNameView.setText(name);

        final EditText editDescripionView = (EditText) findViewById(R.id.editFlowProp_description);
        if (description != null) {
            if (description.length() > 0) {
                editDescripionView.setText(description);
            } else {
                editDescripionView.setText("No description");
            }
        }


        Button doneButton = (Button) findViewById(R.id.editFlowProp_done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flowsDBAdapter.updateRow(flowID, editNameView.getText().toString(), editDescripionView.getText().toString());
                Intent intent = new Intent(EditFlowProperties.this, ViewFlow.class);
                intent.putExtra("flowID", flowID);
                intent.putExtra("name", editNameView.getText().toString());
                intent.putExtra("description", editDescripionView.getText().toString());
                startActivity(intent);
            }
        });

        ImageButton backButton = (ImageButton) findViewById(R.id.editFlowProp_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditFlowProperties.this, ViewFlow.class);
                intent.putExtra("flowID", flowID);
                intent.putExtra("name", name);
                intent.putExtra("description", description);
                startActivity(intent);
            }
        });
    }

}
