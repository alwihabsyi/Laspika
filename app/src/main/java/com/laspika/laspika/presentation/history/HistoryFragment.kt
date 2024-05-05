package com.laspika.laspika.presentation.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.laspika.laspika.core.utils.UiState
import com.laspika.laspika.core.utils.hide
import com.laspika.laspika.core.utils.show
import com.laspika.laspika.core.utils.toast
import com.laspika.laspika.databinding.FragmentHistoryBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<HistoryViewModel>()
    private lateinit var reportsAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRv()
        observer()
    }

    private fun setUpRv() {
        reportsAdapter = HistoryAdapter()
        binding.rvHistory.apply {
            adapter = reportsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observer() {
        viewModel.historyState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    if (state.isLoading == true) binding.progressBar.show()
                    else binding.progressBar.hide()
                }
                is UiState.Success -> {
                    state.data?.let {
                        reportsAdapter.differ.submitList(it)
                        if (it.isNotEmpty()) {
                            binding.tvNoHistory.hide()
                        }
                    }
                }
                is UiState.Error -> {
                    toast(state.error!!)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getHistory()
    }

}