package com.example.rudonews.modules.menu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.example.rudonews.R
import com.example.rudonews.databinding.ActivityMenuBinding
import com.example.rudonews.modules.menu.favorites.FavoritesFragment
import com.example.rudonews.modules.menu.news.NewsFragment
import com.example.rudonews.modules.menu.profile.ProfileFragment

class MenuActivity : AppCompatActivity() {

    val fragments = mutableListOf<Fragment>()
    private val newsFragment = NewsFragment()
    private val profileFragment = ProfileFragment()
    private val favoritesFragment = FavoritesFragment()

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFragments()
        initBottomNavigation()
    }

    private fun initFragments() {
        fragments.add(newsFragment)
        fragments.add(profileFragment)
        fragments.add(favoritesFragment)
    }

    private fun initBottomNavigation() {
        loadFragment(newsFragment)

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.item_home -> loadFragment(newsFragment)
                R.id.item_profile -> loadFragment(profileFragment)
                R.id.item_favorites -> loadFragment(favoritesFragment)
            }
            true
        }
    }

    fun setBottomNavigationItem(position: Int) {
        binding.bottomNavigation.menu.getItem(position).isChecked = true
    }

    fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    enum class Fragments(val position: Int) {
        NEWS(0), PROFILE(1), FAVORITES(2)
    }
}
