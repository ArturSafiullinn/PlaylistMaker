package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class SearchActivity : AppCompatActivity() {

    private var searchText: String = ""
    private val trackList: ArrayList<Track> = arrayListOf()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var emptyView: LinearLayout
    private lateinit var errorView: LinearLayout
    private lateinit var historyContainer: LinearLayout
    private lateinit var clearHistoryButton: Button

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesApi = retrofit.create(ITunesApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        emptyView = findViewById(R.id.empty_view)
        errorView = findViewById(R.id.error_view)
        historyContainer = findViewById(R.id.history_container)
        clearHistoryButton = findViewById(R.id.clear_history_button)
        val backButton = findViewById<ImageView>(R.id.back_to_main_menu)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val historyRecycler = findViewById<RecyclerView>(R.id.history_recycler)
        val searchInput = findViewById<EditText>(R.id.search_input)

        backButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        searchInput.setupClearButtonWithAction()

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchText = searchInput.text.toString()
                if (searchText.isNotEmpty()) {
                    searchTracks(searchText)
                    hideKeyboard()
                }
                true
            } else {
                false
            }
        }

        searchInput.setOnFocusChangeListener { _, _ -> updateHistoryVisibility() }
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateHistoryVisibility()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun afterTextChanged(s: Editable?) = Unit
        })

        trackAdapter = TrackAdapter(trackList) { track ->
            SearchHistory.addTrack(this, track)
            updateHistoryVisibility()
        }

        historyAdapter = TrackAdapter(arrayListOf()) { track ->
            SearchHistory.addTrack(this, track)
            updateHistoryVisibility()
        }

        recyclerView.adapter = trackAdapter
        historyRecycler.adapter = historyAdapter

        clearHistoryButton.setOnClickListener {
            SearchHistory.clearHistory(this)
            updateHistoryVisibility()
        }

        val retryButton = findViewById<Button>(R.id.retry_button)
        retryButton.setOnClickListener {
            val query = searchInput.text.toString()
            if (query.isNotEmpty()) {
                searchTracks(query)
            }
        }
    }

    private fun searchTracks(query: String) {
        errorView.visibility = View.GONE
        emptyView.visibility = View.GONE

        iTunesApi.search(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val results = response.body()?.results ?: emptyList()

                    if (results.isEmpty()) {
                        emptyView.visibility = View.VISIBLE
                        trackList.clear()
                        trackAdapter.notifyDataSetChanged()
                        return
                    }

                    val formattedTracks = results.map { dto ->
                        Track(
                            trackId = dto.trackId,
                            trackName = dto.trackName.orEmpty(),
                            artistName = dto.artistName.orEmpty(),
                            trackTime = dto.trackTimeMillis?.let { formatTime(it) } ?: "",
                            artworkUrl = dto.artworkUrl100.orEmpty()
                        )
                    }

                    trackList.clear()
                    trackList.addAll(formattedTracks)
                    trackAdapter.notifyDataSetChanged()
                } else {
                    errorView.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                t.printStackTrace()
                errorView.visibility = View.VISIBLE
            }
        })
    }

    private fun updateHistoryVisibility() {
        val input = findViewById<EditText>(R.id.search_input)
        val history = SearchHistory.getHistory(this)
        if (input.hasFocus() && input.text.isEmpty() && history.isNotEmpty()) {
            historyContainer.visibility = View.VISIBLE
            historyAdapter.updateData(history)
        } else {
            historyContainer.visibility = View.GONE
        }
    }

    private fun formatTime(ms: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(ms)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(getString(R.string.search_text), searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString("search_text", "") ?: ""
        findViewById<EditText>(R.id.search_input).setText(searchText)
    }

    private fun EditText.setupClearButtonWithAction() {
        val searchIcon = ContextCompat.getDrawable(context, R.drawable.search)
        val clearIcon = ContextCompat.getDrawable(context, R.drawable.ic_clear)
        setCompoundDrawablesWithIntrinsicBounds(searchIcon, null, null, null)

        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val clearDrawable = if (editable?.isNotEmpty() == true) clearIcon else null
                setCompoundDrawablesWithIntrinsicBounds(searchIcon, null, clearDrawable, null)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        })

        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val touchPositionX = event.rawX
                val drawableEndX = this.right - this.compoundPaddingRight
                if (touchPositionX >= drawableEndX) {
                    this.setText("")
                    hideKeyboard()
                    trackList.clear()
                    trackAdapter.notifyDataSetChanged()
                    emptyView.visibility = View.GONE
                    errorView.visibility = View.GONE
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}
