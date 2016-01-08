package com.melnykov.fab.sample.tools;

/**
 * Created by renxin on 2015/9/4.
 */
/*
 * @(#)TextAppearenceUtil.java              Project:RTKSETTINGS
 * Date:2013-1-9
 *
 * Copyright (c) 2013 Geek_Soledad.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.widget.TabWidget;
import android.widget.TextView;

/**
 * @author Geek_Soledad (msdx.android@tom.com)
 */
public class TextAppearanceUtil {

    /**
     * 设置TabWidget的标题的字体
     *
     * @param tabWidget
     *            要设置的TabWidget
     * @param size
     *            字体大小
     */
    public static void setTabWidgetTitle(TabWidget tabWidget, int size) {
        for (int i = 0, count = tabWidget.getChildCount(); i < count; i++) {
            ((TextView) tabWidget.getChildAt(i)
                    .findViewById(android.R.id.title)).setTextSize(size);
        }
    }

    /**
     * 设置TabWidget
     *
     * @param tabWidget
     *            要设置的TabWidget
     * @param size
     *            字体大小
     * @param color
     *            字体颜色
     */
    public static void setTabWidgetTitle(Context context, TabWidget tabWidget, int size,
                                         int color) {
        TextView tv = null;
        for (int i = 0, count = tabWidget.getChildCount(); i < count; i++) {
            tv = ((TextView) tabWidget.getChildAt(i).findViewById(
                    android.R.id.title));
            tv.setTextSize(size);
            tv.getPaint().setFakeBoldText(false);
            tv.setTextColor(context.getResources().getColorStateList(color));
        }
    }
}
