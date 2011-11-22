package com.DGSD.TweeterTweeter;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Author: Daniel Grech
 * Date: 14/11/11 2:26 PM
 * Description :
 */
public class Utils {
    public static boolean isTablet(Context context) {
        // Can use static final constants like HONEYCOMB, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) && (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public static String join(AbstractCollection<String> s, String delimiter) {
        if (s == null || s.isEmpty()) {
            return "";
        }

        Iterator<String> iter = s.iterator();
        StringBuffer buffer = new StringBuffer(iter.next());

        while (iter.hasNext()) {
            buffer.append(delimiter).append(iter.next());
        }

        return buffer.toString();
    }

    public static List<String> unjoin(String string, String joiner) {
        if(string == null || string.length() == 0) {
            return null;
        }

        System.err.println("UNJOINING STRING: " + string);
        return Arrays.asList(string.split(joiner));
    }

    public static int getNumberDigits(String inString){
        if (isEmpty(inString)) {
            return 0;
        }

        int numDigits= 0;

        for (int i = 0, size = inString.length(); i < size; i++) {
            if (Character.isDigit(inString.charAt(i))) {
                numDigits++;
            }
        }
        return numDigits;
    }

    public static boolean isEmpty(String inString) {
        return inString == null || inString.length() == 0;
    }
}
