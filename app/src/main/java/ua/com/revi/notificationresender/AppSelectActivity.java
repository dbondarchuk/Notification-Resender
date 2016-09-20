package ua.com.revi.notificationresender;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class AppSelectActivity extends AppCompatActivity {
    private ArrayList<AppSelect> appSelects = new ArrayList<AppSelect>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_select);

        Intent intent = getIntent();
        String appsExtra = intent.getStringExtra("apps");
        List<String> packages = Arrays.asList(appsExtra.split("\\|"));

        PackageManager pm = getPackageManager();
        List<PackageInfo> apps = pm.getInstalledPackages(0);

        for (int i = 0; i < apps.size(); i++){
            PackageInfo packageInfo = apps.get(i);
            AppSelect appSelect = new AppSelect(
                    packageInfo.packageName,
                    packageInfo.applicationInfo.loadLabel(pm).toString(),
                    packageInfo.applicationInfo.loadIcon(pm),
                    packages.contains(packageInfo.packageName));

            appSelects.add(appSelect);
        }

        AppSelectArrayAdapter adapter = new AppSelectArrayAdapter(this, R.layout.selectapp_listview_item, appSelects);
        ListView listView=(ListView) findViewById(R.id.appsSelectListView);
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        ArrayList<AppSelect> selected = new ArrayList<>();
        for (int i = 0; i < appSelects.size(); i++){
            if (appSelects.get(i).selected){
                selected.add(appSelects.get(i));
            }
        }

        String apps = "";
        for (int i = 0; i < selected.size(); i++){
            apps += selected.get(i).packageName;
            if (i != selected.size() - 1){
                apps += "|";
            }
        }

        Intent intent = new Intent();
        intent.putExtra("apps", apps);
        setResult(RESULT_OK, intent);
        finish();
    }
}
