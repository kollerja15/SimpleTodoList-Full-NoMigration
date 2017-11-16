package at.fhj.mobdev.simpletodo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Date;

import at.fhj.mobdev.simpletodo.db.Todo;
import at.fhj.mobdev.simpletodo.db.TodoConverter;
import at.fhj.mobdev.simpletodo.db.TodoDatabaseHelper;
import at.fhj.mobdev.simpletodo.db.TodoTable;

@SuppressLint("StaticFieldLeak")
public class CreateOrEditActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CreateOrEditActivity";

    public static final String EXTRA_TODO_ID = "id";

    private long todoId;

    private boolean createMode;

    private TextView title;

    private TextView description;

    private CheckBox done;

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        todoId = getIntent().getLongExtra(EXTRA_TODO_ID, 0);
        createMode = todoId == 0;
        Log.i(TAG, "onCreate, given todoId is " + todoId);

        setContentView(R.layout.activity_create_or_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button delete = findViewById(R.id.delete);
        delete.setVisibility(createMode ? View.GONE : View.VISIBLE);
        delete.setOnClickListener(this);

        Button save = findViewById(R.id.save);
        save.setOnClickListener(this);

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        done = findViewById(R.id.done);

        setTitle(createMode ? "Create item" : "Edit item");
    }

    private void updateResultAndFinish(boolean deleted) {
        Intent data = new Intent();
        // used to inform the main activity to that it can show a notification to the user
        data.putExtra(MainActivity.EXTRA_CHANGED_TITLE, title.getText().toString());
        data.putExtra(MainActivity.EXTRA_DELETED, deleted);

        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        database = new TodoDatabaseHelper(this).getWritableDatabase();

        if (!createMode) {
            loadData();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.save) {
            saveData();
            updateResultAndFinish(false);
        } else if (v.getId() == R.id.delete) {
            deleteData();
            updateResultAndFinish(true);
        }
    }

    private void loadData() {
        // another method instead of AsyncTasks: use Threads directly
        new Thread() {
            @Override
            public void run() {
                Cursor cursor = database.query(TodoTable.TABLE_NAME, null, TodoTable._ID + " = ?",
                        new String[] {Long.toString(todoId)}, null, null, null);

                try {
                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        final Todo item = TodoConverter.toObject(cursor);

                        Log.i(TAG, "fetched item " + item);

                        // now update the data on the ui thread (this is what the AsyncTask does for us
                        // in the background)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                title.setText(item.getTitle());
                                description.setText(item.getDescription());
                                done.setChecked(item.isDone());
                            }
                        });
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }.start();
    }

    private void saveData() {
        Todo item = new Todo();
        item.setId(todoId);
        item.setTitle(title.getText().toString());
        item.setDescription(description.getText().toString());
        item.setDone(done.isChecked());
        item.setModified(new Date().toString());


        new AsyncTask<Todo, Void, Void>() {

            @Override
            protected Void doInBackground(Todo... todos) {
                Todo item = todos[0];

                // we know that the item is new if the id == 0, but you could use createMode
                // as a check too
                if (item.getId() == 0) {
                    long insertId = database.insert(TodoTable.TABLE_NAME, null, TodoConverter.toContentValues(item));
                    Log.d(TAG, "inserted item has got the id " + insertId);
                } else {
                    database.update(TodoTable.TABLE_NAME, TodoConverter.toContentValues(item),
                            TodoTable._ID + " = ?", new String[] {Long.toString(todoId)});
                }

                return null;
            }
        }.execute(item);
    }

    private void deleteData() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                database.delete(TodoTable.TABLE_NAME, TodoTable._ID + " = ?", new String[] {Long.toString(todoId)});

                return null;
            }
        }.execute();
    }
}
