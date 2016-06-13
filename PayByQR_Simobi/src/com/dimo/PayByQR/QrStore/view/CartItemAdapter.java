package com.dimo.PayByQR.QrStore.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dimo.PayByQR.QrStore.constans.QrStoreDefine;
import com.dimo.PayByQR.QrStore.model.GoodsData;
import com.dimo.PayByQR.QrStore.utility.ImageLoader;
import com.dimo.PayByQR.QrStore.utility.QRStoreDBUtil;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.utils.DIMOUtils;
import com.dimo.PayByQR.utils.imagecache.ImageCache;
import com.dimo.PayByQR.utils.imagecache.ImageFetcher;

import java.util.ArrayList;

/**
 * Created by san on 1/13/16.
 */
public class CartItemAdapter extends BaseAdapter {
    private ArrayList<GoodsData> cartArraylist;
    private Context ctx;
    private LayoutInflater inflater;
    private ImageFetcher mImageFetcher;
    private Handler onItemDeleted;
    private int maxQtyToAdd;
    private boolean isInEditMode = false;

    public CartItemAdapter(Context ctx, ArrayList<GoodsData> cartlist, Handler onItemDeleted) {
        this.cartArraylist = cartlist;
        this.ctx = ctx;
        this.onItemDeleted = onItemDeleted;
        inflater = LayoutInflater.from(ctx);

        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(ctx, QrStoreDefine.IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        mImageFetcher = new ImageFetcher(ctx, 300);
        mImageFetcher.setLoadingImage(R.drawable.loyalty_list_no_image);
        mImageFetcher.addImageCache(((FragmentActivity) ctx).getSupportFragmentManager(), cacheParams);
    }

    @Override
    public int getCount() {
       return cartArraylist.size();
    }

    @Override
    public Object getItem(int position) {
        return cartArraylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if(null == convertView)
            vi = inflater.inflate(R.layout.qr_cart_each, null);

        ImageButton aButton = (ImageButton) vi.findViewById(R.id.imageButton);
        final ImageView imageView = (ImageView) vi.findViewById(R.id.item_cart_image);
        TextView txtDiscAmount = (TextView) vi.findViewById(R.id.item_cart_discount_amount);
        LinearLayout discountBlock = (LinearLayout) vi.findViewById(R.id.item_cart_discount_block);
        TextView txtItemName = (TextView) vi.findViewById(R.id.item_cart_name);
        final TextView txtItemQty = (TextView) vi.findViewById(R.id.item_cart_qty);
        final TextView txtPaidAmount = (TextView) vi.findViewById(R.id.item_cart_paidAmount);
        TextView txtOriginalAmount = (TextView) vi.findViewById(R.id.item_cart_originalAmount);
        TextView txtDiscountedAmount = (TextView) vi.findViewById(R.id.item_cart_discountedAmount);
        ImageView btnPlus = (ImageView) vi.findViewById(R.id.item_cart_qty_btn_plus);
        ImageView btnMinus = (ImageView) vi.findViewById(R.id.item_cart_qty_btn_minus);
        RelativeLayout originalAmountBlock = (RelativeLayout) vi.findViewById(R.id.item_cart_originalAmount_layout);

        final GoodsData goodsData = cartArraylist.get(position);
        Float amtDisc = new Float(goodsData.discountAmount);
        float disc = (amtDisc/(float)goodsData.price) * 100;

        if(disc > 0) {
            txtDiscAmount.setText((int) disc + "%");
            discountBlock.setVisibility(View.VISIBLE);
            originalAmountBlock.setVisibility(View.VISIBLE);
            txtOriginalAmount.setVisibility(View.VISIBLE);
        }else{
            discountBlock.setVisibility(View.GONE);
            originalAmountBlock.setVisibility(View.GONE);
            txtOriginalAmount.setVisibility(View.GONE);
        }

        txtItemName.setText(goodsData.goodsName);
        txtPaidAmount.setText(ctx.getString(R.string.text_detail_currency) + " " + DIMOUtils.formatAmount(Integer.toString(goodsData.qtyInCart * (int) (goodsData.price-goodsData.discountAmount))));
        txtOriginalAmount.setText(ctx.getString(R.string.text_detail_currency) + " " + DIMOUtils.formatAmount(Integer.toString((int) (goodsData.price))));
        txtDiscountedAmount.setText(ctx.getString(R.string.text_detail_currency) + " " + DIMOUtils.formatAmount(Integer.toString((int) (goodsData.price-goodsData.discountAmount))) + " " + ctx.getText(R.string.tx_store_per_item));
        txtItemQty.setText("" + goodsData.qtyInCart);

        mImageFetcher.loadImage(goodsData.image_url, imageView);
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        else
                            imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                        mImageFetcher.setImageSize(imageView.getWidth());
                        mImageFetcher.loadImage(goodsData.image_url, imageView);
                    }
                });

        if(isInEditMode) aButton.setVisibility(View.VISIBLE);
        else aButton.setVisibility(View.GONE);

        aButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DIMOUtils.showAlertDialog(ctx, null, ctx.getString(R.string.tx_qr_delete, goodsData.goodsName),
                            ctx.getString(R.string.alertdialog_posBtn_hapus),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    QRStoreDBUtil.removeGoodsFromCartsByMerchant(ctx, goodsData.id, goodsData.merchantCode);

                                    dialog.dismiss();
                                    Toast.makeText(ctx, ctx.getString(R.string.text_cart_item_deleted, goodsData.goodsName), Toast.LENGTH_SHORT).show();
                                    Message message = onItemDeleted.obtainMessage(Constant.MESSAGE_END_OK);
                                    onItemDeleted.sendMessage(message);
                                }
                            }, ctx.getString(R.string.alertdialog_posBtn_batal), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemQtyChange(goodsData, true, txtItemQty, txtPaidAmount);
            }
        });
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemQtyChange(goodsData, false, txtItemQty, txtPaidAmount);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, StoreDetailActivity.class);
                intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_MERCHANTID, goodsData.merchantCode);
                intent.putExtra(QrStoreDefine.INTENT_EXTRA_QRSTORE_CART_GOODSID, goodsData.id);
                ctx.startActivity(intent);
            }
        });

        return vi;
    }

    public void setCartArrayList(ArrayList<GoodsData> cartArrayList){
        this.cartArraylist = cartArrayList;
        notifyDataSetChanged();
    }

    public void onItemQtyChange(GoodsData goodsData, boolean isAdd, TextView txtQuantity, TextView txtPaidAmount) {
        if (isAdd) {
            /*1. stock = 0; maxQuantity = any (0-n)
            -> Maaf, stok barang habis
            2. stock = m; maxQuantity = n; stock < maxQuantity
            eg: stock = 5; maxQuantity = 10
            -> Stok yang tersedia untuk barang ini adalah 5
            3. stock = m; maxQuantity = n; stock > maxQuantity
            eg: stock = 10; maxQuantity = 7
            -> Jumlah maksimal yang bisa Anda beli untuk barang ini adalah 7*/

            if(goodsData.maxQuantity == 0){
                maxQtyToAdd = goodsData.stock;
                if (goodsData.qtyInCart < maxQtyToAdd) {
                    goodsData.qtyInCart++;
                } else {
                    String errorMsg = ctx.getString(R.string.error_max_stock, goodsData.stock);
                    if(goodsData.qtyInCart > 0)
                        errorMsg = errorMsg + ctx.getString(R.string.error_max_goods_in_cart, goodsData.qtyInCart);

                    DIMOUtils.showAlertDialog(ctx, null, errorMsg, ctx.getString(R.string.alertdialog_posBtn_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, null, null);
                }
            }else {
                maxQtyToAdd = Math.min(goodsData.stock, goodsData.maxQuantity);
                if (goodsData.qtyInCart < maxQtyToAdd) {
                    goodsData.qtyInCart++;
                } else {
                    String errorMsg = "";
                    if(goodsData.stock < goodsData.maxQuantity){
                        errorMsg = ctx.getString(R.string.error_max_stock, goodsData.stock);
                    }else{
                        errorMsg = ctx.getString(R.string.error_max_qty, goodsData.maxQuantity);
                    }

                    DIMOUtils.showAlertDialog(ctx, null, errorMsg, ctx.getString(R.string.alertdialog_posBtn_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, null, null);
                }
            }
        } else {
            if (goodsData.qtyInCart > 1)
                goodsData.qtyInCart--;
        }

        QRStoreDBUtil.addGoodsToCart(ctx, goodsData);

        txtQuantity.setText(String.valueOf(goodsData.qtyInCart));
        txtPaidAmount.setText(ctx.getString(R.string.text_detail_currency) + " " + DIMOUtils.formatAmount(Integer.toString(goodsData.qtyInCart * (int) (goodsData.price-goodsData.discountAmount))));
        Message message = onItemDeleted.obtainMessage(Constant.MESSAGE_END_ERROR);
        onItemDeleted.sendMessage(message);
    }

    public void setEditMode(boolean isInEditMode){
        this.isInEditMode = isInEditMode;
        notifyDataSetChanged();
    }

    public boolean isInEditMode(){
        return isInEditMode;
    }
}
