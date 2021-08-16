package com.sunragav.scalablecapital.feature.commits

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.sunragav.scalablecapital.R
import com.sunragav.scalablecapital.core.BaseRecyclerViewFragment
import com.sunragav.scalablecapital.core.util.hide
import com.sunragav.scalablecapital.core.util.show
import com.sunragav.scalablecapital.databinding.FragmentSecondBinding
import com.sunragav.scalablecapital.feature.commits.adapter.CommitsAdapter
import com.sunragav.scalablecapital.feature.commits.presenter.CommitsViewModel
import com.sunragav.scalablecapital.feature.commits.repository.remote.models.CommitResponse
import com.sunragav.scalablecapital.feature.commits.transformer.GitHubViewModelTransformer
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@InternalCoroutinesApi
class CommitsFragment : BaseRecyclerViewFragment<CommitResponse>() {
    @Inject
    lateinit var viewModelFactory: CommitsViewModel.Factory
    private val commitsViewModel: CommitsViewModel by viewModels(factoryProducer = { viewModelFactory })
    private val args: CommitsFragmentArgs by navArgs()
    override val listAdapter = CommitsAdapter(GitHubViewModelTransformer())
    private lateinit var binding: FragmentSecondBinding

    override fun setupViewModel() {
        activityViewModel.title.postValue(
            resources.getString(
                R.string.commits_fragment_label,
                args.repoData.repoName
            )
        )
        with(commitsViewModel) {
            commitsList.observe(viewLifecycleOwner) {
                lifecycleScope.launch {
                    it.collectLatest {
                        listAdapter.submitData(it)
                    }
                }
            }
            triggerCommitsLoad.postValue(args.repoData)

            commitsCountLiveData.observe(viewLifecycleOwner) { commitsCountData ->
                binding.commitsCountView.update(commitsCountData)
            }
        }
    }

    override fun getViewBindingAndRecyclerView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): Pair<FragmentSecondBinding, RecyclerView> {
        binding = FragmentSecondBinding.inflate(inflater, container, false)
        return Pair(binding, binding.rvCommits)
    }

    override fun handleLoadState(loadState: CombinedLoadStates) {
        val errorState = when {
            loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
            loadState.append is LoadState.Error -> loadState.append as LoadState.Error
            loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
            else -> null
        }
        errorState?.let {
            binding.emptyView.start()
            binding.rvCommits.hide()
            binding.commitsCountView.hide()
        } ?: run {
            if (loadState.refresh !is LoadState.Loading) {
                binding.emptyView.stop()
                binding.rvCommits.show()
                binding.commitsCountView.show()
            }
        }
    }
}