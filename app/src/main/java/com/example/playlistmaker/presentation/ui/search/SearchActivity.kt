package com.example.playlistmaker.presentation.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.models.SearchScreenState
import com.example.playlistmaker.presentation.ui.SearchHistory
import com.example.playlistmaker.presentation.ui.main.MainActivity
import com.example.playlistmaker.presentation.ui.search.adapter.TrackAdapter
import com.example.playlistmaker.presentation.ui.track.TrackActivity
import com.example.playlistmaker.presentation.viewmodel.SearchViewModel
import com.example.playlistmaker.presentation.viewmodel.SearchViewModelFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val trackList = arrayListOf<Track>()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val viewModel: SearchViewModel by viewModels { SearchViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapters()
        initListeners()
        observeViewModel()

        binding.searchInput.setupClearButtonWithAction()
    }

    private fun initAdapters() {
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

        binding.recyclerView.adapter = trackAdapter
        binding.historyRecycler.adapter = historyAdapter
    }

    private fun initListeners() {
        binding.backToMainMenu.setOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = binding.searchInput.text.toString()
                if (query.isNotEmpty()) {
                    viewModel.search(query)
                    hideKeyboard()
                }
                true
            } else false
        }

        binding.searchInput.setOnFocusChangeListener { _, _ ->
            updateHistoryVisibility()
        }

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateHistoryVisibility()
                if (s.isNullOrEmpty()) {
                    viewModel.clearResults()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun afterTextChanged(s: Editable?) = Unit
        })

        binding.clearHistoryButton.setOnClickListener {
            SearchHistory.clearHistory(this)
            updateHistoryVisibility()
        }

        binding.retryButton.setOnClickListener {
            val query = binding.searchInput.text.toString()
            if (query.isNotEmpty()) {
                viewModel.search(query)
                hideKeyboard()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            when (state) {
                is SearchScreenState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.errorView.visibility = View.GONE
                    binding.emptyView.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                }

                is SearchScreenState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.errorView.visibility = View.VISIBLE
                    binding.emptyView.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                }

                is SearchScreenState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    binding.errorView.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                }

                is SearchScreenState.Content -> {
                    binding.progressBar.visibility = View.GONE
                    binding.errorView.visibility = View.GONE
                    binding.emptyView.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    trackList.clear()
                    trackList.addAll(state.tracks)
                    trackAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun updateHistoryVisibility() {
        val history = SearchHistory.getHistory(this)
        if (binding.searchInput.hasFocus() && binding.searchInput.text.isEmpty() && history.isNotEmpty()) {
            binding.historyContainer.visibility = View.VISIBLE
            historyAdapter.updateData(history)
        } else {
            binding.historyContainer.visibility = View.GONE
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
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
                    binding.emptyView.visibility = View.GONE
                    binding.errorView.visibility = View.GONE
                    return@setOnTouchListener true
                }
            }
            false
        }
    }
}
