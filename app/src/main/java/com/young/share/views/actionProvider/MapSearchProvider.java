package com.young.share.views.actionProvider;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ActionProvider;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.young.share.R;

/**
 * 百度地图搜索的toolbar中的Actionprovider
 * <p/>
 * Created by Nearby Yang on 2016-03-13.
 */
public class MapSearchProvider extends ActionProvider {

    private View view;
    private SearchButtonClick searchButtonClick;
    private TextChangeListener textChangeListener;
    public SearchView searchView;
    private String queryTxt;
    public boolean isShow = false;

    /**
     * Creates a new instance.
     *
     * @param context Context for accessing resources.
     */
    public MapSearchProvider(Context context) {
        super(context);
        view = LayoutInflater.from(context).inflate(R.layout.menu_map_search_provider, null);
    }

    @Override
    public View onCreateActionView() {

        searchView = (SearchView) view.findViewById(R.id.sv_map_search_place);
        SearchView.SearchAutoComplete text = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        text.setTextColor(Color.WHITE);

        final Button searchBtn = (Button) view.findViewById(R.id.btn_map_search);
        searchBtn.setEnabled(false);
        searchBtn.setVisibility(View.GONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryTxt = query;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchBtn.setEnabled(!TextUtils.isEmpty(newText));
                queryTxt = newText;
                if (textChangeListener!=null){
                    textChangeListener.textChange(newText);
                }
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchBtn.setVisibility(View.GONE);
                isShow = false;
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBtn.setVisibility(View.VISIBLE);
                isShow = true;
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchButtonClick.search(queryTxt);
            }
        });

        return view;

    }


    /**
     * 搜索触发
     *
     * @param searchButtonClick
     */
    public void setSearchButtonClick(SearchButtonClick searchButtonClick) {
        this.searchButtonClick = searchButtonClick;
    }

    /**
     * 输入监听
     * @param textChangeListener
     */
    public void setTextChangeListener(TextChangeListener textChangeListener) {
        this.textChangeListener = textChangeListener;
    }

    public interface SearchButtonClick {
        void search(String query);
    }

    public interface TextChangeListener{
        void textChange(String query);
    }
}
