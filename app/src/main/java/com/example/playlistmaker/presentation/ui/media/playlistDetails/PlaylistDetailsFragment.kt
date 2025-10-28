package com.example.playlistmaker.presentation.ui.media.playlistDetails

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.search.adapter.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistDetailsFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: PlaylistDetailsFragmentArgs by navArgs()
    private val viewModel: PlaylistDetailsViewModel by viewModel()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var adapter: TracksInPlaylistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Обработка системной «Назад»
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // --- BottomSheet ---
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            isHideable = false            // Нельзя скрыть
            skipCollapsed = false
            isDraggable = true
            // fitToContents = false уже задан в layout через app:behavior_fitToContents="false"
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        // Делаем так, чтобы верхняя кромка шита была сразу под блоком header/info_card
        // после раскладки вьюх посчитаем нужный peekHeight
        binding.playlistDetailsRoot.doOnLayout {
            val rootH = binding.playlistDetailsRoot.height
            val headerBottom = binding.header.bottom
            val peek = (rootH - headerBottom).coerceAtLeast(0)
            bottomSheetBehavior.peekHeight = peek
        }

        // --- RecyclerView + Adapter ---
        adapter = TrackAdapter(
            mutableListOf(),
            onItemClick = { track -> openPlayer(track) },
            onItemLongClick = { track -> confirmRemove(track) }
        )
        binding.tracksRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@PlaylistDetailsFragment.adapter
        }

        // --- Кнопки ---
        binding.backButton.setOnClickListener { findNavController().navigateUp() }

        binding.shareButton.setOnClickListener {
            // Берём подготовленные данные у VM и вызываем системный share
            val shareText = viewModel.buildShareText()
            if (shareText.isNullOrBlank()) {
                // Нечем делиться (например, пустой плейлист)
                // Можно показать тост/снекбар по вкусу
                return@setOnClickListener
            }
            shareText(shareText)
        }

        binding.menuButton.setOnClickListener {
            // Тут можно открыть BottomSheetDialog / PopupMenu с пунктами «Редактировать», «Удалить плейлист», «Очистить»
            // Для краткости опустим реализацию меню
        }

        // --- Подписки на состояние ---
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    adapter.updateData(state.tracks)
                    render(state) // остальное оформление
                }
            }
        }

        // Стартовая загрузка
        viewModel.load(args.playlistId)
    }

    private fun render(state: PlaylistDetailsUiState) = with(binding) {
        // Обложка
        val corner = resources.getDimensionPixelSize(R.dimen.corner_radius_8)
        val coverPath = state.coverPath
        if (!coverPath.isNullOrBlank()) {
            Glide.with(cover)
                .load(File(coverPath))
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(RoundedCorners(corner))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(cover)
        } else {
            cover.setImageResource(R.drawable.placeholder)
        }

        // Инфо
        title.text = state.title
        description.apply {
            text = state.description.orEmpty()
            visibility = if (state.description.isNullOrBlank()) View.GONE else View.VISIBLE
        }
        meta.text = getString(
            R.string.playlist_meta_template, // напр. "%1$s · %2$s"
            formatDurationMinutes(state.totalDurationMinutes),
            resources.getQuantityString(
                R.plurals.tracks_count, state.tracks.size, state.tracks.size
            )
        )

        // Список треков
        adapter.submitList(state.tracks)

        // Обновим peekHeight на случай, если высота header изменилась (например, скрыли описание)
        playlistDetailsRoot.doOnLayout {
            val rootH = playlistDetailsRoot.height
            val headerBottom = header.bottom
            bottomSheetBehavior.peekHeight = (rootH - headerBottom).coerceAtLeast(0)
        }
    }

    private fun formatDurationMinutes(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return if (hours > 0) getString(R.string.hh_mm_template, hours, minutes)
        else resources.getQuantityString(R.plurals.minutes_short, minutes, minutes)
    }

    private fun openPlayer(track: Track) {
        // Вариант 1: Safe Args (если объявлен action из этого фрагмента в трек)
        // val action = PlaylistDetailsFragmentDirections.actionPlaylistDetailsFragmentToTrackFragment(track)
        // findNavController().navigate(action)

        // Вариант 2: глобальный переход по id фрагмента
        findNavController().navigate(
            R.id.trackFragment,
            bundleOf("track" to track) // ключ как в аргументах TrackFragment
        )
    }

    private fun confirmRemove(track: Track) {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.remove_track_question))
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.removeTrack(track)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun shareText(text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.share)))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
