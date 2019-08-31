package dev.dextra.newsapp.feature.news

import androidx.lifecycle.MutableLiveData
import dev.dextra.newsapp.api.model.Article
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.api.repository.NewsRepository
import dev.dextra.newsapp.base.BaseViewModel
import dev.dextra.newsapp.base.NetworkState


class NewsViewModel(private val newsRepository: NewsRepository) : BaseViewModel() {

    private var source: Source? = null
    val networkState = MutableLiveData<NetworkState>()
    val newsList: MutableLiveData<List<Article>> = MutableLiveData()

    fun configureSource(source: Source) {
        this.source = source
    }

    fun loadNews() {
        networkState.postValue(NetworkState.RUNNING)

        addDisposable(
            newsRepository.getEverything(source?.id).subscribe({ response ->
                newsList.postValue(response.articles)
                if (response.articles.isEmpty()) {
                    networkState.postValue(NetworkState.ERROR)
                } else {
                    networkState.postValue(NetworkState.SUCCESS)
                }
            },
                {
                    networkState.postValue(NetworkState.ERROR)
                })
        )
    }
}
