package com.laspika.laspika.presentation.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.laspika.laspika.core.utils.hide
import com.laspika.laspika.core.utils.show
import com.laspika.laspika.databinding.FragmentNotificationBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private val notificationAdapter by lazy { NotificationAdapter() }
    private val viewModel by viewModel<NotificationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvNotification.apply {
            adapter = notificationAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        observer()
    }

    private fun observer() {
        viewModel.getAllNotifications()
        viewModel.notificationState.observe(viewLifecycleOwner) {
            if (it.isEmpty()) binding.tvNoNotification.show() else binding.tvNoNotification.hide()
            notificationAdapter.differ.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}