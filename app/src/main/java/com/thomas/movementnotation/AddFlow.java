package com.thomas.movementnotation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


/**
 * Created by Thomas on 6/1/2015.
 */
public class AddFlow extends Activity {
    EditText name;
    EditText description;


    FlowsDBAdapter myDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.add_flow);
        myDb = new FlowsDBAdapter(this);
        myDb.open();

        name = (EditText) findViewById(R.id.add_flow_name);
        name.setSelection(0);
        description = (EditText) findViewById(R.id.add_flow_descriptionText);

        final ImageButton backButton = (ImageButton) findViewById(R.id.button_back_add_flow);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddFlow.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button nextButton = (Button) findViewById(R.id.add_flow_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().length() > 0) {
                    if (name.getText().toString().length() <= 40) {
                        Intent intent = new Intent(AddFlow.this, CreateFlow.class);
                        intent.putExtra("name", name.getText().toString());
                        intent.putExtra("description", description.getText().toString());
                        startActivity(intent);
                    } else {
                        Toast.makeText(AddFlow.this, "Keep Flow Name Brief", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddFlow.this, "Name Required", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


}
