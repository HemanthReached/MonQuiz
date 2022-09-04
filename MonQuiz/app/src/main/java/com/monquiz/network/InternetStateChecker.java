package com.monquiz.network;

import android.app.AlertDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.monquiz.R;

import es.dmoral.toasty.Toasty;


public class InternetStateChecker implements ConnectivityReceiverListener {

    private Context context;
    private ConnectivityReceiver receiver;
    AlertDialog.Builder dialogBuilder;
    AlertDialog alertDialog;

    //For Alert Dialog
    private boolean cancelable = true;
    private String title = "UH-SNAP!";
    private String message = "You are not connected to Internet.";
    private int color ;
    private int icon = R.drawable.no_wifi1;
    private int textColor;

    private boolean isAlreadyVisible;

    private InternetStateChecker(Context context, boolean cancelable) {
        this.context = context;
        color = ContextCompat.getColor(context, R.color.bg_gradient_end1);
        textColor = ContextCompat.getColor(context, R.color.white) ;
        this.cancelable = cancelable;
        initReciever(context);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(isConnected) {
            hideAlertDialog();
            isAlreadyVisible = false;
        }else {
            if(!isAlreadyVisible) {
                showAlertDialog();
                isAlreadyVisible = true;
            }
        }
    }

    private void showSnack(boolean isConnected) {
        String message;
        if (isConnected) {
            message = "Good! Connected to Internet";
        } else {
            message = "Sorry! Not connected to internet";
        }
        Toasty.error(context, message, Toasty.LENGTH_SHORT).show();
    }

    public boolean isConnected() {
        ConnectivityManager
                cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void initReciever(Context context) {
        receiver = new ConnectivityReceiver();
        context.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        receiver.setConnectionCallback(this);
    }

    private void showAlertDialog(){
        dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutView = inflater.inflate(R.layout.custom_dialog, null);
        TextView txtTitle = (TextView) layoutView.findViewById(R.id.txtTitle);
        TextView txtMessage = (TextView) layoutView.findViewById(R.id.txtMessage);
        ImageView imageView = (ImageView) layoutView.findViewById(R.id.imageView);
        CardView myCardView = (CardView)layoutView.findViewById(R.id.myCardView);
        Drawable mDrawable = ContextCompat.getDrawable(context, R.drawable.round_corner);
        /*assert mDrawable != null;
        mDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));*/

        // myCardView.setBackgroundColor(color);
        myCardView.setBackground(mDrawable);
        myCardView.setRadius(30);
        txtTitle.setText(title);
        txtMessage.setText(message);
        imageView.setImageResource(icon);
        txtMessage.setTextColor(textColor);
        txtTitle.setTextColor(textColor);

        dialogBuilder.setCancelable(cancelable);
        dialogBuilder.setView(layoutView);
        alertDialog = dialogBuilder.create();
       // alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlideInOutAnimation;
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    private void hideAlertDialog() {
        if(isAlreadyVisible) {
            alertDialog.dismiss();
        }
    }

    public void stop() {
        try {
            context.unregisterReceiver(receiver);
        }catch(Exception e){
        }
    }
    public void start(){
        try {
            context.registerReceiver(receiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }catch(Exception e){
        }
    }
    public static class Builder {
        private Context context;
        private boolean cancelable;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public InternetStateChecker build() {
            return new InternetStateChecker(context,cancelable);
        }
    }
}
