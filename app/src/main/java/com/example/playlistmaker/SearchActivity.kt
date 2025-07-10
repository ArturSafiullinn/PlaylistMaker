package com.example.playlistmaker

import android.content.Intent
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.data.dto.SearchResponse
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.domain.models.Track
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
    private lateinit var historyRecycler: RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchTracks(searchText) }

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
        progressBar = findViewById(R.id.progress_bar) // Используется класс ProgressBar
        val backButton = findViewById<ImageView>(R.id.back_to_main_menu)
        recyclerView = findViewById(R.id.recyclerView)
        historyRecycler = findViewById(R.id.history_recycler)
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
                searchText = s.toString()
                updateHistoryVisibility()
                handler.removeCallbacks(searchRunnable)

                if (searchText.isNotEmpty()) {
                    progressBar.visibility = View.VISIBLE
                    handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
                } else {
                    trackList.clear()
                    trackAdapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun afterTextChanged(s: Editable?) = Unit
        })

        trackAdapter = TrackAdapter(trackList) { track ->
            SearchHistory.addTrack(this, track)
            updateHistoryVisibility()
            val intent = Intent(this, TrackActivity::class.java)
            intent.putExtra("track", track)
            startActivity(intent)
        }

        historyAdapter = TrackAdapter(arrayListOf()) { track ->
            SearchHistory.addTrack(this, track)
            updateHistoryVisibility()
            val intent = Intent(this, TrackActivity::class.java)
            intent.putExtra("track", track)
            startActivity(intent)
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
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        iTunesApi.search(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val results = response.body()?.results ?: emptyList()
                    if (results.isEmpty()) {
                        emptyView.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                        return
                    }

                    val formattedTracks = results.map { dto ->
                        Track(
                            trackId = dto.trackId ?: 0,
                            trackName = dto.trackName.orEmpty().trim(),
                            artistName = dto.artistName.orEmpty().trim(),
                            trackTime = dto.trackTimeMillis?.let { formatTime(it) } ?: "",
                            artworkUrl = dto.artworkUrl100.orEmpty().trim(),
                            collectionName = dto.collectionName.orEmpty().trim(),
                            releaseDate = dto.releaseDate?.take(4).orEmpty(),
                            genre = dto.primaryGenreName.orEmpty().trim(),
                            country = dto.country.orEmpty().trim(),
                            previewUrl = dto.previewUrl.orEmpty().trim()
                        )
                    }

                    trackList.clear()
                    trackList.addAll(formattedTracks)
                    trackAdapter.notifyDataSetChanged()
                    recyclerView.recycledViewPool.clear()
                    recyclerView.visibility = View.VISIBLE
                } else {
                    errorView.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
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
            historyRecycler.recycledViewPool.clear()
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

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(searchRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
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

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 500L
    }
}
