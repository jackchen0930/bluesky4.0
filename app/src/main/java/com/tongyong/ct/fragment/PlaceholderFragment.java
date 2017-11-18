package com.tongyong.ct.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.tongyong.ct.activity.TabMainActivity;
import com.tongyong.ct.bean.DemoBean;
import com.tongyong.ct.bluesky.MainActivity;
import com.tongyong.ct.R;
import com.tongyong.ct.listener.OnLoadNextListener;
import com.tongyong.ct.mycore.afinal.FinalBitmap;
import com.tongyong.ct.mycore.googlevolley.Request;
import com.tongyong.ct.mycore.googlevolley.RequestQueue;
import com.tongyong.ct.mycore.googlevolley.Response;
import com.tongyong.ct.mycore.googlevolley.VolleyRequestManager;
import com.tongyong.ct.mycore.googlevolley.error.VolleyError;
import com.tongyong.ct.mycore.googlevolley.request.GsonRequest;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by Chentao on 15/12/29.
 */
public class PlaceholderFragment extends Fragment implements OnLoadNextListener,SwipeRefreshLayout.OnRefreshListener ,View.OnClickListener{


    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    View rootView;
    //TextView tv_show;
    ImageView igv_show;
    //android.support.v7.widget.RecyclerView recyclerview;
    //private BaseSwipeRefreshLayout swipeRefreshLayout;
    String url_get="http://www.cnperfect.cn/testjson.php";
    Button btn_viewpaper;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //tv_show=(TextView)rootView.findViewById(R.id.section_label);
        igv_show=(ImageView)rootView.findViewById(R.id.igv_show);
        btn_viewpaper=(Button)rootView.findViewById(R.id.btn_viewpaper);
        btn_viewpaper.setOnClickListener(this);
        //recyclerview=(android.support.v7.widget.RecyclerView)rootView.findViewById(R.id.recyclerview);
        /*GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setAdapter(new ColorItemsAdapter(getActivity(), 78));*/





        /*swipeRefreshLayout = (BaseSwipeRefreshLayout)rootView.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);*/

        String imgurl1="http://img1.gtimg.com/news/pics/hv1/115/103/1997/129881305.jpg";
        String imgurl2="https://butterfly.b0.upaiyun.com/demo/sample_md5.jpeg";
        FinalBitmap.create(getActivity()).display(igv_show,imgurl1 );
        //LoadData(url_get);


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }


    void LoadData(String url){
        /*if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }*/
        VolleyRequestManager.init(getActivity());
        RequestQueue queue = VolleyRequestManager.getRequestQueue();
        GsonRequest<DemoBean> myReq = new GsonRequest<DemoBean>(Request.Method.GET,
                url,
                DemoBean.class,
                null,
                new Response.Listener<DemoBean>() {
                    @Override
                    public void onResponse(DemoBean response) {

                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                //swipeRefreshLayout.setRefreshing(false);
                            }

                        }, 3000);

                        if(TextUtils.isEmpty(response.getRow().get(0).getName())){
                            //tv_show.setText("无数据");
                        }else{
                            //tv_show.setText(response.getRow().get(0).getName());
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "异常网络", LENGTH_SHORT).show();
                    }
                });

        queue.add(myReq);
    }


    @Override
    public void onLoadNext() {

    }

    @Override
    public void autoShowOrHideToolbar(boolean show) {

    }

    @Override
    public void onRefresh() {

        // LoadData(url_get);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_viewpaper:

                Intent it=new Intent();
                it.setClass(getActivity(), TabMainActivity.class);
                startActivity(it);
                break;

            default:

                break;
        }

    }

    private class RecyclerView {
    }
}
