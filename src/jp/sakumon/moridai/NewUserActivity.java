package jp.sakumon.moridai;

import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewUserActivity extends Activity implements OnClickListener,
        DialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        Button btn = (Button) findViewById(R.id.entry_button);
        btn.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_user, menu);
        return true;
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub

        // ユーザ名
        EditText name = (EditText) findViewById(R.id.user_form);
        String get_name = name.getText().toString();

        // パスワード
        EditText pass = (EditText) findViewById(R.id.pass_form);
        String get_pass = pass.getText().toString();
        EditText pass2 = (EditText) findViewById(R.id.pass2_form);
        String get_pass2 = pass2.getText().toString();

        // メールアドレス
        EditText email = (EditText) findViewById(R.id.email_form);
        String get_email = email.getText().toString();

        //バリデーションが成功したか確認
        boolean val_tf = validates(get_name, get_pass, get_pass2, get_email);
        
        if (val_tf){
            userAddApi(get_name, get_pass, get_email);
            userAttestApi(get_name);
        }
    }

    /**
     * バリデーションを行う関数
     * 
     * @param name
     * @param pass
     * @param pass2
     * @param email
     */
    public boolean validates(String name, String pass, String pass2, String email) {
        String title = "";
        String message = "";
        
        if (name.length() == 0 || pass.length() == 0 || pass2.length() == 0 || email
                .length() == 0) {
            title = "Error";
            message = "全ての欄に入力してください";
        } else {
            if (!(name.matches("[0-9a-zA-Z]+"))) {
                title = "Error";
                message = "ユーザ名を半角英数字で入力してください";
            } else if (!(pass.equals(pass2))) {
                title = "Error";
                message = "2つのパスワードが一致しません";
            } else if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches())) {
                title = "Error";
                message = "メールアドレスの形式で入力してください";
            }else {
                if(name.length() < 3){
                    title = "Error";
                    message = "名前を3文字以上にしてください";
                }else if (pass.length() < 3){
                    title = "Error";
                    message = "パスワードを3文字以上にしてください";
                }
            }
        }
        
        if (title.equals("Error")){
            showDialog(title, message);
            return false;
        }else
            return true;
    }
    
    /**
     * ユーザ登録API
     * @param get_name
     * @param get_pass
     * @param get_email
     */
    public void userAddApi(String get_name, String get_pass, String get_email){
        // HTTP通信
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        
        params.put("username", get_name);
        params.put("password", get_pass);
        params.put("email", get_email);
        params.put("kentei_id", "3");
                
        String url = "http://api.sakumon.jp/users/add.json";
                
        client.post(url, params, new JsonHttpResponseHandler(){ // client.get を client.post にすれば、POST通信もできます
            @Override
            public void onStart(){
                // 通信開始時の処理
            }

            @Override
            public void onSuccess(JSONObject json){
                // 通信成功時の処理 
                Log.v("response", json.toString());
                Toast.makeText(NewUserActivity.this, json.toString(), Toast.LENGTH_LONG).show();
            }
            
            @Override
            public void onFailure(Throwable e, String response){
                // 通信失敗時の処理
            }

            @Override
            public void onFinish(){
                // 通信終了時の処理
            }
        });
    }
    
    /**
     * ユーザ認証API
     * @param get_name
     */
    public void userAttestApi(String get_name){
        // HTTP通信
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        
        params.put("username", get_name);
        params.put("kentei_id", "3");
        
        String url = "http://api.sakumon.jp/generate_token.json";
        
        client.post(url, params, new JsonHttpResponseHandler(){ // client.get を client.post にすれば、POST通信もできます
            @Override
            public void onStart(){
                // 通信開始時の処理
            }

            @Override
            public void onSuccess(JSONObject json){
                // 通信成功時の処理 
                Log.v("response", json.toString());
                Toast.makeText(NewUserActivity.this, json.toString(), Toast.LENGTH_LONG).show();
            }
               
            @Override
            public void onFailure(Throwable e, String response){
                // 通信失敗時の処理
            }

            @Override
            public void onFinish(){
                // 通信終了時の処理
            }
        });
    }

    public void showDialog(String title, String message) {
        MyDialogFragment newFragment = MyDialogFragment.newInstance(title,
                message, 0);
        // リスナーセット
        newFragment.setDialogListener(this);
        // ここでCancelable(false)をしないと効果が無い
        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void doPositiveClick() {
        // TODO Auto-generated method stub

    }

    @Override
    public void doNegativeClick() {
        // TODO Auto-generated method stub

    }
}
