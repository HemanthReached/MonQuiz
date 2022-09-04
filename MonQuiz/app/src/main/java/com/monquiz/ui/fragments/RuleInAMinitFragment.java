package com.monquiz.ui.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.monquiz.R;
import com.monquiz.utils.UnicodeReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import es.dmoral.toasty.Toasty;


public class RuleInAMinitFragment extends BaseFragment {

    private Context context;
    private TextView tvRuleBookText;
    private StringBuilder text = new StringBuilder();
    private InputStream ims;
    private AssetManager assetManager;
    private String type = "";

    public RuleInAMinitFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public RuleInAMinitFragment(Context context, String type) {
        this.context = context;
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.layout_fragment_rule_book, container, false);
        tvRuleBookText = view.findViewById(R.id.tvRuleBookText);
        assetManager = getActivity().getAssets();
        if (type != null) {
            try {
                ims = assetManager.open("rules_in_a_minit.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (BufferedReader reader = new BufferedReader(new UnicodeReader(ims, "UTF-8"))) {
            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                text.append(mLine);
                text.append('\n');
            }
        } catch (IOException e) {
            Toasty.error(getActivity(), "Error reading file!",
                    Toasty.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            //log the exception
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tvRuleBookText.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            }
            tvRuleBookText.setText(text);
        }
        return view;
    }

}
