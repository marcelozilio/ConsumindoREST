package br.com.pwdinfo.consumindorest.util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Marcelo on 01/11/2016.
 */
public class TesteConexao {

    public static boolean getConexao(Context context) {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        conectado = conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected();
        return conectado;
    }
}
