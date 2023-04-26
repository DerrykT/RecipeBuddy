package com.recipebuddy.components

import androidx.room.*

@Entity(tableName = "Recipe_Info")
data class Recipe_Info(
    @PrimaryKey(autoGenerate = true) val RecipeID: Int,
    val RecipeName: String,
    val RecipeDesc: String,
    val RecipeInstructions: String,
    val RecipeRating: Double
)

@Entity(tableName = "Ingredient_List")
data class Ingredient_List(
    @PrimaryKey(autoGenerate = true) val IngredientID: Int,
    val IngredientName: String
)

@Entity(tableName = "Ingredient_Use")
data class Ingredient_Use(
    @PrimaryKey(autoGenerate = true) val UsageKey: Int,
    val IngredientID: Int, // Electing not to make a FK here, means incomplete data doesn't break instantly
    val RecipeID: Int,
    val Quantity: Int,
    val Unit: String
)

@Entity(tableName = "Recipe_Ingredients")
data class Recipe_Ingredients(
    @PrimaryKey(autoGenerate = true) val PairKey: Int,
    val RecipeID: Int,
    val IngredientID: Int
)

@Entity(tableName = "Tag_List")
data class Tag_List(
    @PrimaryKey(autoGenerate = true) val TagID: Int,
    val Tag: String
)

@Entity(tableName = "Recipe_Tags")
data class Recipe_tags(
    @PrimaryKey(autoGenerate = true) val PairKey: Int,
    val RecipeID: Int,
    val TagID: Int
)

@Entity(tableName = "Image_List")
data class Image_List(
    @PrimaryKey(autoGenerate = true) val ImageID: Int,
    val FileName: String,
    val RecipeID: Int
)

@Dao
interface Insertion {
    @Insert
    fun insertRecipeInfo(recipeInfo: Recipe_Info): Long

    @Insert
    fun insertIngredientList(ingredientList: Ingredient_List): Long

    @Insert
    fun insertIngredientUse(ingredientUse: Ingredient_Use): Long

    @Insert
    fun insertRecipeIngredients(recipeIngredients: Recipe_Ingredients): Long

    @Insert
    fun insertTagList(tagList: Tag_List): Long

    @Insert
    fun insertRecipeTags(recipeTags: Recipe_tags): Long

    @Insert
    fun insertImageList(imageList: Image_List): Long
}


@Database(entities = [
    Recipe_Info::class,
    Ingredient_List::class,
    Ingredient_Use::class,
    Recipe_Ingredients::class,
    Tag_List::class,
    Recipe_tags::class,
    Image_List::class
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
            "AND il.IngredientID = iu.IngredientID " +
            "AND iu.RecipeID = ri.RecipeID")
    fun getRecipeIngredients(searchName: String): List<RecipeIngredientList>

    @Query("SELECT RecipeName " +
            "FROM Recipe_Info")
    fun getRecipeNames(): List<String>

    @Query("SELECT il.FileName " +
            "FROM Recipe_Info ri, Image_List il " +
            "WHERE ri.RecipeName = :searchName " +
            "AND il.RecipeID = ri.RecipeID")
    fun getImageFileNamesByRecipeName(searchName: String): List<String>

    @Query("SELECT tl.tag " +
            "FROM Recipe_Info ri, Tag_List tl, Recipe_Tags rt " +
            "WHERE ri.recipeName = :searchName " +
            "AND rt.RecipeID = ri.RecipeID " +
            "AND tl.tagID = rt.tagID")
    fun getTagsByRecipeName(searchName: String): List<String>

    @Query("SELECT RecipeDesc " +
            "FROM Recipe_Info " +
            "WHERE RecipeID = :searchName")
    fun getDesc(searchName: String): String

    @Query("SELECT RecipeInstructions " +
            "FROM Recipe_Info " +
            "WHERE RecipeID = :searchName")
    fun getInstructions(searchName: String): String
}

data class RecipeIngredientList(
    val IngredientName: String,
    val Quantity: Int,
    val Unit: String
)




