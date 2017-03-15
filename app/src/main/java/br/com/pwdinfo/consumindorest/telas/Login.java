package br.com.pwdinfo.consumindorest.telas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;

import br.com.pwdinfo.consumindorest.R;
import br.com.pwdinfo.consumindorest.modelo.Usuario;
import br.com.pwdinfo.consumindorest.util.Http;

public class Login extends AppCompatActivity implements Serializable, Runnable {

    private EditText etLogin;
    private EditText etSenha;
    private Button btnLogin;
    private TextView tvCriaConta;

    private ProgressDialog dialog;

    private Usuario u = new Usuario();
    private Http http = new Http();
    private Gson gson = new Gson();

    private Handler handler = new Handler();

    private boolean flag;
    private String json;

    public static String loginUsr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        etLogin = (EditText) findViewById(R.id.etLogin);
        etSenha = (EditText) findViewById(R.id.etSenha);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvCriaConta = (TextView) findViewById(R.id.tvCriaConta);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new ProgressDialog(Login.this);
                dialog.setMessage("Fazendo login, aguarde.");
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                Thread t = new Thread(Login.this);
                t.start();
            }
        });

        tvCriaConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Login.this, CadastroUsuario.class);
                startActivity(it);
                finish();
            }
        });
    }

    @Override
    public void run() {
        u.setLogin(etLogin.getText().toString());
        u.setSenha(etSenha.getText().toString());

        json = gson.toJson(u, Usuario.class);

        try {
            flag = http.post(Http.LOGIN, json, "POST");

            if (flag) {
                Intent it = new Intent(Login.this, TelaInicial.class);
                it.putExtra("login", u.getLogin());
                startActivity(it);
                finish();
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(), "Login ou senha incorretos, tente novamente.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (IOException e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Login.this, "Verifique sua conexão.", Toast.LENGTH_SHORT).show();
                }
            });

        } finally {
            dialog.dismiss();
        }
        loginUsr = u.getLogin();
    }
}
