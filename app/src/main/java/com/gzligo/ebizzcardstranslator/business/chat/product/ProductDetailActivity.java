package com.gzligo.ebizzcardstranslator.business.chat.product;

import android.support.v4.view.ViewPager;

import com.gzligo.ebizzcardstranslator.R;
import com.gzligo.ebizzcardstranslator.base.BaseActivity;
import com.gzligo.ebizzcardstranslator.base.mvp.IPresenter;
import com.gzligo.ebizzcardstranslator.common.PreImageViewPager;
import com.gzligo.ebizzcardstranslator.common.ToolActionBar;
import com.gzligo.ebizzcardstranslator.persistence.ProductDetail;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ProductDetailActivity extends BaseActivity {

    @BindView(R.id.preview_actionbar) ToolActionBar previewActionbar;
    @BindView(R.id.pre_img_view_pager) PreImageViewPager preImageViewPager;
    private PreProductPagerAdapter productDetailAdapter;
    private int position;
    private int total;

    @Override
    public IPresenter createPresenter() {
        return null;
    }

    @Override
    public int onLayoutResId() {
        return R.layout.activity_product_detail;
    }

    @Override
    public void initData() {
        position = getIntent().getExtras().getInt("POSITION",0);
        ArrayList<List<ProductDetail>> productDetailList = (ArrayList<List<ProductDetail>>) getIntent().getSerializableExtra("PRODUCT_DETAILS");
        productDetailAdapter = new PreProductPagerAdapter(productDetailList,this);
        preImageViewPager.setAdapter(productDetailAdapter);
        total = productDetailList.size();
    }

    @Override
    public void initViews() {
        previewActionbar.setCenterTitle(position + "" + "/" + total);
        preImageViewPager.setCurrentItem(position-1);
        preImageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (total != -1) {
                    previewActionbar.setCenterTitle(position + 1 + "" + "/" + total);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void initEvents() {
    }

    @OnClick({R.id.tv_close})
    void click() {
        finish();
    }
}
