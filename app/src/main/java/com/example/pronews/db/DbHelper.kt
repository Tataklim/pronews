import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.example.pronews.db.NewsItem
import com.example.pronews.fragments.LikedFragment

const val DATABASENAME = "NEWS DATABASE"
const val TABLENAME = "News"

const val COL_ID = "id"
var COL_AUTHOR = "author"
var COL_CATEGORY = "category"
var COL_COUNTRY = "country"
var COL_DESCRIPTION = "description"
var COL_IMAGE = "image"
var COL_LANGUAGE = "language"
var COL_PUBLISHED_AT = "published_at"
var COL_SOURCE = "source"
var COL_TITLE = "title"
var COL_URL = "url"

class DataBaseHandler(var context: Context) : SQLiteOpenHelper(context, DATABASENAME, null,
    1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
            "CREATE TABLE $TABLENAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_AUTHOR VARCHAR(256),$COL_CATEGORY VARCHAR(256)," +
                    "$COL_COUNTRY VARCHAR(256),$COL_DESCRIPTION VARCHAR(256)," +
                    "$COL_IMAGE VARCHAR(256),$COL_LANGUAGE VARCHAR(256)," +
                    "$COL_PUBLISHED_AT VARCHAR(256),$COL_SOURCE VARCHAR(256)," +
                    "$COL_TITLE VARCHAR(256),$COL_URL VARCHAR(256))"
        db?.execSQL(createTable)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //onCreate(db);
    }
    fun insertData(item: NewsItem) {
        val database = this.writableDatabase

        val contentValues = ContentValues()

        contentValues.put(COL_AUTHOR, item.author)
        contentValues.put(COL_CATEGORY, item.category)
        contentValues.put(COL_COUNTRY, item.country)
        contentValues.put(COL_DESCRIPTION, item.description)
        contentValues.put(COL_IMAGE, item.image)
        contentValues.put(COL_LANGUAGE, item.language)
        contentValues.put(COL_PUBLISHED_AT, item.published_at)
        contentValues.put(COL_SOURCE, item.source)
        contentValues.put(COL_TITLE, item.title)
        contentValues.put(COL_URL, item.url)

        val result = database.insert(TABLENAME, null, contentValues)
        if (result == (0).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteData(item: NewsItem): Boolean {
        val database = this.writableDatabase

        val contentValues = ContentValues()

        contentValues.put(COL_AUTHOR, item.author)
        contentValues.put(COL_CATEGORY, item.category)
        contentValues.put(COL_COUNTRY, item.country)
        contentValues.put(COL_DESCRIPTION, item.description)
        contentValues.put(COL_IMAGE, item.image)
        contentValues.put(COL_LANGUAGE, item.language)
        contentValues.put(COL_PUBLISHED_AT, item.published_at)
        contentValues.put(COL_SOURCE, item.source)
        contentValues.put(COL_TITLE, item.title)
        contentValues.put(COL_URL, item.url)

        return database.delete(TABLENAME, COL_TITLE + "=\"" + item.title + "\"", null) > 0
    }

    fun deleteDB() {
        val database = this.writableDatabase

        return database.execSQL("delete from $TABLENAME")
    }

    fun readData(): MutableList<NewsItem> {
        val list: MutableList<NewsItem> = ArrayList()
        val db = this.readableDatabase
        val query = "Select * from $TABLENAME"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val user = NewsItem()
                user._id = result.getString(result.getColumnIndex(COL_ID))
                user.author = result.getString(result.getColumnIndex(COL_AUTHOR))
                user.category = result.getString(result.getColumnIndex(COL_CATEGORY))
                user.country = result.getString(result.getColumnIndex(COL_COUNTRY))
                user.description = result.getString(result.getColumnIndex(COL_DESCRIPTION))
                user.image = result.getString(result.getColumnIndex(COL_IMAGE))
                user.language = result.getString(result.getColumnIndex(COL_LANGUAGE))
                user.published_at = result.getString(result.getColumnIndex(COL_PUBLISHED_AT))
                user.source = result.getString(result.getColumnIndex(COL_SOURCE))
                user.title = result.getString(result.getColumnIndex(COL_TITLE))
                user.url = result.getString(result.getColumnIndex(COL_URL))
                list.add(user)
            }
            while (result.moveToNext())
        }
        return list
    }

    fun checkIfNewsExists(item: NewsItem): Boolean {
        val db = this.readableDatabase
        val title = item.title
        val query = "Select * from $TABLENAME where $COL_TITLE = \"$title\""
        val result = db.rawQuery(query, null)
        return result.count != 0
    }
}