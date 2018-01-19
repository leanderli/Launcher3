package com.google.android.apps.nexuslauncher.search;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.android.launcher3.allapps.search.AllAppsSearchBarController;
import com.android.launcher3.allapps.search.SearchAlgorithm;

public class b implements SearchAlgorithm, Handler.Callback
{
    private static HandlerThread handlerThread;
    private final Handler mHandler;
    private final Context mContext;
    private final Handler mUiHandler;
    
    public b(Context context) {
        mContext = context;
        mUiHandler = new Handler(this);
        if (handlerThread == null) {
            handlerThread = new HandlerThread("search-thread", -2);
            handlerThread.start();
        }
        mHandler = new Handler(b.handlerThread.getLooper(), this);
    }
    
    private void dj(c componentList) {
        Uri uri = new Uri.Builder()
                .scheme("content")
                .authority("com.google.android.apps.nexuslauncher.appssearch")
                .appendPath(componentList.eH)
                .build();

        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(uri, null, null, null, null);
            int suggestIntentData = cursor.getColumnIndex("suggest_intent_data");
            while (cursor.moveToNext()) {
                componentList.eI.add(AppSearchProvider.dl(Uri.parse(cursor.getString(suggestIntentData)), mContext));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        Message.obtain(mUiHandler, 200, componentList).sendToTarget();
    }
    
    public void cancel(boolean interruptActiveRequests) {
        mHandler.removeMessages(100);
        if (interruptActiveRequests) {
            mUiHandler.removeMessages(200);
        }
    }
    
    public void doSearch(String query, AllAppsSearchBarController.Callbacks callback) {
        mHandler.removeMessages(100);
        Message.obtain(mHandler, 100, new c(query, callback)).sendToTarget();
    }
    
    public boolean handleMessage(final Message message) {
        final boolean b = true;
        switch (message.what) {
            default: {
                return false;
            }
            case 100: {
                dj((c)message.obj);
                return b;
            }
            case 200: {
                final c c = (c)message.obj;
                c.eG.onSearchResult(c.eH, c.eI);
                return b;
            }
        }
    }
}
