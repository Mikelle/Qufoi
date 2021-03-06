package com.mikelle.qufoi.app.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.mikelle.qufoi.app.R;
import com.mikelle.qufoi.app.helper.JsonHelper;
import com.mikelle.qufoi.app.model.Category;
import com.mikelle.qufoi.app.model.JsonAttributes;
import com.mikelle.qufoi.app.model.Theme;
import com.mikelle.qufoi.app.model.quiz.AlphaPickerQuiz;
import com.mikelle.qufoi.app.model.quiz.FillBlankQuiz;

import com.mikelle.qufoi.app.model.quiz.FourQuarterQuiz;
import com.mikelle.qufoi.app.model.quiz.MultiSelectQuiz;
import com.mikelle.qufoi.app.model.quiz.PickerQuiz;
import com.mikelle.qufoi.app.model.quiz.Quiz;
import com.mikelle.qufoi.app.model.quiz.SelectItemQuiz;
import com.mikelle.qufoi.app.model.quiz.ToggleTranslateQuiz;
import com.mikelle.qufoi.app.model.quiz.TrueFalseQuiz;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Database for storing and retrieving info for categories and quizzes
 */
public class QufoiDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "QufoiDatabaseHelper";
    private static final String DB_NAME = "qufoi";
    private static final String DB_SUFFIX = ".db";
    private static final int DB_VERSION = 1;
    private static List<Category> mCategories;
    private static QufoiDatabaseHelper mInstance;
    private final Resources mResources;

    private QufoiDatabaseHelper(Context context) {
        //prevents external instance creation
        super(context, DB_NAME + DB_SUFFIX, null, DB_VERSION);
        mResources = context.getResources();
    }

    private static QufoiDatabaseHelper getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new QufoiDatabaseHelper(context);
        }
        return mInstance;
    }

    /**
     * Gets all categories with their quizzes.
     */
    public static List<Category> getCategories(Context context, boolean fromDatabase) {
        if (null == mCategories || fromDatabase) {
            mCategories = loadCategories(context);
        }
        return mCategories;
    }

    private static List<Category> loadCategories(Context context) {
        Cursor data = QufoiDatabaseHelper.getCategoryCursor(context);
        List<Category> tmpCategories = new ArrayList<>(data.getCount());
        final SQLiteDatabase readableDatabase = QufoiDatabaseHelper.getReadableDatabase(context);
        return tmpCategories;
    }

    private static Cursor getCategoryCursor(Context context) {
        SQLiteDatabase readableDatabase = getReadableDatabase(context);
        Cursor data = readableDatabase
                .query(CategoryTable.NAME, CategoryTable.PROJECTION, null, null, null, null, null);
        data.moveToFirst();
        return data;
    }

    private static void getCategory(Cursor cursor, SQLiteDatabase readableDatabase) {
        // "magic numbers" based on CategoryTable#PROJECTION
        final String id = cursor.getString(0);
        final String name = cursor.getString(1);
        final String themeName = cursor.getString(2);
        final Theme theme = Theme.valueOf(themeName);
        final String isSolved = cursor.getString(3);
        final boolean solved = getBooleanFromDatabase(isSolved);
        final int[] scores = JsonHelper.INSTANCE.jsonArrayToIntArray(cursor.getString(4));

        final List<Quiz> quizzes = getQuizzes(id, readableDatabase);
        //return new Category(name, id, theme, (List<? extends Quiz<Object>>) quizzes, scores, solved);

    }

    private static boolean getBooleanFromDatabase(String isSolved) {
        // json stores booleans as true/false strings, whereas SQLite stores them as 0/1 values
        return null != isSolved && isSolved.length() == 1 && Integer.valueOf(isSolved) == 1;
    }

    public static void getCategoryWith(Context context, String categoryId) {
        SQLiteDatabase readableDatabase = getReadableDatabase(context);
        String[] selectionArgs = {categoryId};
        Cursor data = readableDatabase
                .query(CategoryTable.NAME, CategoryTable.PROJECTION, CategoryTable.COLUMN_ID + "=?",
                        selectionArgs, null, null, null);
        data.moveToFirst();
    }

    public static int getScore(Context context) {
        final List<Category> categories = getCategories(context, false);
        int score = 0;
        for (Category cat : categories) {
            score += cat.getScore();
        }
        return score;
    }

    public static void updateCategory(Context context, Category category) {
        if (mCategories != null && mCategories.contains(category)) {
            final int location = mCategories.indexOf(category);
            mCategories.remove(location);
            mCategories.add(location, category);
        }
        SQLiteDatabase writableDatabase = getWritableDatabase(context);
        ContentValues categoryValues = createContentValuesFor(category);
        writableDatabase.update(CategoryTable.NAME, categoryValues, CategoryTable.COLUMN_ID + "=?",
                new String[]{category.getId()});
        final List<Quiz<Object>> quizzes = category.getQuizzes();
        updateQuizzes(writableDatabase, quizzes);
    }

    private static void updateQuizzes(SQLiteDatabase writableDatabase, List<Quiz<Object>> quizzes) {
        Quiz quiz;
        ContentValues quizValues = new ContentValues();
        String[] quizArgs = new String[1];
        for (int i = 0; i < quizzes.size(); i++) {
            quiz = quizzes.get(i);
            quizValues.clear();
            quizValues.put(QuizTable.COLUMN_SOLVED, quiz.isSolved());

            quizArgs[0] = quiz.getQuestion();
            writableDatabase.update(QuizTable.NAME, quizValues, QuizTable.COLUMN_QUESTION + "=?",
                    quizArgs);
        }
    }

    public static void reset(Context context) {
        SQLiteDatabase writableDatabase = getWritableDatabase(context);
        writableDatabase.delete(CategoryTable.NAME, null, null);
        writableDatabase.delete(QuizTable.NAME, null, null);
        getInstance(context).preFillDatabase(writableDatabase);
    }

    private static List<Quiz> getQuizzes(final String categoryId, SQLiteDatabase database) {
        final List<Quiz> quizzes = new ArrayList<>();
        final Cursor cursor = database.query(QuizTable.NAME, QuizTable.PROJECTION,
                QuizTable.FK_CATEGORY + " LIKE ?", new String[]{categoryId}, null, null, null);
        cursor.moveToFirst();
        do {
            quizzes.add(createQuizDueToType(cursor));
        } while (cursor.moveToNext());
        cursor.close();
        return quizzes;
    }

    private static Quiz createQuizDueToType(Cursor cursor) {
        // "magic numbers" based on QuizTable#PROJECTION
        final String type = cursor.getString(2);
        final String question = cursor.getString(3);
        final String answer = cursor.getString(4);
        final String options = cursor.getString(5);
        final int min = cursor.getInt(6);
        final int max = cursor.getInt(7);
        final int step = cursor.getInt(8);
        final boolean solved = getBooleanFromDatabase(cursor.getString(11));

        switch (type) {
            case JsonAttributes.QuizType.ALPHA_PICKER: {
                return new AlphaPickerQuiz(question, answer, solved);
            }
            case JsonAttributes.QuizType.FILL_BLANK: {
                return createFillBlankQuiz(cursor, question, answer, solved);
            }
            case JsonAttributes.QuizType.FOUR_QUARTER: {
                return createFourQuarterQuiz(question, answer, options, solved);
            }
            case JsonAttributes.QuizType.MULTI_SELECT: {
                return createMultiSelectQuiz(question, answer, options, solved);
            }
            case JsonAttributes.QuizType.PICKER: {
                return new PickerQuiz(question, Integer.valueOf(answer), min, max, step, solved);
            }
            case JsonAttributes.QuizType.SINGLE_SELECT:
                //fall-through intended
            case JsonAttributes.QuizType.SINGLE_SELECT_ITEM: {
                return createSelectItemQuiz(question, answer, options, solved);
            }
            case JsonAttributes.QuizType.TOGGLE_TRANSLATE: {
                return createToggleTranslateQuiz(question, answer, options, solved);
            }
            default: {
                throw new IllegalArgumentException("Quiz type " + type + " is not supported");
            }
        }
    }

    private static Quiz createFillBlankQuiz(Cursor cursor, String question,
                                            String answer, boolean solved) {
        final String start = cursor.getString(9);
        final String end = cursor.getString(10);
        return new FillBlankQuiz(question, answer, start, end, solved);
    }

    private static Quiz createFourQuarterQuiz(String question, String answer,
                                              String options, boolean solved) {
        final int[] answerArray = JsonHelper.INSTANCE.jsonArrayToIntArray(answer);
        final String[] optionsArray = JsonHelper.INSTANCE.jsonArrayToStringArray(options);
        return new FourQuarterQuiz(question, answerArray, optionsArray, solved);
    }

    private static Quiz createMultiSelectQuiz(String question, String answer,
                                              String options, boolean solved) {
        final int[] answerArray = JsonHelper.INSTANCE.jsonArrayToIntArray(answer);
        final String[] optionsArray = JsonHelper.INSTANCE.jsonArrayToStringArray(options);
        return new MultiSelectQuiz(question, answerArray, optionsArray, solved);
    }

    private static Quiz createSelectItemQuiz(String question, String answer,
                                             String options, boolean solved) {
        final int[] answerArray = JsonHelper.INSTANCE.jsonArrayToIntArray(answer);
        final String[] optionsArray = JsonHelper.INSTANCE.jsonArrayToStringArray(options);
        return new SelectItemQuiz(question, answerArray, optionsArray, solved);
    }

    private static Quiz createToggleTranslateQuiz(String question, String answer,
                                                  String options, boolean solved) {
        final int[] answerArray = JsonHelper.INSTANCE.jsonArrayToIntArray(answer);
        final String[][] optionsArrays = extractOptionsArrays(options);
        return new ToggleTranslateQuiz(question, answerArray, optionsArrays, solved);
    }

    private static void createTrueFalseQuiz(String question, String answer, boolean solved) {

        final boolean answerValue = "true".equals(answer);
    }

    private static String[][] extractOptionsArrays(String options) {
        final String[] optionsLvlOne = JsonHelper.INSTANCE.jsonArrayToStringArray(options);
        final String[][] optionsArray = new String[optionsLvlOne.length][];
        for (int i = 0; i < optionsLvlOne.length; i++) {
            optionsArray[i] = JsonHelper.INSTANCE.jsonArrayToStringArray(optionsLvlOne[i]);
        }
        return optionsArray;
    }

    private static ContentValues createContentValuesFor(Category category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CategoryTable.COLUMN_SOLVED, category.isSolved());
        contentValues.put(CategoryTable.COLUMN_SCORES, Arrays.toString(category.getScores()));
        return contentValues;
    }

    private static SQLiteDatabase getReadableDatabase(Context context) {
        return getInstance(context).getReadableDatabase();
    }

    private static SQLiteDatabase getWritableDatabase(Context context) {
        return getInstance(context).getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CategoryTable.CREATE);
        db.execSQL(QuizTable.CREATE);
        preFillDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private void preFillDatabase(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            try {
                fillCategoriesAndQuizzes(db);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "preFillDatabase", e);
        }
    }

    private void fillCategoriesAndQuizzes(SQLiteDatabase db) throws JSONException, IOException {
        ContentValues values = new ContentValues(); // reduce, reuse
        JSONArray jsonArray = new JSONArray(readCategoriesFromResources());
        JSONObject category;
        for (int i = 0; i < jsonArray.length(); i++) {
            category = jsonArray.getJSONObject(i);
            final String categoryId = category.getString(JsonAttributes.ID);
            fillCategory(db, values, category, categoryId);
            final JSONArray quizzes = category.getJSONArray(JsonAttributes.QUIZZES);
            fillQuizzesForCategory(db, values, quizzes, categoryId);
        }
    }

    private String readCategoriesFromResources() throws IOException {
        StringBuilder categoriesJson = new StringBuilder();
        InputStream rawCategories = mResources.openRawResource(R.raw.categories);
        BufferedReader reader = new BufferedReader(new InputStreamReader(rawCategories));
        String line;

        while ((line = reader.readLine()) != null) {
            categoriesJson.append(line);
        }
        return categoriesJson.toString();
    }

    private void fillCategory(SQLiteDatabase db, ContentValues values, JSONObject category,
                              String categoryId) throws JSONException {
        values.clear();
        values.put(CategoryTable.COLUMN_ID, categoryId);
        values.put(CategoryTable.COLUMN_NAME, category.getString(JsonAttributes.NAME));
        values.put(CategoryTable.COLUMN_THEME, category.getString(JsonAttributes.THEME));
        values.put(CategoryTable.COLUMN_SOLVED, category.getString(JsonAttributes.SOLVED));
        values.put(CategoryTable.COLUMN_SCORES, category.getString(JsonAttributes.SCORES));
        db.insert(CategoryTable.NAME, null, values);
    }

    private void fillQuizzesForCategory(SQLiteDatabase db, ContentValues values, JSONArray quizzes,
                                        String categoryId) throws JSONException {
        JSONObject quiz;
        for (int i = 0; i < quizzes.length(); i++) {
            quiz = quizzes.getJSONObject(i);
            values.clear();
            values.put(QuizTable.FK_CATEGORY, categoryId);
            values.put(QuizTable.COLUMN_TYPE, quiz.getString(JsonAttributes.TYPE));
            values.put(QuizTable.COLUMN_QUESTION, quiz.getString(JsonAttributes.QUESTION));
            values.put(QuizTable.COLUMN_ANSWER, quiz.getString(JsonAttributes.ANSWER));
            putNonEmptyString(values, quiz, JsonAttributes.OPTIONS, QuizTable.COLUMN_OPTIONS);
            putNonEmptyString(values, quiz, JsonAttributes.MIN, QuizTable.COLUMN_MIN);
            putNonEmptyString(values, quiz, JsonAttributes.MAX, QuizTable.COLUMN_MAX);
            putNonEmptyString(values, quiz, JsonAttributes.START, QuizTable.COLUMN_START);
            putNonEmptyString(values, quiz, JsonAttributes.END, QuizTable.COLUMN_END);
            putNonEmptyString(values, quiz, JsonAttributes.STEP, QuizTable.COLUMN_STEP);
            db.insert(QuizTable.NAME, null, values);
        }
    }

    private void putNonEmptyString(ContentValues values, JSONObject quiz, String jsonKey,
                                   String contentKey) {
        final String stringToPut = quiz.optString(jsonKey, null);
        if (!TextUtils.isEmpty(stringToPut)) {
            values.put(contentKey, stringToPut);
        }
    }

}
