package com.geminy.productshow.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.geminy.productshow.Activity.GEMINY_ProductDetailInfosActivity;
import com.geminy.productshow.DAO.GEMINY_ProductDAO;
import com.geminy.productshow.Model.GEMINY_Tb_product;
import com.geminy.productshow.R;

/**
 * Created by Hatsune Miku on 2015/8/6.
 */
public class GEMINY_CharacterUIFragment extends Fragment{

    private ListView listView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.geminy_fragment_selection_character,container,false);
        listView=(ListView)rootView.findViewById(R.id.list_character);
        initView();

        return rootView;
    }
    private void initView(){

        GEMINY_ProductDAO GEMINYProductDAO =new GEMINY_ProductDAO(getActivity());
        Bundle bundle=getArguments();
        final String pro_name=bundle.getString(GEMINY_ProductDetailInfosActivity.PRODUCT_NAME, "");
        GEMINY_Tb_product tb_product= GEMINYProductDAO.getByName(pro_name);
        String pro_character=tb_product.getCharacter();
        String[] character=pro_character.split(";");
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,character);
        listView.setAdapter(arrayAdapter);
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
