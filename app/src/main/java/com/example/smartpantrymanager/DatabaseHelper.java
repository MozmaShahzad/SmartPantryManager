package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 2;

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

//SEED DATA INTO DATABASE WITH RECIPES
    private void seedRecipes(SQLiteDatabase db) {
        long id;

        id = insertRecipe(db, "Tomato Pasta",
                "1. Bring a pot of salted water to a boil and cook the pasta until al dente.\n" +
                        "2. While the pasta cooks, heat oil in a pan over medium heat and add minced garlic. Fry until fragrant, about 1 minute.\n" +
                        "3. Add chopped tomato to the pan and simmer for 5-7 minutes, stirring occasionally, until it breaks down into a light sauce.\n" +
                        "4. Drain the pasta and add it directly into the pan with the sauce.\n" +
                        "5. Toss well to coat, season to taste, and serve hot.");
        insertIngredient(db, id, "pasta", 200, "g");
        insertIngredient(db, id, "tomato", 2, "whole");
        insertIngredient(db, id, "garlic", 1, "clove");
        insertIngredient(db, id, "oil", 1, "tbsp");

        id = insertRecipe(db, "Scrambled Eggs on Toast",
                "1. Crack the eggs into a bowl, add the milk, and whisk until fully combined.\n" +
                        "2. Heat a non-stick pan over low-medium heat and melt the butter.\n" +
                        "3. Pour in the egg mixture and let it sit for a few seconds, then gently push the eggs from the edges to the center using a spatula.\n" +
                        "4. Keep folding gently until the eggs are just set but still soft - remove from heat immediately to avoid overcooking.\n" +
                        "5. Toast the bread slices until golden, plate them, and top with the scrambled eggs.");
        insertIngredient(db, id, "egg", 2, "whole");
        insertIngredient(db, id, "bread", 2, "slice");
        insertIngredient(db, id, "butter", 1, "tbsp");
        insertIngredient(db, id, "milk", 2, "tbsp");

        id = insertRecipe(db, "Garlic Fried Rice",
                "1. Heat oil in a wok or large pan over medium-high heat.\n" +
                        "2. Add minced garlic and stir-fry until golden and fragrant, being careful not to burn it.\n" +
                        "3. Add the cooked rice (day-old rice works best) and stir-fry, breaking up any clumps.\n" +
                        "4. Push the rice to one side of the pan, crack the egg into the empty space, and scramble it.\n" +
                        "5. Mix the scrambled egg through the rice, season to taste, and serve hot.");
        insertIngredient(db, id, "rice", 200, "g");
        insertIngredient(db, id, "garlic", 2, "clove");
        insertIngredient(db, id, "oil", 1, "tbsp");
        insertIngredient(db, id, "egg", 1, "whole");

        id = insertRecipe(db, "Cheese Omelette",
                "1. Crack the eggs into a bowl, add a pinch of salt, and whisk until the yolks and whites are fully blended.\n" +
                        "2. Heat a non-stick pan over medium heat and melt the butter, swirling to coat the pan evenly.\n" +
                        "3. Pour in the eggs and let them cook undisturbed for about 30 seconds until the edges start to set.\n" +
                        "4. Sprinkle the grated cheese evenly over one half of the omelette.\n" +
                        "5. Once the eggs are mostly set, fold the empty half over the cheese and cook for another minute until the cheese melts.");
        insertIngredient(db, id, "egg", 3, "whole");
        insertIngredient(db, id, "cheese", 50, "g");
        insertIngredient(db, id, "butter", 1, "tbsp");
        insertIngredient(db, id, "salt", 1, "pinch");

        id = insertRecipe(db, "Tuna Sandwich",
                "1. Drain the tuna well and place it in a mixing bowl.\n" +
                        "2. Finely chop the onion and add it to the bowl along with the mayonnaise.\n" +
                        "3. Mix everything together until well combined, adjusting mayonnaise to your preferred consistency.\n" +
                        "4. Spread the tuna mixture evenly over one slice of bread.\n" +
                        "5. Top with the second slice, press gently, and slice in half to serve.");
        insertIngredient(db, id, "bread", 2, "slice");
        insertIngredient(db, id, "tuna", 100, "g");
        insertIngredient(db, id, "mayonnaise", 1, "tbsp");
        insertIngredient(db, id, "onion", 0.5, "whole");

        id = insertRecipe(db, "Vegetable Stir Fry",
                "1. Wash and chop the carrot and broccoli into bite-sized pieces.\n" +
                        "2. Heat oil in a wok or large pan over high heat until shimmering.\n" +
                        "3. Add the carrot first, since it takes longer to cook, and stir-fry for 2 minutes.\n" +
                        "4. Add the broccoli and continue stir-frying for another 3-4 minutes until vegetables are tender-crisp.\n" +
                        "5. Pour in the soy sauce, toss everything together for 30 seconds, and serve immediately.");
        insertIngredient(db, id, "carrot", 1, "whole");
        insertIngredient(db, id, "broccoli", 100, "g");
        insertIngredient(db, id, "soy sauce", 1, "tbsp");
        insertIngredient(db, id, "oil", 1, "tbsp");

        id = insertRecipe(db, "Banana Pancakes",
                "1. In a bowl, mash the banana thoroughly with a fork until mostly smooth.\n" +
                        "2. Whisk in the egg and milk until the mixture is well combined.\n" +
                        "3. Gradually stir in the flour until a smooth, lump-free batter forms.\n" +
                        "4. Heat a lightly greased non-stick pan over medium heat.\n" +
                        "5. Pour spoonfuls of batter onto the pan, cook until bubbles form on top, then flip and cook the other side until golden.");
        insertIngredient(db, id, "banana", 1, "whole");
        insertIngredient(db, id, "flour", 100, "g");
        insertIngredient(db, id, "egg", 1, "whole");
        insertIngredient(db, id, "milk", 100, "ml");

        id = insertRecipe(db, "Chicken Soup",
                "1. Place the chicken in a pot and cover with the water.\n" +
                        "2. Bring to a boil over high heat, then reduce to a simmer and skim off any foam that rises to the top.\n" +
                        "3. Add the chopped carrot and onion to the pot.\n" +
                        "4. Simmer gently for 25-30 minutes, until the chicken is fully cooked and the vegetables are soft.\n" +
                        "5. Season to taste, shred the chicken if desired, and serve hot.");
        insertIngredient(db, id, "chicken", 200, "g");
        insertIngredient(db, id, "carrot", 1, "whole");
        insertIngredient(db, id, "onion", 1, "whole");
        insertIngredient(db, id, "water", 500, "ml");

        id = insertRecipe(db, "Grilled Cheese Sandwich",
                "1. Butter one side of each bread slice generously.\n" +
                        "2. Place one slice butter-side down in a cold pan, and layer the cheese on top.\n" +
                        "3. Top with the second slice, butter-side facing up.\n" +
                        "4. Turn the heat to medium-low and grill for 3-4 minutes per side, pressing gently, until golden brown and the cheese has melted.\n" +
                        "5. Remove from the pan, slice diagonally, and serve warm.");
        insertIngredient(db, id, "bread", 2, "slice");
        insertIngredient(db, id, "cheese", 50, "g");
        insertIngredient(db, id, "butter", 1, "tbsp");

        id = insertRecipe(db, "Potato Salad",
                "1. Peel and cube the potatoes, then boil in salted water until fork-tender, about 12-15 minutes.\n" +
                        "2. Drain the potatoes and let them cool slightly.\n" +
                        "3. Finely chop the onion.\n" +
                        "4. In a large bowl, combine the potatoes, chopped onion, mayonnaise, and salt.\n" +
                        "5. Gently fold everything together until evenly coated, then chill before serving if possible.");
        insertIngredient(db, id, "potato", 300, "g");
        insertIngredient(db, id, "mayonnaise", 2, "tbsp");
        insertIngredient(db, id, "onion", 0.5, "whole");
        insertIngredient(db, id, "salt", 1, "pinch");

        id = insertRecipe(db, "Fried Egg Rice Bowl",
                "1. Heat the oil in a pan over medium-high heat.\n" +
                        "2. Crack the egg into the pan and fry until the whites are set but the yolk is still runny (or to your preference).\n" +
                        "3. Warm the rice separately, either on the stove or in the microwave.\n" +
                        "4. Spoon the warm rice into a bowl and slide the fried egg on top.\n" +
                        "5. Drizzle with soy sauce and serve immediately.");
        insertIngredient(db, id, "rice", 200, "g");
        insertIngredient(db, id, "egg", 1, "whole");
        insertIngredient(db, id, "soy sauce", 1, "tbsp");
        insertIngredient(db, id, "oil", 1, "tbsp");

        id = insertRecipe(db, "Tomato Soup",
                "1. Roughly chop the tomato, onion, and garlic.\n" +
                        "2. Place all the chopped vegetables into a pot with the water.\n" +
                        "3. Bring to a boil, then reduce heat and simmer for 15-20 minutes until the tomatoes have fully softened.\n" +
                        "4. If desired, blend the soup with an immersion blender or regular blender until smooth.\n" +
                        "5. Season to taste and serve hot, optionally with a drizzle of oil on top.");
        insertIngredient(db, id, "tomato", 4, "whole");
        insertIngredient(db, id, "onion", 1, "whole");
        insertIngredient(db, id, "garlic", 1, "clove");
        insertIngredient(db, id, "water", 300, "ml");

        id = insertRecipe(db, "Peanut Butter Toast",
                "1. Toast the bread slices until golden and crisp.\n" +
                        "2. Spread the peanut butter evenly over each slice while still warm, so it spreads easily.\n" +
                        "3. Slice the banana into thin rounds.\n" +
                        "4. Arrange the banana slices on top of the peanut butter.\n" +
                        "5. Serve immediately, optionally cut in half.");
        insertIngredient(db, id, "bread", 2, "slice");
        insertIngredient(db, id, "peanut butter", 2, "tbsp");
        insertIngredient(db, id, "banana", 1, "whole");

        id = insertRecipe(db, "Chicken Fried Rice",
                "1. Cut the chicken into small bite-sized pieces and season lightly.\n" +
                        "2. Heat a pan and cook the chicken through, about 5-6 minutes, then set it aside.\n" +
                        "3. In the same pan, add the rice and stir-fry for a few minutes, breaking up clumps.\n" +
                        "4. Push the rice aside, crack in the egg, and scramble it, then mix through the rice.\n" +
                        "5. Return the cooked chicken to the pan, add soy sauce, and stir-fry everything together until heated through.");
        insertIngredient(db, id, "rice", 200, "g");
        insertIngredient(db, id, "chicken", 150, "g");
        insertIngredient(db, id, "egg", 1, "whole");
        insertIngredient(db, id, "soy sauce", 1, "tbsp");

        id = insertRecipe(db, "Cucumber Salad",
                "1. Thinly slice the cucumber and onion.\n" +
                        "2. Place both into a mixing bowl.\n" +
                        "3. Add the vinegar and a pinch of salt.\n" +
                        "4. Toss everything together until evenly coated.\n" +
                        "5. Let it sit for 5-10 minutes to allow the flavors to develop before serving.");
        insertIngredient(db, id, "cucumber", 1, "whole");
        insertIngredient(db, id, "onion", 0.5, "whole");
        insertIngredient(db, id, "vinegar", 1, "tbsp");
        insertIngredient(db, id, "salt", 1, "pinch");

        id = insertRecipe(db, "Mashed Potatoes",
                "1. Peel and cube the potatoes, then boil in salted water until very tender, about 15 minutes.\n" +
                        "2. Drain thoroughly and return the potatoes to the pot.\n" +
                        "3. Add the butter and milk while the potatoes are still hot.\n" +
                        "4. Mash until smooth and creamy using a potato masher or fork.\n" +
                        "5. Season with salt to taste and serve warm.");
        insertIngredient(db, id, "potato", 300, "g");
        insertIngredient(db, id, "butter", 1, "tbsp");
        insertIngredient(db, id, "milk", 50, "ml");
        insertIngredient(db, id, "salt", 1, "pinch");

        id = insertRecipe(db, "Egg Fried Noodles",
                "1. Cook the noodles according to package instructions, then drain and set aside.\n" +
                        "2. Heat oil in a pan and fry the minced garlic until fragrant.\n" +
                        "3. Push the garlic aside, crack in the egg, and scramble until just set.\n" +
                        "4. Add the cooked noodles to the pan and toss with the egg and garlic.\n" +
                        "5. Pour in the soy sauce, toss well to coat evenly, and serve hot.");
        insertIngredient(db, id, "noodles", 200, "g");
        insertIngredient(db, id, "egg", 1, "whole");
        insertIngredient(db, id, "soy sauce", 1, "tbsp");
        insertIngredient(db, id, "garlic", 1, "clove");

        id = insertRecipe(db, "Carrot Soup",
                "1. Peel and roughly chop the carrot and onion.\n" +
                        "2. Place them in a pot with the water and bring to a boil.\n" +
                        "3. Reduce heat and simmer for 20 minutes until the carrots are very soft.\n" +
                        "4. Blend the soup until smooth using an immersion or regular blender.\n" +
                        "5. Stir in the butter until melted, season to taste, and serve hot.");
        insertIngredient(db, id, "carrot", 4, "whole");
        insertIngredient(db, id, "onion", 1, "whole");
        insertIngredient(db, id, "water", 400, "ml");
        insertIngredient(db, id, "butter", 1, "tbsp");

        id = insertRecipe(db, "Baked Beans on Toast",
                "1. Toast the bread slices until golden.\n" +
                        "2. Butter the toast while still warm.\n" +
                        "3. Heat the baked beans in a small pot over medium heat, stirring occasionally, for 3-4 minutes.\n" +
                        "4. Place the buttered toast on a plate.\n" +
                        "5. Pour the hot beans generously over the toast and serve immediately.");
        insertIngredient(db, id, "bread", 2, "slice");
        insertIngredient(db, id, "baked beans", 200, "g");
        insertIngredient(db, id, "butter", 1, "tbsp");

        id = insertRecipe(db, "Simple Fried Rice",
                "1. Finely chop the carrot and onion.\n" +
                        "2. Heat oil in a pan or wok over medium-high heat.\n" +
                        "3. Add the carrot and onion, stir-frying until softened, about 3-4 minutes.\n" +
                        "4. Add the rice, breaking up any clumps, and stir-fry for another 2-3 minutes.\n" +
                        "5. Pour in the soy sauce, toss well to coat, and serve hot.");
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
