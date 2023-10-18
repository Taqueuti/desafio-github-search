package br.com.igorbag.githubsearch.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter(private val repositories: List<Repository>) :
    RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    var gitItemLister: (Repository) -> Unit = {}
    var btnShareLister: (Repository) -> Unit = {}
    var openBrowserListener: (Repository) -> Unit = {}

    // Cria uma nova view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
        return ViewHolder(view)
    }

    // Pega o conteudo da view e troca pela informacao de item de uma lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repository = repositories[position]

        holder.name.text = repository.name

        holder.itemView.setOnClickListener {
            gitItemLister(repository)
        }

        holder.btnShare.setOnClickListener {
            btnShareLister(repository)
        }

        holder.ivFavorite.setOnClickListener {
            openBrowserListener(repository)
        }

    }

    // Pega a quantidade de repositorios da lista
    override fun getItemCount(): Int = repositories.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val btnShare: ImageView = view.findViewById(R.id.cl_card_content)
        val ivFavorite: ImageView = view.findViewById(R.id.cl_card_content)

    }
}