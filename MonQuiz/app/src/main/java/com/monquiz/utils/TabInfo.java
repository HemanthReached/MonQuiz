package com.monquiz.utils;

import android.os.Bundle;

public class TabInfo {

    @SuppressWarnings("unused")
    private final String tag;
    public final Class<?> clss;
    public final Bundle args;

    public TabInfo(String _tag, Class<?> _class, Bundle _args) {
        tag = _tag;
        clss = _class;
        args = _args;
    }
}
