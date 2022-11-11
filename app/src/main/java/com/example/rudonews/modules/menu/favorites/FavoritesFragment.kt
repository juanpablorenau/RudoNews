package com.example.rudonews.modules.menu.favorites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rudonews.App
import com.example.rudonews.R
import com.example.rudonews.adapters.NewsRecyclerAdapter
import com.example.rudonews.data.model.News
import com.example.rudonews.databinding.FragmentFavoritesBinding
import com.example.rudonews.modules.menu.MenuActivity
import com.example.rudonews.utils.Utils

class FavoritesFragment : Fragment(), NewsRecyclerAdapter.NewsRecyclerViewInterface {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var viewModel: FavoritesViewModel

    private lateinit var newsRecyclerAdapter: NewsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)
        binding = FragmentFavoritesBinding.bind(view)
        viewModel = ViewModelProvider(this)[FavoritesViewModel::class.java]
        binding.lifecycleOwner = this
        binding.fragment = this
        binding.viewModel = viewModel

        App.user?.let {
            initUi()
            initObservers()
            initListeners()
        } ?: run {
            showRecyclerView(false)
            binding.swipe.visibility = View.GONE
            binding.progressTopBar.visibility = View.GONE
            context?.let { Utils.createGuestDialog(it) }
        }

        return view
    }

    override fun onNewsClickListener(news: News) {
    }

    override fun onFavoriteClickListener(news: News) {
        if (news.is_favorite == true) {
            news.is_favorite = false
            news.id?.let { viewModel.deleteFavoritePost(it) }
        } else {
            news.is_favorite = true
            val hashMap = HashMap<String, String>()
            news.id?.let { hashMap.put("post", it) }
            viewModel.saveFavoritePost(hashMap)
        }
        newsRecyclerAdapter.notifyDataSetChanged()
    }

    override fun onShareClickListener(news: News) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, news.short_description)
        App.instance.startActivity(intent)
    }

    private fun initUi() {
        viewModel.getFavoritePosts(1)
    }

    private fun initObservers() {
        viewModel.favoritesList.observe(viewLifecycleOwner) { favoritesList ->
            showRecyclerView(favoritesList.isNotEmpty())
        }

        viewModel.eventGetFavoritePostsSuccessful.observe(viewLifecycleOwner) { eventGetFavoritePostsSuccessful ->
            if (eventGetFavoritePostsSuccessful) {
                if (viewModel.pager.value?.page == 1) {
                    binding.progressTopBar.visibility = View.INVISIBLE
                    initAdapter()
                    binding.swipe.isRefreshing = false
                } else {
                    binding.progressBottomBar.visibility = View.INVISIBLE
                    val listSize = viewModel.favoritesList.value?.size
                    listSize?.let { it -> newsRecyclerAdapter.notifyItemInserted(it) }
                    viewModel.isLoading.value = false
                }
            } else {
                Utils.createDialog(App.instance, getString(R.string.server_error_dialog))
            }
        }

        viewModel.eventSaveFavoritePostSuccessful.observe(viewLifecycleOwner) { eventSaveFavoritePostSuccessful ->
            if (!eventSaveFavoritePostSuccessful) {
                Utils.createDialog(App.instance, getString(R.string.server_error_dialog))
            }
        }

        viewModel.eventDeleteFavoritePostSuccessful.observe(viewLifecycleOwner) { eventDeleteFavoritePostSuccessful ->
            if (!eventDeleteFavoritePostSuccessful) {
                Utils.createDialog(App.instance, getString(R.string.server_error_dialog))
            }
        }

        viewModel.existsServerResponse.observe(viewLifecycleOwner) { existsResponse ->
            if (!existsResponse) {
                Utils.createDialog(App.instance, getString(R.string.no_server_response))
            }
        }

        viewModel.isNetworkAvailable.observe(viewLifecycleOwner) { isNetworkAvailable ->
            if (!isNetworkAvailable) {
                Utils.createDialog(App.instance, getString(R.string.error_connection))
            }
        }
    }

    private fun showRecyclerView(visible: Boolean) {
        if (visible) {
            binding.textNoFavorites.visibility = View.GONE
            binding.buttonNoFavorites.visibility = View.GONE
            binding.recyclerNews.visibility = View.VISIBLE
        } else {
            binding.textNoFavorites.visibility = View.VISIBLE
            binding.buttonNoFavorites.visibility = View.VISIBLE
            binding.recyclerNews.visibility = View.GONE
        }
    }

    private fun initAdapter() {
        newsRecyclerAdapter = NewsRecyclerAdapter(this, this)
        newsRecyclerAdapter.data = viewModel.favoritesList.value ?: listOf()
        binding.recyclerNews.adapter = newsRecyclerAdapter
        binding.recyclerNews.layoutManager = LinearLayoutManager(App.instance)
    }

    private fun initListeners() {
        initRecyclerAddOnScrollListener()
        initSwipeSetOnRefreshListener()
    }

    private fun initRecyclerAddOnScrollListener() {
        binding.recyclerNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (viewModel.isLoading.value == false) {
                    val itemPosition = linearLayoutManager?.findLastCompletelyVisibleItemPosition()
                    val lastItemPosition = viewModel.favoritesList.value?.size?.minus(1)
                    if (lastItemPosition == itemPosition) {
                        viewModel.isLoading.value = true
                        viewModel.pager.value?.next?.let {
                            binding.progressBottomBar.visibility = View.VISIBLE
                            val nextPage = viewModel.pager.value?.page?.plus(1)
                            nextPage?.let { viewModel.getFavoritePosts(it) }
                        }
                    }
                }
            }
        })
    }

    private fun initSwipeSetOnRefreshListener() {
        binding.swipe.setOnRefreshListener {
            viewModel.favoritesList.value?.clear()
            viewModel.getFavoritePosts(1)
        }
    }

    fun goToNewsFragment() {
        val menuActivity = activity as MenuActivity
        val newsFragment = menuActivity.fragments[MenuActivity.Fragments.NEWS.position]
        menuActivity.loadFragment(newsFragment)
        menuActivity.setBottomNavigationItem(MenuActivity.Fragments.NEWS.position)
    }
}
