package com.thomas.movementnotation;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * Created by Thomas on 6/29/2015.
 */
public class EditTechnique extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.edit_technique);

        Bundle bundle = getIntent().getExtras();
        final String id = bundle.getString("id");

        final TechniquesDBAdapter myDb = new TechniquesDBAdapter(EditTechnique.this);
        myDb.open();

        Cursor row = myDb.getRow(Long.valueOf(id));


        EditText nameChange = (EditText) findViewById(R.id.nameChange);
        EditText descriptionChange = (EditText) findViewById(R.id.descriptionChange);

        nameChange.setText(row.getString(TechniquesDBAdapter.COL_NAME));

        String descript = row.getString(TechniquesDBAdapter.COL_DESCRIPTION);
        if (!descript.equals("No Description")) {
            descriptionChange.setText(descript);
        } else {
            descriptionChange.setHint("Enter a Description");
        }


        String body = row.getString(TechniquesDBAdapter.COL_BODY);
        if (body.equals("Hand")) {
            RadioButton hand = (RadioButton) findViewById(R.id.editHand);
            hand.setChecked(true);
        } else if (body.equals("Arm")) {
            RadioButton arm = (RadioButton) findViewById(R.id.editArm);
            arm.setChecked(true);
        } else if (body.equals("Leg")) {
            RadioButton leg = (RadioButton) findViewById(R.id.editLeg);
            leg.setChecked(true);
        }


        //Button listeners
        ImageButton backButton = (ImageButton) findViewById(R.id.edit_technique_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(EditTechnique.this, ViewTechnique.class);
                Bundle backBundle = new Bundle();
                backBundle.putString("id", id);
                backIntent.putExtras(backBundle);
                startActivity(backIntent);
            }
        });

        Button savebutton = (Button) findViewById(R.id.edit_button_save);
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor row = myDb.getRow(Long.valueOf(id));

                //intent to go back to view technique
                Intent backIntent = new Intent(EditTechnique.this, ViewTechnique.class);
                backIntent.putExtra("id", id);

                EditText nameChange = (EditText) findViewById(R.id.nameChange);
                String newName = nameChange.getText().toString();

                String newDescription = ((EditText) findViewById(R.id.descriptionChange)).getText().toString();

                RadioGroup bodyGroupNew = (RadioGroup) findViewById(R.id.edit_technique_radio_group);
                RadioButton newBodyButton = (RadioButton) findViewById(bodyGroupNew.getCheckedRadioButtonId());
                String newBody = newBodyButton.getText().toString();

                if (newName.equals(row.getString(TechniquesDBAdapter.COL_NAME)) &&
                        newDescription.equals(row.getString(TechniquesDBAdapter.COL_DESCRIPTION)) &&
                        newBody.equals(row.getString(TechniquesDBAdapter.COL_BODY))
                        ) {
                    startActivity(backIntent);
                } else {
                    myDb.updateRow(Long.valueOf(id), newName, newDescription, newBody);
                    Toast.makeText(EditTechnique.this, "Success!", Toast.LENGTH_LONG).show();
                    startActivity(backIntent);
                }
            }
        });

    }
}
