package com.gzligo.ebizzcardstranslator.business.chat.product;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.widget.recyclerview.BaseAdapter;
import com.gzligo.ebizzcardstranslator.business.chat.PreImageActivity;
import com.gzligo.ebizzcardstranslator.net.httputil.HttpUtils;
import com.gzligo.ebizzcardstranslator.persistence.ChatMsgProperty;
import com.gzligo.ebizzcardstranslator.persistence.ProductDetail;
import com.gzligo.ebizzcardstranslator.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lwd on 2017/9/11.
 */

public class PreProductPagerAdapter extends PagerAdapter {
    private List<List<ProductDetail>> preProducts;
    private ProductDetailAdapter productDetailAdapter;
    private Context context;

    public PreProductPagerAdapter(List<List<ProductDetail>> preProducts, Context context) {
        this.preProducts = preProducts;
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.pre_product, null, false);
        RecyclerView productRecyclerView = (RecyclerView) view.findViewById(R.id.product_recycler_view);
        final List<ProductDetail> productDetails = preProducts.get(position);
        productDetailAdapter = new ProductDetailAdapter(productDetails);
        productRecyclerView.setAdapter(productDetailAdapter);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        ViewGroup parent = (ViewGroup) view.getParent();
        if (null!=parent) {
            parent.removeView(view);
        }
        container.addView(view);
        productDetailAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener<ProductDetail>() {
            @Override
            public void onItemClick(View view, int viewType, ProductDetail data, int position) {
                if(ProductDetail.TYPE_IMG_COVER==data.type||ProductDetail.TYPE_IMG_DETAIL==data.type){
                    ArrayList<ChatMsgProperty> chatMsgProperties = new ArrayList<>();
                    int pos = 0;
                    for(int i=0;i<productDetails.size();i++){
                        ProductDetail productDetail = productDetails.get(i);
                        if(ProductDetail.TYPE_IMG_COVER==productDetail.type||ProductDetail.TYPE_IMG_DETAIL==productDetail.type){
                            String mediaId = productDetail.mediaId;
                            if(!TextUtils.isEmpty(mediaId)){
                                ChatMsgProperty chatMsgProperty = new ChatMsgProperty();
                                int type = productDetail.imageType;
                                if(type==1){
                                    chatMsgProperty.setType(1);
                                    chatMsgProperty.setFileUrl(HttpUtils.MEDIA_HOST + mediaId);
                                }else{
                                    chatMsgProperty.setType(2);
                                    String url = CommonUtils.formatMediaUrl(mediaId);
                                    chatMsgProperty.setFileUrl(url);
                                }
                                chatMsgProperties.add(chatMsgProperty);
                                if(data.mediaId.equals(productDetail.mediaId)){
                                    pos = chatMsgProperties.size();
                                }
                            }
                        }
                    }
                    Intent intent = new Intent(context, PreImageActivity.class);
                    intent.putExtra("CHAT_MSG_LIST", chatMsgProperties);
                    intent.putExtra("POSITION",pos-1);
                    context.startActivity(intent);
                }
            }
        });
        return view;
    }

    @Override
    public int getCount() {
        return preProducts.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
