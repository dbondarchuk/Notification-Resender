package ua.com.revi.notificationresender;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by v-dmbon on 9/15/2016.
 */
public class AppSelectArrayAdapter extends ArrayAdapter<AppSelect> {
    private Context context;
    private int resource;
    private ArrayList<AppSelect> objects;

    public AppSelectArrayAdapter(Context context, int resource, ArrayList<AppSelect> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        LayoutInflater inflater=((Activity) context).getLayoutInflater();
        final View row=inflater.inflate(resource,parent,false);
        final TextView nameTextView = (TextView)row.findViewById(R.id.appSelectName);
        final CheckBox selectedCheckbox = (CheckBox) row.findViewById(R.id.appSelectCheckbox);
        final ImageView iconImage = (ImageView) row.findViewById(R.id.appSelectIcon);

        selectedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                objects.get(position).selected = checked;
            }
        });

        nameTextView.setText(objects.get(position).name);
        selectedCheckbox.setChecked(objects.get(position).selected);
        iconImage.setImageDrawable(objects.get(position).icon);

        return row;
    }
}