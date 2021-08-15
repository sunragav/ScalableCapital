package com.sunragav.scalablecapital.feature.repos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.sunragav.scalablecapital.core.BaseRecyclerViewFragment
import com.sunragav.scalablecapital.core.adapter.AbstractPagingAdapter
import com.sunragav.scalablecapital.databinding.FragmentFirstBinding
import com.sunragav.scalablecapital.feature.commits.transformer.GitHubViewModelTransformer
import com.sunragav.scalablecapital.feature.repos.adapter.ReposAdapter
import com.sunragav.scalablecapital.presenter.repos.ReposViewModel
import com.sunragav.scalablecapital.repository.remote.model.RepoResponse
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@InternalCoroutinesApi
class ReposFragment : BaseRecyclerViewFragment<RepoResponse>() {
    @Inject
    lateinit var viewModelFactory: ReposViewModel.Factory
    private val reposViewModel: ReposViewModel by viewModels(factoryProducer = { viewModelFactory })

    override fun setupViewModel() {
        lifecycleScope.launch {
            reposViewModel.repoList.collect {
                Timber.d("Adapter size:%d before", listAdapter.itemCount)
                listAdapter.submitData(it)
            }
        }
    }

    override val listAdapter: AbstractPagingAdapter<RepoResponse> =
        ReposAdapter(GitHubViewModelTransformer())

    override fun getViewBindingAndRecyclerView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): Pair<FragmentFirstBinding, RecyclerView> {
        val binding = FragmentFirstBinding.inflate(inflater, container, false)
        return Pair(binding, binding.rvRepo)
    }
}