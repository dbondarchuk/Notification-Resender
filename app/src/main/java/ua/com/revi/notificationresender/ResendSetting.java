package ua.com.revi.notificationresender;

import java.io.Serializable;

/**
 * Created by v-dmbon on 9/15/2016.
 */
public class ResendSetting implements Serializable {
    /* Setting id */
    public int id;

    /* Setting name */
    public String name;

    /* Determines whether is setting enabled */
    public boolean enabled;

    /* Selected apps */
    public String apps;

    /* Notification title */
    public String title;

    /* Notification body */
    public String body;

    /* Regex to exclude */
    public String excludeRegex;

    /* Determines whether to use regex on exclusion or not */
    public boolean useRegexForExclude;

    /* Remove delay in seconds. 0 - none */
    public int removeDelay;

    public ResendSetting(int id, String name, boolean enabled, String apps, String title, String body, String excludeRegex, boolean useRegexForExclude, int removeDelay){
        this.name = name;
        this.enabled = enabled;
        this.id = id;
        this.apps = apps;
        this.title = title;
        this.body = body;
        this.excludeRegex = excludeRegex;
        this.useRegexForExclude = useRegexForExclude;
        this.removeDelay = removeDelay;
    }
}
