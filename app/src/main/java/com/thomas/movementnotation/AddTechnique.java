package com.thomas.movementnotation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


/**
 * Created by Thomas on 6/1/2015.
 */
public class AddTechnique extends Activity {
    EditText name;
    EditText description;
    RadioGroup bodyChoose;

    TechniquesDBAdapter myDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.add_technique);
        myDb = new TechniquesDBAdapter(this);
        myDb.open();


        bodyChoose = (RadioGroup) findViewById(R.id.add_technique_radio_group);
        name = (EditText) findViewById(R.id.add_technique_name);
        name.requestFocus();
        description = (EditText) findViewById(R.id.add_flow_descriptionText);

        final ImageButton backButton = (ImageButton) findViewById(R.id.button_back_add_technique);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTechnique.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button addTechniqueButton = (Button) findViewById(R.id.add_technique_button);
        addTechniqueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton bodyButtonChecked = null;


                String techniqueName = name.getText().toString();
                String techniqueDescription = description.getText().toString();
                String body = "";


                if (techniqueName.length() == 0) {
                    Toast.makeText(AddTechnique.this, "Please add a name", Toast.LENGTH_LONG).show();
                } else {
                    if (bodyChoose.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(AddTechnique.this, "Please select a body", Toast.LENGTH_LONG).show();
                    } else {
                        bodyButtonChecked = (RadioButton) findViewById(bodyChoose.getCheckedRadioButtonId());
                        if (techniqueDescription.length() == 0) {
                            techniqueDescription += "No Description";
                        }
                        body = bodyButtonChecked.getText().toString();
                        myDb.insertRow(techniqueName, techniqueDescription, body);
                        Toast.makeText(AddTechnique.this, "Technique Added!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(AddTechnique.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
                //TODO set cursor in nameEditText


            }
        });
    }


}
