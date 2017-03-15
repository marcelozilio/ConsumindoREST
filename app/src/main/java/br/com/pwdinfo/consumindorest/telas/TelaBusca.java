package br.com.pwdinfo.consumindorest.telas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.pwdinfo.consumindorest.R;
import br.com.pwdinfo.consumindorest.modelo.Produto;
import br.com.pwdinfo.consumindorest.util.Http;
import br.com.pwdinfo.consumindorest.util.ProdutoAdapter;
import br.com.pwdinfo.consumindorest.util.TesteConexao;

public class TelaBusca extends AppCompatActivity {

    private String jsonProd;
    private Gson gson = new Gson();
    private Http http = new Http();

    private ArrayList<Produto> prodAux;

    private ProgressDialog dialog;

    private ProdutoAdapter adapter;
    private ListView listViewProds;

    ImageView imageView;
    TextView tvConexao;
    Button btnTentar;

    private int posSelec = -1;

    private static final int ALTERAR = 0;
    private static final int DELETAR = 1;

    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_tela_busca);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listViewProds = (ListView) findViewById(R.id.lvProdutos);
        imageView = (ImageView) findViewById(R.id.imgViewWarning);
        tvConexao = (TextView) findViewById(R.id.tvConexao);
        btnTentar = (Button) findViewById(R.id.btnTentar);

        if (TesteConexao.getConexao(TelaBusca.this)) {
            header();
        } else {
            listViewProds.setVisibility(View.INVISIBLE);
            /*Chama outros widgtes*/
            imageView.setVisibility(View.VISIBLE);
            tvConexao.setVisibility(View.VISIBLE);
            btnTentar.setVisibility(View.VISIBLE);

            btnTentar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TesteConexao.getConexao(TelaBusca.this)) {
                        header();
                    } else {
                        msg("Sem conexão", Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    private void header() {
        /*Chama listView*/
        listViewProds.setVisibility(View.VISIBLE);
        /*Esconde outros widgtes*/
        imageView.setVisibility(View.GONE);
        tvConexao.setVisibility(View.GONE);
        btnTentar.setVisibility(View.GONE);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            new DownloadJson().execute(Http.LISTAR);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }


        listViewProds.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                /*Guardando a posição escolhida pelo usuário*/
                posSelec = position;
                return false;
            }
        });
        /*Registrando menu de contexto para a ListView*/
        registerForContextMenu(listViewProds);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Opções");
        menu.addSubMenu(DELETAR, DELETAR, 100, "Deletar");
        menu.addSubMenu(ALTERAR, ALTERAR, 200, "Alterar");
    }//fecha onCreateContextMenu

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETAR:
                final AlertDialog.Builder msg = new AlertDialog.Builder(TelaBusca.this);
                msg.setTitle("Atenção");
                msg.setMessage("Você tem certeza que deseja excluir?");
                msg.setIcon(R.mipmap.ic_launcher);
                msg.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*Resgatando o produto selecionado pelo usuario*/
                        Produto p = prodAux.get(posSelec);
                        try {
                            Http.DELETAR += p.getIdProduto();
                            flag = Boolean.parseBoolean(new Http().get(Http.DELETAR, "DELETE"));

                            if (flag) {
                                msg("Produto deletado", Toast.LENGTH_SHORT);
                                /*Removendo do ArrayList*/
                                prodAux.remove(p);
                                /*Notificando o adapter*/
                                adapter.notifyDataSetChanged();
                            } else {
                                msg("Produto não foi deletado", Toast.LENGTH_SHORT);
                            }
                        } catch (IOException e) {
                            msg("Não foi possível deletar o produto.\n Verifique sua conexão.", Toast.LENGTH_SHORT);
                        }
                    }
                });
                msg.setNeutralButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        msg("Produto não foi deletado", Toast.LENGTH_SHORT);
                    }
                });
                msg.show();
                break;
            case ALTERAR:
                /*Resgatando o produto selecionado pelo usuario*/
                Produto p = prodAux.get(posSelec);
                //Enviando para tela de cadastro (alterar)
                Intent it = new Intent(TelaBusca.this, TelaCadastro.class);
                it.putExtra("p", p);
                it.putExtra("acao", "alterar");
                startActivity(it);
                finish();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    class DownloadJson extends AsyncTask<String, Void, List<Produto>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(TelaBusca.this, "Aguarde", "Buscando produtos...");
        }

        @Override
        protected List<Produto> doInBackground(String... params) {
            try {
                jsonProd = http.get(params[0], "GET");
            } catch (RuntimeException | IOException e) {
                Log.e("doInBackgorund", e.getMessage());
            }
            return getProdutos(jsonProd);
        }

        @Override
        protected void onPostExecute(List<Produto> produtos) {
            super.onPostExecute(produtos);
            dialog.dismiss();
            if (produtos.size() > 0) {
                adapter = new ProdutoAdapter(TelaBusca.this, (ArrayList<Produto>) produtos);
                listViewProds.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                listViewProds.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            } else {
                msg("Não foi possível acessar as informações", Toast.LENGTH_SHORT);
            }
        }

        /**
         * Converte o json numa lista de objetos com a lib Gson
         *
         * @param jsonProd
         * @return produtos
         */
        private List<Produto> getProdutos(String jsonProd) {
            Produto[] prods = gson.fromJson(jsonProd, Produto[].class);

            List<Produto> produtos = new ArrayList<>();

            for (Produto p : prods) {
                produtos.add(p);
            }
            prodAux = (ArrayList<Produto>) produtos;

            return produtos;
        }

    }

    private void msg(String msg, int duracao) {
        Toast.makeText(getBaseContext(), msg, duracao).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent(TelaBusca.this, TelaInicial.class);
        startActivity(it);
        finish();
    }


}