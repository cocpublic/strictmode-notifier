package com.nshmura.strictmodenotifier;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
import static android.content.pm.PackageManager.DONT_KILL_APP;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bzl.apm.strictmode.notifer.R;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

final class StrictModeNotifierInternals {

    private static final Executor fileIoExecutor = newSingleThreadExecutor("File-IO");
    private static final int NOTIFICATION_ID = 1;

    public static void enableReportActivity(Context context) {
        StrictModeNotifierInternals.setEnabled(context, StrictModeReportActivity.class, true);
    }

    public static void startLogWatchService(Context context,
                                            Class<? extends LogWatchService> serviceClass) {
        Intent intent = new Intent(context, serviceClass);
        context.startService(intent);
    }

    public static void setEnabled(Context context, final Class<?> componentClass,
                                  final boolean enabled) {
        final Context appContext = context.getApplicationContext();
        executeOnFileIoThread(new Runnable() {
            @Override
            public void run() {
                setEnabledBlocking(appContext, componentClass, enabled);
            }
        });
    }

    public static void setEnabledBlocking(Context appContext, Class<?> componentClass,
                                          boolean enabled) {
        ComponentName component = new ComponentName(appContext, componentClass);
        PackageManager packageManager = appContext.getPackageManager();
        int newState = enabled ? COMPONENT_ENABLED_STATE_ENABLED : COMPONENT_ENABLED_STATE_DISABLED;
        // Blocks on IPC.
        packageManager.setComponentEnabledSetting(component, newState, DONT_KILL_APP);
    }

    public static void executeOnFileIoThread(Runnable runnable) {
        fileIoExecutor.execute(runnable);
    }

    public static Executor newSingleThreadExecutor(String threadName) {
        return Executors.newSingleThreadExecutor(new StrictModeNotifierSingleThreadFactory(threadName));
    }



    private static final String NOTIFICATION_CHANNEL_ID = "1006";
    private static final String NOTIFICATION_CHANNEL_NAME = "严格模式通知";
    private static final String NOTIFICATION_CHANNEL_DESC = "严格模式相关通知";

    public static void showNotification(Context context, String title, String description, boolean headupEnabled, PendingIntent pendingIntent) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // 设置通知点击事件

        NotificationCompat.Builder notificationBuilder;
        // 创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(NOTIFICATION_CHANNEL_DESC);
            notificationManager.createNotificationChannel(channel);
        }

        // 构建通知
        notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.strictmode_notifier_ic_notification))
                .setSmallIcon(R.drawable.strictmode_notifier_ic_notification)
                .setContentTitle(title)
                .setContentText(description)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (headupEnabled) {
            notificationBuilder.setFullScreenIntent(pendingIntent, true);
        }

        // 发送通知
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
