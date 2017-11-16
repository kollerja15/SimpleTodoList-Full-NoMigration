package at.fhj.mobdev.simpletodo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.fhj.mobdev.simpletodo.db.Todo;
import at.fhj.mobdev.simpletodo.db.TodoConverter;
import at.fhj.mobdev.simpletodo.db.TodoDatabaseHelper;
import at.fhj.mobdev.simpletodo.db.TodoTable;

@SuppressLint("StaticFieldLeak")
public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_CREATE = 100;

    private static final int REQUEST_CODE_EDIT = 200;

    public static final String EXTRA_CHANGED_TITLE = "title";

    public static final String EXTRA_DELETED = "deleted";

    private ListView itemList;

    private TextView noItems;

    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton addItem = findViewById(R.id.add_item);
        addItem.setOnClickListener(this);

        itemList = findViewById(R.id.item_list);
        itemList.setOnItemClickListener(this);

        noItems = findViewById(R.id.no_items);

        adapter = new ListAdapter();
        itemList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter.clearItems();
        loadData();
    }

    private void loadData() {
        new AsyncTask<Void, Void, List<Todo>>() {

            @Override
            protected List<Todo> doInBackground(Void... voids) {
                SQLiteDatabase db = new TodoDatabaseHelper(MainActivity.this).getWritableDatabase();

                Cursor cursor = db.rawQuery("SELECT * FROM " + TodoTable.TABLE_NAME, null);
                // alternative:
                // Cursor cursor = db.query(TodoTable.TABLE_NAME, null, null, null, null, null, null);

                List<Todo> items = new ArrayList<>();

                try {
                    // cursor may be null in error cases
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            Todo item = TodoConverter.toObject(cursor);
                            items.add(item);

                            Log.d(TAG, "doInBackground: found item " + item);
                        }
                    }
                } finally {
                    // always close cursors to prevent memory leaks
                    if (cursor != null) {
                        cursor.close();
                    }
                }

                return items;
            }

            @Override
            protected void onPostExecute(List<Todo> todos) {
                Log.i(TAG, "onPostExecute: found " + todos.size() + " items in database");

                setLoadedItems(todos);
            }
        }.execute();
    }

    private void setLoadedItems(List<Todo> todos) {
        adapter.addItems(todos);

        if (adapter.isEmpty()) {
            noItems.setVisibility(View.VISIBLE);
            itemList.setVisibility(View.GONE);
        } else {
            noItems.setVisibility(View.GONE);
            itemList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_item) {
            Log.i(TAG, "user clicked add-item button");

            Intent intent = new Intent(this, CreateOrEditActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CREATE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        long todoId = adapter.getItemId(position);
        Log.i(TAG, "user clicked on item with id " + todoId);

        Intent intent = new Intent(this, CreateOrEditActivity.class);
        intent.putExtra(CreateOrEditActivity.EXTRA_TODO_ID, todoId);
        startActivityForResult(intent, REQUEST_CODE_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "user returned from create/edit activity");

        String title = data != null ? data.getStringExtra(EXTRA_CHANGED_TITLE) : null;

        if (requestCode == REQUEST_CODE_CREATE && resultCode == RESULT_OK) {
            Snackbar.make(itemList, "New item created: " + title, Snackbar.LENGTH_LONG).show();
        } else if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
            boolean deleted = data != null && data.getBooleanExtra(EXTRA_DELETED, false);

            if (deleted) {
                Snackbar.make(itemList, "Successfully deleted: " + title, Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(itemList, "Item updated: " + title, Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
