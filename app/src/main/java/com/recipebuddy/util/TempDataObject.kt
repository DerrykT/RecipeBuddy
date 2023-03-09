package com.recipebuddy.util

import com.recipebuddy.R

object TempDataObject {
    val username = "Derryk Taylor"


    val ingredients = listOf<Ingredient>(
        Ingredient("Pork half-Loin", "2.2 Lbs"),
        Ingredient("russet Potatoes", "6.5 Lbs"),
        Ingredient("Medium Eggs", "4"),
        Ingredient("Olive Oil", "12 oz")
    )
    val tools = listOf<String>(
        "Medium Pan",
        "Spatula",
        "Carving Knife",
        "Cookie Sheet"
    )

    val tags = listOf<String>(
        "Gluten Free",
        "Breakfast",
        "Vegan",
        "Dairy",
        "Keto"
    )

    val recipes = mutableListOf<Recipe>()

    init {
        val recipeTags = listOf("Dessert", "Dairy")

        val recipeIngredients = listOf(
            Ingredient("Butter", "1 cup"),
            Ingredient("White Sugar", "1 cup"),
            Ingredient("Brown Sugar", "1 cup"),
            Ingredient("Eggs", "2"),
            Ingredient("Vanilla Extract", "2 teaspoons"),
            Ingredient("Baking Soda", "1 teaspoon"),
            Ingredient("Hot Water", "2 teaspoons"),
            Ingredient("Salt", "1/2 teaspoon"),
            Ingredient("All-Purpose Flour", "3 cups"),
            Ingredient("Semisweet Chocolate Chips", "2 cups"),
            Ingredient("Chopped Walnuts", "1 cup")
        )

        val recipeTools = listOf(
            "Baking Sheets",
            "Electric Mixer",
            "Wire Rack"
        )

        val recipeInstructions = listOf(
            Instruction("Gather your ingredients, making sure your butter is softened, and your eggs are room temperature."),
            Instruction("Preheat the oven to 350 degrees F (175 degrees C)."),
            Instruction("Beat butter, white sugar, and brown sugar with an electric mixer in a large bowl until smooth."),
            Instruction("Beat in eggs, one at a time, then stir in vanilla."),
            Instruction("Dissolve baking soda in hot water. Add to batter along with salt."),
            Instruction("Stir in flour, chocolate chips, and walnuts."),
            Instruction("Drop spoonfuls of dough 2 inches apart onto ungreased baking sheets."),
            Instruction("Bake in the preheated oven until edges are nicely browned, about 10 minutes.", "10 Minutes"),
            Instruction("Cool on the baking sheets briefly before removing to a wire rack to cool completely.")
        )

        for (i in 0..20) {
            recipes.add(
                Recipe(
                    "Chocolate Chip Cookies",
                    4,
                    "45 Minutes",
                    recipeTags,
                    recipeIngredients,
                    recipeTools,
                    recipeInstructions,
                    R.drawable.cookies_recipe_image
                )
            )
        }
    }
}

data class Ingredient(val name: String, val weight: String)

data class Recipe(
    val name: String,
    val rating: Int,
    val time: String,
    val tags: List<String>,
    val ingredients: List<Ingredient>,
    val tools: List<String>,
    val instructions: List<Instruction>,
    val imageRes: Int
)

data class Instruction(val text: String, val timer: String? = null)
