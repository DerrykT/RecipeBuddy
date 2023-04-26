package com.recipebuddy.components

import androidx.room.*

@Entity(tableName = "Recipe_Info")
data class Recipe_Info(
    @PrimaryKey val RecipeName: String,
    val RecipeDesc: String,
    val RecipeInstructions: String,
    val RecipeRating: Double,
    val AssociatedUser: String,
    val Time: String,
    val Picture: Int,
    val Tools: String
)

@Entity(tableName = "Ingredient_List")
data class Ingredient_List(
    @PrimaryKey val IngredientName: String,
    val Quantity: Int,
    val Unit: String
)

@Entity(tableName = "Ingredient_Use")
data class Ingredient_Use(
    @PrimaryKey(autoGenerate = true) val UsageKey: Int,
    val IngredientName: String,
    val RecipeName: String,
    val Quantity: Int,
    val Unit: String
)

@Entity(tableName = "Tag_List")
data class Tag_List(
    @PrimaryKey val Tag: String
)

@Entity(tableName = "Recipe_Tags")
data class Recipe_tags(
    @PrimaryKey(autoGenerate = true) val PairKey: Int,
    val RecipeName: String,
    val Tag: String
)

@Entity(tableName = "Users")
data class Users(
    @PrimaryKey val Username: String,
    val Password: String
)


@Dao
interface Insertion {
    @Insert
    fun insertRecipeInfo(recipeInfo: Recipe_Info): Long

    @Delete
    fun deleteRecipe(recipeInfo: Recipe_Info)

    @Insert
    fun insertIngredientList(ingredientList: Ingredient_List): Long

    @Insert
    fun insertIngredientUse(ingredientUse: Ingredient_Use): Long

    @Insert
    fun insertTagList(tagList: Tag_List): Long

    @Insert
    fun insertRecipeTags(recipeTags: Recipe_tags): Long

    @Insert
    fun insertUser(user: Users): Long
}


@Database(entities = [
    Recipe_Info::class,
    Ingredient_List::class,
    Ingredient_Use::class,
    Tag_List::class,
    Recipe_tags::class,
], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun insertion(): Insertion
    abstract fun readData(): ReadData
}

@Dao
interface ReadData{
    @Query("SELECT il.IngredientName, iu.Quantity, iu.Unit " +
            "FROM Recipe_Info ri, Ingredient_List il, Ingredient_Use iu " +
            "WHERE ri.recipeName = :searchName " +
            "AND il.IngredientName = iu.IngredientName " +
            "AND iu.RecipeName = ri.RecipeName")
    fun getRecipeIngredients(searchName: String): List<RecipeIngredientList>

    @Query("SELECT RecipeName " +
            "FROM Recipe_Info")
    fun getRecipeNames(): List<String>


    @Query("SELECT tl.tag " +
            "FROM Recipe_Info ri, Tag_List tl, Recipe_Tags rt " +
            "WHERE ri.recipeName = :searchName " +
            "AND rt.RecipeName = ri.RecipeName ")
    fun getTagsByRecipeName(searchName: String): List<String>

    @Query("SELECT RecipeDesc " +
            "FROM Recipe_Info " +
            "WHERE RecipeName = :searchName")
    fun getDesc(searchName: String): String

    @Query("SELECT RecipeInstructions " +
            "FROM Recipe_Info " +
            "WHERE RecipeName = :searchName")
    fun getInstructions(searchName: String): String

    @Query("SELECT Password " +
            "FROM Users " +
            "Where Username = :searchName")
    fun getPassword(searchName: String): String
}

data class RecipeIngredientList(
    val IngredientName: String,
    val Quantity: Int,
    val Unit: String
)




