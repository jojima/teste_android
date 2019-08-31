package dev.dextra.newsapp.feature.news.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.dextra.newsapp.R
import dev.dextra.newsapp.api.model.Article
import dev.dextra.newsapp.feature.news.NewsActivity
import kotlinx.android.synthetic.main.item_article.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ArticleListAdapter(val listener: NewsActivity) :
    RecyclerView.Adapter<ArticleListAdapter.ArticleListAdapterViewHolder>() {

    private val dateFormat =
        SimpleDateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT)
    private val parseFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    private val dataset: ArrayList<Article> = ArrayList()

    override fun getItemCount(): Int = dataset.size

//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//
//        val article = getItem(position)
//
//        var convertView2 = convertView
//
//        if (convertView2 == null) {
//            convertView2 =
//                LayoutInflater.from(context).inflate(R.layout.item_article, parent, false)
//        }
//
//        if (convertView2 != null) {
//            article?.let { foundArticle ->
//                convertView2.rootView.article_name.text = foundArticle.title
//                convertView2.rootView.article_description.text = foundArticle.description
//                convertView2.rootView.article_author.text = foundArticle.author
//                convertView2.rootView.article_date.text =
//                    dateFormat.format(parseFormat.parse(foundArticle.publishedAt))
//                convertView2.setOnClickListener { listener.onClick(foundArticle) }
//            }
//        }
//
//        return convertView2 ?: LayoutInflater.from(context).inflate(
//            R.layout.item_article,
//            parent,
//            false
//        )
//    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleListAdapterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleListAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleListAdapterViewHolder, position: Int) {
        val article = dataset[position]

        holder.view.setOnClickListener { listener.onClick(article) }

        holder.view.article_name.text = article.title
        holder.view.article_date.text = dateFormat.format(parseFormat.parse(article.publishedAt))
        holder.view.article_author.text = article.author
        holder.view.article_description.text = article.description
    }


    fun add(articles: List<Article>) {
        dataset.addAll(articles)
    }

    fun clear() {
        dataset.clear()
    }

    class ArticleListAdapterViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    interface SourceListAdapterItemListener {

        fun onClick(article: Article)

    }
}