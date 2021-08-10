package com.sunragav.scalablecapital.core

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.sunragav.scalablecapital.core.adapter.AbstractPagingAdapter
import com.sunragav.scalablecapital.presenter.HomeViewModel
import com.sunragav.scalablecapital.repository.remote.model.GitHubModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

abstract class BaseFragment<T : GitHubModel> : Fragment() {
    @Inject
    lateinit var factory: HomeViewModel.Factory
    protected val viewModel: HomeViewModel by activityViewModels(factoryProducer = { factory })

    protected abstract val listAdapter: AbstractPagingAdapter<T>


    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    abstract fun setupViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val (binding, recyclerView) = getViewBinding(inflater, container)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }
        setupViewModel()
        return binding.root
    }

    abstract fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): Pair<ViewBinding, RecyclerView>
}