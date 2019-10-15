package com.pertamina.spbucare.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.pertamina.spbucare.databinding.FragmentUserListBinding
import com.pertamina.spbucare.network.NetworkState
import com.pertamina.spbucare.ui.adapter.UserAdapter
import com.pertamina.spbucare.ui.adapter.UserListener
import com.pertamina.spbucare.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class UserListFragment(val type: String) : Fragment() {
    private lateinit var binding: FragmentUserListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserListBinding.inflate(inflater, container, false)
        val userVM: UserViewModel by viewModel { parametersOf(type) }
        binding.lifecycleOwner = this
        binding.viewModel = userVM
        setupRecyclerView()
        setupObserver(userVM)
        return binding.root
    }

    private fun setupObserver(userVM: UserViewModel) {
        binding.apply {
            userVM.networkState.observe(viewLifecycleOwner, Observer {
                refreshLayout.isRefreshing = it == NetworkState.RUNNING
                refreshLayout.setOnRefreshListener { userVM.refreshUsers(type) }
            })
        }
    }

    private fun setupRecyclerView() {
        binding.apply {
            rvUsers.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            var adapter = UserAdapter(UserListener { user ->
                val action = ManageUserFragmentDirections.actionShowPertaminaDetail(user)
                findNavController().navigate(action)
            })
            if (type == "spbu") {
                adapter = UserAdapter(UserListener { user ->
                    val action = ManageUserFragmentDirections.actionShowSpbuDetail(user)
                    findNavController().navigate(action)
                })
            }
            rvUsers.adapter = adapter
        }
    }


}
