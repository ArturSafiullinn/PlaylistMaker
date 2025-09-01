package com.example.playlistmaker.presentation.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.mappers.toUi
import com.example.playlistmaker.presentation.models.SearchScreenState
import com.example.playlistmaker.presentation.models.UiTrack
import com.example.playlistmaker.presentation.ui.search.adapter.TrackAdapter
import com.example.playlistmaker.presentation.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    companion object {
        private const val EXTRA_TRACK = "track"
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private val viewModel: SearchViewModel by viewModel()
    private var suppressTextWatcher = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapters()
        initListeners()
        observeViewModel()
        binding.searchInput.setupClearButtonWithAction()

        if (viewModel.state.value == null) {
            viewModel.loadHistory()
        }
        restoreSearchTextSafely(viewModel.getLastQuery())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun openTrackDetails(track: Track) {
        val uiTrack: UiTrack = track.toUi()
        val action = SearchFragmentDirections.actionSearchFragmentToTrackFragment(uiTrack)
        findNavController().navigate(action)
    }

    private fun restoreSearchTextSafely(text: String) {
        suppressTextWatcher = true
        binding.searchInput.setText(text)
        binding.searchInput.setSelection(binding.searchInput.text.length)
        suppressTextWatcher = false
    }

    private fun initListeners() {
        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = binding.searchInput.text.toString()
                if (query.isNotEmpty()) {
                    viewModel.cancelDebounce()
                    viewModel.search(query)
                    hideKeyboard()
                }
                true
            } else false
        }

        binding.searchInput.setOnFocusChangeListener { _, _ ->
            if (binding.searchInput.text.isEmpty() && viewModel.state.value !is SearchScreenState.Content) {
                viewModel.loadHistory()
            }
        }

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (suppressTextWatcher) return
                val text = s?.toString().orEmpty()
                viewModel.onQueryChanged(text)
                if (text.isEmpty()) {
                    viewModel.clearResults()
                    viewModel.loadHistory()
                } else {
                    viewModel.searchDebounce(text)
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
                viewModel.cancelDebounce()
                viewModel.search(query)
                hideKeyboard()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchScreenState.Loading -> showOnly(binding.progressBar)

                is SearchScreenState.Error -> showOnly(binding.errorView)

                is SearchScreenState.Empty -> showOnly(binding.emptyView)

                is SearchScreenState.Content -> {
                    showOnly(binding.recyclerView)
                    trackAdapter.updateData(state.tracks)
                }

                is SearchScreenState.History -> {
                    if (state.tracks.isNotEmpty()) {
                        showOnly(binding.historyContainer)
                        historyAdapter.updateData(state.tracks)
                    } else {
                        showNone()
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
        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
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

    private fun showNone() {
        binding.progressBar.visibility = View.GONE
        binding.errorView.visibility = View.GONE
        binding.emptyView.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.historyContainer.visibility = View.GONE
    }
}