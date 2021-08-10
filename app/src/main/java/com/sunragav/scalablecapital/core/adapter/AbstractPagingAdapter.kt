package com.sunragav.scalablecapital.core.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.sunragav.scalablecapital.repository.remote.model.GitHubModel


abstract class AbstractPagingAdapter<T : GitHubModel> :
    PagingDataAdapter<T, AbstractPagingAdapter.ViewHolder<T>>(
        Differentiator()
    ) {
    override fun getItemViewType(position: Int): Int {
        return position
    }

    abstract class ViewHolder<T : GitHubModel>(binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(position: Int)
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        holder.bind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        return createViewHolder(parent)
    }

    abstract fun createViewHolder(parent: ViewGroup): ViewHolder<T>


    class Differentiator<T : GitHubModel> : DiffUtil.ItemCallback<T>() {

        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem.identifier == newItem.identifier
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: T, newItem: T): Any = Any()
    }

}