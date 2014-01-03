package com.yyunikov.dimblock.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
import com.yyunikov.dimblock.notification.DimBlockNotification;

/**
 * Author: yyunikov
 * Date: 12/28/13
 */
public class DimBlockService extends Service{

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        startForeground(DimBlockNotification.getId(),DimBlockNotification.getNotification(getApplicationContext()));
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

}