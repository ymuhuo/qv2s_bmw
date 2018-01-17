package com.bmw.peek2.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.bmw.peek2.R;

import java.io.IOException;
import java.util.List;

import static android.content.pm.PackageManager.GET_PROVIDERS;

/**
 * Created by admin on 2017/5/31.
 */

public class SystemUtil {

    public static void shutdown(){
        try {
            Runtime.getRuntime().exec(new String[]{"su","-c","reboot -p"});
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.log("shutdown error:"+e);
        }
    }

    public static boolean isAddShortCut(Context context){
        boolean isInstallShortcut = false;
        try {
            ContentResolver cr = context.getContentResolver();
            String AUTHORITY = getAuthorityFromPerssion(context, "com.android.launcher.permission.READ_SETTINGS");
            Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
            Cursor c = cr.query(CONTENT_URI, new String[]{"title"}, "title=?", new String[]{context.getString(R.string.app_name)}, null);

            if (c != null && c.getCount() > 0) {
                isInstallShortcut = true;
            }
            if (c != null)
                c.close();
        }catch (Exception e){
            LogUtil.error("check is add shortCut error:"+e.toString());
        }
        return isInstallShortcut;
    }

    private static String getAuthorityFromPerssion(Context context, String permission) {
        if(TextUtils.isEmpty(permission))
        return null;
        List<PackageInfo> packInfos = context.getPackageManager().getInstalledPackages(GET_PROVIDERS);
        if(packInfos == null)
            return null;
        for(PackageInfo info:packInfos){
            ProviderInfo[] providerInfos = info.providers;
            if(providerInfos != null){
                for(ProviderInfo provider :providerInfos){
                    if(permission.equals(provider.readPermission) || permission.equals(provider.writePermission)){
                        return provider.authority;
                    }
                }
            }
        }
        return null;
    }

}
