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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.software.shell.fab.ActionButton;

import java.util.ArrayList;

public class FlowListFragment extends Fragment {
    FlowsDBAdapter flowDB;
    FramesDBAdapter framesDB;
    FlowListAdapter listAdapter;
    ListView flowListView;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flowlistfragment, container, false);

        flowDB = new FlowsDBAdapter(getActivity());
        flowDB.open();

        framesDB = new FramesDBAdapter(getActivity());
        framesDB.open();

        //populating listview
        Cursor allData = flowDB.getAllRows();

        final Flow[] data = generateFlowList(allData);
        listAdapter = new FlowListAdapter(getActivity(), R.layout.flow_item, data);
        flowListView = (ListView) view.findViewById(R.id.flow_list_view);
        flowListView.setAdapter(listAdapter);

        //ACTION BUTTON SETUP
        ActionButton actionButton = (ActionButton) view.findViewById(R.id.add_flow_action_button);
        actionButton.setButtonColor(getResources().getColor(R.color.fab_material_green_500));
        actionButton.setImageResource(R.drawable.fab_plus_icon);
        actionButton.show();


        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddFlow.class);
                startActivity(intent);
            }
        });

        //SETTING DELETE OPTION FROM LISTVIEW
        flowListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                final Flow flow = (Flow) adapterView.getItemAtPosition(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                String name = flow.getName();

                builder.setCancelable(true);
                builder.setTitle("Delete: " + name + " ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        flowDB.deleteFlow(flow.getFlowID());
                        framesDB.deleteRow(flow.getFlowID());
                        Flow[] data = generateFlowList(flowDB.getAllRows());
                        flowListView.setAdapter(new FlowListAdapter(getActivity(), R.layout.flow_item, data));
                        Toast.makeText(getActivity(), "Flow Deleted!", Toast.LENGTH_LONG).show();

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

        flowListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final Flow flow = (Flow) adapterView.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), ViewFlow.class);
                intent.putExtra("flowID", flow.getFlowID());
                intent.putExtra("name", flow.getName());
                intent.putExtra("description", flow.getDescription());
                startActivity(intent);
            }
        });


        return view;
    }

    public Flow[] generateFlowList(Cursor allData) {
        Flow[] data = new Flow[allData.getCount()];
        allData.moveToFirst();
        int i = 0;
        while (!allData.isAfterLast()) {
            data[i] = new Flow(allData.getString(FlowsDBAdapter.COL_NAME),
                    allData.getString(FlowsDBAdapter.COL_DESCRIPTION),
                    allData.getLong(FlowsDBAdapter.COL_ROWID));
            i++;
            allData.moveToNext();
        }
        return data;
    }

    @Override
    public void onResume() {
        super.onResume();
        Cursor allData = flowDB.getAllRows();
        listAdapter = new FlowListAdapter(getActivity(), R.layout.flow_item, generateFlowList(allData));
        flowListView.setAdapter(listAdapter);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Cursor allData = flowDB.getAllRows();
        listAdapter = new FlowListAdapter(getActivity(), R.layout.flow_item, generateFlowList(allData));
        flowListView.setAdapter(listAdapter);
    }
}
