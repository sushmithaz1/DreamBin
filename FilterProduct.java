package com.example.vegies;
import android.widget.Filter;

import com.example.vegies.adapter.AdapterProductSeller;
import com.example.vegies.adapter.AdapterProductUser;
import com.example.vegies.models.ModelProduct;

import java.util.ArrayList;

public class FilterProduct extends Filter{
    private AdapterProductSeller adapter;
    private ArrayList<ModelProduct> filterList;

    public FilterProduct(AdapterProductSeller adapter, ArrayList<ModelProduct> filterList){
        this.adapter=adapter;
        this.filterList=filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        //validation data for search query
        if(constraint!=null&& constraint.length()>0){
            //search filed not empty,searching something,perform search


            //change to uppecase to make it insensitive
            constraint=constraint.toString().toUpperCase();
            //store our filtered list
            ArrayList<ModelProduct> filteredModels=new ArrayList<>();
            for(int i=0;i<filterList.size();i++){
                //check
                if(filterList.get(i).getProductTitle().toUpperCase().contains(constraint)||
                        filterList.get(i).getProductCategory().toUpperCase().contains(constraint)){
                    //add filtered data tolist
                    filteredModels.add(filterList.get(i));

                }
            }
            results.count=filteredModels.size();
            results.values=filteredModels;
        }
        else{
            //search filed empty not searching,return original/all/complete list

            results.count=filterList.size();
            results.values=filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
    adapter.productList=(ArrayList<ModelProduct>) results.values;

//refreh adapter
        adapter.notifyDataSetChanged();
    }
}
