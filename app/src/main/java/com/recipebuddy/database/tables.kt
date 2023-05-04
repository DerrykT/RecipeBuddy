package com.recipebuddy.database

import android.content.Context
import android.graphics.Bitmap
import androidx.room.*
import com.recipebuddy.util.Recipe

@Entity(tableName = "Recipe_Info")
data class Recipe_Info(
    @PrimaryKey val RecipeName: String,
    var RecipeTimers: String,
    var RecipeInstructions: String,
    val RecipeRating: Int,
    val AssociatedUser: String,
    val Time: Int,
    val Picture: ByteArray,
    val Tools: String
)

@Entity(tableName = "Ingredient_List")
data class Ingredient_List(
    @PrimaryKey val IngredientName: String,
    val Quantity: Double,
    val Unit: String
)

@Entity(tableName = "Tools_List")
data class Tool_List(
    @PrimaryKey val ToolName: String
)

@Entity(tableName = "Ingredient_Use", primaryKeys = ["IngredientName", "RecipeName"])
data class Ingredient_Use(
    val IngredientName: String,
    val RecipeName: String,
    val Quantity: Double,
    val Unit: String
)

@Entity(tableName = "Tag_List")
data class Tag_List(
    @PrimaryKey val Tag: String
)

@Entity(tableName = "Recipe_Tags", primaryKeys = ["RecipeName", "Tag"])
data class Recipe_Tags(
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

    @Delete
    fun deleteTool(toolList: Tool_List)

    @Delete
    fun deleteTag(tagList: Tag_List)

    @Query("DELETE FROM Ingredient_List WHERE IngredientName = :name")
    fun deleteIngredient(name: String)

    @Insert
    fun insertIngredientList(ingredientList: Ingredient_List): Long

    @Insert
    fun insertIngredientUse(ingredientUse: Ingredient_Use): Long

    @Insert
    fun insertTagList(tagList: Tag_List): Long

    @Insert
    fun insertRecipeTags(recipeTags: Recipe_Tags): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: Users): Long

    @Insert
    fun insertTool(tool: Tool_List): Long
}


@Database(entities = [
    Recipe_Info::class,
    Ingredient_List::class,
    Tool_List::class,
    Ingredient_Use::class,
    Tag_List::class,
    Recipe_Tags::class,
    Users::class,
], version = 13)
abstract class AppDatabase : RoomDatabase() {
    abstract fun insertion(): Insertion
    abstract fun readData(): ReadData

    companion object {
        private var instance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, "recipe_database"
                ).fallbackToDestructiveMigration().build()
            }
            return instance as AppDatabase
        }
    }
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

    @Query("SELECT * FROM Tag_List")
    fun getTags(): List<Tag_List>

    @Query("SELECT * FROM Recipe_Info ri ORDER BY ri.RecipeName DESC LIMIT 10")
    fun getRecipes(): List<Recipe_Info>

    @Query("SELECT * FROM Ingredient_List")
    fun getIngredients(): List<Ingredient_List>

    @Query("SELECT ToolName FROM Tools_List")
    fun getTools(): List<String>

    @Query("SELECT DISTINCT rt.tag " +
            "FROM Recipe_Info ri, Tag_List tl, Recipe_Tags rt " +
            "WHERE ri.recipeName = :searchName " +
            "AND rt.RecipeName = ri.RecipeName ")
    fun getTagsByRecipeName(searchName: String): List<String>

    @Query("SELECT Password " +
            "FROM Users " +
            "Where Username = :searchName")
    fun getPassword(searchName: String): String

    //get list of recipe names that contain a specific tag
    @Query ("SELECT rt.RecipeName " +
            "FROM Recipe_Tags rt " +
            "WHERE rt.Tag = :selectedTag ")
    fun getRecipeNamesByTag(selectedTag: String?): List<String>

    //get list of recipes from recipe_info where recipe name = searchName
    @Query("SELECT * " +
            "FROM Recipe_Info ri " +
            "WHERE LOWER(ri.RecipeName) LIKE :searchName||'%'")
    fun getRecipeListByRecipeName(searchName: String?): List<Recipe_Info>

    //get list of recipes from recipe_info where rating = passedRating
    @Query("SELECT * " +
            "FROM Recipe_Info ri " +
            "WHERE ri.RecipeRating >= :passedRating ")
    fun getRecipeListByRating(passedRating: Int?): List<Recipe_Info>
}

data class RecipeIngredientList(
    val IngredientName: String,
    val Quantity: Double,
    val Unit: String
)


fun List<RecipeIngredientList>.toMutableStringToDoubleMap(): MutableMap<String, Double> {
    val mutableMap = mutableMapOf<String, Double>()

    this.forEach {
        mutableMap[it.IngredientName] = it.Quantity
    }

    return mutableMap
}