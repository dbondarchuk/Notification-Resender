package ua.com.revi.notificationresender;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by v-dmbon on 9/15/2016.
 */
public class ResendSettingArrayAdapter extends ArrayAdapter<ResendSetting> {
    private Context context;
    private int resource;
    private ArrayList<ResendSetting> objects;

    public ResendSettingArrayAdapter(Context context, int resource, ArrayList<ResendSetting> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        LayoutInflater inflater=((Activity) context).getLayoutInflater();
        final View row=inflater.inflate(resource,parent,false);
        TextView nameTextView = (TextView)row.findViewById(R.id.name);
        final Switch enabledSwitch = (Switch)row.findViewById(R.id.enabled);
        nameTextView.setText(objects.get(position).name);
        enabledSwitch.setChecked(objects.get(position).enabled);
        final ImageButton removeButton = (ImageButton)row.findViewById(R.id.remove);
        removeButton.setTag(position);
        final ResendSettingArrayAdapter adapter = this;

        enabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                objects.get(position).enabled = isChecked;
                Snackbar.make(row, context.getString(isChecked ? R.string.setting_was_enabled : R.string.setting_was_disabled, objects.get(position).name) , Snackbar.LENGTH_LONG).show();
            }
        });
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = objects.get(position).name;

                new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.are_you_sure_title))
                        .setMessage(context.getString(R.string.are_you_sure_delete_setting_message, name))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                objects.remove(position);
                                adapter.notifyDataSetChanged();
                                Snackbar.make(row, context.getString(R.string.setting_was_removed, name) , Snackbar.LENGTH_LONG).show();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        return row;
    }
}