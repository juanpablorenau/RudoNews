package com.example.rudonews.modules.menu.news

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rudonews.App
import com.example.rudonews.R
import com.example.rudonews.adapters.CommentRecyclerAdapter
import com.example.rudonews.data.model.Comment
import com.example.rudonews.data.model.News
import com.example.rudonews.databinding.ActivityDescriptionBinding
import com.example.rudonews.utils.Utils
import com.example.rudonews.utils.hideKeyboard

class DescriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDescriptionBinding
    private lateinit var viewModel: DescriptionViewModel

    private lateinit var commentRecyclerAdapter: CommentRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[DescriptionViewModel::class.java]
        binding.lifecycleOwner = this
        binding.activity = this
        binding.viewModel = viewModel
        setContentView(binding.root)

        initUi()
        initObservers()
        initListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        if (viewModel.news.value?.is_favorite == true) {
            menu?.let {
                menu.findItem(R.id.menu_item_favorite).icon =
                    ContextCompat.getDrawable(this, R.drawable.ic_favorite)
            }
        } else {
            menu?.let {
                menu.findItem(R.id.menu_item_favorite).icon =
                    ContextCompat.getDrawable(this, R.drawable.ic_favorite_border)
            }
        }
        menu?.let {
            menu.findItem(R.id.menu_item_favorite).setOnMenuItemClickListener {
                if (viewModel.news.value?.is_favorite == true) {
                    menu.findItem(R.id.menu_item_favorite).icon =
                        ContextCompat.getDrawable(this, R.drawable.ic_favorite_border)
                    viewModel.news.value?.id?.let { viewModel.deleteFavoritePost(it) }
                } else {
                    menu.findItem(R.id.menu_item_favorite).icon =
                        ContextCompat.getDrawable(this, R.drawable.ic_favorite)
                    val hashMap = HashMap<String, String>()
                    viewModel.news.value?.id?.let { hashMap.put("post", it) }
                    viewModel.saveFavoritePost(hashMap)
                }
                true
            }

            menu.findItem(R.id.menu_item_share).setOnMenuItemClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.action = Intent.ACTION_SEND
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, viewModel.news.value?.title)
                App.instance.startActivity(intent)
                true
            }
        }
        return true
    }

    private fun initUi() {
        initData()
        initToolbar()
    }

    private fun initData() {
        initNews()
        initComments()
    }

    private fun initNews() {
        val news = intent.getSerializableExtra(getString(R.string.intent_news)) as? News
        binding.news = news
        binding.textDate.text = news?.creation_date?.split("T")?.get(0) ?: ""
        viewModel.news.value = news

        Glide.with(App.instance)
            .load(news?.image)
            .centerCrop()
            .into(binding.imageViewDescription)
    }

    private fun initComments() {
        viewModel.news.value?.id?.let { viewModel.getCommentsByPost(it, 1) }
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbarDescription)
        supportActionBar?.title = viewModel.news.value?.title
        binding.toolbarDescription.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initObservers() {
        viewModel.comment.observe(this) { comment ->
            val icon = ContextCompat.getDrawable(this, R.drawable.ic_send)
            val drawable = icon?.let { DrawableCompat.wrap(it) }
            when (comment.length) {
                in 1..MAX_COMMENT_LENGTH -> {
                    drawable?.let { DrawableCompat.setTint(it, getColor(R.color.fuchsia)) }
                    binding.imageSend.setImageDrawable(drawable)
                }
                else -> {
                    drawable?.let { DrawableCompat.setTint(it, Color.GRAY) }
                    binding.imageSend.setImageDrawable(drawable)
                }
            }
            binding.textCommentLength.text =
                getString(
                    R.string.comment_length,
                    comment.length,
                    MAX_COMMENT_LENGTH
                )
        }

        viewModel.eventGetCommentsByPostSuccessful.observe(this) { eventGetCommentsByPostSuccessful ->
            if (eventGetCommentsByPostSuccessful) {
                if (viewModel.pager.value?.page == 1) {
                    binding.progressTopBar.visibility = View.INVISIBLE
                    initAdapter()
                } else {
                    binding.progressBottomBar.visibility = View.INVISIBLE
                    val listSize = viewModel.commentList.value?.size
                    listSize?.let { it -> commentRecyclerAdapter.notifyItemInserted(it) }
                    viewModel.isLoading.value = false
                }
            } else {
                Utils.createDialog(this, getString(R.string.server_error_dialog))
            }
        }

        viewModel.eventCreateCommentSuccessful.observe(this) { eventCreateCommentSuccessful ->
            if (eventCreateCommentSuccessful) {
                val listSize = viewModel.commentList.value?.size
                listSize?.minus(1)?.let {
                    commentRecyclerAdapter.notifyItemRangeInserted(
                        it,
                        listSize
                    )
                    binding.recyclerView.smoothScrollToPosition(it)
                }
            } else {
                Utils.createDialog(this, getString(R.string.server_error_dialog))
            }
        }

        viewModel.eventSaveFavoritePostSuccessful.observe(this) { eventSaveFavoritePostSuccessful ->
            if (!eventSaveFavoritePostSuccessful) {
                Utils.createDialog(this, getString(R.string.server_error_dialog))
            }
        }

        viewModel.eventDeleteFavoritePostSuccessful.observe(this) { eventDeleteFavoritePostSuccessful ->
            if (!eventDeleteFavoritePostSuccessful) {
                Utils.createDialog(this, getString(R.string.server_error_dialog))
            }
        }

        viewModel.existsServerResponse.observe(this) { existsResponse ->
            if (!existsResponse) {
                Utils.createDialog(this, getString(R.string.no_server_response))
            }
        }

        viewModel.isNetworkAvailable.observe(this) { isNetworkAvailable ->
            if (!isNetworkAvailable) {
                Utils.createDialog(this, getString(R.string.error_connection))
            }
        }
    }

    private fun initAdapter() {
        commentRecyclerAdapter = CommentRecyclerAdapter()
        commentRecyclerAdapter.data = viewModel.commentList.value ?: listOf()
        binding.recyclerView.adapter = commentRecyclerAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun initListeners() {
        initRecyclerAddOnScrollListener()
    }

    private fun initRecyclerAddOnScrollListener() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = binding.recyclerView.layoutManager as? LinearLayoutManager
                if (viewModel.isLoading.value == false) {
                    val itemPosition = linearLayoutManager?.findLastCompletelyVisibleItemPosition()
                    val lastItemPosition = viewModel.commentList.value?.size?.minus(1)
                    if (lastItemPosition == itemPosition) {
                        viewModel.isLoading.value = true
                        viewModel.pager.value?.next?.let {
                            val nextPage = viewModel.pager.value?.page?.plus(1)
                            nextPage?.let { nextPage ->
                                if (nextPage < 3) {
                                    binding.progressBottomBar.visibility = View.VISIBLE
                                    val newsId = viewModel.news.value?.id
                                    newsId?.let { newsId ->
                                        viewModel.getCommentsByPost(
                                            newsId,
                                            nextPage
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    fun createComment() {
        when (viewModel.comment.value?.length) {
            in 1..MAX_COMMENT_LENGTH -> {
                hideKeyboard()
                val comment = Comment(App.user, viewModel.comment.value)
                val newsId = viewModel.news.value?.id
                newsId?.let { newsId ->
                    viewModel.createComment(
                        newsId,
                        comment
                    )
                }
                viewModel.comment.value = ""
            }
            else -> {}
        }
    }

    companion object {
        const val MAX_COMMENT_LENGTH = 250
    }
}
