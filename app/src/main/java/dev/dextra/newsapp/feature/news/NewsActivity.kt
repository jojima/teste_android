package dev.dextra.newsapp.feature.news

import PaginationScrollListener
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.dextra.newsapp.R
import dev.dextra.newsapp.api.model.Article
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.api.repository.NewsRepository
import dev.dextra.newsapp.base.BaseListActivity
import dev.dextra.newsapp.base.repository.EndpointService
import dev.dextra.newsapp.feature.news.adapter.ArticleListAdapter
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.android.synthetic.main.activity_sources.*


const val NEWS_ACTIVITY_SOURCE = "NEWS_ACTIVITY_SOURCE"

class NewsActivity : BaseListActivity(), ArticleListAdapter.SourceListAdapterItemListener {
    override val emptyStateTitle: Int = R.string.empty_state_title_news
    override val emptyStateSubTitle: Int = R.string.empty_state_subtitle_news
    override val errorStateTitle: Int = R.string.error_state_title_news
    override val errorStateSubTitle: Int = R.string.error_state_subtitle_news
    override val mainList: View
        get() = news_list

    override fun setupLandscape() {
//        sources_filters.orientation = LinearLayout.HORIZONTAL
    }

    override fun setupPortrait() {
//        sources_filters.orientation = LinearLayout.VERTICAL
    }

    override fun executeRetry() {
        loadArticles()
    }

    private val newsViewModel = NewsViewModel(NewsRepository(EndpointService()))
    private var viewManager: RecyclerView.LayoutManager = GridLayoutManager(this, 1)
    private var viewAdapter: ArticleListAdapter = ArticleListAdapter(this)
    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_news)

        (intent?.extras?.getSerializable(NEWS_ACTIVITY_SOURCE) as Source).let { source ->
            title = source.name

            newsViewModel.configureSource(source)
        }

        loadArticles()
        setupList()
        super.onCreate(savedInstanceState)
    }

    override fun onClick(article: Article) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(article.url)
        startActivity(i)
    }

    private var loading: Dialog? = null

    fun showLoading() {
        if (loading == null) {
            loading = Dialog(this)
            loading?.apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                setContentView(R.layout.dialog_loading)
            }
        }
        loading?.show()
    }

    fun showData(articles: List<Article>) {
        val viewAdapter = ArticleListAdapter(this@NewsActivity)
        news_list.adapter = viewAdapter

        news_list.addOnScrollListener(object : PaginationScrollListener(viewManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                //you have to call loadmore items to get more data
                loadArticles()
            }
        })
    }

    private fun setupList() {
        news_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }


    private fun loadArticles() {
        newsViewModel.newsList.observe(this, Observer {
            viewAdapter.apply {
                clear()
                notifyDataSetChanged()
                add(it)
                notifyDataSetChanged()
                isLoading = false
                news_list.scrollToPosition(0)
            }
        })

        newsViewModel.networkState.observe(this, networkStateObserver)

        newsViewModel.loadNews()
    }
}
