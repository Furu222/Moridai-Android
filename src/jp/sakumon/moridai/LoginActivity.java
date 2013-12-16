package jp.sakumon.moridai;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class LoginActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		// ClicKListenerをセット
		ImageButton startButton = (ImageButton)findViewById(R.id.startButton);
		startButton.setOnClickListener(this);
		
		Integer userId = new GetSharedPreferences().getUserId(this);
		if (userId == -1){ // プリファレンスに値がないときは保存処理を呼び出し
			saveUserId(1);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	/**
	 * イメージボタンをクリックしたときの処理
	 */
	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		this.finish(); // Activityの終了（戻るボタンで戻らないようにする）
	}
	
	
	/**
	 * プレリファレンスにユーザIDを登録
	 */
	public void saveUserId(Integer userId){
		// プリファレンスの準備。今回はキーと値を保存出来るSharedPreferenceを使う。user_data というファイルに保存する
		// MODE_PRIVATEでこのアプリだけが使用できるように設定
		SharedPreferences pref = this.getSharedPreferences("user_data", Context.MODE_PRIVATE);
		
		// プリファレンスに書き込むためのEditorオブジェクト取得
		Editor editor = pref.edit();
		// user_id というキーで値を保存
		editor.putInt("user_id", userId);
		// ここで実際にファイルに書き込む
		editor.commit();
	}

}
