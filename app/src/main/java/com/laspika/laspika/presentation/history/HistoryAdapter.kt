package com.laspika.laspika.presentation.history

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.laspika.laspika.R
import com.laspika.laspika.core.data.ReportData
import com.laspika.laspika.core.utils.Constants
import com.laspika.laspika.databinding.ItemHistoryBinding
import com.laspika.laspika.presentation.detail.ReportDetailActivity

class HistoryAdapter: Adapter<HistoryAdapter.HistoryViewHolder>() {
    inner class HistoryViewHolder(val binding: ItemHistoryBinding): ViewHolder(binding.root) {
        fun bind(reportData: ReportData) {
            val context = itemView.context
            binding.apply {
                historyTitle.text = context.getString(R.string.history_title, reportData.kategoriPelanggaran)
                historyBody.text = when (reportData.status) {
                    Constants.CANCELLED -> context.getString(R.string.riwayat_cancelled)
                    Constants.APPROVED -> context.getString(R.string.riwayat_approved)
                    else -> context.getString(R.string.riwayat_pending)
                }
                historyDate.text = context.getString(R.string.notification_date, reportData.waktuPerkiraan, reportData.tanggal)

                itemView.setOnClickListener {
                    context.startActivity(
                        Intent(context, ReportDetailActivity::class.java).apply {
                            putExtra(ReportDetailActivity.REPORT_DETAIL, reportData)
                        }
                    )
                }
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<ReportData>() {
        override fun areItemsTheSame(oldItem: ReportData, newItem: ReportData): Boolean {
            return oldItem.id == newItem.id && oldItem.status == newItem.status
        }

        override fun areContentsTheSame(oldItem: ReportData, newItem: ReportData): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder =
        HistoryViewHolder(
            ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }
}