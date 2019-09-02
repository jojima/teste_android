package dev.dextra.newsapp.feature.news.adapter

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import dev.dextra.newsapp.api.model.Article
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.api.repository.NewsRepository
import dev.dextra.newsapp.base.NetworkState
import io.reactivex.disposables.CompositeDisposable


class NewsDataSourceFactory(
    private val compositeDisposable: CompositeDisposable,
    private val newsRepository: NewsRepository
) : DataSource.Factory<Int, Article>() {
    var networkState = MutableLiveData<NetworkState>()
    val newsDataSourceLiveData = MutableLiveData<NewsDataSource>()
    private var source: Source? = null
    val newsDataSource = NewsDataSource(newsRepository, compositeDisposable)


    override fun create(): DataSource<Int, Article> {
        source?.let {
            newsDataSource.configureSource(it)
        }
        networkState = newsDataSource.networkState
        newsDataSourceLiveData.postValue(newsDataSource)
        return newsDataSource
    }

    fun configureSource(source: Source) {
        this.source = source
    }
}