package dev.dextra.newsapp.feature.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dev.dextra.newsapp.api.model.Article
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.api.repository.NewsRepository
import dev.dextra.newsapp.base.BaseViewModel
import dev.dextra.newsapp.base.NetworkState
import dev.dextra.newsapp.base.repository.EndpointService
import dev.dextra.newsapp.feature.news.adapter.NewsDataSourceFactory
import io.reactivex.disposables.CompositeDisposable


class NewsViewModel : BaseViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val newsRepository: NewsRepository = NewsRepository(EndpointService())
    var networkState = MutableLiveData<NetworkState>()
    val newsDataSourceFactory: NewsDataSourceFactory = NewsDataSourceFactory(compositeDisposable, newsRepository)
    var listLiveData: LiveData<PagedList<Article>>
    private val pageSize = 10

    private var source: Source? = null

    init {
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize * 2)
            .setEnablePlaceholders(false)
            .build()
        listLiveData = LivePagedListBuilder(newsDataSourceFactory, config).build()
    }

    fun configureSource(source: Source) {
        newsDataSourceFactory.configureSource(source)
        this.source = source
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
