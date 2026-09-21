package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.List;

// Represents one recipe, along with the list of ingredients it requires.
public class Recipe {
    public int id;
    public String name;
    public String instructions;
    public List<RecipeIngredient> ingredients = new ArrayList<>();

    public Recipe(int id, String name, String instructions) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
    }
}