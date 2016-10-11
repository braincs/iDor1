package com.example.shuai.idor1;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Shuai on 16/10/10.
 *
 * Definition: Useful static methods
 */
public class SC_Utils {

    /**
     * toastMessage: 蒙版消息显示
     */
    public static void toastMessage(Context context, String s){
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public static void debug(String s){
        Log.d("SC_Debug------>",s);
    }

}
