package ua.com.revi.notificationresender;

/**
 * Created by v-dmbon on 9/15/2016.
 */
public class ResendSetting {
    /* Setting id */
    public int id;

    /* Setting name */
    public String name;

    /* Determines whether is setting enabled */
    public boolean enabled;

    /* Selected apps */
    public String apps;

    /* Regex to exclude */
    public String excludeRegex;

    /* Determines whether to use regex on exclusion or not */
    public boolean useRegexForExclude;

    /* Remove delay in seconds */
    public int removeDelay;

    public ResendSetting(String name, boolean enabled){
        this.name = name;
        this.enabled = enabled;
    }
}