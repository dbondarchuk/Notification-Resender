package ua.com.revi.notificationresender;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<ResendSetting> settings;
    private ResendSettingArrayAdapter resendSettingArrayAdapter;
    private DbHelper dbHelper = new DbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = dbHelper.getSettings();
        resendSettingArrayAdapter = new ResendSettingArrayAdapter(this, R.layout.resendsetting_listview_item, settings);
        ListView listView=(ListView) findViewById(R.id.listView);
        listView.setAdapter(resendSettingArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("setting", (ResendSetting)adapterView.getItemAtPosition(position));
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*ResendSetting setting = new ResendSetting("FB", true);
                settings.add(setting);
                resendSettingArrayAdapter.notifyDataSetChanged();
                Snackbar.make(view, getString(R.string.setting_was_added, setting.name), Snackbar.LENGTH_LONG).show();*/
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        if( !checkNotificationListenEnabled() ) {
            askNotificationAccess();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        settings = dbHelper.getSettings();
        resendSettingArrayAdapter.clear();
        resendSettingArrayAdapter.addAll(settings);
        resendSettingArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean checkNotificationListenEnabled(){
        ContentResolver contentResolver = this.getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = this.getPackageName();

        // check to see if the enabledNotificationListeners String contains our package name
        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName))
            // in this situation we know that the user has not granted the app the Notification access permission
            return false;
        else
            return true;
    }

    private void askNotificationAccess() {
        AlertDialog alert = new AlertDialog.Builder(this)
                .setTitle(getString(android.R.string.dialog_alert_title))
                .setMessage(getString(R.string.system_notice_need_notification_access))
                .setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                        startActivity(intent);
                    }
                })
                .setNegativeButton(getString(android.R.string.no), null)
                .create();
        alert.show();
    }
}
