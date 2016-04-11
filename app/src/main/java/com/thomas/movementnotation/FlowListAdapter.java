package com.thomas.movementnotation;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by thomasoropeza on 1/19/16.
 */
public class FlowListAdapter extends ArrayAdapter<Flow> {

    Context context;
    int layoutResourceId;
    Flow data[] = null;

    public FlowListAdapter(Context context, int layoutResourceId, Flow[] data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        FlowHolder flowHolder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            flowHolder = new FlowHolder();
            flowHolder.name = (TextView) row.findViewById(R.id.flow_name_list);
            row.setTag(flowHolder);
        } else {
            flowHolder = (FlowHolder) row.getTag();
        }

        Flow flow = data[position];
        flowHolder.name.setText(flow.getName());

        return row;
    }

    static class FlowHolder {
        TextView name;
        TextView description;
    }
}
