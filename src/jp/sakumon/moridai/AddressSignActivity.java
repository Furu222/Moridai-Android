package jp.sakumon.moridai;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddressSignActivity extends Activity implements OnClickListener, DialogListener {

	// ボタンを定義
	Button attendButton;
	Button addressSearchButton;
	Button sendButton;
	
	String DialogSwitch = ""; // ダイアログの種類を識別

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_sign);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Buttonにクリックリスナー登録
		attendButton = (Button) findViewById(R.id.attendButton);
		attendButton.setOnClickListener(this);
		addressSearchButton = (Button) findViewById(R.id.addressSearchButton);
		addressSearchButton.setOnClickListener(this);
		sendButton = (Button) findViewById(R.id.sendButton);
		sendButton.setOnClickListener(this);
	}

	/**
	 * ボタンが押された時
	 */
	@Override
	public void onClick(View v) {
		if (v == attendButton) {
		    // 個人情報についての注意アラート表示
		    DialogSwitch = "attend";
		    String title = getString(R.string.attend_button);
	        String message = getString(R.string.attend_alert_message);
			showDialog(title, message, 0);
		} else if (v == addressSearchButton) {
			searchAddress();
		} else if (v == sendButton) {
			sendForm();
		}
	}

	/**
	 * 住所検索
	 */
	public void searchAddress() {
		// 入力された郵便番号取得
		EditText postCodeEdit = (EditText) findViewById(R.id.postCode);
		String postCodeStr = postCodeEdit.getText().toString();

		// HTTP通信
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();

		// パラメータ設定
		params.put("zipcode", postCodeStr);
		String url = "http://zipcloud.ibsnet.co.jp/api/search";

		client.get(url, params, new MyJsonResponseHandler(this) {
			@Override
			public void onSuccess(JSONObject json) {
				try {
					// エラーコード取得
					Integer statusCode = Integer.parseInt(json
							.getString("status"));
					if (statusCode == 200) { // 成功している場合
						JSONArray resultsArray = json.getJSONArray("results");
						JSONObject result = resultsArray.getJSONObject(0);
						// 住所読み込み
						String newAddress = result.getString("address1");
						newAddress = newAddress + result.getString("address2");
						newAddress = newAddress + result.getString("address3");
						EditText addressEdit = (EditText) findViewById(R.id.address);
						addressEdit.setText(newAddress);
						// 住所にフォーカス移す
						addressEdit.requestFocus();
						addressEdit.setSelection(addressEdit.getText().length());
					} else if (statusCode == 400 || statusCode == 500) {
					    DialogSwitch = "Error";
			            String title = "Error";
			            String message = json.getString("message");
			            showDialog(title, message, 0);
					}
				} catch (JSONException e) {
				    DialogSwitch = "Error";
                    String title = "Error";
                    String message = "郵便番号が間違っている可能性があります。もう一度確認してみてください。";
                    showDialog(title, message, 0);
				}
			}

			@Override
			public void onFailure(Throwable e, String response) {
			    DialogSwitch = "Error";
                String title = "Error";
                String message = "エラーが発生しました。お手数ですが電波状況が良いところでもう一度操作をやり直してみて下さい。";
                showDialog(title, message, 0);
			}
		});
	}

	/**
	 * 応募するボタンを押した時の処理
	 */
	public void sendForm() {
	    DialogSwitch = "sendForm";
        String title = "確認";
        String message = "この入力内容で応募してよろしいですか？";
        showDialog(title, message, 1);
	}

	/**
	 * ユーザデータ登録
	 */
	public void sendUserData() {
		// EditText取得
		EditText nameEdit = (EditText) findViewById(R.id.name);
		EditText postCodeEdit = (EditText) findViewById(R.id.postCode);
		EditText addressEdit = (EditText) findViewById(R.id.address);

		// null判定のために、Stringを取得
		String nameStr = nameEdit.getText().toString();
		String postCodeStr = postCodeEdit.getText().toString();
		String addressStr = addressEdit.getText().toString();
		
		// どれかのEditTextがNullのときは、送信しない
		if (nameStr.equals("") || postCodeStr.equals("")
				|| addressStr.equals("")) {
		    DialogSwitch = "Error";
		    String message = "氏名、郵便番号、住所のいずれかが未入力です。もう一度確認して下さい。";
		    showDialog("Error", message, 0);
		} else if (nameStr.length() <= 1 || postCodeStr.length() < 7
				|| addressStr.length() < 5) {
		    DialogSwitch = "Error";
            String message = "氏名、郵便番号、住所のいずれかの値が間違っています。もう一度確認して下さい。";
            showDialog("Error", message, 0);
		} else { // 未入力の項目がない場合は、住所登録
			// HTTP通信
			AsyncHttpClient client = new AsyncHttpClient();
			RequestParams params = new RequestParams();

			// パラメータ設定
			params.put("name", nameStr);
			params.put("postcode", postCodeStr);
			params.put("address", addressStr);

			String url = "http://n0.x0.to/rskweb/moridai/user_entry.json";

			client.post(url, params, new MyJsonResponseHandler(this) {
				@Override
				public void onSuccess(JSONObject json) {
					try {
						String response = json.getString("response"); // サーバからの結果
						if (response.equals("Saved")) { // 回答情報登録できたとき
						    DialogSwitch = "sendUserData";
						    String title = "応募完了！";
						    String message = getString(R.string.send_user_data_message);
						    showDialog(title, message, 1);
						} else if (response.equals("Error")) { // 問題登録できないとき（サーバ側のエラー）
						    DialogSwitch = "Error";
				            String message = "サーバへのアクセスに失敗しました。お手数ですがやり直してみて下さい。";
				            showDialog("Error", message, 0);
						} else if (response.equals("Data is Empty")) { // 問題登録できないとき（プログラム側のエラー）
						    DialogSwitch = "Error";
                            String message = "データが送られていません。お手数ですが最初からやり直してみて下さい。";
                            showDialog("Error", message, 0);
						}
					} catch (JSONException e) {
					    DialogSwitch = "Error";
                        String message = "何らかのエラーが発生しました。お手数ですがもう一度やり直して下さい。";
                        showDialog("Error", message, 0);
					}
				}

				@Override
				public void onFailure(Throwable e, String response) {
				    DialogSwitch = "Error";
                    String message = "エラーが発生しました。お手数ですが電波状況が良いところでもう一度操作をやり直してみて下さい。";
                    showDialog("Error", message, 0);
				}
			});
		}
	}
	
    /**
     * ダイアログ表示関数
     * @param type ダイアログタイプ 0:OKボタンのみ 1:OK, NGボタン
     */
    public void showDialog(String title, String message, int type){
        MyDialogFragment newFragment = MyDialogFragment.newInstance(title, message, type);
        // リスナーセット
        newFragment.setDialogListener(this);
        if (DialogSwitch.equals("sendUserData")){ // この時はsetCancelable=falseにする
            newFragment.setCancelable(false);
        }
        newFragment.show(getFragmentManager(), "dialog");
    }
    
    /**
     * OKボタンをおした時
     */
    public void doPositiveClick() {
        if(DialogSwitch.equals("sendForm")){
            sendUserData();
        }else if (DialogSwitch.equals("sendUserData")){
            finish();
        }
    }

    /**
     * NGボタンをおした時
     */
    @Override
    public void doNegativeClick() {

    }

	/**
	 * メニュー作成
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_address_sign, menu);
		return true;
	}

	/**
	 * メニューが選択されたときの処理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) { // if使うとエラー（itemがInt形式なため）
		case android.R.id.home: // アプリアイコン（ホームアイコン）を押した時の処理
			finish();
			break;
		}
		return true;
	}
}
