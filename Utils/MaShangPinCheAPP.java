package com.mashangpinche.client;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.mashangpinche.R;
import com.mashangpinche.client.db.Destination;
import com.mashangpinche.client.db.Member;
import com.mashangpinche.client.util.AppUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Created by JOE on 2016/3/5.
 */
public class MaShangPinCheAPP extends Application {

    private static final String PROPERTIES_MDN_NAME = "MDN_NAME";

    public static final String ONTOLOGY_IMEI = "IMEI";
    public static final String ONTOLOGY_IMSI = "IMSI";
    public static final String ONTOLOGY_SERVICE_NAME = "service";
    public static final String ONTOLOGY_SERVICE_TYPE = "serviceType";
    public static final String ONTOLOGY_SERVICE_LVL1 = "serviceLvl1";
    public static final String ONTOLOGY_SERVICE_LVL2 = "serviceLvl2";
    public static final String ONTOLOGY_SERVICE_DESCRIPTION = "serviceDescription";
    public static final String ONTOLOGY_SEARCH_SPACEDISTANCE = "searchSpaceDistance";
    public static final String ONTOLOGY_SEARCH_TIMEDISTANCE = "searchTimeDistance";
    public static final String ONTOLOGY_NICK = "nick";
    public static final String ONTOLOGY_PHONENUM = "phoneNum";
    public static final String ONTOLOGY_TAID = "taID";
    public static final String ONTOLOGY_LATITUDE = "latitude";
    public static final String ONTOLOGY_LONGITUDE = "longitude";
    public static final String ONTOLOGY_IMAGEURI = "imageUri";
    public static final String ONTOLOGY_MEMBER = "member";
    public static final String ONTOLOGY_PUBLICATION = "publication";
    public static final String ONTOLOGY_REGISTER = "register";
    public static final String ONTOLOGY_GENDER = "gender";
    public static final String ONTOLOGY_AGE = "age";
    public static final String ONTOLOGY_LASTNAME = "lastName";
    public static final String ONTOLOGY_PORTRAITFILE = "portraitFile";
    public static final String ONTOLOGY_ISROOKIE = "isRookie";

    public static final int CONS_SERVICECODE_DEFAULT = 0x0010000;

    //连接服务器超时时间
    public static final int CONS_CONNECTION_TIMEOUT = 5000;

    public static final String CONS_SERVICE_TYPE_CUSTOMER = "c";
    public static final String CONS_SERVICE_TYPE_MERCHANT = "m";
    public static final String CONS_SERVICE_TYPE_MUTUAL = "cm";

    public static final String CONS_PORTRAIT_FILENAME_PRE = "IMG_";
    public static final String CONS_PORTRAIT_FILENAME_SUR = ".png";
    public static final String CONS_PORTRAIT_FOLDER_NAME = "portraits";
    public static final String CONS_PORTRAIT_DEFAULT_NAME = "portrait_default_me.png";
    public static final int CONS_AGE_DEFAULT = 25;
    public static final int CONS_AGE_MAX = 80;
    public static final int CONS_AGE_MIN = 8;
    public static final String CONS_GENDER_DEFAULT = "m";

    public static final int CONS_PORTRAIT_WIDTH = 256;
    public static final int CONS_PORTRAIT_HEIGHT = 256;

    public static final String CONS_GENDER_MALE = "m";
    public static final String CONS_GENDER_FEMALE = "f";

    public static final String CONS_COMMAND_SPLITTER = ",";

    // 两次点击退出时间间隔。
    public static final int CONS_DOUBLE_CLICK_EXIT_INTERVAL = 2000;

    public static final int RESPONSE_CODE_COMMAND_NOTHING = 0x01;
    //强制更新，在架构发生重大变化的时候使用。
    public static final int RESPONSE_CODE_COMMAND_FORCE_UPDATE = 0x02;

    public static final int RESPONSE_CODE_FEEDBACK_FAIL = 0x42;
    public static final int RESPONSE_CODE_FEEDBACK_SUCCESS = 0x41;
    public static final int RESPONSE_CODE_UPDATEMEMBER_FAIL = 0x32;
    public static final int RESPONSE_CODE_UPDATEMEMBER_SUCCESS = 0x31;
    public static final int RESPONSE_CODE_VCODE_SEND_FAIL = 0x23;
    public static final int RESPONSE_CODE_VCODE_FAIL = 0x22;
    public static final int RESPONSE_CODE_VCODE_SUCCESS = 0x21;
    public static final int RESPONSE_CODE_VCODE_GENERATE = 0x20;
    public static final int RESPONSE_CODE_LOGIN_FAIL = 0x11;
    public static final int RESPONSE_CODE_LOGIN_SUCCESS = 0x10;
    public static final int EXCEPTION_HTTP = -0x20;
    public static final int EXCEPTION_CONNECT_TIMEOUT = -0x30;
    public static final int EXCEPTION_UNKNOWN = -0xf0;

    /*
	 * MemberID和RegisterID的字符长度。ID都是12字节，16进制用24个字符表示，故长度为24.
	 */
    public static final int OBJECT_ID_LENGTH = 24;
    /*
     * 服务器返回字符代码长度，为2字符表示。
     */
    public static final int RESPONSE_CODE_LENGTH = 2;

    /*
	 * 两次获取验证码时间间隔，单位秒。
	 */
    public static final int INTERVAL_BETWEEN_GETVCODE = 60;

    /*
     * 消息，通知register2页面的重新获得验证码的按钮更新剩余时间。。
     */
    public static final int MSG_TO_RSTEP2_SECONDS_LEFT_BEFORE_GETVCODE = 0x10;

    /*
     * 消息，通知register2页面的重新获取验证码的按钮可用。
     */
    public static final int MSG_TO_RSTEP2_ACTIVE_RESEND = 0x11;
    /*
     * 消息，通知register2页面连接服务器超时。
     */
    public static final int MSG_TO_RSTEP2_EX_CONNECT_TIMEOUT = 0x12;
    /*
     * 消息，通知register2页面发生未知异常
     */
    public static final int MSG_TO_RSTEP2_EX_UNKNOWN = 0x13;
    /*
     * 验证码发送成功
     */
    public static final int MSG_TO_RSTEP2_VCODE_SENT = 0x14;
    /*
     * 验证码发送失败
     */
    public static final int MSG_TO_RSTEP2_VCODE_SENT_FAIL = 0x15;

    public static final int MSG_TO_RSTEP2_VCODE_FAIL = 0x16;
    public static final int MSG_TO_RSTEP2_VCODE_SUCCESS = 0x17;

    public static final int MSG_TO_LOGIN_EX_CONN_TIMEOUT = 0x02;
    public static final int MSG_TO_LOGIN_EX_UKNOWN = 0x03;
    public static final int MSG_TO_LOGIN_LOGIN_SUCCESS = 0x04;
    public static final int MSG_TO_LOGIN_LOGIN_FAIL = 0x05;
    public static final int MSG_TO_LOGIN_COMMAND_FORCE_UPDATE = 0x06;
    public static final int MSG_TO_LOGIN_COMMAND_ACCOMPLISHED = 0x07;

    public static final int MSG_TO_MEMBERINFO_EX_CONN_TIMEOUT = 0X22;
    public static final int MSG_TO_MEMBERINFO_EX_UNKNOWN = 0X23;
    public static final int MSG_TO_MEMBERINFO_UPDATEMEMBER_SUCCESS = 0x24;
    public static final int MSG_TO_MEMBERINFO_UPDATEMEMBER_FAIL = 0x25;
    public static final int MSG_TO_MEMBERINFO_LOGIN_FAIL = 0x26;
    public static final int MSG_TO_MEMBERINFO_HPICKER_CHANGED = 0x27;

    public static final int MSG_TO_IWANT_CANNT_GET_LOCATION = 0x31;
    public static final int MSG_TO_IWANT_EX_CONN_TIMEOUT = 0x32;
    public static final int MSG_TO_IWANT_EX_UNKNOWN = 0x33;
    public static final int MSG_TO_IWANT_CANNT_GET_DESTINATION = 0x34;
    public static final int MSG_TO_IWANT_GOT_DESTINATION = 0x35;
    public static final int MSG_TO_IWANT_GOT_LOCATION = 0x36;
    public static final int MSG_TO_IWANT_CANCEL_LOCATING = 0x37;
    public static final int MSG_TO_IWANT_CANCEL_GET_DESTINATION = 0x38;
    public static final int MSG_TO_IWANT_GET_LOCATION = 0x39;


    public static final int MSG_TO_MAIN_HPICKER_CHANGED = 0x41;
    public static final int MSG_TO_MAIN_GOT_PLIST = 0x42;
    public static final int MSG_TO_MAIN_EX_CONN_TIMEOUT = 0x43;
    public static final int MSG_TO_MAIN_EX_UNKNOWN = 0x44;
    public static final int MSG_TO_MAIN_GOT_PORTRAIT_BYTES = 0x45;
    public static final int MSG_TO_MAIN_INFOWINDOW_DRAWN = 0x46;
    //联系后，找到了TA
    public static final int MSG_TO_MAIN_IT_IS_TA = 0x47;
    //联系后，不是合适的TA
    public static final int MSG_TO_MAIN_NOT_TA = 0x48;



    public static final int MSG_TO_FEEDBACK_EX_UNKNOWN = 0x51;
    public static final int MSG_TO_FEEDBACK_SUCCESS = 0x52;
    public static final int MSG_TO_FEEDBACK_FAIL = 0x53;

    public enum RESPONSE_CODE {
        RESPONSE_CODE_LOGIN_SUCCESS(0x10), RESPONSE_CODE_LOGIN_FAIL(0x20);
        private int code;

        private RESPONSE_CODE(int theCode) {
            this.code = theCode;
        }

        public int getCode() {
            return this.code;
        }
    }

    public enum MSG {
        MSG_REGISTER_UPDATE_BT_RESEND(0x10), MSG_MSPC_HSV(0x20);
        private int msgCode;

        private MSG(int code) {
            this.msgCode = code;
        }

        public int getMsgCode() {
            return this.msgCode;
        }
    }

    public static String SHARED_PREFERENCES_NAME = "properties";

    public static final String SMS_CONTENT = "一起拼车吧";

    public static final int CONNETION_TIMEOUT = 10000;

    /**
     * Constant used for the retrieving the last location provider
     */
    public static final String LOCATION_PROVIDER = "LOCATION_PROVIDER";
    /**
     * Key for retrieving the phone number
     */
    public static final String PREFERENCE_PHONE_NUMBER = "PREFERENCE_PHONE_NUMBER";

    public static final String PREFERENCE_IMEI = "PREFERENCE_IMEI";

    private static List<Activity> activityList = new ArrayList<Activity>();
    public static MsgHandler msgHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        // 创建Portrait文件夹和默认头像文件。
        // 最好的方式是检测用户是否是首次使用，如果是的话，创建文件和文件夹。
        File myDefaultPortraitFile = new File(getPortraitFileDir(),
                CONS_PORTRAIT_DEFAULT_NAME);
        if(!myDefaultPortraitFile.exists()) {
            createDefaultPortraitFile();
        }

        msgHandler = new MsgHandler();
    }

    public void createDefaultPortraitFile() {
        File portraitFileDir = getPortraitFileDir();
        File defaultPortraitFile = new File(portraitFileDir,
                CONS_PORTRAIT_DEFAULT_NAME);
        if(!defaultPortraitFile.exists()) {
            try{
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                        R.mipmap.portrait_default_ta);
                FileOutputStream fos = new FileOutputStream(defaultPortraitFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //退出所有activity
    public void onTerminate() {
        super.onTerminate();
        for(Activity activity: activityList) {
            if(activity != null) {
                activity.finish();
                activityList.remove(activity);
            }
        }
    }

    /**
     * Returns the last stored value for a property (uses Android persistent
     * store)
     *
     * @param key
     *            name of the property to be retrieved
     * @return value of the property from the persistent store
     */
    public String getProperty(String key) {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    /**
     * Saves a property on persistent store (uses Android persistent store)
     *
     * @param key
     *            name of the property to be saved
     * @param value
     *            value of the property to be saved
     */
    public void setProperty(String key, String value) {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    //得到手机的IMEI码
    public String getIMEI() {
        TelephonyManager telMgr = (TelephonyManager)this.getSystemService(TELEPHONY_SERVICE);
        String IMEI = telMgr.getDeviceId();
        return IMEI.toString();
    }

    public String getIMSI() {
        TelephonyManager telMgr = (TelephonyManager)this.getSystemService(TELEPHONY_SERVICE);
        String IMSI = telMgr.getSubscriberId();
        return IMSI;
    }

    public String getMdnPhone() {
        TelephonyManager telMgr = (TelephonyManager)this.getSystemService(TELEPHONY_SERVICE);
        String mdnPhone = telMgr.getLine1Number();
        return mdnPhone;
    }

    public String getMdnSaved() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
        String mdnSaved = prefs.getString(PROPERTIES_MDN_NAME, null);
        return mdnSaved;
    }

    public void setMdnSaved(String mdnSaved) {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTIES_MDN_NAME, mdnSaved);
        editor.commit();
        return;
    }

    public String getTaId() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
        String taId = prefs.getString(ONTOLOGY_TAID, null);
        return taId;
    }

    public void setTaId(String taId) {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ONTOLOGY_TAID, taId);
        editor.commit();
        return;
    }

    public boolean isUserRookie() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
        return prefs.getBoolean(ONTOLOGY_ISROOKIE, true);
    }
    //set the user is not first time use this app
    public void setUserIsNotRookie() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ONTOLOGY_ISROOKIE, false);
        editor.commit();
        return;
    }

    public String getServerBaseURL() {
        return "http://"+getApplicationContext().getResources().getString(R.string.host)+
                ":"
                +getApplicationContext().getResources().getString(R.string.port)+
                getApplicationContext().getResources().getString(R.string.folder);
    }

    public void setMember(Member member) {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ONTOLOGY_TAID, member.getId());
        editor.putString(ONTOLOGY_PHONENUM, member.getPhoneNum());
        editor.putString(ONTOLOGY_IMSI, member.getImsi());
        editor.putString(ONTOLOGY_GENDER, member.getGender());

        editor.putInt(ONTOLOGY_AGE, member.getAge());
        editor.putString(ONTOLOGY_PORTRAITFILE, member.getPortraitFileName());
        editor.commit();
        return;
    }

    /**
     * 读取preference中的会员信息，返回一个member对象，如果没有member信息，也会返回一个空对象。
     *
     * @return
     */
    public Member getMember() {
        Member member = new Member();
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
        member.setId(prefs.getString(ONTOLOGY_TAID, null));
        member.setPhoneNum(prefs.getString(ONTOLOGY_PHONENUM, null));
        member.setImsi(prefs.getString(ONTOLOGY_IMSI, null));
        member.setGender(prefs.getString(ONTOLOGY_GENDER, null));
        member.setAge(prefs.getInt(ONTOLOGY_AGE, 0));
        member.setPortraitFileName(prefs.getString(ONTOLOGY_PORTRAITFILE, null));
        return member;
    }

    /**
     * 获得portrait的文件目录，如果不存在then创建。目录为iwantu目录下创建爱你文件夹portraits.
     *
     * @return
     */
    public File getPortraitFilesDir() {
        File mediaStorageDir = new File(this.getFilesDir(),
                CONS_PORTRAIT_FOLDER_NAME);
        if(!mediaStorageDir.exists()) {
            mediaStorageDir.mkdir();
        }
        return mediaStorageDir;
    }

    /**
     * 读取含有destiantion的csv文件, 并返回 cityName相关的destination list。 文件必须是utf-8的编码。
     *
     *
     * @return ArrayList<Destination>
     */

    public ArrayList<Destination> getCommonDestFromResource(int res_id, String theCityName) {
        ArrayList<Destination> dList = new ArrayList<Destination>();
        try{
            CSVReader reader;
            InputStream in = getResources().openRawResource(res_id);
            reader = new CSVReader(new InputStreamReader(in));
            String[] nextLineArray;
            while(((nextLineArray = reader.readNext()) != null) && (nextLineArray.length>0)) {
                String cityName = nextLineArray[0];
                if(cityName != null && cityName.equals(theCityName)) {
                    Destination d = new Destination();
                    String destName = nextLineArray[1];
                    float latitude = Float.parseFloat(nextLineArray[2]);
                    float longitude = Float.parseFloat(nextLineArray[3]);
                    d.setCityName(cityName);
                    d.setDestName(destName);
                    d.setLatitude(latitude);
                    d.setLongitude(longitude);
                    dList.add(d);
                }
            }
            reader.close();
        }catch(FileNotFoundException e) {
            return null;
        }catch (IOException e) {
            return null;
        }
        return dList;
    }

    /**
     * 获得默认的portrait文件
     *
     * @return
     * @throws IOException
     */
    public File getDefaultPortraitFile() {
        return getResourceFile(R.mipmap.portrait_default_ta);
    }

    public File getResourceFile(int resourceId) {
        File file = new File(this.getResources().getResourceName(resourceId));
        return file;
    }

    public static void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public static void removeActivity(Activity activity) {
        for(Activity acti: activityList) {
            if(acti == activity) {
                activityList.remove(acti);
                break;
            }
        }
    }

    public static Activity getActivity(Class<?> c) {
        for(Activity a: activityList) {
            if(c == a.getClass()) {
                return a;
            }
        }
        return null;
    }

    /**
     * 获取当前网络连接状态.
     *
     * @return
     */
    public boolean isNetworkConnected() {
        Context context = getApplicationContext();
        try{
            // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
            ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connMgr != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connMgr.getActiveNetworkInfo();
                if(info != null && info.isConnected()) {
                    if(info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将bytes所代表的图像内容写入到fileName代表的文件中。
     * @param bytes
     * @return
     */
    public File createPortraitFile(String filename, byte[] bytes) {
        File file = new File(getPortraitFileDir(), filename);
        try{
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
        }catch(FileNotFoundException e) {
            return null;
        }catch(IOException e) {
            return null;
        }
        return file;
    }

    /**
     * 通过文件名获取drawable对象。
     * @return
     */
    public Drawable getDrawableFromFileName(String filename, int reqWidth, int reqHeight) {
        File portraitFile = new File(getPortraitFileDir().getPath()+File.separator+filename);
        if(!portraitFile.exists()) {
            return null;
        }
        return getDrawableFromFile(portraitFile, reqWidth, reqHeight);
    }

    /**
     * 通过文件获取drawable对象。
     * @param file
     * @return
     */
    public Drawable getDrawableFromFile(File file, int reqWidth, int reqHeight) {
        if(!file.exists()) {
            return null;
        }
        Bitmap bitmap = AppUtil.decodeBitmapFromFile(file.getPath(), reqWidth, reqHeight);
        if( null == bitmap) {
            return null;
        }
        return new BitmapDrawable(getResources(), bitmap);
    }

    public static class MsgHandler extends Handler {
        public void handleMessage(Message msg) {
            int msgCode = msg.what;
            // 发送给login页面的消息
            if (msgCode < 0x10) {
                LoginActivity a1 = (LoginActivity) getActivity(LoginActivity.class);
                if (null != a1) {
                    a1.handleMsg(msg);
                }
                // 发送给register step2的消息
            } else if (msgCode < 0x20) {
                RegisterStep2Activity a2 = (RegisterStep2Activity) getActivity(RegisterStep2Activity.class);
                if (null != a2) {
                    a2.handleMsg(msg);
                }
                // 发送给 member info的消息
            } else if (msgCode < 0x30) {
                MemberInfoActivity a3 = (MemberInfoActivity) getActivity(MemberInfoActivity.class);
                if (null != a3) {
                    a3.handleMsg(msg);
                }
                // 发送给 iwant的消息
            } else if (msgCode < 0x40) {
                IWantActivity a4 = (IWantActivity) getActivity(IWantActivity.class);
                if (null != a4) {
                    a4.handleMsg(msg);
                }
                // 发送给main的消息
            } else if (msgCode < 0x50) {
                MainActivity a5 = (MainActivity) getActivity(MainActivity.class);
                if (null != a5) {
                    a5.handleMsg(msg);
                }
                // 发送给feedback的消息
            } else if (msgCode < 0x60) {
                FeedBackActivity a6 = (FeedBackActivity) getActivity(FeedBackActivity.class);
                if (null != a6) {
                    a6.handleMsg(msg);
                }
            }
        };
    };

    public void toast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }


}
