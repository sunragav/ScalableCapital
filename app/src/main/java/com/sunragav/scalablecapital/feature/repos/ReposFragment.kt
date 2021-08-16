package com.sunragav.scalablecapital.feature.repos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.sunragav.scalablecapital.app.di.Owner
import com.sunragav.scalablecapital.core.BaseRecyclerViewFragment
import com.sunragav.scalablecapital.core.adapter.AbstractPagingAdapter
import com.sunragav.scalablecapital.core.util.hide
import com.sunragav.scalablecapital.core.util.show
import com.sunragav.scalablecapital.databinding.FragmentFirstBinding
import com.sunragav.scalablecapital.feature.commits.transformer.GitHubViewModelTransformer
import com.sunragav.scalablecapital.feature.repos.adapter.ReposAdapter
import com.sunragav.scalablecapital.feature.repos.presenter.ReposViewModel
import com.sunragav.scalablecapital.feature.repos.repository.remote.models.RepoResponse
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

    @Inject
    @Owner
    lateinit var user: String
    private val reposViewModel: ReposViewModel by viewModels(factoryProducer = { viewModelFactory })
    private lateinit var binding: FragmentFirstBinding
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
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return Pair(binding, binding.rvRepo)
    }

    override fun handleLoadState(loadState: CombinedLoadStates) {
        val errorState = when {
            loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
            loadState.append is LoadState.Error -> loadState.append as LoadState.Error
            loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
            else -> null
        }
        errorState?.let {
            binding.emptyView.start(login = user)
            binding.rvRepo.hide()
        } ?: run {
            if (loadState.refresh !is LoadState.Loading) {
                binding.emptyView.stop()
                binding.rvRepo.show()
            }
        }
    }
}