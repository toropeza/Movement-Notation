package com.thomas.movementnotation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.LinkedList;

public class EditFlow extends Activity {

    private String name;
    private String description;
    private long flowID;
    private int currentFrame = 0;
    private int[] cells;
    private int totalFrames;
    private TextView[] cellViews;
    private ArrayList<String> techniqueIDS;
    private LinkedList<Technique[]> frameList;
    private Technique[] tmpFrame;
    private TechniquesDBAdapter techniquesDBAdapter;
    private FlowsDBAdapter flowsDBAdapter;
    private FramesDBAdapter framesDBAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_flow);

        flowsDBAdapter = new FlowsDBAdapter(EditFlow.this);
        flowsDBAdapter.open();

        framesDBAdapter = new FramesDBAdapter(EditFlow.this);
        framesDBAdapter.open();

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        flowID = bundle.getLong("flowID");
        description = bundle.getString("description");

        findViewById(R.id.editFlow_previous).setVisibility(View.INVISIBLE);
        findViewById(R.id.editFlow_DeleteFrame).setVisibility(View.INVISIBLE);

        //ACTION BUTTON SETUP
        ActionButton actionButton = (ActionButton) findViewById(R.id.addFrame);
        actionButton.setButtonColor(getResources().getColor(R.color.fab_material_green_500));
        actionButton.setImageResource(R.drawable.fab_plus_icon);
        actionButton.show();

        TextView nameView = (TextView) findViewById(R.id.editFlow_name);
        nameView.setText(name);

        //getting all of the techniqueIDS
        techniquesDBAdapter = new TechniquesDBAdapter(EditFlow.this);
        techniquesDBAdapter.open();
        final Cursor techniqueCursor = techniquesDBAdapter.getAllRows();

        techniqueIDS = new ArrayList<>();
        while (!techniqueCursor.isAfterLast()) {
            String id = techniqueCursor.getString(TechniquesDBAdapter.COL_ROWID);
            techniqueIDS.add(id);
            techniqueCursor.moveToNext();
        }

        frameList = new LinkedList<>();
        tmpFrame = new Technique[4];
        frameList.add(tmpFrame);

        setFrameCount();

        final ArrayList<String> orderedIds = new ArrayList<String>();

        TextView r0c0 = (TextView) findViewById(R.id.editFlow_r0c0text);
        TextView r0c1 = (TextView) findViewById(R.id.editFlow_r0c1text);
        TextView r1c0 = (TextView) findViewById(R.id.editFlow_r1c0text);
        TextView r1c1 = (TextView) findViewById(R.id.editFlow_r1c1text);

        cells = new int[4];
        cells[0] = (R.id.editFlow_r0c0text);
        cells[1] = (R.id.editFlow_r0c1text);
        cells[2] = (R.id.editFlow_r1c0text);
        cells[3] = (R.id.editFlow_r1c1text);

        cellViews = new TextView[4];
        cellViews[0] = r0c0;
        cellViews[1] = r0c1;
        cellViews[2] = r1c0;
        cellViews[3] = r1c1;

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EditFlow.this, android.R.layout.select_dialog_singlechoice);
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
                final AlertDialog.Builder builder = new AlertDialog.Builder(EditFlow.this);
                builder.setTitle("Pick a Technique");

                final TextView cell = (TextView) v;


                builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pickedID;
                        pickedID = orderedIds.get(which);
                        int cellID = cell.getId();
                        if (pickedID.equals("[None]")) {
                            switch (cellID) {
                                case R.id.editFlow_r0c0text:
                                    tmpFrame[0] = null;
                                    break;
                                case R.id.editFlow_r0c1text:
                                    tmpFrame[1] = null;
                                    break;
                                case R.id.editFlow_r1c0text:
                                    tmpFrame[2] = null;
                                    break;
                                case R.id.editFlow_r1c1text:
                                    tmpFrame[3] = null;
                                    break;
                            }
                        } else {
                            Cursor cursorpicked = techniquesDBAdapter.getRow(Long.valueOf(pickedID));
                            cell.setText(cursorpicked.getString(TechniquesDBAdapter.COL_NAME));

                            switch (cellID) {
                                case R.id.editFlow_r0c0text:
                                    tmpFrame[0] = new Technique(cursorpicked.getString(TechniquesDBAdapter.COL_NAME),
                                            cursorpicked.getInt(TechniquesDBAdapter.COL_ROWID));
                                    break;
                                case R.id.editFlow_r0c1text:
                                    tmpFrame[1] = new Technique(cursorpicked.getString(TechniquesDBAdapter.COL_NAME),
                                            cursorpicked.getInt(TechniquesDBAdapter.COL_ROWID));
                                    break;
                                case R.id.editFlow_r1c0text:
                                    tmpFrame[2] = new Technique(cursorpicked.getString(TechniquesDBAdapter.COL_NAME),
                                            cursorpicked.getInt(TechniquesDBAdapter.COL_ROWID));
                                    break;
                                case R.id.editFlow_r1c1text:
                                    tmpFrame[3] = new Technique(cursorpicked.getString(TechniquesDBAdapter.COL_NAME),
                                            cursorpicked.getInt(TechniquesDBAdapter.COL_ROWID));
                                    break;
                            }
                        }
                        populateCells(tmpFrame);
                        frameList.set(currentFrame, tmpFrame);

                    }
                });
                builder.show();
            }
        };

        r0c0.setOnClickListener(cellClickListener);
        r0c1.setOnClickListener(cellClickListener);
        r1c0.setOnClickListener(cellClickListener);
        r1c1.setOnClickListener(cellClickListener);

        //Load in all of the frames
        Cursor framesCursor = framesDBAdapter.getFlow(flowID);
        frameList = loadFlows(framesCursor);
        totalFrames = frameList.size();

        //Display contents of first frame
        tmpFrame = frameList.get(currentFrame);
        populateCells(tmpFrame);

        if (totalFrames == 1) {
            findViewById(R.id.editFlow__next).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Happens when the user clicks to the next frame
     */
    public void nextFrame(View view) {
        currentFrame++;
        if (currentFrame > 0) {
            findViewById(R.id.editFlow_previous).setVisibility(View.VISIBLE);
            findViewById(R.id.editFlow_DeleteFrame).setVisibility(View.VISIBLE);
        }
        if (currentFrame < totalFrames) {
            tmpFrame = frameList.get(currentFrame);
        }
        if (currentFrame + 1 >= totalFrames) {
            findViewById(R.id.editFlow__next).setVisibility(View.INVISIBLE);
        }

        populateCells(tmpFrame);
        setFrameCount();
    }

    /**
     * Happens when the use clicks the previous button to view the previous frame
     */
    public void prevFrame(View view) {
        currentFrame--;
        if (currentFrame <= 0) {
            findViewById(R.id.editFlow_previous).setVisibility(View.INVISIBLE);
            findViewById(R.id.editFlow_DeleteFrame).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.editFlow_previous).setVisibility(View.VISIBLE);
        }
        tmpFrame = frameList.get(currentFrame);
        populateCells(tmpFrame);
        setFrameCount();
        findViewById(R.id.editFlow__next).setVisibility(View.VISIBLE);
    }


    public void setFrameCount() {
        TextView frameCount = (TextView) findViewById(R.id.editFlow_frameCount);
        frameCount.setText(String.valueOf(currentFrame + 1));
    }

    public void backButtonClick(View view) {
        Intent intent = new Intent(EditFlow.this, PracticeFlow.class);
        intent.putExtra("name", name);
        intent.putExtra("flowID", flowID);
        intent.putExtra("description", description);
        startActivity(intent);
    }

    public void doneClick(View view) {
        framesDBAdapter.deleteFlow(flowID);
        if (frameList.size() > 0) {

            for (int i = 0; i < frameList.size(); i++) {
                long[] ids = new long[4];

                Technique[] frame = frameList.get(i);
                for (int j = 0; j < frame.length; j++) {
                    Technique technique = frame[j];
                    if (technique != null) {
                        ids[j] = technique.getId();
                    }
                }
                framesDBAdapter.insertRow(flowID, i, ids[0], ids[1], ids[2], ids[3]);
            }

            Intent intent = new Intent(EditFlow.this, PracticeFlow.class);
            intent.putExtra("flowID", flowID);
            intent.putExtra("name", name);
            intent.putExtra("description", description);
            startActivity(intent);
        }
    }

    private LinkedList<Technique[]> loadFlows(Cursor framesCursor) {
        LinkedList<Technique[]> frameList = new LinkedList<>();
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
            cellViews[i].setText("");
        }
        for (int i = 0; i < frame.length; i++) {
            if (frame[i] != null) {
                cellViews[i].setText(frame[i].getName());
            }
        }
    }

    public void deleteFrame(View view) {
        currentFrame--;
        totalFrames--;

        if (currentFrame == 0) {
            findViewById(R.id.editFlow_previous).setVisibility(View.INVISIBLE);
            findViewById(R.id.editFlow_DeleteFrame).setVisibility(View.INVISIBLE);
        }

        if (currentFrame + 1 >= frameList.size()) {
            findViewById(R.id.editFlow__next).setVisibility(View.INVISIBLE);
        }
        tmpFrame = frameList.get(currentFrame);
        frameList.remove(currentFrame + 1);
        populateCells(tmpFrame);
        setFrameCount();
    }

    public void addFrame(View view) {
        currentFrame++;
        totalFrames++;
        tmpFrame = new Technique[4];
        for (Technique technique : tmpFrame) {
            technique = new Technique();
        }
        findViewById(R.id.editFlow_previous).setVisibility(View.VISIBLE);
        findViewById(R.id.editFlow_DeleteFrame).setVisibility(View.VISIBLE);
        frameList.add(currentFrame, tmpFrame);
        populateCells(tmpFrame);
        setFrameCount();
    }

    public void helpClick(View view) {
        showHelpDialog();
    }

    public void showHelpDialog() {
        final AlertDialog.Builder welcomeDialog = new AlertDialog.Builder(EditFlow.this);
        welcomeDialog.setTitle("Welcome to Flow Creation!");
        welcomeDialog.setMessage("Each square represents a portion of your body.\n The top two squares represent the top left and" +
                " top right of your body and the bottom two squares are for the bottom left and bottom right of your body. " +
                "Tap on one of them to select a technique then add new frames to create a sequence.");
        welcomeDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        welcomeDialog.show();
    }
}
