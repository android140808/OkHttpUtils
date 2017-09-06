package avater.avaterprojects;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.security.MessageDigest;
import java.util.HashMap;

import okhttp3.Request;

public class SecondActivity extends AppCompatActivity {

    Button btn_reg;
    TextView result;
    String url = "http://test3plus.fashioncomm.com/sport/api/reg_for_france";

    /* MD5进行密码加密*/
    public String MD5(String inStr) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];

        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();

        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }

        return hexValue.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        btn_reg = (Button) findViewById(R.id.btn_reg);
        result = (TextView) findViewById(R.id.result);
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap map = new HashMap();
                map.put("userName", "LaiEnYin");
                map.put("email", "3038000191@qq.com");
                map.put("password", MD5("123456"));
                map.put("gender", "0");
                map.put("birthDay", "1992-01-01");//日期要这种模式 1993-09-04
                map.put("height", "173");
                map.put("weight", "50");
                map.put("heightUnit", "1");//默认为公制来保存
                map.put("weightUnit", "1");//默认为公制来保存
                map.put("countryCode", "0");
                map.put("imgUrl", "");
                map.put("encryptMode", "1");
                MyOkHttpClient.getInstance().postAsynAvater(url, new MyOkHttpClient.AvaterResultCallBack<ResultData>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Log.e("TAG", "error!!");
                    }

                    @Override
                    public void onResponse(final ResultData response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                result.setText(response.toString() + "");
                            }
                        });
                    }
                }, map, "reg");
            }
        });
    }
}
