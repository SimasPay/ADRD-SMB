package com.dimo.PayByQR.QrStore.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.QrStore.model.Cart;
import com.dimo.PayByQR.QrStore.model.Merchant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by san on 1/18/16.
 */
public class UtilDb {


    public static String QR_STORE_DB = "QrStore.db.cuiy";


    public static void resetData (Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(QR_STORE_DB, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(QR_STORE_DB);
        editor.commit();
    }

    private static void setReload(Context context, List <Cart> listar)
    {
        resetData(context);
        saveListData(context, listar);
    }

    public static void updatePaidTransid(Context context, String transId)
    {
        List arlist=  readListData(context) ;

        if (arlist==null)
            return ;

        for (int i =0; i <arlist.size();i++)
        {
            Cart cart=(Cart) arlist.get(i);
            if (cart.getTrandId().equals(transId)) {
                {   arlist.remove(arlist.get(i));
                    cart.setIsPaid(true);
                    arlist.add(cart);
                }

            }

        }


        setReload(context, arlist);

    }

    public static HashMap <String, Merchant> getMerchantMenuUnpaid(Context  context)
    {

        List<Cart> ar=readListData(context);
        if (ar == null)
            return new HashMap<>();

        ArrayList <Merchant> am = new ArrayList<>();
        HashMap <String, Merchant> hashMapMerchant = new HashMap<>();
        for (int i = 0 ; i<ar.size() ; i++){
            //boolean toUpdate=false;
            /*for (int i = 0; i < c.size(); i++)
            {
                *//*if ((c.getMerchantCode().equals(am.get(i).getMerchantCode()))
                        && (c.isPaid()==false))*//*
                if (c.isPaid() == false) {
                    toUpdate = true;
                    break;
                }
            }*/

            Cart c = ar.get(i);
            if (!c.isPaid()) {
                //toUpdate = true;

                Merchant mUpdate = new Merchant();
                mUpdate.setMerchantCode(c.getMerchantCode());
                // mUpdate.setListitem(getCartMerchant(context,c.getMerchantCode(),isPaid).size());
                mUpdate.setMerchantName(c.getMerchantName());
                mUpdate.setUrlImageMerhcant(c.getImageUrlmerchant());
                hashMapMerchant.put(c.getMerchantCode(), mUpdate);
                if(!am.contains(mUpdate)) {
                    am.add(mUpdate);
                    if(PayByQRProperties.isDebugMode()) Log.d("RHIO", "added merchant "+c.getMerchantName()+" with code "+c.getMerchantCode());
                }
            }

            /*if (toUpdate) {
                Merchant mUpdate= new Merchant();
                mUpdate.setMerchantCode(c.getMerchantCode());
               // mUpdate.setListitem(getCartMerchant(context,c.getMerchantCode(),isPaid).size());
                mUpdate.setMerchantName(c.getMerchantName());
                mUpdate.setUrlImageMerhcant(c.getImageUrlmerchant());
                am.add(mUpdate);

            }*/
        }



        return hashMapMerchant;

    }


    public static List getMerchantMenu(Context  context, boolean isPaid)
    {

        List<Cart> ar=readListData(context);
        if (ar==null)
            return null;

        List <Merchant> am= new ArrayList<>();


        for (Cart c : ar){
            {
                 boolean toUpdate=true;
                for (int i = 0; i < am.size(); i++)
                {
                    if ((c.getMerchantCode().equals(am.get(i).getMerchantCode()))
                        && (c.isPaid()!=isPaid))
                         toUpdate=false;
                }
                if (toUpdate)
                {
                    Merchant mUpdate= new Merchant();
                     mUpdate.setMerchantCode(c.getMerchantCode());
                    mUpdate.setListitem(getCartMerchant(context, c.getMerchantCode(), isPaid).size());
                    mUpdate.setMerchantName(c.getMerchantName());
                    mUpdate.setUrlImageMerhcant(c.getImageUrlmerchant());
                    am.add(mUpdate);

                }
            }
        }



   return am;

    }


    public  static void removeListByMerchantInvoiceId(Context context, String merchantCode, String invoiceid) {
        SharedPreferences prefs = context.getSharedPreferences(QR_STORE_DB, Context.MODE_PRIVATE);
        List arlist=  readListData(context) ;

        if (arlist==null) {
            return ;
        }

        boolean isexist= isInvoiceIdExist(context,invoiceid);

        if (isexist) {
             for (int i =0; i <arlist.size();i++)
             {
                Cart cart=(Cart) arlist.get(i);
                 if (cart.getInvoiceId().equals(invoiceid))
                     arlist.remove(cart);

             }


            setReload(context, arlist);

        }



    }

    public  static void removeListByMerchant(Context context, String merchantCode)
    {
        //SharedPreferences prefs = context.getSharedPreferences(QR_STORE_DB, Context.MODE_PRIVATE);
        List <Cart>cartList=  readListData(context) ;

        if (cartList==null) {

            return ;
        }
         int i=0;
        List iremove= new ArrayList<>();
        for ( i=0;i<cartList.size();i++)
        {
            if (cartList.get(i).getMerchantCode().equals(merchantCode) && cartList.get(i).isPaid()==false)
            {
                if (PayByQRProperties.isDebugMode()) Log.d("remove ","will remove==" +i);

                iremove.add(i);
            }

        }

        for (i=iremove.size()-1;i>=0;i--)
        {
            if (PayByQRProperties.isDebugMode()) Log.d("remove ","index" +iremove.get(i));
            Cart cr=cartList.get(i);
              cartList.remove(cr);
        }




        setReload(context, cartList);
    }

    public static void saveListData(Context context, List dataList){

        SharedPreferences prefs = context.getSharedPreferences(QR_STORE_DB, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson g = new Gson();
        String gs = g.toJson(dataList);
        if (PayByQRProperties.isDebugMode()) Log.d("datato SAVE===========\n",gs+"\n==================\n");
        editor.putString(QR_STORE_DB, gs);
        editor.commit();
    }


    public static void putCartData(Context context, Cart c)

    {

        if (isInvoiceIdExist(context,c.getInvoiceId()))
        {
            List <Cart>ar=  readListData(context) ;
            int poslocation=positionExist(context,c.getInvoiceId());
            if (!ar.get(poslocation).isPaid())
                ar.remove(poslocation);
            ar.add(c);
            saveListData(context,ar);

        }
        else
            saveCartData(context,c);

    }
    public static void updateCartData(Context context, Cart c)

    {
        if (isInvoiceIdExist(context,c.getInvoiceId()))
        {
            List <Cart>ar=  readListData(context) ;
            int poslocation=positionExist(context,c.getInvoiceId());
            if (!ar.get(poslocation).isPaid())
                   ar.remove(poslocation);
            ar.add(c);
            saveListData(context,ar);

        }
        else
            saveCartData(context,c);

    }
    public static void saveCartData(Context context, Cart c)
    {

        SharedPreferences prefs = context.getSharedPreferences(QR_STORE_DB, Context.MODE_PRIVATE);
        List ar=  readListData(context) ;

        if (ar==null) {
            ar = new ArrayList();
            ar.add(c);
            saveListData(context, ar);
            return ;
         }


       boolean isexist= isInvoiceIdExist(context,c.getInvoiceId());

        if (isexist)
        {
            int poslocation=positionExist(context, c.getInvoiceId());
            int quantyty= getCartMerchantInvoiceId(context, c.getInvoiceId());
            resetData(context);
            c.setDetailQuantity(c.getDetailQuantity() + quantyty);
            ar.remove(poslocation);
            ar.add(c);
            saveListData(context,ar);
            return;
        }
        else
        {
            ar.add(c);
            saveListData(context, ar);
            return ;
        }



    }
    public static void updateInvoiceIdPe(Context context, String transId, String invoiceId)

    {   List <Cart> arlist =  readListData(context) ;
        if (arlist==null)
            return ;



        for (int i =0; i <arlist.size();i++)
        {
            Cart cart= arlist.get(i);
            if ((cart.getTrandId().equals(transId))&& (cart.isPaid()==false))
            {
                arlist.get(i).setTrandId(transId);
                arlist.get(i).setIsPaid(true);
                arlist.get(i).setInvoiceIdPE(invoiceId);
            }
        }



        setReload(context, arlist);


    }
    public static void updateStatus(Context context, String MerchantCode, String transId)

    {   List <Cart>arlist=  readListData(context) ;
        if (arlist==null)
            return ;
        for (int i =0; i <arlist.size();i++)
        {
            Cart cart= arlist.get(i);
            if ((cart.getMerchantCode().equals(MerchantCode))&& (cart.isPaid()==false))
            {

                arlist.get(i).setTrandId(transId);

            }
        }

        setReload(context, arlist);


    }

    public static void updateStatus(Context context, String MerchantCode, int status)
    {

        List arlist=  readListData(context) ;
        if (arlist==null)
            return ;
        for (int i =0; i <arlist.size();i++)
        {
            Cart cart=(Cart) arlist.get(i);
            if (cart.getMerchantCode().equals(MerchantCode)) {
                arlist.remove(cart);
                cart.setStatus(status);
                arlist.add(cart);
            }
        }

        setReload(context,arlist);

    }

    public static int positionExist(Context context,String invoiceId)
    {
        SharedPreferences prefs = context.getSharedPreferences(QR_STORE_DB, Context.MODE_PRIVATE);

        int  isexist=-9;
        String jsonStr = prefs.getString(QR_STORE_DB, "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Cart>>(){}.getType();
        List<Cart> cartList = gson.fromJson(jsonStr, type);
        if (cartList==null)
            return -9;

        for (int i =0; i<cartList.size(); i++)
        {
            Cart c = cartList.get(i);
            if (c.getInvoiceId().equals(invoiceId) && (c.isPaid()==false))
                isexist=i;
        }
       return isexist;
    }

    public static int  getTotalperMerchant(Context context,String merchantCode)
    {
        SharedPreferences prefs = context.getSharedPreferences(QR_STORE_DB, Context.MODE_PRIVATE);

         int total=0;
            String jsonStr = prefs.getString(QR_STORE_DB, "");
            Gson gson = new Gson();
            Type type = new TypeToken<List<Cart>>(){}.getType();
            List<Cart> cartList = gson.fromJson(jsonStr, type);
            if (cartList==null)
                return 0;
            for (Cart c : cartList){
                if (c.getMerchantCode().equals(merchantCode))
                    total+=c.getDetailQuantity();
             }

            return total;
    }

    public static boolean isInvoiceIdExist(Context context,String invoiceId)
    {
        SharedPreferences prefs = context.getSharedPreferences(QR_STORE_DB, Context.MODE_PRIVATE);

        boolean isexist=false;
        String jsonStr = prefs.getString(QR_STORE_DB, "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Cart>>(){}.getType();
        List<Cart> cartList = gson.fromJson(jsonStr, type);
        if (cartList==null)
            return false;
        for (Cart c : cartList){
            if ((c.getInvoiceId().equals(invoiceId)) && (!c.isPaid()))
                isexist=true;

        }

        return isexist;
    }
    public static int getCartMaximumInvoiceId(Context context,String invoiceId)
    {
        SharedPreferences prefs = context.getSharedPreferences(QR_STORE_DB, Context.MODE_PRIVATE);

        int lntotal=0;
        int currentquantity=0;
        String jsonStr = prefs.getString(QR_STORE_DB, "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Cart>>(){}.getType();
        List<Cart> cartList = gson.fromJson(jsonStr, type);
        if (cartList==null)
            return 0;
        for (Cart c : cartList){
            if ((c.getInvoiceId().equals(invoiceId)) && c.isPaid()==false)
            {
                currentquantity+=c.getDetailQuantity();
                lntotal+=(c.getMaxQuantity());
            }
        }

        return lntotal-currentquantity;
    }
    public static int getCartMerchantInvoiceId(Context context,String invoiceId)
    {
        SharedPreferences prefs = context.getSharedPreferences(QR_STORE_DB, Context.MODE_PRIVATE);

        int lntotal=0;
        String jsonStr = prefs.getString(QR_STORE_DB, "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Cart>>(){}.getType();
        List<Cart> cartList = gson.fromJson(jsonStr, type);
        if (cartList==null)
            return 0;
        for (Cart c : cartList){
            if (c.getInvoiceId().equals(invoiceId))
            {
                lntotal+=(c.getDetailQuantity());
            }
        }

        return lntotal;
    }
    public static long getCartMerchantAmount(Context context,String MerchantCode)
    {

        SharedPreferences prefs = context.getSharedPreferences(QR_STORE_DB, Context.MODE_PRIVATE);

        long lntotal=0;
        String jsonStr = prefs.getString(QR_STORE_DB, "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Cart>>(){}.getType();
        List<Cart> cartList = gson.fromJson(jsonStr, type);
        if (cartList==null)
            return 0;
        for (Cart c : cartList){
            if ((c.getMerchantCode().equals(MerchantCode))
                && c.isPaid()==false)
            {
                lntotal+=((c.getPrice()-c.getDiscountAmount())* c.getDetailQuantity());
            }
        }

        return lntotal;
    }

    public static Cart getCartbaseIdMerchantcode(Context context,String MerchantCode,String id)
    {
        SharedPreferences prefs = context.getSharedPreferences(QR_STORE_DB, Context.MODE_PRIVATE);
       // List<Cart> cartListMerchant=new ArrayList<>();
        String jsonStr = prefs.getString(QR_STORE_DB, "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Cart>>(){}.getType();
        List<Cart> cartList = gson.fromJson(jsonStr, type);
        Cart cret = new Cart();
        if (cartList==null)
            return null;
        for (Cart c : cartList){
            if (c.getMerchantCode().equals(MerchantCode) && c.getId().equals(id))
                cret=c;

        }

        return cret;
    }
    public static List getCartMerchant(Context context,String MerchantCode,boolean isPaid)
    {

        SharedPreferences prefs = context.getSharedPreferences(QR_STORE_DB, Context.MODE_PRIVATE);
        List<Cart> cartListMerchant=new ArrayList<>();
        String jsonStr = prefs.getString(QR_STORE_DB, "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Cart>>(){}.getType();
        List<Cart> cartList = gson.fromJson(jsonStr, type);
        if (cartList==null)
            return null;
        for (Cart c : cartList){

            if ((c.getMerchantCode().equals(MerchantCode))
                && (c.isPaid()==isPaid))
                cartListMerchant.add(c);
        }

        return cartListMerchant;
    }


    public static List readListData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(QR_STORE_DB, Context.MODE_PRIVATE);

        String jsonStr = prefs.getString(QR_STORE_DB, "");
        if (PayByQRProperties.isDebugMode())  Log.d("Cart builk", jsonStr);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Cart>>(){}.getType();
        List<Cart> cartList = gson.fromJson(jsonStr, type);
        if (cartList==null)
            return null;
        for (Cart c : cartList){
       //     Log.d("Cart Details", c.getId() + "-" + c.getMerchantCode() + "-");
        }

        return cartList;


    }


}
