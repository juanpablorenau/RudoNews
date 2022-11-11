package com.example.rudonews.modules.menu.news

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rudonews.App
import com.example.rudonews.R
import com.example.rudonews.adapters.NewsRecyclerAdapter
import com.example.rudonews.data.model.News
import com.example.rudonews.databinding.FragmentNewsBinding
import com.example.rudonews.utils.Utils
import com.google.android.material.chip.Chip

class NewsFragment :
    Fragment(),
    NewsRecyclerAdapter.NewsRecyclerViewInterface {

    private lateinit var binding: FragmentNewsBinding
    private lateinit var viewModel: NewsViewModel

    private lateinit var newsRecyclerAdapter: NewsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)
        binding = FragmentNewsBinding.bind(view)
        viewModel = ViewModelProvider(this)[NewsViewModel::class.java]
        binding.lifecycleOwner = this
        binding.fragment = this
        binding.viewModel = viewModel

        initUi()
        initObservers()
        initListeners()

        return view
    }

    override fun onNewsClickListener(news: News) {
        App.user?.let {
            val intent = Intent(App.instance, DescriptionActivity::class.java)
            intent.putExtra(getString(R.string.intent_news), news)
            startActivity(intent)
        }
    }

    override fun onFavoriteClickListener(news: News) {
        App.user?.let {
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
    }

    override fun onShareClickListener(news: News) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, news.short_description)
        App.instance.startActivity(intent)
    }

    override fun onResume() {
        viewModel.newsList.value?.clear()
        viewModel.getNews(1)
        super.onResume()
    }

    private fun initUi() {
        initData()
        checkIfUserRegistered()
    }

    private fun initData() {
        viewModel.getCategories()
    }

    private fun checkIfUserRegistered() {
        if (App.user == null) {
            context?.let { Utils.createGuestDialog(it) }
        }
    }

    private fun initObservers() {
        viewModel.eventGetCategoriesSuccessful.observe(viewLifecycleOwner) { eventLoadDataSuccessful ->
            if (eventLoadDataSuccessful) {
                initChips()
            } else {
                Utils.createDialog(App.instance, getString(R.string.server_error_dialog))
            }
        }

        viewModel.eventGetNewsSuccessful.observe(viewLifecycleOwner) { eventGetNewsSuccessful ->
            if (eventGetNewsSuccessful) {
                if (viewModel.pager.value?.page == 1) {
                    binding.progressTopBar.visibility = View.INVISIBLE
                    initAdapter()
                    binding.swipe.isRefreshing = false
                } else {
                    binding.progressBottomBar.visibility = View.INVISIBLE
                    val listSize = viewModel.newsList.value?.size
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

        viewModel.eventGetPostsByCategorySuccessful.observe(viewLifecycleOwner) { eventGetPostsByCategorySuccessful ->
            if (eventGetPostsByCategorySuccessful) {
                viewModel.newsList.value?.sortBy { it.category?.name }
                newsRecyclerAdapter.data = viewModel.newsList.value ?: listOf()
            } else {
                Utils.createDialog(App.instance, getString(R.string.server_error_dialog))
            }
        }

        viewModel.eventGetPostsBySearchSuccessful.observe(viewLifecycleOwner) { eventGetPostsBySearchSuccessful ->
            if (eventGetPostsBySearchSuccessful) {
                newsRecyclerAdapter.data = viewModel.newsList.value ?: listOf()
            } else {
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

    private fun initAdapter() {
        newsRecyclerAdapter = NewsRecyclerAdapter(this, this, this)
        newsRecyclerAdapter.data = viewModel.newsList.value ?: listOf()
        binding.recyclerNews.adapter = newsRecyclerAdapter
        binding.recyclerNews.layoutManager = LinearLayoutManager(App.instance)
    }

    private fun initChips() {
        viewModel.categoriesList.value?.sortBy { it.name }
        viewModel.categoriesList.value?.forEach {
            val chip = Chip(context).apply {
                text = it.name
                isCheckable = true
                chipStrokeColor = ContextCompat.getColorStateList(context, R.color.chip_border)
                chipBackgroundColor =
                    ContextCompat.getColorStateList(context, R.color.chip_background)
                chipStrokeWidth = 1F
                checkedIconTint =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.fuchsia))
                setTextColor(ContextCompat.getColorStateList(context, R.color.chip_text))
                setCheckedIconResource(R.drawable.ic_done)
            }
            binding.chipGroup.addView(chip as View)
        }
    }

    private fun initListeners() {
        initRecyclerAddOnScrollListener()
        initSwipeSetOnRefreshListener()
        initChipGroupSetOnCheckedStateChangeListener()
        initSearchViewSetOnQueryTextListener()
    }

    private fun initRecyclerAddOnScrollListener() {
        binding.recyclerNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (viewModel.isLoading.value == false) {
                    val itemPosition =
                        linearLayoutManager?.findLastCompletelyVisibleItemPosition()
                    val lastItemPosition = viewModel.newsList.value?.size?.minus(1)
                    if (lastItemPosition == itemPosition) {
                        viewModel.isLoading.value = true
                        viewModel.pager.value?.next?.let {
                            binding.progressBottomBar.visibility = View.VISIBLE
                            val nextPage = viewModel.pager.value?.page?.plus(1)
                            nextPage?.let { viewModel.getNews(it) }
                        }
                    }
                }
            }
        })
    }

    private fun initSwipeSetOnRefreshListener() {
        binding.swipe.setOnRefreshListener {
            binding.textApplyFilters.isVisible = false
            val checkedChipIds = binding.chipGroup.checkedChipIds
            checkedChipIds.forEach { id ->
                val chip = binding.chipGroup.findViewById<Chip>(id)
                chip.apply {
                    isChecked = false
                }
                binding.chipGroup.childDrawableStateChanged(chip)
            }
            binding.searchNews.setQuery("", false)
            binding.searchNews.clearFocus()
            viewModel.newsList.value?.clear()
            viewModel.getNews(1)
        }
    }

    private fun initChipGroupSetOnCheckedStateChangeListener() {
        binding.chipGroup.setOnCheckedStateChangeListener { chipGroup, checkedIds ->
            binding.textApplyFilters.isVisible = checkedIds.isNotEmpty()
            if (checkedIds.isNotEmpty()) {
                viewModel.selectedCategoriesList.value?.clear()
                checkedIds.forEach { id ->
                    val chip = chipGroup.findViewById<Chip>(id)
                    val chipText = chip.text.toString()
                    viewModel.selectedCategoriesList.value?.add(chipText)
                }
            } else {
                viewModel.getNews(1)
            }
        }
    }

    fun applyFilters() {
        viewModel.selectedCategoriesList.value?.let { viewModel.getPostsByCategory(it, 1) }
    }

    private fun initSearchViewSetOnQueryTextListener() {
        binding.searchNews.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchNews.clearFocus()
                viewModel.getPostsBySearch(query.toString())
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        val clearButton: ImageView =
            binding.searchNews.findViewById(androidx.appcompat.R.id.search_close_btn)
        clearButton.setOnClickListener {
            binding.searchNews.setQuery("", false)
            viewModel.newsList.value?.clear()
            viewModel.getNews(1)
        }
    }
}
