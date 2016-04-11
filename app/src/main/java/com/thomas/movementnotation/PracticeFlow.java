package com.thomas.movementnotation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class PracticeFlow extends Activity {

    private FlowsDBAdapter flowsDBAdapter;
    private FramesDBAdapter framesDBAdapter;
    private TechniquesDBAdapter techniquesDBAdapter;
    private TextView[] cells;
    private ArrayList<String> techniqueIDS;
    private Technique[] frame;
    private ArrayList<String> orderedIds;
    private ArrayList<Technique[]> frameList;
    private int currentFrame = 1;
    private int totalFrames;
    private HelpDBHandler helpDBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practice_flow);

        //Load in database
        flowsDBAdapter = new FlowsDBAdapter(PracticeFlow.this);
        framesDBAdapter = new FramesDBAdapter(PracticeFlow.this);
        techniquesDBAdapter = new TechniquesDBAdapter(PracticeFlow.this);
        flowsDBAdapter.open();
        framesDBAdapter.open();
        techniquesDBAdapter.open();

        helpDBHandler = new HelpDBHandler(PracticeFlow.this);
        helpDBHandler.open();
        Cursor helpCursor = helpDBHandler.getAllRows();
        //one would mean that a flow was created but not practiced
        if (helpCursor.getCount() == 1) {
            showHelpDialog();
            helpDBHandler.insertRow(1);
        }

        //Load Properties
        Bundle bundle = getIntent().getExtras();
        final long flowID = bundle.getLong("flowID");
        final String name = bundle.getString("name");
        final String description = bundle.getString("description");

        //load in cells
        TextView r0c0 = (TextView) findViewById(R.id.r0c0text_practice);
        TextView r0c1 = (TextView) findViewById(R.id.r0c1text_practice);
        TextView r1c0 = (TextView) findViewById(R.id.r1c0text_practice);
        TextView r1c1 = (TextView) findViewById(R.id.r1c1text_practice);

        cells = new TextView[4];
        cells[0] = r0c0;
        cells[1] = r0c1;
        cells[2] = r1c0;
        cells[3] = r1c1;

        //Display properties
        TextView nameView = (TextView) findViewById(R.id.practiceFlow_name);
        nameView.setText(name);

        //Load in all of the frames
        Cursor framesCursor = framesDBAdapter.getFlow(flowID);
        totalFrames = framesCursor.getCount();
        frameList = loadFlows(framesCursor);

        //display initial frame number
        setFrameCount();

        final Button previousButton = (Button) findViewById(R.id.practice_flow_previous);
        previousButton.setVisibility(View.INVISIBLE);

        //Display contents of first frame
        frame = frameList.get(currentFrame - 1);
        populateCells(frame);
        //Load the rest of the frame contents in a collection

        final ImageButton backButton = (ImageButton) findViewById(R.id.button_back_practice_flow);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PracticeFlow.this, ViewFlow.class);
                intent.putExtra("flowID", flowID);
                intent.putExtra("name", name);
                intent.putExtra("description", description);
                startActivity(intent);
            }
        });

        final Button nextButton = (Button) findViewById(R.id.practice_flow_next);
        if (totalFrames == 1) {
            nextButton.setVisibility(View.INVISIBLE);
        }
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentFrame++;
                if (currentFrame >= totalFrames) {
                    nextButton.setVisibility(View.INVISIBLE);
                }
                frame = frameList.get(currentFrame - 1);
                populateCells(frame);
                if (currentFrame > 1) {
                    previousButton.setVisibility(View.VISIBLE);
                }
                setFrameCount();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentFrame--;
                if (currentFrame <= 1) {
                    previousButton.setVisibility(View.INVISIBLE);
                }
                if (currentFrame < totalFrames) {
                    nextButton.setVisibility(View.VISIBLE);
                }
                frame = frameList.get(currentFrame - 1);
                populateCells(frame);
                setFrameCount();
            }
        });

        final Cursor techniqueCursor = techniquesDBAdapter.getAllRows();
        techniqueIDS = new ArrayList<>();
        orderedIds = new ArrayList<String>();
        while (!techniqueCursor.isAfterLast()) {
            String id = techniqueCursor.getString(TechniquesDBAdapter.COL_ROWID);
            techniqueIDS.add(id);
            techniqueCursor.moveToNext();
        }

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PracticeFlow.this, android.R.layout.select_dialog_singlechoice);
        for (String id : techniqueIDS) {
            orderedIds.add(id);
            Long dbid = Long.valueOf(id);
            Cursor element = techniquesDBAdapter.getRow(dbid);
            arrayAdapter.add(element.getString(TechniquesDBAdapter.COL_NAME));
        }
        View.OnClickListener cellClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(PracticeFlow.this);

                final TextView cell = (TextView) v;

                int cellID = cell.getId();
                Technique pickedTechnique = null;
                switch (cellID) {
                    case R.id.r0c0text_practice:
                        pickedTechnique = frame[0];
                        break;
                    case R.id.r0c1text_practice:
                        pickedTechnique = frame[1];
                        break;
                    case R.id.r1c0text_practice:
                        pickedTechnique = frame[2];
                        break;
                    case R.id.r1c1text_practice:
                        pickedTechnique = frame[3];
                        break;
                }
                if (pickedTechnique != null) {
                    builder.setTitle(pickedTechnique.getName());
                    builder.setMessage(pickedTechnique.getBody() + " Technique" + "\n\n" + pickedTechnique.getDescription());
                    builder.show();

                }
            }
        };

        r0c0.setOnClickListener(cellClickListener);
        r0c1.setOnClickListener(cellClickListener);
        r1c0.setOnClickListener(cellClickListener);
        r1c1.setOnClickListener(cellClickListener);

        ImageButton editFlow = (ImageButton) findViewById(R.id.practice_flow_edit);
        editFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PracticeFlow.this, EditFlow.class);
                intent.putExtra("flowID", flowID);
                intent.putExtra("name", name);
                intent.putExtra("description", description);
                startActivity(intent);
            }
        });
    }

    private ArrayList<Technique[]> loadFlows(Cursor framesCursor) {
        ArrayList<Technique[]> frameList = new ArrayList<>();
        framesCursor.moveToFirst();
        while (!framesCursor.isAfterLast()) {
            Technique[] frameArray = new Technique[4];
            for (int i = 0; i < cells.length; i++) {
                int techniqueID = framesCursor.getInt(FramesDBAdapter.cellDBColumns[i]);
                if (techniqueID != 0) {
                    Cursor techniqueCursor = techniquesDBAdapter.getRow(techniqueID);
                    String techniqueName = techniqueCursor.getString(TechniquesDBAdapter.COL_NAME);
                    String description = techniqueCursor.getString(TechniquesDBAdapter.COL_DESCRIPTION);
                    String body = techniqueCursor.getString(TechniquesDBAdapter.COL_BODY);
                    Technique technique = new Technique(techniqueName, techniqueID, description, body);
                    frameArray[i] = technique;
                }
            }
            frameList.add(frameArray);
            framesCursor.moveToNext();
        }

        return frameList;
    }

    public void populateCells(Technique[] frame) {
        for (int i = 0; i < frame.length; i++) {
            cells[i].setText("");

        }
        for (int i = 0; i < frame.length; i++) {
            if (frame[i] != null) {
                cells[i].setText(frame[i].getName());
            }
        }
    }

    public void setFrameCount() {
        TextView frameCount = (TextView) findViewById(R.id.frameCount_practice);
        frameCount.setText(String.valueOf(currentFrame));
    }

    public void helpClick(View view) {
        showHelpDialog();
    }

    private void showHelpDialog() {
        final AlertDialog.Builder welcomeDialog = new AlertDialog.Builder(PracticeFlow.this);
        welcomeDialog.setTitle("Welcome to Flow Practice!");
        welcomeDialog.setMessage("Here you are able to cycle through the flow that you just created.\n\n" +
                "Tap on any of the techniques in the frame to see details about it");
        welcomeDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        welcomeDialog.show();
    }

}
