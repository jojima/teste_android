package dev.dextra.newsapp.feature.news.adapter

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.NonNull
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import dev.dextra.newsapp.api.model.Article
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.api.repository.NewsRepository
import dev.dextra.newsapp.base.NetworkState
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class NewsDataSource internal constructor(
    private val repository: NewsRepository, private val
    compositeDisposable: CompositeDisposable
) : PageKeyedDataSource<Int, Article>() {
    private var source: Source? = null
    private var pageNum: Int = 1
    val networkState = MutableLiveData<NetworkState>()
    private var retryCompletable: Completable? = null

    companion object {
        const val TAG: String = "NewsDataSource"
    }

    @SuppressLint("CheckResult")
    override fun loadInitial(@NonNull params: LoadInitialParams<Int>, @NonNull callback: LoadInitialCallback<Int, Article>) {
        Log.d(TAG, "Load first page")
        networkState.postValue(NetworkState.RUNNING)
        compositeDisposable.add(
            repository.getEverything(source?.id).subscribe({ response ->
                if (response.articles.isEmpty()) {
                    networkState.postValue(NetworkState.EMPTY)
                    Log.d(TAG, "Load failed")
                } else {
                    networkState.postValue(NetworkState.SUCCESS)
                    Log.d(TAG, "Load successful")
                }
                pageNum++
                callback.onResult(response.articles, null, 2)
            }, {
                networkState.postValue(NetworkState.ERROR)
            })
        )
    }

    override fun loadBefore(@NonNull params: LoadParams<Int>, @NonNull callback: LoadCallback<Int, Article>) {

    }

    @SuppressLint("CheckResult")
    override fun loadAfter(@NonNull params: LoadParams<Int>, @NonNull callback: LoadCallback<Int, Article>) {
        Log.d(TAG, "Load after, get next articles")
        networkState.postValue(NetworkState.RUNNING)
        compositeDisposable.add(
            repository.getEverything(source?.id, params.key).subscribe({ response ->
                if (response.articles.isEmpty()) {
                    networkState.postValue(NetworkState.EMPTY)
                    Log.d(TAG, "Load failed")
                } else {
                    networkState.postValue(NetworkState.SUCCESS)
                    Log.d(TAG, "Load successful")
                }
                callback.onResult(response.articles, params.key + 1)
            }, {
                networkState.postValue(NetworkState.ERROR)
            })
        )
    }

    fun retry() {
        if (retryCompletable != null) {
            compositeDisposable.add(
                retryCompletable!!
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
            )
        }
    }

    fun configureSource(source: Source) {
        this.source = source
    }
}