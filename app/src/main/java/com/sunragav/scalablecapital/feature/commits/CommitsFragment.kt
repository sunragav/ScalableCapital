package com.sunragav.scalablecapital.feature.commits

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.sunragav.scalablecapital.R
import com.sunragav.scalablecapital.core.BaseFragment
import com.sunragav.scalablecapital.databinding.FragmentSecondBinding
import com.sunragav.scalablecapital.feature.commits.adapter.CommitsAdapter
import com.sunragav.scalablecapital.repository.remote.model.Commit
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@InternalCoroutinesApi
class CommitsFragment : BaseFragment<Commit>() {
    private val args: CommitsFragmentArgs by navArgs()
    override val listAdapter = CommitsAdapter()

    override fun setupViewModel() {
        viewModel.commitsList.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                it.collectLatest {
                    Timber.d("Adapter updated with new items")
                    listAdapter.submitData(it)
                }
            }
        }
        viewModel.repoName.postValue(args.repoName)
        viewModel.title.postValue(
            resources.getString(
                R.string.commits_fragment_label,
                args.repoName
            )
        )
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): Pair<FragmentSecondBinding, RecyclerView> {
        val binding = FragmentSecondBinding.inflate(inflater, container, false)
        return Pair(binding, binding.rvCommits)
    }

}