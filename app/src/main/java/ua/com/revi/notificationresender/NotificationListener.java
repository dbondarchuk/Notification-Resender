package ua.com.revi.notificationresender;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableString;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

/**
 * Created by v-dmbon on 9/16/2016.
 */
public class NotificationListener extends NotificationListenerService {
    public static StatusBarNotification lastSbn;
    private static int lastId = 1;
    private static Timer timer = new Timer();

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn.getPackageName().equals(getApplication().getPackageName())) {
            return;
        }
        Log.i("NOTIFICATION", "From package: " + sbn.getPackageName());
        DbHelper dbHelper = new DbHelper(this);
        ArrayList<ResendSetting> settings = dbHelper.getSettings();

        lastSbn = sbn;
        final NotificationListener listener = this;
        final PackageManager pm = getPackageManager();

        for (int i = 0; i < settings.size(); i++) {
            final ResendSetting setting = settings.get(i);
            List<String> apps = Arrays.asList(setting.apps.split("\\|"));
            if (setting.enabled && apps.contains(sbn.getPackageName())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Notification lastNotification = lastSbn.getNotification();
                            ApplicationInfo ai = pm.getApplicationInfo(lastSbn.getPackageName(), 0);
                            String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");

                            if (setting.excludeRegex != null && setting.excludeRegex.length() > 0){
                                String text = lastNotification.extras.get("android.text").toString();
                                if (!setting.useRegexForExclude){
                                    if (text.toLowerCase().contains(setting.excludeRegex.toLowerCase())){
                                        return;
                                    }
                                } else {
                                    Pattern pattern = Pattern.compile(setting.excludeRegex);
                                    if (pattern.matcher(text).matches()){
                                        return;
                                    }
                                }
                            }

                            String title = setting.title
                                    .replace("%appName%", applicationName)
                                    .replace("%title%", lastNotification.extras.get(Notification.EXTRA_TITLE).toString())
                                    .replace("%text%", lastNotification.extras.get("android.text").toString());
                            String body = setting.body
                                    .replace("%appName%", applicationName)
                                    .replace("%title%", lastNotification.extras.get(Notification.EXTRA_TITLE).toString())
                                    .replace("%text%", lastNotification.extras.get("android.text").toString());

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(listener)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle(title)
                                    .setContentText(body)
                                    .setTicker(title + "\n" + body);

                            int NOTIFICATION_ID = ++lastId;
                            if (lastId > 1000) {
                                lastId = 1;
                            }

                            Intent targetIntent = new Intent(listener, MainActivity.class);
                            PendingIntent contentIntent = PendingIntent.getActivity(listener, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            builder.setContentIntent(contentIntent);
                            NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            nManager.notify(NOTIFICATION_ID, builder.build());

                            if (setting.removeDelay > 0) {
                                timer.schedule(new RemoveNotificationTask(NOTIFICATION_ID), setting.removeDelay * 1000);
                            }

                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.i("NOTIFICATION", "Package: " + lastSbn.getPackageName() + " skipped");
                    }
                }).start();
            }
        }
    }

    private class RemoveNotificationTask extends TimerTask {
        private int notificationId;

        public RemoveNotificationTask(int notificationId) {
            this.notificationId = notificationId;
        }

        public void run() {
            NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nManager.cancel(notificationId);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //Log.i("NOTIF!", "On remove");
    }

    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

/*    public static class Notif implements Serializable {
        public String appName = "";
        public String fromName = "";
        public String msgText = "";
        public Integer noticeType = 0;

        public static Notif fromSbn(StatusBarNotification sbn, int notice_type) {
            Notif nf = new Notif();
            //String ticker = "";

            if (sbn == null || sbn.getPackageName() == null)
                return nf;

            Log.i("Package", sbn.getPackageName());

            if (sbn.getNotification() == null)
                return nf;

//            if( sbn.getNotification().tickerText != null ) {
//                ticker = sbn.getNotification().tickerText.toString();
//            }
            if (sbn.getNotification().extras != null) {
                Bundle extras = sbn.getNotification().extras;
                if (extras.containsKey(Notification.EXTRA_TITLE))
                    nf.fromName = extras.get(Notification.EXTRA_TITLE).toString();
                if (extras.containsKey("android.text"))
                    nf.msgText = extras.get("android.text").toString();
            }

//            Log.i("Ticker", ticker);
            if (nf.fromName != null)
                Log.i("Title", nf.fromName);
//            if( nf.msgText != null )
//                Log.i("Text", nf.msgText);
            nf.noticeType = notice_type;

            return nf;
        }

        public Integer getDeviceNoticeType() {
            *//*switch (this.noticeType) {
                *//**//*case 1:
                    return Constants.ALERT_TYPE_MESSAGE;
                case 2:
                    return Constants.ALERT_TYPE_CLOUD;
                case 3:
                    return Constants.ALERT_TYPE_ERROR;
                default:
                    return Constants.ALERT_TYPE_MESSAGE;*//**//*
            }*//*
            return 1;
        }

        public void setAppName(String value) {
            this.appName = value;
        }

        public String getAppName() {
            return this.appName;
        }

        public void setFromName(String value) {
            this.fromName = value;
        }

        public String getFromName() {
            return this.fromName;
        }

        public void setMsgText(String value) {
            this.msgText = value;
        }

        public String getMsgText() {
            return this.msgText;
        }

        public void setNoticeType(Integer type) {
            this.noticeType = type;
        }

        public Integer getNoticeType() {
            return this.noticeType;
        }
    }*/
}
