package me.albfan.util;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

abstract public class Util {
    static public String md5(final String s) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final byte[] array = md.digest(s.getBytes("CP1252"));
            final StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    static public String formatDate(final String format, final String string) {
        try {
            final SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
            return DateFormat.getDateTimeInstance().format(df.parse(string));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return string;
    }

    public interface Listener {
        void callback(final String s);
    }

    public interface TaskListener {
        void doInBackground(final Object response);
        void onPostExecute(final Object response);
    }
}
