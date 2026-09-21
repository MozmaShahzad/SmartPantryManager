package com.example.smartpantrymanager;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

//this class contains the "strict matching" business logic.
//it checks which recipes can be made using ONLY what is in the pantry.

public class RecipeMatcher {

    private final DatabaseHelper dbHelper;

    public RecipeMatcher(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    //returns the recipe where EVERY required ingredient
    //is available in the pantry, in sufficient quantity
    public List<Recipe> getSuggestedRecipes() {
        List<Recipe> allRecipes = loadAllRecipesWithIngredients();
        List<PantryItem> pantryItems = loadPantryItems();

        List<Recipe> matches = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            if(recipeIsFullyMatched(recipe,pantryItems)) {
                matches.add(recipe);
            }
        }
        return matches;

}

//returns recipes that are missing JUST 1 ingredient, or does
    //not have enough of 1 ingredient.
    //these are not fully matches but close - it will be shown
    //separately as "ALMOST THERE" recipes.

    public List<Recipe> getAlmostThereRecipes() {
        List<Recipe> allRecipes = loadAllRecipesWithIngredients();
        List<PantryItem> pantryItems = loadPantryItems();

        List<Recipe> almostThere = new ArrayList<>();

        for (Recipe recipe : allRecipes) {

            int missingCount = countMissingIngredients(recipe, pantryItems);

            if (missingCount == 1) {
                almostThere.add(recipe);
            }
        }

        return almostThere;
    }

    //counts how many required ingredients are not enough or are missing
    private int countMissingIngredients(Recipe recipe, List<PantryItem> pantryItems) {
        int missing = 0;
        for(RecipeIngredient required : recipe.ingredients) {
            PantryItem matchingPantryItem = findMatchingPantryItem(required.name, pantryItems);
            if(matchingPantryItem == null || matchingPantryItem.quantity < required.quantity) {
                missing++;
            }
        }
        return missing;
    }

//checks ONE recipe against the pantry - will return true, only if every ingredient is present in a good quantity
    private boolean recipeIsFullyMatched(Recipe recipe, List<PantryItem> pantryItems) {
        for (RecipeIngredient required : recipe.ingredients) {

            PantryItem matchingPantryItem = findMatchingPantryItem(required.name, pantryItems);

            //if ingredient is not available, recipe fails
            if(matchingPantryItem == null){
                return false;
            }

            //ingredient exists but not in enough quantity
            if(matchingPantryItem.quantity<required.quantity) {
                return false;
            }
        }
        //every ingredient passed
        return true;
    }

    //finds a pantry item that matches the required ingredients
    //ignores case, extra spaces and singular/plural differences
    private PantryItem findMatchingPantryItem(String requiredName, List<PantryItem> pantryItems) {
        String normalizedRequired = normalize(requiredName);

        for(PantryItem item : pantryItems) {
            String normalizedPantry = normalize(item.name);
            if(normalizedPantry.equals(normalizedRequired)) {
                return item;
            }
        }
        return null; //if not found
    }

    // Cleans up an ingredient name so matching isn't broken by simple differences:
    // - lowercase everything ("Tomato" == "tomato")
    // - trim extra spaces
    // - remove a trailing "s" to handle basic plurals ("tomatoes" ~ "tomato")
    private String normalize(String name) {
        String cleaned = name.trim().toLowerCase();

        //handle plural forms
        if (cleaned.endsWith("es")) {
            cleaned = cleaned.substring(0, cleaned.length() - 2);
        } else if (cleaned.endsWith("s") && !cleaned.endsWith("ss")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }

        return cleaned;
    }

    //loads every recipe from the database, with the list of required ingredients
    private List<Recipe> loadAllRecipesWithIngredients() {
        List<Recipe> recipes = new ArrayList<>();
        Cursor recipeCursor = dbHelper.getAllRecipes();

        if(recipeCursor.moveToFirst()) {
            do{
                int id = recipeCursor.getInt(recipeCursor.getColumnIndexOrThrow("id"));
                String name = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow("name"));
                String instructions = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow("instructions"));

                Recipe recipe = new Recipe(id, name, instructions);

                // Load this recipe's required ingredients
                Cursor ingredientCursor = dbHelper.getIngredientsForRecipe(id);
                if (ingredientCursor.moveToFirst()) {
                    do {
                        String ingName = ingredientCursor.getString(ingredientCursor.getColumnIndexOrThrow("ingredient_name"));
                        double ingQty = ingredientCursor.getDouble(ingredientCursor.getColumnIndexOrThrow("quantity_needed"));
                        String ingUnit = ingredientCursor.getString(ingredientCursor.getColumnIndexOrThrow("unit"));
                        recipe.ingredients.add(new RecipeIngredient(ingName, ingQty, ingUnit));
                    } while (ingredientCursor.moveToNext());
                }
                ingredientCursor.close();

                recipes.add(recipe);
            } while (recipeCursor.moveToNext());
        }
        recipeCursor.close();

        return recipes;
    }

    // Loads all current pantry items
    private List<PantryItem> loadPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        Cursor cursor = dbHelper.getAllPantryItems();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow("quantity"));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow("unit"));
                String expiryDate = cursor.getString(cursor.getColumnIndexOrThrow("expiry_date"));
                items.add(new PantryItem(id, name, quantity, unit, expiryDate));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return items;
    }
}




























