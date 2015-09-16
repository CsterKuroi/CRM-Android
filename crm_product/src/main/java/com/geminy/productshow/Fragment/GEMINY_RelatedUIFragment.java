package com.geminy.productshow.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.geminy.productshow.Activity.GEMINY_ProductDetailInfosActivity;
import com.geminy.productshow.DAO.GEMINY_ProductDAO;
import com.geminy.productshow.Model.GEMINY_Tb_product;
import com.geminy.productshow.R;
import com.geminy.productshow.GEMINY_mAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hatsune Miku on 2015/8/9.
 */
public class GEMINY_RelatedUIFragment extends Fragment{
    private ListView listView;
    private View rootView;
    public static final String PRO_NAME="pro_name";
    public static final String UID="UID";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(listView==null){
        rootView=inflater.inflate(R.layout.geminy_fragment_selection_related,container,false);}
        ViewGroup parent=(ViewGroup)rootView.getParent();
        if(parent!=null){
            parent.removeView(rootView);
        }
        listView=(ListView)rootView.findViewById(R.id.list_related);
        initView();
        return rootView;
    }
    private void initView(){
        GEMINY_ProductDAO GEMINYProductDAO =new GEMINY_ProductDAO(getActivity());
        Bundle bundle=getArguments();
        final String pro_name=bundle.getString(GEMINY_ProductDetailInfosActivity.PRODUCT_NAME);
        final String uid=bundle.getString((GEMINY_ProductDetailInfosActivity.UID));
        GEMINY_Tb_product tb_product= GEMINYProductDAO.getByName(pro_name);
        String pro_related=tb_product.getRelated();
        String[] related=pro_related.split(";");
        List<String> names=new ArrayList<String>();
        List<String> types=new ArrayList<String>();
        List<String> imgIds=new ArrayList<String>();
        List<String> nums=new ArrayList<String>();
        for(int l=0;l<related.length;l++){
            GEMINY_Tb_product tb_related_pro= GEMINYProductDAO.getByName(related[l]);
            names.add(tb_related_pro.getName());
            types.add(tb_related_pro.getType());
            imgIds.add(tb_related_pro.getPicture());
            nums.add("库存"+tb_related_pro.getNum()+"个");
        }
        GEMINY_mAdapter adapter=new GEMINY_mAdapter(getActivity(),names,types,imgIds,nums);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view.findViewById(R.id.list_item_name);
                String pro_name = tv.getText().toString();
                Bundle bundle=new Bundle();
                String uid = getArguments().getString(UID);
                Intent intent=new Intent(getActivity(),GEMINY_ProductDetailInfosActivity.class);
                bundle.putString(PRO_NAME,pro_name);
                bundle.putString(UID,uid);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
