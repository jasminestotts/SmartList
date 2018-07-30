package com.gmail.jasminestotts.smartlist;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gmail.jasminestotts.smartlist.db.ItemContract;
import com.gmail.jasminestotts.smartlist.db.ItemDbHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ItemDbHelper helper;
    private ListView itemListView;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new ItemDbHelper(this);
        itemListView = findViewById(R.id.list_shopping);
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_item:
            final EditText itemEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add a new item")
                        .setMessage("Add your next item here")
                        .setView(itemEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String item = String.valueOf(itemEditText.getText());
                                SQLiteDatabase db = helper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(ItemContract.ItemEntry.COL_ITEM_TITLE, item);
                                db.insertWithOnConflict(ItemContract.ItemEntry.TABLE,
                                        null,
                                        values,
                                        SQLiteDatabase.CONFLICT_REPLACE);
                                db.close();
                                updateUI();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
            dialog.show();
            return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI() {
        ArrayList<String> itemList = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(ItemContract.ItemEntry.TABLE,
                new String[]{ItemContract.ItemEntry._ID,
                        ItemContract.ItemEntry.COL_ITEM_TITLE},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(ItemContract.ItemEntry.COL_ITEM_TITLE);
            itemList.add(cursor.getString(idx));
        }

        if (arrayAdapter == null) {
            arrayAdapter = new ArrayAdapter<>(this, R.layout.item_shopping,
                    R.id.item_title, itemList);
            itemListView.setAdapter(arrayAdapter);
        } else {
            arrayAdapter.clear();
            arrayAdapter.addAll(itemList);
            arrayAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }

    public void deleteItem(View view) {
        View parent = (View) view.getParent();
        TextView itemTextView = parent.findViewById(R.id.item_title);
        String item = String.valueOf(itemTextView.getText());
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(ItemContract.ItemEntry.TABLE,
                ItemContract.ItemEntry.COL_ITEM_TITLE + " = ?",
                new String[]{item});
        db.close();
        updateUI();
    }
}
