package com.example.votingapp.db;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.votingapp.model.Candidate;
import com.example.votingapp.model.LogEntry;
import com.reinaldoarrosi.android.querybuilder.sqlite.QueryBuilder;
import com.reinaldoarrosi.android.querybuilder.sqlite.criteria.Criteria;
import com.reinaldoarrosi.android.querybuilder.sqlite.projection.Projection;

import java.util.ArrayList;
import java.util.List;

public class LogDbHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "PVALUE";
    private static final int DB_VERSION = 3;

    private static final String TABLE = "logs";
    private static final String USER = "user_name";
    private static final String ELECTION_CODE = "election_code";
    private static final String AC_CODE = "ac_code";
    private static final String AC_NAME = "ac_name";
    private static final String PARTY_CODE = "party_code";
    private static final String PARTY_NAME = "party_name";
    private static final String CANDIDATE_NAME = "candidate_name";

    private static final String ROUND_NO = "round_no";

    private static final String TIMESTAMP = "timestamp";

    private static final String ID_COL = "id";

    public LogDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TIMESTAMP+" DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + USER + " TEXT,"
                + AC_NAME + " TEXT,"
                + AC_CODE + " TEXT,"
                + PARTY_CODE + " TEXT,"
                + PARTY_NAME + " TEXT,"
                + CANDIDATE_NAME + " TEXT,"
                + ROUND_NO + " TEXT,"
                + ELECTION_CODE + " TEXT)";
        db.execSQL(query);
    }

    public boolean doesTableExist(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public void saveLog(LogEntry logEntry) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();
        if(!doesTableExist(db, TABLE)){
            onCreate(db);
        }
        ContentValues values = new ContentValues();
        values.put(ELECTION_CODE, logEntry.getElectionCode());
        values.put(AC_CODE, logEntry.getAcCode());
        values.put(AC_NAME, logEntry.getAcName());
        values.put(PARTY_CODE, logEntry.getPartyCode());
        values.put(PARTY_NAME, logEntry.getPartyName());
        values.put(CANDIDATE_NAME, logEntry.getCandidateName());
        values.put(ROUND_NO, logEntry.getRoundNo());
        values.put(USER, logEntry.getUserName());
        db.insert(TABLE, null, values);
        db.close();
    }

    public LogEntry[] findLogsByUserIdAndElectionCode(String electionCode, String userName){
        SQLiteDatabase db = this.getReadableDatabase();
        if(!doesTableExist(db, TABLE)){
            onCreate(db);
            return new LogEntry[0];
        }
        QueryBuilder queryBuilder = new QueryBuilder().select(AC_CODE, AC_NAME, PARTY_NAME, PARTY_CODE, CANDIDATE_NAME, ROUND_NO, TIMESTAMP)
                .from(TABLE)
                .orderByDescending(TIMESTAMP)
                .whereAnd(Criteria.equals(ELECTION_CODE, 1)
                        .and(Criteria.equals(USER, 2)));
        Cursor cursor = db.rawQuery(queryBuilder.build(), new String[]{electionCode, userName});
        List<LogEntry> logEntries = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                LogEntry logEntry = new LogEntry(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getInt(5),
                        cursor.getString(6));
                logEntries.add(logEntry);
            } while (cursor.moveToNext());
        }
        cursor.close();
        LogEntry[] logEntriesArray = new LogEntry[logEntries.size()];
        int i = 0;
        for(LogEntry logEntry : logEntries){
            logEntriesArray[i++] = logEntry;
        }
        db.close();
        return logEntriesArray;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
}
