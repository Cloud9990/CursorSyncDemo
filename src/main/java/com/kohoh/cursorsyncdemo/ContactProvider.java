package com.kohoh.cursorsyncdemo;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by kohoh on 14-11-3.
 */
public class ContactProvider extends ContentProvider {
    private SQLiteOpenHelper sqLiteOpenHelper;
    private ContentResolver contentResolver;
    private UriMatcher uriMatcher;

    final private int DIR = 0;
    final private int ITEM = 1;

    @Override
    public boolean onCreate() {
        sqLiteOpenHelper = ContactContract.getSqliteOpenHelper(getContext());
        contentResolver = getContext().getContentResolver();
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(ContactContract.AUTHORITY, "contact", DIR);
        uriMatcher.addURI(ContactContract.AUTHORITY, "contact/#", ITEM);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        if (uriMatcher.match(uri) == ITEM) {
            return null;
        }

        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(ContactContract.CONTACT_TABLE, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(contentResolver, ContactContract.SYNC_SIGNAL_URI);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ITEM:
                return "vnd.android.cursor.item/vnd.con.kohoh.cursorsyncdemo";
            case DIR:
                return "vnd.android.cursor.dir/vnd.con.kohoh.cursorsyncdemo";
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) == ITEM) {
            return null;
        }

        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        long id = database.insert(ContactContract.CONTACT_TABLE, null, values);

        if (id >= 0) {
            contentResolver.notifyChange(ContactContract.SYNC_SIGNAL_URI, null);
        }

        return uri.withAppendedPath(ContactContract.CONTACT_URI, String.valueOf(id));
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (uriMatcher.match(uri) == ITEM) {
            return 0;
        }

        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        int result = database.delete(ContactContract.CONTACT_TABLE, selection, selectionArgs);

        if (result > 0) {
            contentResolver.notifyChange(ContactContract.SYNC_SIGNAL_URI, null);
        }

        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (uriMatcher.match(uri) == ITEM) {
            return 0;
        }

        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        int result = database.update(ContactContract.CONTACT_TABLE, values, selection, selectionArgs);

        if (result > 0) {
            contentResolver.notifyChange(ContactContract.SYNC_SIGNAL_URI, null);
        }

        return result;
    }
}
