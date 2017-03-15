package br.com.pwdinfo.consumindorest.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.pwdinfo.consumindorest.R;
import br.com.pwdinfo.consumindorest.modelo.Produto;

/**
 * Created by Marcelo on 31/10/2016.
 */
public class ProdutoAdapter extends BaseAdapter {
    private Context context;

    private ArrayList<Produto> listaProduto;
    private LayoutInflater inflater;

    public TextView tvIdProduto;
    public TextView tvNome;
    public TextView tvValorCusto;
    public TextView tvQuantidade;

    public ProdutoAdapter(Context context, ArrayList<Produto> listaProduto) {
        //super();
        this.context = context;
        this.listaProduto = listaProduto;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public void notifyDataSetChanged() {
        // TODO Auto-generated method stub
        try {
            super.notifyDataSetChanged();
        } catch (Exception e) {
            // TODO: handle exception
            trace("Erro : " + e.getMessage());
        }
    }

    public void toast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    private void trace(String msg) {
        toast(msg);
    }

    public void add(Produto prod) {
        listaProduto.add(prod);
    }

    public void remove(Produto prod) {
        listaProduto.remove(prod);
    }

    @Override
    public int getCount() {
        return listaProduto.size();
    }

    @Override
    public Produto getItem(int position) {
        return listaProduto.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            Produto prod = listaProduto.get(position);

            if (convertView == null)
                convertView = inflater.inflate(R.layout.lista_produtos, null);

            tvIdProduto = (TextView) convertView.findViewById(R.id.tvIdProduto);
            tvNome = (TextView) convertView.findViewById(R.id.tvNome);
            tvValorCusto = (TextView) convertView.findViewById(R.id.tvValorCusto);
            tvQuantidade = (TextView) convertView.findViewById(R.id.tvQuantidade);

            tvIdProduto.setText("Código: " + prod.getIdProduto());
            tvNome.setText("Nome: " + prod.getNome());
            tvValorCusto.setText("Valor Custo: R$ " + prod.getValorCusto());
            tvQuantidade.setText("Quantidade: " + prod.getQuantidade());

            return convertView;
        } catch (Exception e) {
            trace("Erro : " + e.getMessage());
        }
        return convertView;
    }
}
