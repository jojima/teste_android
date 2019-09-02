package dev.dextra.newsapp.feature.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.dextra.newsapp.R
import dev.dextra.newsapp.api.model.Article
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.base.BaseListActivity
import dev.dextra.newsapp.feature.news.adapter.ArticleListAdapter
import kotlinx.android.synthetic.main.activity_news.*


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
//        loadArticles()
    }


    private lateinit var newsViewModel :NewsViewModel
    private lateinit var articleListAdapter: ArticleListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_news)

        (intent?.extras?.getSerializable(NEWS_ACTIVITY_SOURCE) as Source).let { source ->
            title = source.name

            newsViewModel = ViewModelProviders.of(this).get(NewsViewModel::class.java)
            newsViewModel.configureSource(source)
        }

        initAdapter()

        super.onCreate(savedInstanceState)
    }

    override fun onClick(article: Article) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(article.url)
        startActivity(i)
    }

    private fun initAdapter() {
        articleListAdapter = ArticleListAdapter()
        news_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        news_list.adapter = articleListAdapter
        newsViewModel.listLiveData.observe(this, Observer {
            articleListAdapter.submitList(it)
        })
        newsViewModel.newsDataSourceFactory.newsDataSource.networkState.observe(this, networkStateObserver)
    }
}
