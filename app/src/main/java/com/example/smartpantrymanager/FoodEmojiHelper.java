package com.example.smartpantrymanager;

//maps an ingredient or recipe name to an emoji
//this will give lists some visuals without needing
//real images or icons

public class FoodEmojiHelper {

    public static String getEmoji(String name) {
        String n = name.toLowerCase();

        if (n.contains("pasta") || n.contains("noodle")) return "🍝";
        if (n.contains("egg")) return "🥚";
        if (n.contains("rice")) return "🍚";
        if (n.contains("tomato")) return "🍅";
        if (n.contains("cheese")) return "🧀";
        if (n.contains("bread") || n.contains("toast")) return "🍞";
        if (n.contains("chicken")) return "🍗";
        if (n.contains("tuna") || n.contains("fish")) return "🐟";
        if (n.contains("banana")) return "🍌";
        if (n.contains("potato")) return "🥔";
        if (n.contains("carrot")) return "🥕";
        if (n.contains("onion")) return "🧅";
        if (n.contains("garlic")) return "🧄";
        if (n.contains("cucumber")) return "🥒";
        if (n.contains("soup")) return "🍲";
        if (n.contains("salad")) return "🥗";
        if (n.contains("sandwich")) return "🥪";
        if (n.contains("pancake")) return "🥞";
        if (n.contains("bean")) return "🫘";
        if (n.contains("milk") || n.contains("butter")) return "🧈";
        if (n.contains("oil")) return "🫒";
        if (n.contains("salt") || n.contains("pepper")) return "🧂";

        return "🍽️"; //for anything unmatched
    }
}
