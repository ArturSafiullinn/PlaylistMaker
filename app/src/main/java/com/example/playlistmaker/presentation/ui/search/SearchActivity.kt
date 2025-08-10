package com.example.playlistmaker.presentation.ui.search

import com.example.playlistmaker.domain.models.Track as DomainTrack
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.presentation.mappers.toUi
import com.example.playlistmaker.presentation.models.SearchScreenState
import com.example.playlistmaker.presentation.models.UiTrack
import com.example.playlistmaker.presentation.ui.search.adapter.TrackAdapter
import com.example.playlistmaker.presentation.ui.track.TrackActivity
import com.example.playlistmaker.presentation.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_TRACK = "track"
    }
    private lateinit var binding: ActivitySearchBinding
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private val viewModel: SearchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapters()
        initListeners()
        observeViewModel()

        binding.searchInput.setupClearButtonWithAction()
        viewModel.loadHistory()
    }

    private fun initAdapters() {
        trackAdapter = TrackAdapter(mutableListOf()) { track ->
            viewModel.addTrackToHistory(track)
            openTrackDetails(track)
        }

        historyAdapter = TrackAdapter(mutableListOf()) { track ->
            viewModel.addTrackToHistory(track)
            openTrackDetails(track)
        }

        binding.recyclerView.adapter = trackAdapter
        binding.historyRecycler.adapter = historyAdapter
    }

    private fun openTrackDetails(track: DomainTrack) {
        val uiTrack: UiTrack = track.toUi()
        val intent = Intent(this, TrackActivity::class.java)
            .putExtra(EXTRA_TRACK, uiTrack)
        startActivity(intent)
    }

    private fun initListeners() {
        binding.backToMainMenu.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
            if (binding.searchInput.text.isEmpty()) {
                viewModel.loadHistory()
            }
        }

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    viewModel.clearResults()
                    viewModel.loadHistory()
                } else {
                    viewModel.searchDebounce(s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun afterTextChanged(s: Editable?) = Unit
        })

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
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
                    showOnly(binding.progressBar)
                }

                is SearchScreenState.Error -> {
                    showOnly(binding.errorView)
                }

                is SearchScreenState.Empty -> {
                    showOnly(binding.emptyView)
                }

                is SearchScreenState.Content -> {
                    showOnly(binding.recyclerView)
                    trackAdapter.updateData(state.tracks)
                }

                is SearchScreenState.History -> {
                    if (binding.searchInput.hasFocus() && binding.searchInput.text.isEmpty()) {
                        if (state.tracks.isNotEmpty()) {
                            binding.historyContainer.visibility = View.VISIBLE
                            historyAdapter.updateData(state.tracks)
                        } else {
                            binding.historyContainer.visibility = View.GONE
                        }

                    } else {
                        binding.historyContainer.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun showOnly(viewToShow: View) {
        val allViews = listOf(
            binding.progressBar,
            binding.errorView,
            binding.emptyView,
            binding.recyclerView,
            binding.historyContainer
        )
        allViews.forEach { it.visibility = if (it == viewToShow) View.VISIBLE else View.GONE }
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
                    viewModel.clearResults()
                    viewModel.loadHistory()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }
}