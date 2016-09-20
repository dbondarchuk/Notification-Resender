package ua.com.revi.notificationresender;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class EditActivity extends AppCompatActivity {
    private boolean isEdit = false;
    private ResendSetting originalSetting;

    private String packages = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        if (intent.hasExtra("setting")){
            Object setting = intent.getSerializableExtra("setting");
            if (setting instanceof ResendSetting){
                isEdit = true;
                originalSetting = (ResendSetting)setting;
                initializeEdit(originalSetting);
            }
        }

        Button addButton = (Button) this.findViewById(R.id.selectAppsButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent appSelectIntent = new Intent(EditActivity.this, AppSelectActivity.class);
                appSelectIntent.putExtra("apps", packages);
                startActivityForResult(appSelectIntent, 1);
            }
        });

        ImageView showInfoButton = (ImageView) this.findViewById(R.id.showInfoButton);
        showInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(EditActivity.this)
                        .setTitle(getString(R.string.edit_info_title))
                        .setMessage(getString(R.string.edit_info_message))
                        .setIcon(android.R.drawable.ic_menu_help)
                        .setPositiveButton(android.R.string.ok, null).show();
            }
        });
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

    }

    public void onAddButtonClick(View view) {
        EditText nameEdit = (EditText)this.findViewById(R.id.nameEditText);
        EditText titleEdit = (EditText)this.findViewById(R.id.titleEditText);
        EditText bodyEdit = (EditText)this.findViewById(R.id.bodyEditText);
        EditText excludeEdit = (EditText)this.findViewById(R.id.excludeEditText);
        CheckBox excludeRegexCheckbox = (CheckBox)this.findViewById(R.id.excludeRegexCheckBox);
        EditText removalDelayEdit = (EditText)this.findViewById(R.id.removalDelayText);

        DbHelper dbHelper = new DbHelper(this);

        String name = nameEdit.getText().toString();
        String title = titleEdit.getText().toString();
        String body = bodyEdit.getText().toString();
        String exclude = excludeEdit.getText().toString();
        String removalDelayString = removalDelayEdit.getText().toString();
        int removalDelay = Integer.parseInt(removalDelayString.length() == 0 ? "0" : removalDelayString);
        if (name.length() == 0){
            Snackbar.make(view, getString(R.string.name_shouldnot_be_empty) , Snackbar.LENGTH_LONG).show();
            return;
        }
        if (!dbHelper.checkName(name, isEdit ? originalSetting.id : 0)){
            Snackbar.make(view, getString(R.string.name_already_taken, name) , Snackbar.LENGTH_LONG).show();
            return;
        }
        if (title.length() == 0){
            Snackbar.make(view, getString(R.string.title_shouldnot_be_empty) , Snackbar.LENGTH_LONG).show();
            return;
        }
        if (body.length() == 0){
            Snackbar.make(view, getString(R.string.body_shouldnot_be_empty) , Snackbar.LENGTH_LONG).show();
            return;
        }

        if (excludeRegexCheckbox.isChecked()){
            try {
                Pattern.compile(exclude.toString());
            } catch (PatternSyntaxException exception) {
                Snackbar.make(view, getString(R.string.wrong_regex) , Snackbar.LENGTH_LONG).show();
                return;
            }
        }

        ResendSetting setting = new ResendSetting(0, name, true, packages, title, body, exclude, excludeRegexCheckbox.isChecked(), removalDelay);

        if (isEdit){
            dbHelper.editSetting(originalSetting.id, setting);
        } else {
            dbHelper.addSetting(setting);
        }

        final Activity activity = this;
        Snackbar.make(view, getString(isEdit? R.string.setting_was_edited : R.string.setting_was_added, name), Snackbar.LENGTH_SHORT).setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                activity.finish();
                super.onDismissed(snackbar, event);
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            String apps = data.getStringExtra("apps");
            TextView appsCountTextView = (TextView)this.findViewById(R.id.appsCountText);

            packages = apps;
            appsCountTextView.setText(String.valueOf(apps.length() > 0 ? apps.split("\\|").length : 0));
        }
    }

    private void initializeEdit(ResendSetting setting){
        EditText nameEdit = (EditText)this.findViewById(R.id.nameEditText);
        EditText titleEdit = (EditText)this.findViewById(R.id.titleEditText);
        EditText bodyEdit = (EditText)this.findViewById(R.id.bodyEditText);
        EditText excludeEdit = (EditText)this.findViewById(R.id.excludeEditText);
        CheckBox excludeRegexCheckbox = (CheckBox)this.findViewById(R.id.excludeRegexCheckBox);
        EditText removalDelayEdit = (EditText)this.findViewById(R.id.removalDelayText);

        TextView activityTitle = (TextView)this.findViewById(R.id.addSettingActivityTitle);
        TextView appsCountTextView = (TextView)this.findViewById(R.id.appsCountText);
        Button addButton = (Button) this.findViewById(R.id.addSettingButton);

        nameEdit.setText(setting.name);
        titleEdit.setText(setting.title);
        bodyEdit.setText(setting.body);
        excludeEdit.setText(setting.excludeRegex);
        excludeRegexCheckbox.setChecked(setting.useRegexForExclude);
        removalDelayEdit.setText(String.valueOf(setting.removeDelay));

        packages = setting.apps;
        appsCountTextView.setText(String.valueOf(packages.length() > 0 ? packages.split("\\|").length : 0));

        activityTitle.setText(getString(R.string.edit_setting_activity_title));
        addButton.setText(getString(R.string.edit_setting_button));
    }
}
