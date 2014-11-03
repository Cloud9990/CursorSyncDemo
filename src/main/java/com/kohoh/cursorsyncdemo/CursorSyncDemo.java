package com.kohoh.cursorsyncdemo;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class CursorSyncDemo extends Activity {

    private ListView listView;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursor_sync_demo);

        String[] from = {ContactContract.NAME, ContactContract.PHONE};
        int[] to = {R.id.name, R.id.phone};

        listView = (ListView) findViewById(R.id.lv);
        adapter = new SimpleCursorAdapter(this, R.layout.contact_item, null, from, to,
                CursorAdapter.FLAG_AUTO_REQUERY);
        listView.setAdapter(adapter);

        loadData();

        registerForContextMenu(listView);
    }

    private void loadData() {
        SQLiteOpenHelper sqliteOpenHelper = ContactContract.getSqliteOpenHelper(this);
        SQLiteDatabase database = sqliteOpenHelper.getReadableDatabase();
        String[] columns = {ContactContract._ID, ContactContract.NAME, ContactContract.PHONE};
        Cursor cursor = database.query(ContactContract.CONTACT_TABLE, columns, null, null, null,
                null, null);
        cursor.setNotificationUri(this.getContentResolver(), ContactContract.SYNC_SIGNAL_URI);
        adapter.changeCursor(cursor);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete:
                AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)
                        item.getMenuInfo();

                ContactContract.ContactDatabaseHelper.deleteContact(this, menuInfo.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_item_menu, menu);
    }
}
