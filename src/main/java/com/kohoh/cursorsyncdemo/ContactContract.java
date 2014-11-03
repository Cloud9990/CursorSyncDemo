package com.kohoh.cursorsyncdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * Created by kohoh on 14-11-3.
 */
public class ContactContract implements BaseColumns {

    static final int DATABSE_VERSION = 1;
    static final String DATABASE_NAME = "contact.db";

    static final String CONTACT_TABLE = "contact";
    static final String NAME = "name";
    static final String PHONE = "phone";

    static final String AUTHORITY = "com.kohoh.cursorsyncdemo";
    static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    static final Uri CONTACT_URI = Uri.withAppendedPath(BASE_URI, "contact");
    static final Uri SYNC_SIGNAL_URI = Uri.withAppendedPath(BASE_URI, "SYNC_SIGNAL_URI");

    static public ContactDatabaseHelper getSqliteOpenHelper(Context context) {
        return new ContactDatabaseHelper(context);
    }

    static class ContactDatabaseHelper extends SQLiteOpenHelper {
        public ContactDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABSE_VERSION);
        }

        static public long addContact(SQLiteDatabase database, String name, int phone) {
            Preconditions.checkNotNull(database);
            Preconditions.checkNotNull(phone);
            Preconditions.checkArgument(!Strings.isNullOrEmpty(name));

            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME, name);
            contentValues.put(PHONE, phone);

            return database.insert(CONTACT_TABLE, null, contentValues);
        }

        static public void deleteContact(Context context, long id) {
            Preconditions.checkNotNull(context);
            Preconditions.checkArgument(id >= 0);

            ContactContract.ContactDatabaseHelper databaseHelper = ContactContract.
                    getSqliteOpenHelper(context);
            SQLiteDatabase databasea = databaseHelper.getWritableDatabase();
            String where = ContactContract._ID + " = ?";
            String[] whereArgs = {String.valueOf(id)};
            databasea.delete(ContactContract.CONTACT_TABLE, where, whereArgs);
            context.getContentResolver().notifyChange(ContactContract.SYNC_SIGNAL_URI, null);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + CONTACT_TABLE + "( " +
                    _ID + " INTEGER PRIMARY KEY," +
                    NAME + " TEXT," +
                    PHONE + " INTERGER)");

            addContact(db, "aaa", 111);
            addContact(db, "bbb", 222);
            addContact(db, "ccc", 333);
            addContact(db, "ddd", 444);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
