package com.github.calories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;


import androidx.core.content.ContextCompat;

import com.github.calories.models.Category;
import com.github.calories.models.Exercise;
import com.github.calories.models.Food;
import com.github.calories.models.Ingredient;
import com.github.calories.models.RawValues;
import com.github.calories.models.Record;
import com.github.calories.models.Stats;
import com.github.calories.models.Workout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kotlin.Pair;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    //database name
    public static final String DATABASE_NAME = "11zon";
    //database version
    public static final int DATABASE_VERSION = 1;

    // Table for foods
    public static final String TABLE_RECORD = "tbl_record";
    public static final String TABLE_FOOD = "tbl_food";
    public static final String TABLE_INGREDIENTS = "tbl_ingredients";
    public static final String TABLE_R_F = "tbl_r_f";
    public static final String TABLE_F_I = "tbl_f_i";

    // Table for weight
    public static final String TABLE_WEIGHT = "tbl_weight";
    
    // Table for workout
    public static final String TABLE_CATEGORY = "tbl_category";
    public static final String TABLE_EXERCISE = "tbl_exercise";
    public static final String TABLE_E_C = "tbl_e_c";
    public static final String TABLE_WORKOUT = "tbl_workout";
    public static final String TABLE_W_E = "tbl_w_e";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query;
        //creating table
        query = "CREATE TABLE " + TABLE_RECORD + "(ID INTEGER PRIMARY KEY, Date TEXT)";
        db.execSQL(query);

        query = "CREATE TABLE " + TABLE_FOOD + "(ID INTEGER PRIMARY KEY, Name TEXT, " +
                "Energy_kcal_100g INTEGER, " +
                "fat_100g REAL, " +
                "fiber_100g REAL, " +
                "proteins_100g REAL, " +
                "salt_100g REAL, " +
                "saturated_fat_100g REAL, " +
                "sodium_100g REAL, " +
                "sugars_100g REAL)";
        db.execSQL(query);

        query = "CREATE TABLE " + TABLE_INGREDIENTS + "(ID TEXT PRIMARY KEY, Name TEXT)";
        db.execSQL(query);

        query = "CREATE TABLE " + TABLE_R_F + "(" +
                "ID_Record INTEGER NOT NULL, " +
                "ID_Food INTEGER NOT NULL, " +
                "Quantity INTEGER, " +
                "Unit TEXT," +
                "PRIMARY KEY ( ID_Record, ID_Food))";
        db.execSQL(query);

        query = "CREATE TABLE " + TABLE_F_I + "(" +
                "ID_Food INTEGER NOT NULL, " +
                "ID_Ingredient TEXT NOT NULL, " +
                "percent_estimate REAL, " +
                "PRIMARY KEY ( ID_Food, ID_Ingredient))";
        db.execSQL(query);

        query = "CREATE TABLE " + TABLE_WEIGHT + "(" +
                "Date TEXT PRIMARY KEY, " +
                "value REAL NOT NULL)";
        db.execSQL(query);

        query = "CREATE TABLE " + TABLE_CATEGORY + "(" +
                "ID INTEGER PRIMARY KEY, " +
                "Name TEXT NOT NULL)";
        db.execSQL(query);

        query = "CREATE TABLE " + TABLE_EXERCISE + "(" +
                "ID INTEGER PRIMARY KEY, " +
                "RecoverTime INTEGER, " +
                "Repetition INTEGER, " +
                "Weighted INTEGER, " +
                "Time INTEGER, " +
                "Name TEXT NOT NULL)";
        db.execSQL(query);

        query = "CREATE TABLE " + TABLE_WORKOUT + "(" +
                "ID INTEGER PRIMARY KEY, " +
                "Name TEXT NOT NULL," +
                "RecoverTime INTEGER)";
        db.execSQL(query);

        query = "CREATE TABLE " + TABLE_E_C + "(" +
                "ID_Exercise INTEGER NOT NULL, " +
                "ID_Category INTEGER NOT NULL, " +
                "PRIMARY KEY ( ID_Exercise, ID_Category))";
        db.execSQL(query);

        query = "CREATE TABLE " + TABLE_W_E + "(" +
                "ID_Workout INTEGER NOT NULL, " +
                "ID_Exercise INTEGER NOT NULL, " +
                "PRIMARY KEY ( ID_Workout, ID_Exercise))";
        db.execSQL(query);
    }

    //upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_R_F);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_E_C);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_W_E);
        onCreate(db);
    }

    public long addRecord(Record record) {
        SQLiteDatabase sqLiteDatabase = this .getWritableDatabase();

        if(record.getFoods() == null)
            return -1;

        //inserting new row
        long record_id = sqLiteDatabase.insert(TABLE_RECORD, null , extractValuesRecord(record));

        for(Food food : record.getFoods()) {

            long food_id = sqLiteDatabase.insert(TABLE_FOOD, null , extractValuesFood(food));
            if(food_id == -1)
                food_id = Long.parseLong(food.getId());

            // insert in R_F
            ContentValues values_R_F = new ContentValues();
            values_R_F.put("ID_Record",record_id);
            values_R_F.put("ID_Food",food_id);
            values_R_F.put("Quantity",food.getQuantity());
            values_R_F.put("Unit",food.getUnit());

            sqLiteDatabase.insert(TABLE_R_F, null , values_R_F);


            if(food.getIngredients() != null)
                for(Ingredient ingredient: food.getIngredients()) {

                    sqLiteDatabase.insert(TABLE_INGREDIENTS, null , extractValuesIngredient(ingredient));

                    // insert in F_I
                    ContentValues values_F_I = new ContentValues();
                    values_F_I.put("ID_Food",food_id);
                    values_F_I.put("ID_Ingredient",ingredient.getId());
                    values_F_I.put("percent_estimate",ingredient.getPercentEstimate());

                    sqLiteDatabase.insert(TABLE_F_I, null , values_F_I);
                }

        }

        sqLiteDatabase.close();
        return record_id;
    }

    private static ContentValues extractValuesRecord(Record record) {
        ContentValues values =  new ContentValues();
        values.put("Date", record.getDate());
        return values;
    }

    private static ContentValues extractValuesFood(Food food) {
        ContentValues values =  new ContentValues();
        values.put("ID", food.getId());
        values.put("Name", food.getName());
        values.put("Energy_kcal_100g", food.getEnergyKCAL100g());
        values.put("fat_100g", food.getFat100g());
        values.put("fiber_100g", food.getFiber100g());
        values.put("proteins_100g", food.getProteins100g());
        values.put("salt_100g", food.getSalt100g());
        values.put("saturated_fat_100g", food.getSaturatedFat100g());
        values.put("sodium_100g", food.getSodium100g());
        values.put("sugars_100g", food.getSugars100g());
        return values;
    }

    private static ContentValues extractValuesIngredient(Ingredient ingredient) {
        ContentValues values =  new ContentValues();
        values.put("ID", ingredient.getId());
        values.put("Name", ingredient.getName());
        return values;
    }

    public HashMap<String,RawValues> getEnergyPerDay(String start_date, String end_date) {
        HashMap<String,RawValues> map = new HashMap<>();
        String select_query= "SELECT "+ TABLE_RECORD +".ID, Date(" + TABLE_RECORD + ".Date) AS day, SUM("+ TABLE_R_F +".Quantity*"+ TABLE_FOOD +".Energy_kcal_100g/100) AS energy  FROM " + TABLE_RECORD + " INNER JOIN "+ TABLE_R_F +" ON "+ TABLE_R_F +".ID_Record = " + TABLE_RECORD + ".ID INNER JOIN "+ TABLE_FOOD +" ON "+ TABLE_FOOD +".ID = "+ TABLE_R_F +".ID_Food WHERE day BETWEEN \""+ start_date +"\" AND \""+ end_date +"\" GROUP BY day;";
        //Log.d(TAG, "getEnergyPerDay: " + select_query);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(0) != null) {
                    RawValues val = new RawValues(cursor.getLong(0), cursor.getString(1), cursor.getLong(2));
                    map.put(val.getDate(),val);
                }

            }while (cursor.moveToNext());
        }

        cursor.close();
        return map;
    }

    public ArrayList<Food> getMostEatenFood(int limit) {
        ArrayList<Food> foods = new ArrayList<>();

        String select_query = "SELECT ID_Food,tbl_food.Name,COUNT(tbl_r_f.ID_Food) as c FROM tbl_r_f INNER JOIN tbl_food ON  tbl_r_f.ID_Food = tbl_food.ID GROUP BY tbl_r_f.ID_Food ORDER BY c DESC LIMIT " + limit;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(0) != null) {
                    foods.add(new Food(cursor.getString(0),
                            cursor.getString(1),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null));
                }

            }while (cursor.moveToNext());
        }
        cursor.close();
        return foods;
    }


    public List<Food> getFoodByQuery(String query) {
        /*SELECT * FROM mytable
WHERE column1 LIKE '%word1%'*/

        ArrayList<Food> foods = new ArrayList<>();

        String select_query = "SELECT * FROM " + TABLE_FOOD + " WHERE Name LIKE '%" + query +"%'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(0) != null) {
                    foods.add(new Food(cursor.getString(0),
                            cursor.getString(1),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null));
                }

            }while (cursor.moveToNext());
        }
        cursor.close();
        return foods;

    }

    public Food getFood(String food_id) {
        String select_query = "SELECT * FROM "+ TABLE_FOOD +" LEFT JOIN " + TABLE_F_I +" ON "+ TABLE_F_I +".ID_Food = "+ TABLE_FOOD +".ID LEFT JOIN "+ TABLE_INGREDIENTS +" ON "+ TABLE_F_I +".ID_Ingredient = "+ TABLE_INGREDIENTS +".ID WHERE "+ TABLE_FOOD +".ID = " + food_id;

        Log.d(TAG, "getFood: (" + select_query + ")");
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        Food food = null;
        boolean isFirst = true;
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(0) != null) {

                    if(isFirst) {
                        //Log.d(TAG, "getFood: food setup");
                        food = new Food(
                                cursor.getString(0),
                                cursor.getString(1),
                                cursor.getInt(2),
                                cursor.getDouble(3),
                                cursor.getDouble(4),
                                cursor.getDouble(5),
                                cursor.getDouble(6),
                                cursor.getDouble(7),
                                cursor.getDouble(8),
                                cursor.getDouble(9),
                                null
                                );
                        isFirst = false;
                    }

                    String id = cursor.getString(11);
                    if(id != null)
                        ingredients.add(new Ingredient(id, cursor.getString(14), cursor.getDouble(12)));
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        if(food == null)
            return null;

        food.setIngredients(ingredients);
        return food;
    }

    public ArrayList<Ingredient> getIngredients(Food food) {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        String select_query = "SELECT  "+ TABLE_INGREDIENTS +".*, "+ TABLE_F_I +".percent_estimate FROM "+ TABLE_FOOD +" INNER JOIN " + TABLE_F_I +" ON "+ TABLE_F_I +".ID_Food = "+ TABLE_FOOD +".ID INNER JOIN "+ TABLE_INGREDIENTS +" ON "+ TABLE_F_I +".ID_Ingredient = "+ TABLE_INGREDIENTS +".ID WHERE "+ TABLE_FOOD +".ID = " + food.getId();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(0) != null) {
                    ingredients.add(new Ingredient(cursor.getString(0), cursor.getString(1), cursor.getDouble(2)));
                }

            }while (cursor.moveToNext());
        }
        cursor.close();
        return ingredients;
    }

    public Record getRecord(String record_id) {
        //SELECT * FROM tbl_record INNER JOIN tbl_r_f ON tbl_record.ID = tbl_r_f.ID_Record INNER JOIN tbl_food ON tbl_r_f.ID_Food = tbl_food.ID INNER JOIN tbl_f_i ON tbl_f_i.ID_Food = tbl_food.ID INNER JOIN tbl_ingredients ON tbl_ingredients.ID = tbl_f_i.ID_Ingredient WHERE tbl_record.ID = 1
        Record record = null;
        String select_query = "SELECT * FROM tbl_record INNER JOIN tbl_r_f ON tbl_record.ID = tbl_r_f.ID_Record WHERE tbl_record.ID =" + record_id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        boolean isFirst = true;
        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(0) != null) {

                    if(isFirst) {
                        record = new Record(cursor.getLong(0),cursor.getString(1));
                        isFirst =false;
                    }

                    Food food = getFood(cursor.getString(3));
                    if(food != null) {
                        food.setQuantity(cursor.getInt(4));
                        food.setUnit(cursor.getString(5));
                        record.addFood(food);
                    }
                }

            }while (cursor.moveToNext());
        }
        cursor.close();
        return record;
    }

    public List<Record> getRecords(String day) {
        List<Record> records = new ArrayList<>();
        String select_query = "SELECT *,Date(tbl_record.Date) AS day FROM tbl_record WHERE day = \""+ day +"\"";
        //Log.d(TAG, "getRecords: (" + select_query + ")");
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(0) != null) {
                    Record record = getRecord(cursor.getLong(0) + "");
                    if(record != null)
                        records.add(record);
                }

            }while (cursor.moveToNext());
        }
        cursor.close();
        return records;

    }

    public List<Stats> getStats(String start_date, String end_date) {
        List<Stats> statsList = new ArrayList<>();

        String select_query = "SELECT SUM(tbl_food.Energy_kcal_100g*Quantity/100) AS energy,  SUM(tbl_food.fat_100g*Quantity/100) AS fat,  SUM(tbl_food.fiber_100g*Quantity/100) AS fiber,  SUM(tbl_food.proteins_100g*Quantity/100) AS protein,  SUM(tbl_food.salt_100g*Quantity/100) AS salt,  SUM(tbl_food.saturated_fat_100g*Quantity/100) AS saturated_fat,  SUM(tbl_food.sodium_100g*Quantity/100) AS sodium,  SUM(tbl_food.sugars_100g*Quantity/100) AS sugar,  Date(tbl_record.Date) AS day  FROM tbl_record INNER JOIN tbl_r_f ON tbl_r_f.ID_Record = tbl_record.ID INNER JOIN tbl_food ON tbl_food.ID = tbl_r_f.ID_Food WHERE day BETWEEN \""+ start_date +"\" AND \""+ end_date +"\" GROUP BY day ORDER BY day;";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(0) != null) {
                    statsList.add(new Stats(cursor.getDouble(0),
                            cursor.getDouble(1),
                            cursor.getDouble(2),
                            cursor.getDouble(3),
                            cursor.getDouble(4),
                            cursor.getDouble(5),
                            cursor.getDouble(6),
                            cursor.getDouble(7),
                            cursor.getString(8)
                            ));
                }

            }while (cursor.moveToNext());
        }
        cursor.close();
        return statsList;
    }

    /*public void deleteFoods(List<String> foods) {
        StringBuilder query = new StringBuilder("DELETE FROM " + TABLE_FOOD + " WHERE id IN (");
        for(int i = 0 ; i < foods.size() -1 ; i++) {
            query.append(foods.get(i).getId()).append(", ");
        }
        query.append(foods.get(foods.size()-1).getId()).append(")");
        Log.d(TAG, "deleteFoods: " + query);
        SQLiteDatabase db = this.getWritableDatabase();
        db.rawQuery(query.toString(), null).close();
    }*/

    public void deleteFoodLink(List<Pair<String,String>> links) {
        if(links.size() == 0)
            return;

        SQLiteDatabase db = this.getWritableDatabase();
        for(Pair<String, String> link : links)  {
            db.delete(TABLE_R_F, " ID_Record="+link.getFirst()+ " AND ID_Food="+link.getSecond(), null);
        }
        db.close();
    }







    public void deleteRecords(List<String> records) {
        if(records.size() == 0)
            return;

        StringBuilder query = new StringBuilder("ID IN (");
        for(int i = 0 ; i < records.size() ; i++) {
            query.append(records.get(i));
            if(i != records.size() - 1)
                query.append(", ");
        }
        query.append(")");
        Log.d(TAG, "deleteRecords: " + query);

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECORD, query.toString(), null);
        db.close();
    }

    public void addWeight(String date, Float weight) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values =  new ContentValues();
        values.put("Date", date);
        values.put("value", weight);
        db.insertOrThrow(TABLE_WEIGHT, null , values);

        db.close();

    }

    public HashMap<String,Float> getWeights(String start_date, String end_date) {
        HashMap<String,Float> map = new HashMap<>();
        String select_query= "SELECT Date(Date) as day, value FROM "+ TABLE_WEIGHT +" WHERE day BETWEEN \""+ start_date +"\" AND \""+ end_date +"\"";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);
        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(0) != null) {
                    map.put(cursor.getString(0),cursor.getFloat(1));
                }
            }while (cursor.moveToNext());
        }

        cursor.close();
        return map;
    }

    public Category addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values =  new ContentValues();
        values.put("Name", category.getName());
        category.setId(db.insert(TABLE_CATEGORY, null , values));

        db.close();
        return category;
    }

    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        String select_query = "SELECT * FROM " + TABLE_CATEGORY ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        if (cursor.moveToFirst()) {
            do {
                categories.add(new Category(cursor.getString(1),cursor.getLong(0)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    public void deleteCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORY,"ID=" + category.getId(),null);
        db.delete(TABLE_E_C,"ID_Category=" + category.getId(),null);
        db.close();
    }

    public Exercise addExercise(Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues exerciseValues =  new ContentValues();
        exerciseValues.put("Name", exercise.getName());
        exerciseValues.put("RecoverTime", exercise.getRecoverTime());
        exerciseValues.put("Time", exercise.getTime());
        exerciseValues.put("Weighted", exercise.getWeighted()?1:0);
        exerciseValues.put("Repetition", exercise.getRepetitionCount());
        exercise.setId(db.insert(TABLE_EXERCISE, null , exerciseValues));

        // Insert in db the categories selected for this exercise
        if(exercise.getCategories() != null)
            for(Category category: exercise.getCategories()) {
                ContentValues e_c_values =  new ContentValues();
                e_c_values.put("ID_Exercise",exercise.getId());
                e_c_values.put("ID_Category",category.getId());
                db.insert(TABLE_E_C,null,e_c_values);
            }

        db.close();
        return exercise;
    }

    public Workout addWorkout(Workout workout) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues workoutValues =  new ContentValues();
        workoutValues.put("Name", workout.getName());
        workoutValues.put("RecoverTime", workout.getRecoverTime());
        workout.setId(db.insert(TABLE_WORKOUT, null , workoutValues));

        // Insert in db the categories selected for this exercise
        if(workout.getExercises() != null)
            for(Exercise exercise: workout.getExercises()) {
                ContentValues w_e_values =  new ContentValues();
                w_e_values.put("ID_Exercise",exercise.getId());
                w_e_values.put("ID_Workout",workout.getId());
                db.insert(TABLE_W_E,null,w_e_values);
            }

        db.close();
        return workout;
    }

    public List<Workout> getWorkouts() {
        List<Workout> workouts = new ArrayList<>();

        String select_query = "SELECT * FROM " + TABLE_WORKOUT ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        if (cursor.moveToFirst()) {
            do {
                workouts.add(new Workout(cursor.getLong(0),cursor.getString(1),cursor.getInt(2), null));
            }while (cursor.moveToNext());
        }
        cursor.close();

        return workouts;
    }

    public List<Exercise> getExercises() {
        List<Exercise> exercises = new ArrayList<>();

        String select_query = "SELECT * FROM tbl_exercise LEFT JOIN tbl_e_c ON tbl_exercise.ID = tbl_e_c.ID_Exercise LEFT JOIN tbl_category ON tbl_category.ID = tbl_e_c.ID_Category ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        long currentID = -1;
        Exercise buffer = null;
        if (cursor.moveToFirst()) {
            do {
                if(currentID !=  cursor.getLong(0)) {
                    if(buffer != null)
                        exercises.add(buffer);

                    buffer = new Exercise();
                    buffer.setId(cursor.getLong(0));
                    buffer.setRecoverTime(cursor.getInt(1));
                    buffer.setRepetitionCount(cursor.getInt(2));
                    buffer.setWeighted(cursor.getInt(3) == 1);
                    buffer.setTime(cursor.getInt(4));
                    buffer.setName(cursor.getString(5));
                    currentID = buffer.getId();
                }
                else
                {
                    if(cursor.getString(7) != null)
                        buffer.addCategory(new Category(cursor.getString(7),cursor.getLong(5)));
                }

            }while (cursor.moveToNext());
            if(buffer != null)
                exercises.add(buffer);
        }
        cursor.close();
        return exercises;
    }


    public List<Exercise> getExerciseByWorkoutID(Long workout_id, boolean loadImages, Context context) {
        List<Exercise> exercises = new ArrayList<>();

        String select_query = "SELECT * FROM tbl_w_e INNER JOIN tbl_exercise ON tbl_w_e.ID_Exercise = tbl_exercise.ID WHERE tbl_w_e.ID_Workout = " + workout_id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        if (cursor.moveToFirst()) {
            do {
                Exercise e = new Exercise(cursor.getLong(1),cursor.getString(7), null,cursor.getInt(3),cursor.getInt(4),cursor.getInt(5) == 1,cursor.getInt(6));
                if(loadImages) {
                    e.loadBitmap(context);
                }
                exercises.add(e);
            }while (cursor.moveToNext());
        }
        cursor.close();

        return exercises;
    }



    // Copy the database from assets
    public static void copyDataBase(Context context) throws IOException {

        File DB_FILE = context.getDatabasePath(DATABASE_NAME);

        InputStream mInput = context.getAssets().open(DATABASE_NAME);
        OutputStream mOutput = new FileOutputStream(DB_FILE);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }


    //add the new expense
    /*public long addExpense(Expense expense) {
        SQLiteDatabase sqLiteDatabase = this .getWritableDatabase();
        //inserting new row
        long id = sqLiteDatabase.insert(TABLE_EXPENSE, null , extractValuesExpense(expense));
        //close database connection
        sqLiteDatabase.close();
        return id;
    }

    public long addWeek(Week week) {
        SQLiteDatabase sqLiteDatabase = this .getWritableDatabase();
        //inserting new row
        long id = sqLiteDatabase.insert(TABLE_WEEK, null , extractValuesWeek(week));
        //close database connection
        sqLiteDatabase.close();
        return id;
    }


    //add the new category
    public long addCategory(Category category) {
        SQLiteDatabase sqLiteDatabase = this .getWritableDatabase();
        //inserting new row
        long id = sqLiteDatabase.insert(TABLE_CATEGORY, null , extractValuesCategory(category));
        //close database connection
        sqLiteDatabase.close();
        return id;
    }

    //get the all expenses
    public ArrayList<Expense> getExpenses() {
        // select all query
        String select_query= "SELECT *FROM " + TABLE_EXPENSE;
        SQLiteDatabase db = this .getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        return extractExpenses(cursor);
    }

    //get the all expenses
    public ArrayList<Category> getCategories() {
        // select all query
        String select_query= "SELECT *FROM " + TABLE_CATEGORY;
        SQLiteDatabase db = this .getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        return extractCategories(cursor);
    }

    public double getTotalMoneySpent() {
        SQLiteDatabase db = this .getWritableDatabase();

        Cursor c = db.rawQuery("select sum(Value) from " + TABLE_EXPENSE, null);
        double value = 0;
        if(c.moveToFirst())
            value = c.getDouble(0);
        else
            value = -1;
        c.close();
        return value;
    }


    public ArrayList<Pair<Long,Double>> getSpentPerCategory() {
        SQLiteDatabase db = this .getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT category, SUM(value) as total FROM " + TABLE_EXPENSE + " GROUP BY category", null);

        ArrayList<Pair<Long,Double>> arrayList = new ArrayList<>();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Pair<Long, Double> cat = new Pair<>(cursor.getLong(0),cursor.getDouble(1));
                arrayList.add(cat);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    public ArrayList<Week> getAllWeeks() {
        // select all query
        String select_query= "SELECT *FROM " + TABLE_WEEK;
        SQLiteDatabase db = this .getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);

        return extractWeeks(cursor);
    }


    public ArrayList<Week> getWeeks() {
        SQLiteDatabase db = this .getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + TABLE_WEEK + ".ID," + TABLE_WEEK + ".goal,"+ TABLE_WEEK +".Date,SUM(" + TABLE_EXPENSE + ".Value) FROM " + TABLE_WEEK + " INNER JOIN "+ TABLE_EXPENSE +" on Date(" + TABLE_EXPENSE + ".Date,'weekday 0','-6 days') = " + TABLE_WEEK + ".Date GROUP BY " + TABLE_WEEK + ".ID ORDER BY " + TABLE_WEEK + ".Date DESC;", null);

        ArrayList<Week> arrayList = new ArrayList<>();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(0) != null)
                    arrayList.add(new Week(cursor.getString(0), cursor.getDouble(1), cursor.getString(2), cursor.getDouble(3) ));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }


    public double getMoneySpent(Calendar start, Calendar end) {
        SQLiteDatabase db = this .getWritableDatabase();

        String startDateQueryDate = TimeUtils.formatSQL(start.toInstant(),"Europe/Paris");
        String endDateQueryDate = TimeUtils.formatSQL(end.toInstant(),"Europe/Paris");

        Cursor c = db.rawQuery("select sum(Value) from " + TABLE_EXPENSE + " where Date BETWEEN '" + startDateQueryDate + "' AND '" + endDateQueryDate + "'", null);

        double value = 0;
        if(c.moveToFirst())
            value = c.getDouble(0);
        else
            value = -1;
        c.close();
        return value;
    }

    //get all expenses between dates
    public ArrayList<Expense> getExpenses(Calendar start, Calendar end) {

        String startDateQueryDate = TimeUtils.formatSQL(start.toInstant(),"Europe/Paris");
        String endDateQueryDate = TimeUtils.formatSQL(end.toInstant(),"Europe/Paris");

        SQLiteDatabase db = this .getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_EXPENSE + " where Date BETWEEN '" + startDateQueryDate + "' AND '" + endDateQueryDate + "' ORDER BY Date DESC", null);

        return extractExpenses(cursor);
    }

    //get all expenses during the current week
    public ArrayList<Expense> getCurrentWeekExpenses() {
        return getExpenses(
                TimeUtils.getFirstDayOfWeek("Europe/Paris"),
                TimeUtils.getLastDayOfWeek("Europe/Paris")
        );
    }

    //delete an expense
    public void deleteExpense(String ID) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //deleting row
        sqLiteDatabase.delete(TABLE_EXPENSE, "ID=" + ID, null);
        sqLiteDatabase.close();
    }

    //delete a category
    public void deleteCategory(String ID) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //deleting row
        sqLiteDatabase.delete(TABLE_CATEGORY, "ID=" + ID, null);
        sqLiteDatabase.close();
    }

    public void deleteWeek(String ID) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //deleting row
        sqLiteDatabase.delete(TABLE_WEEK, "ID=" + ID, null);
        sqLiteDatabase.close();
    }

    //update a week
    public void updateWeek(Week week) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //updating row
        sqLiteDatabase.update(TABLE_EXPENSE, extractValuesWeek(week), "ID=" + week.getID(), null);
        sqLiteDatabase.close();
    }

    public void updateWeek(String date, double goal) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //updating row
        Week w = new Week();
        w.setDate(date);
        w.setGoal(goal);
        int i = sqLiteDatabase.update(TABLE_WEEK, extractValuesWeek(w), "Date='" + date + "'", null);
        Log.d(TAG,"updateWeek: " + i);
        sqLiteDatabase.close();
    }



    //update the expense
    public void updateExpense(Expense expense) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //updating row
        sqLiteDatabase.update(TABLE_EXPENSE, extractValuesExpense(expense), "ID=" + expense.getID(), null);
        sqLiteDatabase.close();
    }

    //update the category
    public void updateCategory(Category category) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //updating row
        sqLiteDatabase.update(TABLE_CATEGORY, extractValuesCategory(category), "ID=" + category.getID(), null);
        sqLiteDatabase.close();
    }

    private static ArrayList<Week> extractWeeks(Cursor cursor) {
        ArrayList<Week> arrayList = new ArrayList<>();

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Week week = new Week();
                week.setID(cursor.getString(0));
                week.setGoal(cursor.getDouble(1));
                week.setDate(cursor.getString(2));

                arrayList.add(week);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }


    private static ArrayList<Expense> extractExpenses(Cursor cursor) {
        ArrayList<Expense> arrayList = new ArrayList<>();

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setID(cursor.getString(0));
                expense.setTitle(cursor.getString(1));
                expense.setCategory(cursor.getLong(2));
                expense.setValue(cursor.getDouble(3));
                expense.setDate(cursor.getString(4));

                arrayList.add(expense);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }


    private static ArrayList<Category> extractCategories(Cursor cursor) {
        ArrayList<Category> arrayList = new ArrayList<>();

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setID(cursor.getString(0));
                category.setName(cursor.getString(1));
                category.setSmiley(cursor.getString(2));

                arrayList.add(category);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    private static ContentValues extractValuesExpense(Expense expense) {
        ContentValues values =  new ContentValues();
        values.put("Title", expense.getTitle());
        values.put("Category", expense.getCategory());
        values.put("Value", expense.getValue());
        values.put("Date", expense.getDate());
        return values;
    }

    private static ContentValues extractValuesWeek(Week week) {
        Log.d(TAG,"extractValuesWeek: " + week.toString());
        ContentValues values =  new ContentValues();
        values.put("Date", week.date);
        values.put("Goal", week.goal);
        return values;
    }

    private static ContentValues extractValuesCategory(Category category) {
        ContentValues values =  new ContentValues();
        values.put("Name", category.getName());
        values.put("Smiley", category.getSmiley());
        return values;
    }*/
}
