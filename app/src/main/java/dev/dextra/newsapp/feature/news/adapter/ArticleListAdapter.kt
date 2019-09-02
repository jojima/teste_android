package dev.dextra.newsapp.feature.news.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.dextra.newsapp.R
import dev.dextra.newsapp.api.model.Article
import kotlinx.android.synthetic.main.item_article.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ArticleListAdapter : PagedListAdapter<Article, RecyclerView.ViewHolder>(NewsDiffCallback) {

    private val dateFormat =
        SimpleDateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT)
    private val parseFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return NewsListAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as NewsListAdapterViewHolder)
        val article = getItem(position)
        holder.view.article_name.text = article?.title
        holder.view.article_date.text = dateFormat.format(parseFormat.parse(article?.publishedAt))
        holder.view.article_author.text = article?.author
        holder.view.article_description.text = article?.description
    }

    companion object {
        val NewsDiffCallback = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem
            }
        }
    }

    class NewsListAdapterViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    interface SourceListAdapterItemListener {

        fun onClick(article: Article)
    }
}