package com.pertamina.spbucare.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.pertamina.spbucare.R
import com.pertamina.spbucare.databinding.FragmentManageUserBinding
import com.pertamina.spbucare.ui.adapter.ViewPagerAdapter

class ManageUserFragment : Fragment() {
    private lateinit var binding: FragmentManageUserBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageUserBinding.inflate(inflater, container, false)
        binding.apply {
            val adapter = ViewPagerAdapter(childFragmentManager)
            vpManage.adapter = adapter
            tlManage.setupWithViewPager(vpManage)
            speedDial.addActionItem(
                SpeedDialActionItem.Builder(R.id.fab_add_user, R.drawable.ic_new_user)
                    .setFabBackgroundColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorGreen,
                            requireContext().theme
                        )
                    )
                    .setFabImageTintColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorWhite,
                            requireContext().theme
                        )
                    )
                    .setLabel(getString(R.string.tambah_pengguna))
                    .setLabelBackgroundColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorWhite,
                            requireContext().theme
                        )
                    )
                    .create()
            )
            speedDial.addActionItem(
                SpeedDialActionItem.Builder(R.id.fab_create_order, R.drawable.ic_shopping_bag)
                    .setFabBackgroundColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorBlue,
                            requireContext().theme
                        )
                    )
                    .setLabel(getString(R.string.create_order))
                    .setLabelBackgroundColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorWhite,
                            requireContext().theme
                        )
                    )
                    .create()
            )

            speedDial.setOnActionSelectedListener { speedDialActionItem ->
                when (speedDialActionItem.id) {
                    R.id.fab_add_user -> {
                        val action = ManageUserFragmentDirections.actionCreateUser()
                        findNavController().navigate(action)
                        false // true to keep the Speed Dial open
                    }
                    R.id.fab_create_order -> {
                        val action = ManageUserFragmentDirections.actionCreateOrder()
                        findNavController().navigate(action)
                        false // true to keep the Speed Dial open
                    }
                    else -> false
                }
            }
        }
        return binding.root
    }
}