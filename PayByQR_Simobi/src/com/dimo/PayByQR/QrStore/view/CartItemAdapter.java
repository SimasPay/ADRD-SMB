package com.dimo.PayByQR.QrStore.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.PayByQRSDK;
import com.dimo.PayByQR.QrStore.constans.QrStoreDefine;
import com.dimo.PayByQR.QrStore.model.Cart;
import com.dimo.PayByQR.QrStore.model.GoodsData;
import com.dimo.PayByQR.QrStore.utility.ImageLoader;
import com.dimo.PayByQR.QrStore.utility.QRStoreDBUtil;
import com.dimo.PayByQR.QrStore.utility.QrStoreUtil;
import com.dimo.PayByQR.QrStore.utility.UtilDb;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.utils.DIMOUtils;

import java.util.ArrayList;
import java.util.List;

import static com.dimo.PayByQR.QrStore.utility.QrStoreUtil.displayMenu;

/**
 * Created by san on 1/13/16.
 */
public class CartItemAdapter extends BaseAdapter {
    private ArrayList<GoodsData> cartArraylist;
    private Context ctx;
    private LayoutInflater inflater;
    private ImageLoader imgLoader;
    private Handler onItemDeleted;

    public CartItemAdapter(Context ctx, ArrayList<GoodsData> cartlist, Handler onItemDeleted) {
        this.cartArraylist = cartlist;
        this.ctx = ctx;
        this.onItemDeleted = onItemDeleted;
        inflater = LayoutInflater.from(ctx);

        imgLoader = new ImageLoader(ctx);
        imgLoader.setIsScale(false);
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
        ImageView imageView = (ImageView) vi.findViewById(R.id.item_cart_image);
        TextView txtDiscAmount = (TextView) vi.findViewById(R.id.item_cart_discount_amount);
        LinearLayout discountBlock = (LinearLayout) vi.findViewById(R.id.item_cart_discount_block);
        TextView txtItemName = (TextView) vi.findViewById(R.id.item_cart_name);
        TextView txtItemQty = (TextView) vi.findViewById(R.id.item_cart_qty);
        TextView txtPaidAmount = (TextView) vi.findViewById(R.id.item_cart_paidAmount);
        TextView txtOriginalAmount = (TextView) vi.findViewById(R.id.item_cart_originalAmount);

        final GoodsData goodsData = cartArraylist.get(position);
        Float amtDisc = new Float(goodsData.discountAmount);
        float disc = (amtDisc/(float)goodsData.price) * 100;

        if(disc > 0) {
            discountBlock.setVisibility(View.VISIBLE);
            txtDiscAmount.setText((int) disc + "%");
        }else{
            discountBlock.setVisibility(View.GONE);
        }

        txtItemName.setText(goodsData.goodsName);
        txtPaidAmount.setText(ctx.getString(R.string.text_detail_currency) + " " + DIMOUtils.formatAmount(Integer.toString(goodsData.qtyInCart * (int) (goodsData.price-goodsData.discountAmount))));
        txtOriginalAmount.setText(goodsData.qtyInCart+" x "+ctx.getString(R.string.text_detail_currency) + " " + DIMOUtils.formatAmount(Integer.toString((int) (goodsData.price-goodsData.discountAmount))));
        txtItemQty.setText("" + goodsData.qtyInCart);

        imgLoader.DisplayImage(goodsData.image_url, R.drawable.loyalty_list_no_image, imageView);

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
                                    Toast.makeText(ctx, "" + goodsData.goodsName + " telah di hapus dari keranjang belanja", Toast.LENGTH_SHORT).show();
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

        return vi;
    }

    public void setCartArrayList(ArrayList<GoodsData> cartArrayList){
        this.cartArraylist = cartArrayList;
        notifyDataSetChanged();
    }
}
