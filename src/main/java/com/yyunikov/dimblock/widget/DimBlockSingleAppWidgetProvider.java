/*
 * Copyright 2014 Yuriy Yunikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yyunikov.dimblock.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.google.analytics.tracking.android.MapBuilder;
import com.yyunikov.dimblock.R;
import com.yyunikov.dimblock.base.Model;
import com.yyunikov.dimblock.controller.DimPreferenceController;

/**
 * @author yyunikov
 */
public class DimBlockSingleAppWidgetProvider extends AppWidgetProvider {
    private static final ComponentName THIS_APPWIDGET =
            new ComponentName("com.yyunikov.dimblock","com.yyunikov.dimblock.widget.DimBlockSingleAppWidgetProvider");

    private static final String ACTION_STATE_CHANGE = "com.yyunikov.dimblock.DimBlockStateChange";

    private static final int[] IND_DRAWABLE_OFF = {
            R.drawable.appwidget_settings_ind_off_c_holo,
    };

    private static final int[] IND_DRAWABLE_ON = {
            R.drawable.appwidget_settings_ind_on_c_holo,
    };

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        // Update each requested appWidgetId
        final DimPreferenceController controller = new DimPreferenceController(context);

        final RemoteViews view = buildUpdate(context, getActualState(controller), controller);

        for (final int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, view);
        }

        Model.getInstance().getGaTracker().send(MapBuilder
                .createEvent("UX", "Widget onUpdate", "Small size", null)
                .build());
    }

    /**
     * Receives and processes a button pressed intent or state change.
     *
     * @param context context
     * @param intent  Indicates the pressed button.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        final DimPreferenceController controller = new DimPreferenceController(context);
        if (intent.getAction().equals(ACTION_STATE_CHANGE)) {
            final boolean state = getActualState(controller);
            updateWidget(context, !state, controller);
            DimBlockAppWidgetProvider.updateWidget(context, !state, controller);
        }
    }

    /**
     * Load image for given widget and build {@link android.widget.RemoteViews} for it.
     *
     * @param context context
     * @param state state of dim block
     */
    private static RemoteViews buildUpdate(final Context context, final boolean state, final DimPreferenceController controller) {
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_single);

        views.setOnClickPendingIntent(R.id.btn_dim_block, getLaunchPendingIntent(context, R.id.btn_dim_block));

        if (state) {
            views.setImageViewResource(R.id.ind_dim_block, IND_DRAWABLE_ON[0]);
            controller.setDimEnabled(true);
            views.setImageViewResource(R.id.img_dim_block, R.drawable.icon_enabled);
        } else if (!state) {
            views.setImageViewResource(R.id.ind_dim_block, IND_DRAWABLE_OFF[0]);
            controller.setDimEnabled(false);
            views.setImageViewResource(R.id.img_dim_block, R.drawable.icon_disabled);
        }

        return views;
    }

    /**
     * Gets actual state of dim block.
     *
     * @return gets the actual state of the widget
     */
    private static boolean getActualState(final DimPreferenceController controller) {
        return controller.isDimBlocked();
    }

    /**
     * Creates PendingIntent to notify the widget of a button click.
     *
     * @param context
     * @return
     */
    private static PendingIntent getLaunchPendingIntent(final Context context, final int buttonId) {
        final Intent launchIntent = new Intent();
        launchIntent.setClass(context, DimBlockSingleAppWidgetProvider.class);

        switch (buttonId) {
            case R.id.btn_dim_block:
                launchIntent.setAction(ACTION_STATE_CHANGE);
                break;
        }

        final PendingIntent pi = PendingIntent.getBroadcast(context, 0 /* no requestCode */,
                launchIntent, 0 /* no flags */);
        return pi;
    }

    /**
     * Updates the widget when something changes, or when a button is pushed.
     *
     * @param context
     */
    public static void updateWidget(final Context context, final boolean state, final DimPreferenceController controller) {
        final RemoteViews views = buildUpdate(context, state ,controller);

        // Update specific list of appWidgetIds if given, otherwise default to all
        final AppWidgetManager gm = AppWidgetManager.getInstance(context);
        gm.updateAppWidget(THIS_APPWIDGET, views);
    }
}