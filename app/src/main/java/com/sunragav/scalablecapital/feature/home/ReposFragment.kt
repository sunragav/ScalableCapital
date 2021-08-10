package com.sunragav.scalablecapital.feature.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.sunragav.scalablecapital.core.BaseFragment
import com.sunragav.scalablecapital.core.adapter.AbstractPagingAdapter
import com.sunragav.scalablecapital.databinding.FragmentFirstBinding
import com.sunragav.scalablecapital.feature.home.adapter.ReposAdapter
import com.sunragav.scalablecapital.repository.remote.model.Repo
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@InternalCoroutinesApi
class ReposFragment : BaseFragment<Repo>() {

    override fun setupViewModel() {
        lifecycleScope.launch {
            viewModel.repoList.collect {
                Timber.d("Adapter size:%d before", listAdapter.itemCount)
                listAdapter.submitData(it)
            }
        }
    }

    override val listAdapter: AbstractPagingAdapter<Repo> = ReposAdapter()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): Pair<FragmentFirstBinding, RecyclerView> {
        val binding = FragmentFirstBinding.inflate(inflater, container, false)
        return Pair(binding, binding.rvRepo)
    }
}