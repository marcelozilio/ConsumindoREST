package br.com.pwdinfo.consumindorest.telas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import br.com.pwdinfo.consumindorest.R;
import br.com.pwdinfo.consumindorest.modelo.Produto;
import br.com.pwdinfo.consumindorest.util.Http;

public class TelaCadastro extends AppCompatActivity implements Runnable {

    private EditText etCodigo;
    private EditText etNome;
    private EditText etValorCusto;
    private EditText etQuantidade;
    private Button btnAdicionar;
    private Button btnAlterar;

    private Produto p;

    private Gson gson = new Gson();
    private Http http = new Http();
    private String jsonProd;

    private Handler handler = new Handler();
    private ProgressDialog dialog;

    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);/*seta de back <-*/

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        etCodigo = (EditText) findViewById(R.id.etCodigo);
        etNome = (EditText) findViewById(R.id.etNome);
        etValorCusto = (EditText) findViewById(R.id.etValorCusto);
        etQuantidade = (EditText) findViewById(R.id.etQuantidade);
        btnAdicionar = (Button) findViewById(R.id.btnAdicionar);
        btnAlterar = (Button) findViewById(R.id.btnAlterar);

        btnAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new ProgressDialog(TelaCadastro.this);
                dialog.setMessage("Enviando produto, aguarde.");
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                new Thread(TelaCadastro.this).start();
            }
        });

        /**
         * Alterar
         * @see TelaBusca
         **/
        String acao = getIntent().getStringExtra("acao");

        if (acao != null) {
            /*Habilitando a visualização do botão alterar*/
            btnAlterar.setVisibility(View.VISIBLE);
            btnAdicionar.setVisibility(View.INVISIBLE);
            /*Desabilitando para a edição o campo do código*/
            etCodigo.setEnabled(false);

            Produto p = (Produto) getIntent().getSerializableExtra("p");
            etCodigo.setText(String.valueOf(p.getIdProduto()));
            etNome.setText(String.valueOf(p.getNome()));
            etValorCusto.setText(String.valueOf(p.getValorCusto()));
            etQuantidade.setText(String.valueOf(p.getQuantidade()));
        }

        /**Botão Alterar*/
        btnAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p = new Produto(
                        Long.parseLong(etCodigo.getText().toString()),
                        etNome.getText().toString(),
                        Double.parseDouble(etValorCusto.getText().toString()),
                        Long.parseLong(etQuantidade.getText().toString())
                );
                jsonProd = gson.toJson(p, Produto.class); //cria o json
                try {
                    flag = (http.post(Http.ALTERAR, jsonProd, "PUT"));
                    if (flag) {
                        msg("Produto alterado.", Toast.LENGTH_SHORT);
                        limparCampos();
                    } else {
                        msg("Produto não alterado.", Toast.LENGTH_SHORT);
                    }
                } catch (IOException e) {
                    msg("Não foi possível alterar o produto.\n" + e.getMessage(), Toast.LENGTH_LONG);
                }
                //Setando o botão para invisível novamente
                btnAlterar.setVisibility(View.INVISIBLE);
                btnAdicionar.setVisibility(View.VISIBLE);
                etCodigo.setEnabled(true);
            }
        });//fecha Alterar
    }

    @Override
    public void run() {
        try {
            //TODO criar validacoes de campos
            p = new Produto(
                    Long.parseLong(etCodigo.getText().toString()),
                    etNome.getText().toString(),
                    Double.parseDouble(etValorCusto.getText().toString()),
                    Long.parseLong(etQuantidade.getText().toString())
            );

            jsonProd = gson.toJson(p, Produto.class);
            flag = http.post(Http.INSERIR, jsonProd, "POST");

            if (flag) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        limparCampos();
                        msg("Produto Cadastrado\n" + p.toString(), Toast.LENGTH_LONG);
                    }
                });
            } else {
                msg("Produto não cadastrado", Toast.LENGTH_SHORT);
            }
        } catch (IOException e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    msg("Não foi possível cadastrar o produto.\n", Toast.LENGTH_LONG);
                }
            });
        } finally {
            dialog.dismiss();
        }
    }

    public void msg(String mensagem, int duracao) {
        Toast.makeText(getBaseContext(), mensagem, duracao).show();
    }

    private void limparCampos() {
        etCodigo.setText(null);
        etNome.setText(null);
        etValorCusto.setText(null);
        etQuantidade.setText(null);
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
        Intent it = new Intent(TelaCadastro.this, TelaInicial.class);
        startActivity(it);
        finish();
    }
}
