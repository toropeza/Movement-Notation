package com.thomas.movementnotation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Thomas on 8/11/2015.
 */
public class CreateFlow extends Activity {
    String name;
    String description;
    private int currentFrame = 1;
    private ArrayList<String> techniqueIDS;
    private Technique[] tmpFrame;
    private int[] cells;
    long flowID;
    HelpDBHandler helpDBHandler;

    //Temporary dataset for the flow
    //each flow has frames, and 3 rows 3 cols
    // int frame --> 2d matrix
    private LinkedList<Technique[]> frameList;

    TechniquesDBAdapter techniquesDBAdapter;
    FlowsDBAdapter flowsDBAdapter;
    FramesDBAdapter framesDBAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.create_flow);

        findViewById(R.id.create_flow_previous).setVisibility(View.INVISIBLE);
        findViewById(R.id.create_flow_DeleteFrame).setVisibility(View.INVISIBLE);
        findViewById(R.id.create_flow_next).setVisibility(View.INVISIBLE);

        helpDBHandler = new HelpDBHandler(CreateFlow.this);
        helpDBHandler.open();
        Cursor helpCursor = helpDBHandler.getAllRows();
        if (helpCursor.getCount() == 0) {
            showHelpDialog();
            helpDBHandler.insertRow(1);
        }

        //ACTION BUTTON SETUP
        ActionButton actionButton = (ActionButton) findViewById(R.id.addFrame);
        actionButton.setButtonColor(getResources().getColor(R.color.fab_material_green_500));
        actionButton.setImageResource(R.drawable.fab_plus_icon);
        actionButton.show();

        //getting all of the techniqueIDS
        techniquesDBAdapter = new TechniquesDBAdapter(CreateFlow.this);
        techniquesDBAdapter.open();
        final Cursor techniqueCursor = techniquesDBAdapter.getAllRows();

        techniqueIDS = new ArrayList<>();
        while (!techniqueCursor.isAfterLast()) {
            String id = techniqueCursor.getString(TechniquesDBAdapter.COL_ROWID);
            techniqueIDS.add(id);
            techniqueCursor.moveToNext();
        }

        flowsDBAdapter = new FlowsDBAdapter(CreateFlow.this);
        flowsDBAdapter.open();
        framesDBAdapter = new FramesDBAdapter(CreateFlow.this);
        framesDBAdapter.open();

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        description = bundle.getString("description");


        TextView nameView = (TextView) findViewById(R.id.create_flow_name);
        nameView.setText(name);

        frameList = new LinkedList<>();
        tmpFrame = new Technique[4];
        frameList.add(tmpFrame);

        setFrameCount();

        final ArrayList<String> orderedIds = new ArrayList<String>();

        TextView r0c0 = (TextView) findViewById(R.id.r0c0text);
        TextView r0c1 = (TextView) findViewById(R.id.r0c1text);
        TextView r1c0 = (TextView) findViewById(R.id.r1c0text);
        TextView r1c1 = (TextView) findViewById(R.id.r1c1text);


        cells = new int[4];
        cells[0] = (R.id.r0c0text);
        cells[1] = (R.id.r0c1text);
        cells[2] = (R.id.r1c0text);
        cells[3] = (R.id.r1c1text);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CreateFlow.this, android.R.layout.select_dialog_singlechoice);
        for (String id : techniqueIDS) {
            orderedIds.add(id);
            Long dbid = Long.valueOf(id);
            Cursor element = techniquesDBAdapter.getRow(dbid);
            arrayAdapter.add(element.getString(TechniquesDBAdapter.COL_NAME));
        }
        arrayAdapter.add("[None]");
        orderedIds.add("[None]");

        View.OnClickListener cellClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateFlow.this);
                builder.setTitle("Pick a Technique");

                final TextView cell = (TextView) v;


                builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pickedID;
                        pickedID = orderedIds.get(which);
                        int cellID = cell.getId();
                        if (pickedID.equals("[None]")) {
                            cell.setText("");
                            switch (cellID) {
                                case R.id.r0c0text:
                                    tmpFrame[0] = null;
                                    break;
                                case R.id.r0c1text:
                                    tmpFrame[1] = null;
                                    break;
                                case R.id.r1c0text:
                                    tmpFrame[2] = null;
                                    break;
                                case R.id.r1c1text:
                                    tmpFrame[3] = null;
                                    break;
                            }
                        } else {
                            Cursor cursorpicked = techniquesDBAdapter.getRow(Long.valueOf(pickedID));
                            cell.setText(cursorpicked.getString(TechniquesDBAdapter.COL_NAME));

                            switch (cellID) {
                                case R.id.r0c0text:
                                    tmpFrame[0] = new Technique(cursorpicked.getString(TechniquesDBAdapter.COL_NAME),
                                            cursorpicked.getInt(TechniquesDBAdapter.COL_ROWID));
                                    break;
                                case R.id.r0c1text:
                                    tmpFrame[1] = new Technique(cursorpicked.getString(TechniquesDBAdapter.COL_NAME),
                                            cursorpicked.getInt(TechniquesDBAdapter.COL_ROWID));
                                    break;
                                case R.id.r1c0text:
                                    tmpFrame[2] = new Technique(cursorpicked.getString(TechniquesDBAdapter.COL_NAME),
                                            cursorpicked.getInt(TechniquesDBAdapter.COL_ROWID));
                                    break;
                                case R.id.r1c1text:
                                    tmpFrame[3] = new Technique(cursorpicked.getString(TechniquesDBAdapter.COL_NAME),
                                            cursorpicked.getInt(TechniquesDBAdapter.COL_ROWID));
                                    break;
                            }
                        }
                    }
                });
                builder.show();
            }
        };

        r0c0.setOnClickListener(cellClickListener);
        r0c1.setOnClickListener(cellClickListener);
        r1c0.setOnClickListener(cellClickListener);
        r1c1.setOnClickListener(cellClickListener);
    }

    public void backButtonClick(View view) {
        Intent backIntent = new Intent(CreateFlow.this, AddFlow.class);
        startActivity(backIntent);

    }

    /**
     * Happens when the user clicks to the next frame
     */
    public void nextFrame(View view) {
        currentFrame++;

        //if the next frame has never been reached create a new tmpFrame
        if (currentFrame + 1 >= frameList.size()) {
            findViewById(R.id.create_flow_next).setVisibility(View.INVISIBLE);
        }
        tmpFrame = frameList.get(currentFrame - 1);
        populateCells(tmpFrame);

        setFrameCount();

        findViewById(R.id.create_flow_previous).setVisibility(View.VISIBLE);
        findViewById(R.id.create_flow_DeleteFrame).setVisibility(View.VISIBLE);
    }

    /**
     * Happens when the use clicks the previous button to view the previous frame
     */
    public void prevFrame(View view) {
        currentFrame--;
        if (currentFrame == 1) {
            findViewById(R.id.create_flow_previous).setVisibility(View.INVISIBLE);
            findViewById(R.id.create_flow_DeleteFrame).setVisibility(View.INVISIBLE);
        }
        if (currentFrame > 0) {
            tmpFrame = frameList.get(currentFrame - 1);
            populateCells(tmpFrame);
            setFrameCount();
        }
        findViewById(R.id.create_flow_next).setVisibility(View.VISIBLE);
    }

    public void populateCells(Technique[] techniques) {
        for (int i = 0; i < cells.length; i++) {
            if (techniques[i] != null) {
                TextView cell = (TextView) findViewById(cells[i]);
                cell.setText(techniques[i].getName());
            } else {
                TextView cell = (TextView) findViewById(cells[i]);
                cell.setText("");
            }
        }
    }

    public void setFrameCount() {
        TextView frameCount = (TextView) findViewById(R.id.frameCount);
        frameCount.setText(String.valueOf(currentFrame));
    }

    public void doneClick(View view) {
        flowID = flowsDBAdapter.insertRow(name, description);
        if (frameList.size() > 0) {

            for (int i = 0; i < frameList.size(); i++) {
                long[] ids = new long[4];

                Technique[] frame = frameList.get(i);
                for (int j = 0; j < frame.length; j++) {
                    Technique technique = frame[j];
                    if (technique != null) {
                        ids[j] = technique.getId();
                        techniquesDBAdapter.markUsed(technique.getId());
                    }
                }
                framesDBAdapter.insertRow(flowID, i, ids[0], ids[1], ids[2], ids[3]);
            }

            startActivity(new Intent(CreateFlow.this, MainActivity.class));
        }
    }

    public void deleteFrame(View view) {
        currentFrame--;
        if (currentFrame == 1) {
            findViewById(R.id.create_flow_previous).setVisibility(View.INVISIBLE);
            findViewById(R.id.create_flow_DeleteFrame).setVisibility(View.INVISIBLE);
        }
        //if the next frame has never been reached create a new tmpFrame
        if (currentFrame + 1 >= frameList.size()) {
            findViewById(R.id.create_flow_next).setVisibility(View.INVISIBLE);
        }
        tmpFrame = frameList.get(currentFrame - 1);
        frameList.remove(currentFrame);
        populateCells(tmpFrame);
        setFrameCount();

    }

    public void addFrame(View view) {
        if (currentFrame == 1) {
            findViewById(R.id.create_flow_previous).setVisibility(View.VISIBLE);
            findViewById(R.id.create_flow_DeleteFrame).setVisibility(View.VISIBLE);
        }
        currentFrame++;
        tmpFrame = new Technique[4];
        for (Technique technique : tmpFrame) {
            technique = new Technique();
        }
        frameList.add(currentFrame - 1, tmpFrame);
        populateCells(tmpFrame);
        setFrameCount();
    }

    public void helpClick(View view) {
        showHelpDialog();
    }

    public void showHelpDialog() {
        final AlertDialog.Builder welcomeDialog = new AlertDialog.Builder(CreateFlow.this);
        welcomeDialog.setTitle("Welcome to Flow Creation!");
        welcomeDialog.setMessage("Each square represents a portion of your body.\n\nThe top two squares represent the top left and" +
                " top right of your body and the bottom two squares are for the bottom left and bottom right of your body. " +
                "\n\nTap on one of them to select a technique then add new frames to create a sequence.");
        welcomeDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        welcomeDialog.show();
    }

}
