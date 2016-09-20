package ua.com.revi.notificationresender;

import android.graphics.drawable.Drawable;

/**
 * Created by v-dmbon on 9/19/2016.
 */
public class AppSelect {
    public String name;

    public String packageName;

    public boolean selected;

    public Drawable icon;

    public AppSelect(String packageName, String name, Drawable icon, boolean selected){
        this.name = name;
        this.packageName = packageName;
        this.icon = icon;
        this.selected = selected;
    }
}
