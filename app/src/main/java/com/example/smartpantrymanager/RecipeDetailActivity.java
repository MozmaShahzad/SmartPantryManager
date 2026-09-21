package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

//Shows full details of one recipe: name, ingredients, and preparation steps.
public class RecipeDetailActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        dbHelper = new DatabaseHelper(this);

        TextView recipeNameText = findViewById(R.id.recipeNameText);
        TextView ingredientsList = findViewById(R.id.ingredientsList);
        TextView instructionsText = findViewById(R.id.instructionsText);

        //Get which recipe to show, passed in from SuggestedRecipesActivity
        int recipeId = getIntent().getIntExtra("recipeId", -1);

        // Load the recipe's own details (name + instructions)
        Cursor recipeCursor = dbHelper.getAllRecipes();
        if (recipeCursor.moveToFirst()) {
            do {
                int id = recipeCursor.getInt(recipeCursor.getColumnIndexOrThrow("id"));
                if (id == recipeId) {
                    String name = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow("name"));
                    String instructions = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow("instructions"));
                    recipeNameText.setText(name);
                    instructionsText.setText(instructions);
                    break;
                }
            } while (recipeCursor.moveToNext());
        }
        recipeCursor.close();

        // Load and display all required ingredients, one per line
        StringBuilder ingredientsBuilder = new StringBuilder();
        Cursor ingredientCursor = dbHelper.getIngredientsForRecipe(recipeId);
        if (ingredientCursor.moveToFirst()) {
            do {
                String ingName = ingredientCursor.getString(ingredientCursor.getColumnIndexOrThrow("ingredient_name"));
                double ingQty = ingredientCursor.getDouble(ingredientCursor.getColumnIndexOrThrow("quantity_needed"));
                String ingUnit = ingredientCursor.getString(ingredientCursor.getColumnIndexOrThrow("unit"));
                ingredientsBuilder.append("• ").append(ingQty).append(" ").append(ingUnit)
                        .append(" ").append(ingName).append("\n");
            } while (ingredientCursor.moveToNext());
        }
        ingredientCursor.close();

        ingredientsList.setText(ingredientsBuilder.toString().trim());
    }
}