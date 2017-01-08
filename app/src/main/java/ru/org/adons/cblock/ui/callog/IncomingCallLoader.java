package ru.org.adons.cblock.ui.callog;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;

public class IncomingCallLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private Context context;
    private AutoDataAdapter adapter;
    public static final String[] CALLS_SUMMARY_PROJECTION = new String[]{
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.DATE,
            CallLog.Calls.CACHED_NAME
    };
    private static final int COLUMN_NUMBER_INDEX = 1;
    private static final int COLUMN_DATE_INDEX = 2;
    private static final int COLUMN_NAME_INDEX = 3;

    public IncomingCallLoader(Context context, AutoDataAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri = CallLog.Calls.CONTENT_URI;

        String select = "(" + CallLog.Calls.TYPE + " = " + CallLog.Calls.INCOMING_TYPE + ")";
        return new CursorLoader(context, baseUri,
                CALLS_SUMMARY_PROJECTION, select, null,
                CallLog.Calls.DEFAULT_SORT_ORDER);

    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        adapter.clear();
        AutoListItem item;
        String phone_number;
        String date;
        String name;
        boolean isData = data.moveToFirst();
        while (isData) {
            phone_number = data.getString(COLUMN_NUMBER_INDEX);
            date = data.getString(COLUMN_DATE_INDEX);
            name = data.getString(COLUMN_NAME_INDEX);
            item = new AutoListItem(context, phone_number, date, name);
            adapter.add(item);
            isData = data.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        adapter.clear();
    }

}