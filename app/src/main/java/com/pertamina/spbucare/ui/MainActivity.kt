package com.pertamina.spbucare.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.pertamina.spbucare.R
import com.pertamina.spbucare.databinding.ActivityMainBinding
import com.pertamina.spbucare.viewmodel.SyncViewModel
import com.pertamina.spbucare.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val userVM: UserViewModel by viewModel { parametersOf("") }
    private var cookieType: String? = ""
    private var cookieName: String? = ""
    private var cookiePosition: String? = ""
    private var cookieIsAdmin: Boolean = false
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val user = getSharedPreferences("user", MODE_PRIVATE)
        val viewModel = ViewModelProviders.of(this).get(SyncViewModel::class.java)
        cookieType = user.getString("type", "missing")
        cookieName = user.getString("userName", "missing")
        cookiePosition = user.getString("position", "missing")
        cookieIsAdmin = user.getBoolean("isAdmin", false)
        viewModel.syncData(cookieType.toString())

        navController = findNavController(R.id.main_nav_fragment)
        setSupportActionBar(binding.toolbar)
        setNavigationView()
        setBottomNavigation(cookieType)
        setupObserver()
        onDestinationChanged()
    }

    private fun setupObserver() {
        userVM.user.observe(this, Observer { user ->
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
                if (it.isSuccessful) {
                    val token = it.result?.token
                    user.token = token.toString()
                    userVM.updateUser(user)
                } else {
                    Log.d("testing", it.exception?.message)
                }
            }
        })
    }

    private fun setNavigationView() {
        drawerLayout = binding.drawerLayout
        binding.navigationView.setupWithNavController(navController)
        binding.navigationView.setNavigationItemSelectedListener(this)
        val navigationViewMenu = binding.navigationView.menu
        if (!cookieIsAdmin) {
            navigationViewMenu.findItem(R.id.manage_user).isVisible = false
        }
        val headerView = binding.navigationView.getHeaderView(0)
        val navUsername = headerView.findViewById(R.id.user_name) as TextView
        val navUserType = headerView.findViewById(R.id.user_type) as TextView
        navUsername.text = cookieName?.capitalizeWords()
        navUserType.text = if (cookieType == "spbu") cookieType else cookiePosition
    }

    private fun setBottomNavigation(cookieType: String?) {
        //Setup the navGraph for this activity
        val navHostFragment = main_nav_fragment as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        var graph = inflater.inflate(R.navigation.main_nav)

        binding.mainBottomNav.menu.clear()
        var topDestination = setOf(
            R.id.dash_spbu_dest,
            R.id.history_spbu_dest,
            R.id.notification_dest
        )
        if (cookieType.equals("spbu")) {
            binding.mainBottomNav.inflateMenu(R.menu.menu_bottom_nav)
        } else {
            topDestination = setOf(
                R.id.dash_pertamina_dest,
                R.id.history_pertamina_dest,
                R.id.confirmation_dest,
                R.id.notification_dest
            )
            binding.mainBottomNav.inflateMenu(R.menu.menu_bottom_nav_pertamina)
            graph = inflater.inflate(R.navigation.main_nav_pertamina)
        }
        navHostFragment.navController.graph = graph
        appBarConfiguration = AppBarConfiguration(topDestination, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.mainBottomNav.setupWithNavController(navController)
    }

    private fun setToolbar(isTopDestination: Boolean) {
        if (isTopDestination) {
            binding.toolbarLayout.visibility = View.VISIBLE
            supportActionBar?.title = ""
            binding.ivToolbar.visibility = View.VISIBLE
            binding.tvTitle.visibility = View.GONE
            setToolbarColorBackground("#FFFFFF")
            binding.mainBottomNav.visibility = View.VISIBLE
        } else {
//            if (cookieType.equals("spbu")) {
//                when (destId) {
//                    R.id.cancel_dest -> setToolbarColorBackground("#DA251C")
//                    R.id.deposit_dest -> setToolbarColorBackground("#85C226")
//                    R.id.addition_dest -> setToolbarColorBackground("#32599C")
//                    R.id.quiz_dest -> setToolbarColorBackground("#85C226")
//                    R.id.withdraw_dest -> setToolbarColorBackground("#32599C")
//                    R.id.emergency_dest -> setToolbarColorBackground("#DA251C")
//                }
//            }
//            binding.tvTitle.text = destination.label
            binding.toolbarLayout.visibility = View.GONE
            binding.ivToolbar.visibility = View.GONE
//            binding.tvTitle.visibility = View.VISIBLE
            binding.mainBottomNav.visibility = View.GONE
        }
    }

    private fun onDestinationChanged() =
        navController.addOnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.show()
            val destId = destination.id
            if (cookieType.equals("spbu")) {
                val isTopDestination =
                    destId == R.id.dash_spbu_dest || destId == R.id.history_spbu_dest || destId == R.id.notification_dest
                setToolbar(isTopDestination)
            } else if (!cookieType.equals("spbu")) {
                val isTopDestination =
                    destId == R.id.dash_pertamina_dest || destId == R.id.history_pertamina_dest || destId == R.id.confirmation_dest || destId == R.id.notification_dest
                setToolbar(isTopDestination)
            }
        }

    override fun onSupportNavigateUp() =
        navController.navigateUp(appBarConfiguration) || navController.navigateUp(drawerLayout)

    override fun onBackPressed() =
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START) else super.onBackPressed()

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.signout -> {
                userVM.signout()
                binding.progressbar.visibility = View.VISIBLE
            }
            R.id.edit_password -> {
                navController.navigate(
                    R.id.change_password_dest,
                    null,
                    NavOptions.Builder().setEnterAnim(R.anim.slide_in_right)
                        .setExitAnim(R.anim.slide_out_left)
                        .setPopEnterAnim(R.anim.slide_in_left)
                        .setPopExitAnim(R.anim.slide_out_right).build()
                )
            }
            R.id.edit_profile -> {
                navController.navigate(
                    R.id.change_profile_user_dest,
                    null,
                    NavOptions.Builder().setEnterAnim(R.anim.slide_in_right)
                        .setExitAnim(R.anim.slide_out_left)
                        .setPopEnterAnim(R.anim.slide_in_left)
                        .setPopExitAnim(R.anim.slide_out_right).build()
                )
            }
            R.id.manage_user -> {
                navController.navigate(
                    R.id.manage_user_dest,
                    null,
                    NavOptions.Builder().setEnterAnim(R.anim.slide_in_right)
                        .setExitAnim(R.anim.slide_out_left)
                        .setPopEnterAnim(R.anim.slide_in_left)
                        .setPopExitAnim(R.anim.slide_out_right).build()
                )
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setToolbarColorBackground(color: String) =
        binding.toolbar.setBackgroundColor(Color.parseColor(color))

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authListener)
    }

    private val authListener = FirebaseAuth.AuthStateListener {
        if (it.currentUser == null) {
            binding.progressbar.visibility = View.GONE
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }
}