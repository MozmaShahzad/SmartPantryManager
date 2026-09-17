package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    //table names
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //pantry table
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "quantity REAL, " +
                "unit TEXT, " +
                "expiry_date TEXT)");

        // Recipes table
        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "instructions TEXT)");

        // Recipe ingredients table
        db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER, " +
                "ingredient_name TEXT, " +
                "quantity_needed REAL, " +
                "unit TEXT, " +
                "FOREIGN KEY(recipe_id) REFERENCES " + TABLE_RECIPES + "(id))");
        seedRecipes(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        onCreate(db);
    }

    //PANTRY CRUD METHODS

    //CREATE - add a new pantry item
    public long addPantryItem(String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiry_date", expiryDate);
        long id = db.insert(TABLE_PANTRY, null, values);
        db.close();
        return id;
    }

    //READ - get all pantry items
    public Cursor getAllPantryItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PANTRY, null);
    }

    //READ - get one pantry item by ID
    public Cursor getPantryItemById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PANTRY + " WHERE id=?",
                new String[]{String.valueOf(id)});
    }

    //UPDATE - edit an existing pantry item
    public int updatePantryItem(int id, String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiry_date", expiryDate);
        int rows = db.update(TABLE_PANTRY, values, "id=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    // DELETE - remove a pantry item
    public void deletePantryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, "id=?", new String[]{String.valueOf(id)});
        db.close();

    }

    //RECIPE METHODS

    // Add a recipe, returns the new recipe's id
    public long addRecipe(String name, String instructions) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("instructions", instructions);
        long id = db.insert(TABLE_RECIPES, null, values);
        db.close();
        return id;
    }

    // Add one required ingredient for a recipe
    public void addRecipeIngredient(long recipeId, String ingredientName, double quantityNeeded, String unit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("recipe_id", recipeId);
        values.put("ingredient_name", ingredientName);
        values.put("quantity_needed", quantityNeeded);
        values.put("unit", unit);
        db.insert(TABLE_RECIPE_INGREDIENTS, null, values);
        db.close();
    }

    // Get all recipes
    public Cursor getAllRecipes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RECIPES, null);
    }

    // Get all ingredients required for one recipe
    public Cursor getIngredientsForRecipe(long recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RECIPE_INGREDIENTS + " WHERE recipe_id=?",
                new String[]{String.valueOf(recipeId)});
    }

// SEED DATA

    private void seedRecipes(SQLiteDatabase db) {
        long id;

        id = insertRecipe(db, "Tomato Pasta", "Boil pasta. Fry garlic in oil. Add chopped tomato, simmer. Mix with pasta.");
        insertIngredient(db, id, "pasta", 200, "g");
        insertIngredient(db, id, "tomato", 2, "whole");
        insertIngredient(db, id, "garlic", 1, "clove");
        insertIngredient(db, id, "oil", 1, "tbsp");

        id = insertRecipe(db, "Scrambled Eggs on Toast", "Whisk eggs with milk. Melt butter, scramble eggs. Toast bread, serve together.");
        insertIngredient(db, id, "egg", 2, "whole");
        insertIngredient(db, id, "bread", 2, "slice");
        insertIngredient(db, id, "butter", 1, "tbsp");
        insertIngredient(db, id, "milk", 2, "tbsp");

        id = insertRecipe(db, "Garlic Fried Rice", "Fry garlic in oil. Add cooked rice, stir-fry. Push aside, scramble egg in, mix.");
        insertIngredient(db, id, "rice", 200, "g");
        insertIngredient(db, id, "garlic", 2, "clove");
        insertIngredient(db, id, "oil", 1, "tbsp");
        insertIngredient(db, id, "egg", 1, "whole");

        id = insertRecipe(db, "Cheese Omelette", "Whisk eggs with salt. Melt butter, pour eggs, add cheese, fold when set.");
        insertIngredient(db, id, "egg", 3, "whole");
        insertIngredient(db, id, "cheese", 50, "g");
        insertIngredient(db, id, "butter", 1, "tbsp");
        insertIngredient(db, id, "salt", 1, "pinch");

        id = insertRecipe(db, "Tuna Sandwich", "Mix tuna, mayo, chopped onion. Spread on bread, close sandwich.");
        insertIngredient(db, id, "bread", 2, "slice");
        insertIngredient(db, id, "tuna", 100, "g");
        insertIngredient(db, id, "mayonnaise", 1, "tbsp");
        insertIngredient(db, id, "onion", 0.5, "whole");

        id = insertRecipe(db, "Vegetable Stir Fry", "Chop vegetables. Stir-fry in oil until tender. Add soy sauce, toss.");
        insertIngredient(db, id, "carrot", 1, "whole");
        insertIngredient(db, id, "broccoli", 100, "g");
        insertIngredient(db, id, "soy sauce", 1, "tbsp");
        insertIngredient(db, id, "oil", 1, "tbsp");

        id = insertRecipe(db, "Banana Pancakes", "Mash banana, mix with egg and milk. Stir in flour. Cook spoonfuls on a pan.");
        insertIngredient(db, id, "banana", 1, "whole");
        insertIngredient(db, id, "flour", 100, "g");
        insertIngredient(db, id, "egg", 1, "whole");
        insertIngredient(db, id, "milk", 100, "ml");

        id = insertRecipe(db, "Chicken Soup", "Boil chicken in water. Add chopped carrot and onion, simmer until soft.");
        insertIngredient(db, id, "chicken", 200, "g");
        insertIngredient(db, id, "carrot", 1, "whole");
        insertIngredient(db, id, "onion", 1, "whole");
        insertIngredient(db, id, "water", 500, "ml");

        id = insertRecipe(db, "Grilled Cheese Sandwich", "Butter bread outside. Place cheese between slices. Grill both sides until golden.");
        insertIngredient(db, id, "bread", 2, "slice");
        insertIngredient(db, id, "cheese", 50, "g");
        insertIngredient(db, id, "butter", 1, "tbsp");

        id = insertRecipe(db, "Potato Salad", "Boil and cube potatoes. Mix with mayo, chopped onion, salt.");
        insertIngredient(db, id, "potato", 300, "g");
        insertIngredient(db, id, "mayonnaise", 2, "tbsp");
        insertIngredient(db, id, "onion", 0.5, "whole");
        insertIngredient(db, id, "salt", 1, "pinch");

        id = insertRecipe(db, "Fried Egg Rice Bowl", "Fry egg in oil. Serve over warm rice, drizzle with soy sauce.");
        insertIngredient(db, id, "rice", 200, "g");
        insertIngredient(db, id, "egg", 1, "whole");
        insertIngredient(db, id, "soy sauce", 1, "tbsp");
        insertIngredient(db, id, "oil", 1, "tbsp");

        id = insertRecipe(db, "Tomato Soup", "Simmer chopped tomato, onion, garlic in water until soft. Blend if desired.");
        insertIngredient(db, id, "tomato", 4, "whole");
        insertIngredient(db, id, "onion", 1, "whole");
        insertIngredient(db, id, "garlic", 1, "clove");
        insertIngredient(db, id, "water", 300, "ml");

        id = insertRecipe(db, "Peanut Butter Toast", "Toast bread. Spread peanut butter, top with banana slices.");
        insertIngredient(db, id, "bread", 2, "slice");
        insertIngredient(db, id, "peanut butter", 2, "tbsp");
        insertIngredient(db, id, "banana", 1, "whole");

        id = insertRecipe(db, "Chicken Fried Rice", "Cook chicken, set aside. Fry rice and egg, add chicken back in, add soy sauce.");
        insertIngredient(db, id, "rice", 200, "g");
        insertIngredient(db, id, "chicken", 150, "g");
        insertIngredient(db, id, "egg", 1, "whole");
        insertIngredient(db, id, "soy sauce", 1, "tbsp");

        id = insertRecipe(db, "Cucumber Salad", "Slice cucumber and onion. Toss with vinegar and salt.");
        insertIngredient(db, id, "cucumber", 1, "whole");
        insertIngredient(db, id, "onion", 0.5, "whole");
        insertIngredient(db, id, "vinegar", 1, "tbsp");
        insertIngredient(db, id, "salt", 1, "pinch");

        id = insertRecipe(db, "Mashed Potatoes", "Boil potatoes until soft. Mash with butter, milk, and salt.");
        insertIngredient(db, id, "potato", 300, "g");
        insertIngredient(db, id, "butter", 1, "tbsp");
        insertIngredient(db, id, "milk", 50, "ml");
        insertIngredient(db, id, "salt", 1, "pinch");

        id = insertRecipe(db, "Egg Fried Noodles", "Boil noodles. Fry garlic and egg, mix in noodles and soy sauce.");
        insertIngredient(db, id, "noodles", 200, "g");
        insertIngredient(db, id, "egg", 1, "whole");
        insertIngredient(db, id, "soy sauce", 1, "tbsp");
        insertIngredient(db, id, "garlic", 1, "clove");

        id = insertRecipe(db, "Carrot Soup", "Simmer chopped carrot and onion in water until soft. Blend with butter.");
        insertIngredient(db, id, "carrot", 4, "whole");
        insertIngredient(db, id, "onion", 1, "whole");
        insertIngredient(db, id, "water", 400, "ml");
        insertIngredient(db, id, "butter", 1, "tbsp");

        id = insertRecipe(db, "Baked Beans on Toast", "Toast bread and butter it. Heat beans, pour over toast.");
        insertIngredient(db, id, "bread", 2, "slice");
        insertIngredient(db, id, "baked beans", 200, "g");
        insertIngredient(db, id, "butter", 1, "tbsp");

        id = insertRecipe(db, "Simple Fried Rice", "Chop carrot and onion, fry until soft. Add rice and soy sauce, stir-fry.");
        insertIngredient(db, id, "rice", 200, "g");
        insertIngredient(db, id, "carrot", 1, "whole");
        insertIngredient(db, id, "onion", 0.5, "whole");
        insertIngredient(db, id, "soy sauce", 1, "tbsp");
    }

    // Helper: insert a recipe during seeding, returns its new id
    private long insertRecipe(SQLiteDatabase db, String name, String instructions) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("instructions", instructions);
        return db.insert(TABLE_RECIPES, null, values);
    }

    // Helper: insert one ingredient row during seeding
    private void insertIngredient(SQLiteDatabase db, long recipeId, String name, double qty, String unit) {
        ContentValues values = new ContentValues();
        values.put("recipe_id", recipeId);
        values.put("ingredient_name", name);
        values.put("quantity_needed", qty);
        values.put("unit", unit);
        db.insert(TABLE_RECIPE_INGREDIENTS, null, values);
    }
}
