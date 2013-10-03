/*
 * Copyright 2011, 2012 Sony Corporation
 * Copyright (C) 2012-2013 Sony Mobile Communications AB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package ru.thegoncharov.authwatch;

import com.sony.smallapp.phone.SmallAppWindow;
import com.sony.smallapp.phone.SmallApplication;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

public class SmallAuthWatch extends SmallApplication {
    //@Override
    public void onCreate() {
        super.onCreate();

        setContentView(R.layout.smallappmain);

        setMinimizedView(R.layout.minimized);

        setTitle(R.string.app_name);

        SmallAppWindow.Attributes attr = getWindow().getAttributes();

        attr.width = getResources().getDimensionPixelSize(R.dimen.width);
        attr.height = getResources().getDimensionPixelSize(R.dimen.height);

      attr.minWidth = getResources().getDimensionPixelSize(R.dimen.min_width);
        /* Set the minimum height of the application, if it's resizable */
      attr.minHeight = getResources().getDimensionPixelSize(R.dimen.min_height);

        /* Use this flag to make the application window resizable */
        //attr.flags |= SmallAppWindow.Attributes.FLAG_RESIZABLE;
        /* Use this flag to remove the titlebar from the window */
//      attr.flags |= SmallAppWindow.Attributes.FLAG_NO_TITLEBAR;
        /* Use this flag to enable hardware accelerated rendering */
//      attr.flags |= SmallAppWindow.Attributes.FLAG_HARDWARE_ACCELERATED;

        /* Set the window attributes to apply the changes above */
        getWindow().setAttributes(attr);

        setupOptionMenu();
    }

    /*public void onStart() {
        super.onStart();
    }

    public void onStop() {
        super.onStop();
    }*/

    /*public void onDestroy() {
        super.onDestroy();
    }*/

    private void setupOptionMenu() {
        /*
        View header = LayoutInflater.from(this).inflate(R.layout.header, null);

        final View optionMenu = header.findViewById(R.id.option_menu);
        optionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(SmallAuthWatch.this, optionMenu);
                popup.getMenuInflater().inflate(R.menu.menus, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(SmallAuthWatch.this,
                                R.string.menu_clicked, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                popup.show();
            }
        });
        */

        /* Deploy the option menu in the header area of the titlebar */
        //getWindow().setHeaderView(header);
    }
}
