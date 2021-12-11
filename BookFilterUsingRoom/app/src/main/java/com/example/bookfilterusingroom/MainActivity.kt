package com.example.bookfilterusingroom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val authorName = findViewById<TextInputLayout>(R.id.authorInputLayout)
        val filterButton = findViewById<Button>(R.id.filterButton)
        val bookCount = findViewById<TextView>(R.id.resultCountView)
        val booksView = findViewById<TextView>(R.id.booksView)


        val allBooks = mutableListOf<Bookdata>()
        val myApplication = application as MyApplication
        val httpApiService = myApplication.httpApiService

        CoroutineScope(Dispatchers.IO).launch {
            var decodedJsonResult = httpApiService.getMyBookData()
            for (i in decodedJsonResult)
                allBooks.add(i)
            var auth: Int = 0
            for (item in allBooks) {

                AppDatabase.getDatabase(this@MainActivity).authorDao()
                    .insert(Authors(author = item.author, country = item.country))
                auth = AppDatabase.getDatabase(this@MainActivity).authorDao()
                    .getAuhtor(item.author).Aid
                AppDatabase.getDatabase(this@MainActivity).BookDao()
                    .InsertBooks(
                        Book(
                            aid = auth,
                            language = item.language,
                            imageLink = item.imageLink,
                            link = item.link,
                            pages = item.pages,
                            title = item.title,
                            year = item.year
                        )
                    )
            }
        }
        filterButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val booksList: List<AuthorsandBooks> =
                    AppDatabase.getDatabase(this@MainActivity).authorDao()
                        .JoinedDetails(authorName.editText?.text?.toString()?.lowercase())
                withContext(Dispatchers.Main) {

                    var count: Int = 0
                    var res = ""
                    count = booksList.size
                    var displayCount = 0
                    booksList.forEach{
                        if(displayCount < 3){
                            res += "Result : ${it.title} (${it.BookID})\n"
                            displayCount++
                        }
                    }
                    bookCount.text = "Result: $count"
                    booksView.text = res
                }
            }

        }

    }
}