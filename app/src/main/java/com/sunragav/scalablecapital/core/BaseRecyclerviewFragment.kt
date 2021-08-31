package com.sunragav.scalablecapital.core

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.paging.CombinedLoadStates
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.sunragav.scalablecapital.core.adapter.AbstractPagingAdapter
import com.sunragav.scalablecapital.core.adapter.GitHubLoadStateAdapter
import com.sunragav.scalablecapital.presenter.HomeViewModel
import com.sunragav.scalablecapital.repository.remote.model.GitHubModel
import dagger.android.support.AndroidSupportInjection

abstract class BaseRecyclerViewFragment<T : GitHubModel> : Fragment() {
    protected val activityViewModel: HomeViewModel by activityViewModels()

    protected abstract val listAdapter: AbstractPagingAdapter<T>

    private lateinit var recyclerView: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    abstract fun setupViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val (binding, recyclerView) = getViewBindingAndRecyclerView(inflater, container)
        this.recyclerView = recyclerView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    open fun setupView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter.apply {
                addLoadStateListener { loadState ->
                    handleLoadState(loadState)
                }
                withLoadStateFooter(
                    footer = GitHubLoadStateAdapter { listAdapter.retry() }
                )
            }
        }
    }

    abstract fun handleLoadState(loadState: CombinedLoadStates)

    abstract fun getViewBindingAndRecyclerView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): Pair<ViewBinding, RecyclerView>
}