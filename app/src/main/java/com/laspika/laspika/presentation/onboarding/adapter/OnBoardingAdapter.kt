package com.laspika.laspika.presentation.onboarding.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.laspika.laspika.databinding.ViewpagerContainerBinding

class OnBoardingAdapter : Adapter<OnBoardingAdapter.OnBoardingViewHolder>() {

    private var onBoardingItems: MutableList<OnBoardingItem> = mutableListOf()

    inner class OnBoardingViewHolder(val binding: ViewpagerContainerBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        return OnBoardingViewHolder(
            ViewpagerContainerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = onBoardingItems.size

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        with(holder) {
            with(onBoardingItems[position]) {
                binding.tvOnboardingTitle.text = this.title
                binding.tvOnboardingDescription.text = this.description
                binding.ivOnboarding.setImageResource(this.img)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setNewItem(data: List<OnBoardingItem>) {
        onBoardingItems.clear()
        onBoardingItems.addAll(data)
        notifyDataSetChanged()
    }
}

data class OnBoardingItem(
    val title: String,
    val description: String,
    val img: Int
)