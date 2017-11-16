package at.fhj.mobdev.simpletodo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todos.db";

    public static final int DATABASE_VERSION = 2;

    private static SQLiteDatabase database;

    public TodoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null) {
            database = new TodoDatabaseHelper(context).getWritableDatabase();
        }

        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE todos (" +
                    "  _id          INTEGER PRIMARY KEY NOT NULL," +
                    "  title        TEXT NOT NULL," +
                    "  description  TEXT NOT NULL," +
                    "  done         INTEGER NOT NULL DEFAULT(0)" +
                ")");

        db.insert(TodoTable.TABLE_NAME, null,
                TodoConverter.toContentValues(new Todo("Prepare data class", "Create a data class to hold your data")));
        db.insert(TodoTable.TABLE_NAME, null,
                TodoConverter.toContentValues(new Todo("Create table classes", "Create classes holding field names and other things")));
        db.insert(TodoTable.TABLE_NAME, null,
                TodoConverter.toContentValues(new Todo("Extend SQLite open helper", "Class used to actually create the database")));
        db.insert(TodoTable.TABLE_NAME, null,
                TodoConverter.toContentValues(new Todo("Use the database", "End of story")));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<2 && newVersion>=2){
            db.execSQL("ALTER TABLE " + TodoTable.TABLE_NAME + " ADD COLUMN " + TodoTable.COLUMN_LAST_UPDATED + " TEXT");
        }

    }

}
