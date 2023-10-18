package br.com.igorbag.githubsearch.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    lateinit var nomeUsuario: EditText
    lateinit var btnConfirmar: Button
    lateinit var listaRepositories: RecyclerView
    lateinit var githubApi: GitHubService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        showUserName()
        setupRetrofit()
        getAllReposByUserName()
    }

    // Metodo responsavel por realizar o setup da view e recuperar os Ids do layout
    fun setupView() {
        nomeUsuario = findViewById(R.id.et_nome_usuario)
        btnConfirmar = findViewById(R.id.btn_confirmar)
        listaRepositories = findViewById(R.id.rv_lista_repositories)
        setupListeners()
    }

    //metodo responsavel por configurar os listeners click da tela
    private fun setupListeners() {
        btnConfirmar.setOnClickListener {
            saveUserLocal()
        }
    }


    // salvar o usuario preenchido no EditText utilizando uma SharedPreferences
    private fun saveUserLocal() {
        val usuario = nomeUsuario.text.toString()
        val sharedPreferences = getSharedPreferences("MinhasPreferencias", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("usuario", usuario)
        editor.apply()
    }

    private fun showUserName() {
        val sharedPreferences = getSharedPreferences("MinhasPreferencias", MODE_PRIVATE)
        val usuarioSalvo = sharedPreferences.getString("usuario", "")
        nomeUsuario.setText(usuarioSalvo)
    }

    //Metodo responsavel por fazer a configuracao base do Retrofit
    fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        githubApi = retrofit.create(GitHubService::class.java)
    }

    //Metodo responsavel por buscar todos os repositorios do usuario fornecido
    fun getAllReposByUserName() {
        val userName = nomeUsuario.text.toString()

        // Realizar a chamada à API do GitHub
        githubApi.getAllRepositoriesByUser(userName).enqueue(object : Callback<List<Repository>> {
            override fun onResponse(
                call: Call<List<Repository>>,
                response: Response<List<Repository>>
            ) {
                if (response.isSuccessful) {
                    // A resposta da API foi bem-sucedida
                    val repositories = response.body()
                    if (repositories != null) {
                        // Chame o método setupAdapter para configurar o RecyclerView com a lista de repositórios
                        setupAdapter(repositories)
                    }
                } else {
                    Log.e("erro", "Desculpe, mas deu erro!!")
                }
            }

            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                Log.e("MinhaActivity", "Erro na solicitação: ${t.message}")
            }
        })
    }

    // Metodo responsavel por realizar a configuracao do adapter
    fun setupAdapter(list: List<Repository>) {
        val adapter = RepositoryAdapter(list)
        adapter.gitItemLister = { repository ->
            Log.d("MainActivity", "Item do repositório clicado: ${repository.name}")
        }

        adapter.btnShareLister = { repository ->
            shareRepositoryLink((repository.htmlUrl))
        }

        adapter.openBrowserListener = { repository ->
            openBrowser(repository.htmlUrl)
        }

        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        listaRepositories.addItemDecoration(dividerItemDecoration)
        listaRepositories.adapter = adapter
    }


    // Metodo responsavel por compartilhar o link do repositorio selecionado
    fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    // Metodo responsavel por abrir o browser com o link informado do repositorio

    fun openBrowser(urlRepository: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )

    }

}