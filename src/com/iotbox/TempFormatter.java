/*
 * Copyright 2015 
 * 
 */

package com.iotbox;

import java.text.DecimalFormat;

import com.github.mikephil.charting.utils.ValueFormatter;

/**
 * This ValueFormatter is just for convenience and simply puts a "C" sign after
 * each value.
 * 
 * @author hehc
 */

public class TempFormatter implements ValueFormatter {

    protected DecimalFormat mFormat;

    public TempFormatter() {
        mFormat = new DecimalFormat(".0");
    }

    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value) + " C";
    }
}
