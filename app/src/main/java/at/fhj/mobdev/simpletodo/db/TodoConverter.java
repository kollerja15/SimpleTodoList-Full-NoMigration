package at.fhj.mobdev.simpletodo.db;

import android.content.ContentValues;
import android.database.Cursor;

public class TodoConverter {

    public static ContentValues toContentValues(Todo todo) {
        ContentValues values = new ContentValues();

        if (todo.getId() > 0) {
            // if id == 0, this is a new item to create, do not set _id in this case, as otherwise
            // an item with id=0 is being created in the DB
            values.put(TodoTable._ID, todo.getId());
        }

        values.put(TodoTable.COLUMN_TITLE, todo.getTitle());
        values.put(TodoTable.COLUMN_DESCRIPTION, todo.getDescription());
        values.put(TodoTable.COLUMN_LAST_UPDATED, todo.getModified());
        values.put(TodoTable.COLUMN_DONE, todo.isDone() ? 1 : 0);

        return values;
    }

    public static Todo toObject(Cursor cursor) {
        Todo result = new Todo();

        result.setId(cursor.getLong(cursor.getColumnIndex(TodoTable._ID)));
        result.setTitle(cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_TITLE)));
        result.setDescription(cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_DESCRIPTION)));
        result.setModified(cursor.getString(cursor.getColumnIndex(TodoTable.COLUMN_LAST_UPDATED)));
        result.setDone(cursor.getInt(cursor.getColumnIndex(TodoTable.COLUMN_DONE)) == 1);

        return result;
    }
}
