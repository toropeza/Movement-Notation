// ------------------------------------ DBADapter.java ---------------------------------------------

// TODO: Change the package to match your project.
package com.thomas.movementnotation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


// TO USE:
// Change the package (at top) to match your project.
// Search for "TODO", and make the appropriate changes.
public class FramesDBAdapter {

    /////////////////////////////////////////////////////////////////////
    //	Constants & Data
    /////////////////////////////////////////////////////////////////////
    // For logging:
    private static final String TAG = "FramesDBAdapter";


    // DB Fields
    public static final String KEY_ROWID = "_id";
    public static final int COL_ROWID = 0;
    public static final int[] cellDBColumns = {3, 4, 5, 6};
    /*
     * CHANGE 1:
     */
    // TODO: Setup your fields here:
    public static final String KEY_FLOWID = "flowid";
    public static final String KEY_FRAME = "frame";
    public static final String KEY_CELL1 = "cell1";
    public static final String KEY_CELL2 = "cell2";
    public static final String KEY_CELL3 = "cell3";
    public static final String KEY_CELL4 = "cell4";


    // TODO: Setup your field numbers here (0 = KEY_ROWID, 1=...)
    public static final int COL_FLOWID = 1;
    public static final int COL_FRAME = 2;
    public static final int COL_CELL1 = 3;
    public static final int COL_CELL2 = 4;
    public static final int COL_CELL3 = 5;
    public static final int COL_CELL4 = 6;


    public static final String[] ALL_KEYS = new String[]{KEY_ROWID, KEY_FLOWID, KEY_FRAME, KEY_CELL1, KEY_CELL2, KEY_CELL3, KEY_CELL4};

    // DB info: it's name, and the table we are using (just one).
    public static final String DATABASE_NAME = "movement_notation_frames";
    public static final String DATABASE_TABLE = "frames";
    // Track DB version if a new version of your app changes the format.
    public static final int DATABASE_VERSION = 11;

    private static final String DATABASE_CREATE_SQL =
            "create table " + DATABASE_TABLE
                    + " (" + KEY_ROWID + " integer primary key autoincrement, "

			/*
             * CHANGE 2:
			 */
                    // TODO: Place your fields here!
                    // + KEY_{...} + " {type} not null"
                    //	- Key is the column name you created above.
                    //	- {type} is one of: text, integer, real, blob
                    //		(http://www.sqlite.org/datatype3.html)
                    //  - "not null" means it is a required field (must be given a value).
                    // NOTE: All must be comma separated (end of line!) Last one must have NO comma!!
                    + KEY_FLOWID + " INTEGER NOT NULL, "
                    + KEY_FRAME + " INTEGER NOT NULL, "
                    + KEY_CELL1 + " INTEGER, "
                    + KEY_CELL2 + " INTEGER, "
                    + KEY_CELL3 + " INTEGER, "
                    + KEY_CELL4 + " INTEGER"
                    // Rest  of creation:
                    + ");";

    // Context of application who uses us.
    private final Context context;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    /////////////////////////////////////////////////////////////////////
    //	Public methods:
    /////////////////////////////////////////////////////////////////////

    public FramesDBAdapter(Context ctx) {
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }

    // Open the database connection.
    public FramesDBAdapter open() {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        myDBHelper.close();
    }

    // Add a new set of values to the database.
    public long insertRow(long flowID, long frame, long c1, long c2, long c3, long c4) {
        /*
		 * CHANGE 3:
		 */
        // TODO: Update data in the row with new fields.
        // TODO: Also change the function's arguments to be what you need!
        // Create row's data:
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_FLOWID, flowID);
        initialValues.put(KEY_FRAME, frame);
        initialValues.put(KEY_CELL1, c1);
        initialValues.put(KEY_CELL2, c2);
        initialValues.put(KEY_CELL3, c3);
        initialValues.put(KEY_CELL4, c4);


        // Insert it into the database.
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    // Delete a row from the database, by rowId (primary key)
    public boolean deleteRow(long rowId) {
        String where = KEY_FLOWID + "=" + rowId;
        return db.delete(DATABASE_TABLE, where, null) != 0;
    }

    // Delete a row from the database, by rowId (primary key)
    public boolean deleteFlow(long flowID) {
        String where = KEY_FLOWID + "=" + flowID;
        return db.delete(DATABASE_TABLE, where, null) != 0;
    }

    public void deleteAll() {
        Cursor c = getAllRows();
        long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
        if (c.moveToFirst()) {
            do {
                deleteRow(c.getLong((int) rowId));
            } while (c.moveToNext());
        }
        c.close();
    }

    // Return all data in the database.
    public Cursor getAllRows() {
        String where = null;
        Cursor c = db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Return all data in the database.
    public Cursor getFlow(long flowID) {
        String where = KEY_FLOWID + "=" + flowID;
        Cursor c = db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public boolean containsTechnique(long techniqueID) {
        Cursor c = null;
        int count = -1;
        String where = " WHERE " + KEY_CELL1 + "='" + techniqueID + "' OR "
                + KEY_CELL2 + "='" + techniqueID + "' OR "
                + KEY_CELL3 + "='" + techniqueID + "' OR "
                + KEY_CELL4 + "='" + techniqueID + "'";
        String query = "SELECT * FROM "
                + DATABASE_TABLE + where;

        try {
            c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                count = c.getCount();
            }
            return count > 0;
        } finally {
            if (c != null) {
                c.close();
            }
        }


    }
    /////////////////////////////////////////////////////////////////////
    //	Private Helper Classes:
    /////////////////////////////////////////////////////////////////////

    /**
     * Private class which handles database creation and upgrading.
     * Used to handle low-level database access.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            // Recreate new database:
            onCreate(_db);
        }
    }
}
