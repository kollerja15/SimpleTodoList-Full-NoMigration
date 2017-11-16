package at.fhj.mobdev.simpletodo.db;

import android.provider.BaseColumns;

public interface TodoTable extends BaseColumns {

    // _id is defined by BaseColumns interface

    public static final String TABLE_NAME = "todos";

    public static final String COLUMN_TITLE = "title";

    public static final String COLUMN_DESCRIPTION = "description";

    public static final String COLUMN_DONE = "done";

    public static final String COLUMN_LAST_UPDATED = "modified";
}
