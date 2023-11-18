package com.example.votingapp.db;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.votingapp.model.Candidate;
import com.reinaldoarrosi.android.querybuilder.sqlite.QueryBuilder;
import com.reinaldoarrosi.android.querybuilder.sqlite.criteria.Criteria;
import com.reinaldoarrosi.android.querybuilder.sqlite.projection.Projection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CandidateDbHelper extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private final static String DB_NAME = "PVALUE";

    private static final int DB_VERSION = 3;

    private static final String TABLE_NAME = "candidates";

    private static final String ID_COL = "id";

    private static final String AC_CODE = "ac_code";

    private static final String AC_NAME = "ac_name";

    private static final String PARTY_NAME = "p_name";

    private static final String PARTY_CODE = "p_code";

    private static final String CANDIDATE_NAME = "candidate_name";

    private static final String ELECTION_CODE = "election_code";

    private static final String ELECTION_NAME = "election_name";

    private static final String CANDIDATE_CODE = "candidate_code";

    private boolean isInitialized = false;

    Context context;


    // creating a constructor for our database handler.
    public CandidateDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }



    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AC_NAME + " TEXT,"
                + AC_CODE + " TEXT,"
                + PARTY_CODE + " TEXT,"
                + PARTY_NAME + " TEXT,"
                + CANDIDATE_NAME + " TEXT,"
                + ELECTION_CODE + " TEXT,"
                + ELECTION_NAME + " TEXT,"
                + CANDIDATE_CODE + " INTEGER)";
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

    public boolean isInitialized(String electionCode){
        SQLiteDatabase db = this.getReadableDatabase();
        if(!doesTableExist(db, TABLE_NAME)){
            db.close();
            return false;
        }
        QueryBuilder builder = new QueryBuilder();
        QueryBuilder queryBuilder = builder.select(Projection.countRows()).from(TABLE_NAME).whereAnd(Criteria.equals(ELECTION_CODE, 1));
        Cursor cursor = db.rawQuery(queryBuilder.build(), new String[]{electionCode});
        boolean isTableInitialized = false;
        if (cursor.moveToFirst()) {
           if(cursor.getInt(0) > 0){
               Log.d(TAG, "isInitialized: "+cursor.getInt(0));
               isTableInitialized = true;
           }
        }
        cursor.close();
        return isTableInitialized;
    }

    // this method is use to add new course to our sqlite database.
    public void addCandidate(String electionCode, Candidate[] candidates) {
        SQLiteDatabase db = this.getReadableDatabase();
        if(doesTableExist(db, TABLE_NAME)){
            onCreate(db);
        }
        for (Candidate candidate : candidates) {
            if(candidate.getElectionCode().equals(electionCode)) {
                ContentValues values = new ContentValues();
                values.put(ELECTION_CODE, candidate.getElectionCode());
                values.put(ELECTION_NAME, candidate.getElectionName());
                values.put(AC_CODE, candidate.getAssemblyConstituencyCode());
                values.put(AC_NAME, candidate.getAssemblyConstituencyName());
                values.put(PARTY_CODE, candidate.getPartyCode());
                values.put(PARTY_NAME, candidate.getPartyName());
                values.put(CANDIDATE_NAME, candidate.getCandidateName());
                values.put(CANDIDATE_CODE, candidate.getCandidateCode());
                db.insert(TABLE_NAME, null, values);
            }
        }
        isInitialized = true;
    }

    List<Candidate>  getAllCandidates(){
        QueryBuilder queryBuilder = new QueryBuilder().
                select(ELECTION_CODE, ELECTION_NAME, AC_CODE, AC_NAME, PARTY_CODE, PARTY_NAME, CANDIDATE_NAME, CANDIDATE_CODE)
                .from(TABLE_NAME);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryBuilder.build(), null);
        List<Candidate> candidates = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Candidate candidate = new Candidate();
                candidate.setElectionCode(cursor.getString(0));
                candidate.setCandidateName(cursor.getString(1));
                candidate.setAssemblyConstituencyCode(cursor.getString(2));
                candidate.setAssemblyConstituencyName(cursor.getString(3));
                candidate.setPartyCode(cursor.getString(4));
                candidate.setPartyName(cursor.getString(5));
                candidate.setElectionName(cursor.getString(6));
                candidate.setCandidateCode(cursor.getInt(7));
            } while (cursor.moveToNext());
        }
        return candidates;
    }



    public List<String> getAllACCodes(String electionCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT ac_code FROM candidates";
        Set<String> values = new HashSet<>();
        Cursor cursorCandidates
                = db.rawQuery(query, null);
        if (cursorCandidates.moveToFirst()) {
            do {
                values.add(cursorCandidates.getString(0));
            } while (cursorCandidates.moveToNext());
        }
        cursorCandidates.close();
        List<String> acCodeList = new ArrayList<>(values);
        acCodeList.sort(Comparator.comparing(Integer::parseInt));
        return acCodeList;
    }

    public List<String> getAllACNames(String electionCode) {
        String query = "SELECT DISTINCT ac_name FROM candidates";
        SQLiteDatabase db = this.getReadableDatabase();
        Set<String> values = new HashSet<>();
        Cursor cursorCandidates
                = db.rawQuery(query, null);
        if (cursorCandidates.moveToFirst()) {
            do {
                values.add(cursorCandidates.getString(0));
            } while (cursorCandidates.moveToNext());
        }
        cursorCandidates.close();
        return new ArrayList<>(values);
    }

    public List<String> findPartyCodesByCandidateNameAndPartyName(String partyName, String candidateName) {
        QueryBuilder builder = new QueryBuilder();
        builder.select(PARTY_CODE)
                .from(TABLE_NAME)
                .whereAnd(
                        Criteria.equals(PARTY_NAME, 1)
                                .and(Criteria.equals(CANDIDATE_NAME, 1)
                                ));
        SQLiteDatabase db = this.getReadableDatabase();
        Set<String> values = new HashSet<>();
        Cursor cursorCandidates
                = db.rawQuery(builder.build(), new String[]{partyName, candidateName});
        if (cursorCandidates.moveToFirst()) {
            do {
                values.add(cursorCandidates.getString(0));
            } while (cursorCandidates.moveToNext());
        }
        cursorCandidates.close();
        return new ArrayList<>(values);
    }


    public String findACCodeByACName(String ACName) {
        SQLiteDatabase db = this.getReadableDatabase();
        QueryBuilder builder = new QueryBuilder();
        builder.select(AC_CODE)
                .from(TABLE_NAME)
                .whereAnd(
                        Criteria.equals(AC_NAME, 1)
                );
        Cursor cursorCandidates
                = db.rawQuery(builder.build(), new String[]{ACName});
        String res = "";
        if (cursorCandidates.moveToFirst()) {
            do {
                res = cursorCandidates.getString(0);
            } while (cursorCandidates.moveToNext());
        }
        return res;
    }

    public String findACNameByACCode(String ACCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        QueryBuilder builder = new QueryBuilder();
        builder.select(AC_NAME)
                .from(TABLE_NAME)
                .whereAnd(
                        Criteria.equals(AC_CODE, 1)
                );
        Cursor cursorCandidates
                = db.rawQuery(builder.build(), new String[]{ACCode});
        String res = "";
        if (cursorCandidates.moveToFirst()) {
            do {
                res = cursorCandidates.getString(0);
            } while (cursorCandidates.moveToNext());
        }
        cursorCandidates.close();
        return res;
    }

    public List<String> findPartyCodesByCandidateName(String candidateName) {
        QueryBuilder builder = new QueryBuilder();
        builder.select(PARTY_CODE)
                .from(TABLE_NAME)
                .whereAnd(
                        Criteria.equals(CANDIDATE_NAME, 1)
                                );
        SQLiteDatabase db = this.getReadableDatabase();
        Set<String> values = new HashSet<>();
        Cursor cursorCandidates
                = db.rawQuery(builder.build(), new String[]{candidateName});
        if (cursorCandidates.moveToFirst()) {
            do {
                values.add(cursorCandidates.getString(0));
            } while (cursorCandidates.moveToNext());
        }
        cursorCandidates.close();
        return new ArrayList<>(values);
    }

    public List<String> findPartyNamesByCandidateName(String candidateName) {
        QueryBuilder builder = new QueryBuilder();
        builder.select(PARTY_NAME)
                .from(TABLE_NAME)
                .whereAnd(
                        Criteria.equals(CANDIDATE_NAME, 1)
                );
        SQLiteDatabase db = this.getReadableDatabase();
        Set<String> values = new HashSet<>();
        Cursor cursorCandidates
                = db.rawQuery(builder.build(), new String[]{candidateName});
        if (cursorCandidates.moveToFirst()) {
            do {
                values.add(cursorCandidates.getString(0));
            } while (cursorCandidates.moveToNext());
        }
        cursorCandidates.close();
        return new ArrayList<>(values);
    }

    public List<String> findCandidateNamesByACCode(String ACCode) {
        QueryBuilder builder = new QueryBuilder();
        builder.select(CANDIDATE_NAME)
                .from(TABLE_NAME)
                .whereAnd(
                        Criteria.equals(AC_CODE, 1)
                );
        SQLiteDatabase db = this.getReadableDatabase();
        Set<String> values = new HashSet<>();
        Cursor cursorCandidates
                = db.rawQuery(builder.build(), new String[]{ACCode});
        if (cursorCandidates.moveToFirst()) {
            do {
                values.add(cursorCandidates.getString(0));
            } while (cursorCandidates.moveToNext());
        }
        cursorCandidates.close();
        return new ArrayList<>(values);
    }


    public String findPartyNameByPartyCode(String partyCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        QueryBuilder builder = new QueryBuilder();
        builder.select(PARTY_NAME)
                .from(TABLE_NAME)
                .whereAnd(
                        Criteria.equals(PARTY_CODE, 1)
                );
        Cursor cursorCandidates
                = db.rawQuery(builder.build(), new String[]{partyCode});
        String res = "";
        if (cursorCandidates.moveToFirst()) {
            do {
                res = cursorCandidates.getString(0);
            } while (cursorCandidates.moveToNext());
        }
        return res;
    }

    public List<String> findPartyCodeByPartyName(String partyName) {
        QueryBuilder builder = new QueryBuilder();
        builder.select(PARTY_CODE)
                .from(TABLE_NAME)
                .whereAnd(
                        Criteria.equals(PARTY_NAME, 1)
                );
        SQLiteDatabase db = this.getReadableDatabase();
        Set<String> values = new HashSet<>();
        Cursor cursorCandidates
                = db.rawQuery(builder.build(), new String[]{partyName});
        if (cursorCandidates.moveToFirst()) {
            do {
                values.add(cursorCandidates.getString(0));
            } while (cursorCandidates.moveToNext());
        }
        cursorCandidates.close();
        return new ArrayList<>(values);
    }

    public int findCandidateCodeByACCodeAndCandidateName(String acCode, String candidateName){
        SQLiteDatabase db = this.getReadableDatabase();
        QueryBuilder builder = new QueryBuilder();
        builder.select(CANDIDATE_CODE)
                .from(TABLE_NAME)
                .whereAnd(
                        Criteria.equals(AC_CODE, 1)
                                .and(Criteria.equals(CANDIDATE_NAME, 2))
                );
        Cursor cursorCandidates
                = db.rawQuery(builder.build(), new String[]{acCode, candidateName});
        int res = -1;
        if (cursorCandidates.moveToFirst()) {
            do {
                res = cursorCandidates.getInt(0);
            } while (cursorCandidates.moveToNext());
        }
        return res;
    }

    public List<String> findCandidateNameByPartyNameAndACCode(String acCode, String partyName) {
        QueryBuilder builder = new QueryBuilder();
        builder.select(CANDIDATE_NAME)
                .from(TABLE_NAME)
                .whereAnd(
                        Criteria.equals(AC_CODE, 1).and(
                                Criteria.equals(PARTY_NAME, 2)
                        )
                );
        SQLiteDatabase db = this.getReadableDatabase();
        Set<String> values = new HashSet<>();
        Cursor cursorCandidates
                = db.rawQuery(builder.build(), new String[]{acCode, partyName});
        if (cursorCandidates.moveToFirst()) {
            do {
                values.add(cursorCandidates.getString(0));
            } while (cursorCandidates.moveToNext());
        }
        cursorCandidates.close();
        return new ArrayList<>(values);
    }

    public List<String> findPartyNamesByACCode(String acCode) {
        QueryBuilder builder = new QueryBuilder();
        builder.select(PARTY_NAME)
                .from(TABLE_NAME)
                .whereAnd(
                        Criteria.equals(AC_CODE, 1)
                );
        SQLiteDatabase db = this.getReadableDatabase();
        Set<String> values = new HashSet<>();
        Cursor cursorCandidates
                = db.rawQuery(builder.build(), new String[]{acCode});
        if (cursorCandidates.moveToFirst()) {
            do {
                values.add(cursorCandidates.getString(0));
            } while (cursorCandidates.moveToNext());
        }
        cursorCandidates.close();
        return new ArrayList<>(values);
    }

    public List<String> findPartyCodesByACCode(String acCode) {
        QueryBuilder builder = new QueryBuilder();
        builder.select(PARTY_CODE)
                .from(TABLE_NAME)
                .whereAnd(
                        Criteria.equals(AC_CODE, 1)
                );
        SQLiteDatabase db = this.getReadableDatabase();
        Set<String> values = new HashSet<>();
        Cursor cursorCandidates
                = db.rawQuery(builder.build(), new String[]{acCode});
        if (cursorCandidates.moveToFirst()) {
            do {
                values.add(cursorCandidates.getString(0));
            } while (cursorCandidates.moveToNext());
        }
        cursorCandidates.close();
        return new ArrayList<>(values);
    }

    public String findCandidateNamesByPartyCodeAndACCode(String partyCode, String acCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        QueryBuilder builder = new QueryBuilder();
        builder.select(CANDIDATE_NAME)
                .from(TABLE_NAME)
                .whereAnd(
                        Criteria.equals(PARTY_CODE, 1)
                                .and(Criteria.equals(AC_CODE, 2))
                );
        Cursor cursorCandidates
                = db.rawQuery(builder.build(), new String[]{partyCode, acCode});
        String res = "";
        if (cursorCandidates.moveToFirst()) {
            do {
                res = cursorCandidates.getString(0);
            } while (cursorCandidates.moveToNext());
        }
        return res;
    }


    public boolean isValidACName(String ACName) {
        QueryBuilder builder = new QueryBuilder();
        builder.select(AC_NAME).distinct()
                .from(TABLE_NAME).whereAnd(Criteria.equals(AC_NAME, 1));
        SQLiteDatabase db = this.getReadableDatabase();
        Set<String> values = new HashSet<>();
        Cursor cursorCandidates
                = db.rawQuery(builder.build(), new String[]{ACName});
        boolean isValidAC = false;
        if(cursorCandidates.moveToFirst()){
           isValidAC = true;
        }
        cursorCandidates.close();
        return isValidAC;
    }

    public boolean isValidACCode(String ACCode) {
        QueryBuilder builder = new QueryBuilder();
        builder.select(AC_CODE).distinct()
                .from(TABLE_NAME).whereAnd(Criteria.equals(AC_CODE, 1));
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorCandidates
                = db.rawQuery(builder.build(), new String[]{ACCode});
        boolean isValidAC = false;
        if(cursorCandidates.moveToFirst()){
            isValidAC = true;
        }
        cursorCandidates.close();
        return isValidAC;
    }

    public boolean isValidCandidateName(String candidateName) {
        QueryBuilder builder = new QueryBuilder();
        builder.select(CANDIDATE_NAME).distinct()
                .from(TABLE_NAME).whereAnd(Criteria.equals(CANDIDATE_NAME, 1));
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorCandidates
                = db.rawQuery(builder.build(), new String[]{candidateName});
        boolean isValidAC = false;
        if(cursorCandidates.moveToFirst()){
            isValidAC = true;
        }
        cursorCandidates.close();
        return isValidAC;
    }


    public boolean isValidPartyName(String partyName) {
        QueryBuilder builder = new QueryBuilder();
        builder.select(PARTY_NAME).distinct()
                .from(TABLE_NAME).whereAnd(Criteria.equals(PARTY_NAME, 1));
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorCandidates
                = db.rawQuery(builder.build(), new String[]{partyName});
        boolean isValid = false;
        if(cursorCandidates.moveToFirst()){
            isValid = true;
        }
        cursorCandidates.close();
        return isValid;
    }

    public boolean isValidPartyCode(String partyCode) {
        QueryBuilder builder = new QueryBuilder();
        builder.select(PARTY_CODE).distinct()
                .from(TABLE_NAME).whereAnd(Criteria.equals(PARTY_CODE, 1));
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorCandidates
                = db.rawQuery(builder.build(), new String[]{partyCode});
        boolean isValid = false;
        if(cursorCandidates.moveToFirst()){
            isValid = true;
        }
        cursorCandidates.close();
        return isValid;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

