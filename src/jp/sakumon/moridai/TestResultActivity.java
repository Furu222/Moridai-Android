package jp.sakumon.moridai;

import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class TestResultActivity extends Activity implements OnClickListener, DialogListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_result);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// 住所登録ボタンを非表示にする
		Button signButton = (Button)findViewById(R.id.addressSignButton);
		signButton.setVisibility(View.GONE);
		
		setContentView(); // Viewに値をセット
	}
	
	public void setContentView(){
		Intent intent = getIntent(); // 前の画面からデータ受け取り
		int size = intent.getIntExtra("size", 0);
		int rightCount = intent.getIntExtra("rightCount", -1);
		int year = intent.getIntExtra("year", -1);
		int grade = intent.getIntExtra("grade", -1);
		
		TextView rightCountText = (TextView)findViewById(R.id.rightCount);
		TextView resultText = (TextView)findViewById(R.id.resultText);
		
		String text = size + "問中" + rightCount + "問";
		rightCountText.setText(text);
		double rightResult = (double)rightCount / (double)size * 100.0;
		// 住所登録ボタンの設定
//		Button signButton = (Button)findViewById(R.id.addressSignButton);
//		signButton.setOnClickListener(this);
		if (rightResult >= 70.0){
			if (year == 0){ // 入門編の場合
				text = "もりけん入門クイズに合格しました！！";
			}else{
				text = year + "年の" + grade + "級に合格しました！！";
			}
		}else{
			text = "不合格です・・・";
//			// 住所登録ボタンを非表示にする
//			signButton.setVisibility(View.GONE);
		}
		resultText.setText(text);
		
		setContentListView(); // 不合格問題をリストに追加
	}
	
	public void setContentListView(){
		ListView lv = (ListView)findViewById(R.id.questionList);
		
		// 不正解問題リスト読み込み
		TestData testData = TestData.getInstance();
		SparseArray<HashMap<String, String>> questionData = testData.getRootData();
		
		if (questionData.size() != 0){ // 不正解問題がある場合
			String[] value = new String[questionData.size()];
			for (int i = 0; i < questionData.size(); i++){ // データの数だけ実行
				value[i] = questionData.get(i).get("question");
			}
			ArrayAdapter<String> adapter = (new ArrayAdapter<String>(this, R.layout.row, value){
				/**
				 * GetViewをオーバーライドして背景色を交互に変える
				 */
				@Override
				public View getView(int position, View convertView, ViewGroup parent){
					View view = super.getView(position, convertView, parent);
					
					if (position % 2 == 0){
						view.setBackgroundResource(R.drawable.listitem_color1);
					}else{
						view.setBackgroundResource(R.drawable.listitem_color2);
					}
					
					return view;
				}
			});
			lv.setAdapter(adapter);
			
			/**
			 * リストの項目をクリックしたときの処理
			 * @param position タッチした場所（一番上は0）
			 */
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id){
					// インテント処理（画面遷移）
					Intent intent = new Intent(TestResultActivity.this, QuestionConfirmActivity.class);
					intent.putExtra("position", position); // どの問題かを記録
					
					startActivity(intent);
				}
			});
		}
	}
	
	/**
	 * ボタンを押した時の処理
	 * @param v ボタンのID
	 */
	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		Intent intent = new Intent(this, AddressSignActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_test_result, menu);
		return true;
	}
	
	/**
	 * メニューが選択されたときの処理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){ // if使うとエラー（itemがInt形式なため）
		case android.R.id.home:   // アプリアイコン（ホームアイコン）を押した時の処理
		    String title = "トップページへ戻ります";
            String message = "トップページに戻ります。よろしいですか？";
            showDialog(title, message, 1);
			
			break;
		}
		return true;
	}
	
	/**
	 * 端末の戻るボタンを押した時の処理
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK){
		    String title = "トップページへ戻ります";
            String message = "トップページに戻ります。よろしいですか？";
            showDialog(title, message, 1);
			
			return true;
		}
		return false;
	}
	
	/**
     * ダイアログ表示関数
     * @param type ダイアログタイプ 0:OKボタンのみ 1:OK, NGボタン
     */
    public void showDialog(String title, String message, int type){
        MyDialogFragment newFragment = MyDialogFragment.newInstance(title, message, type);
        // リスナーセット
        newFragment.setDialogListener(this);
        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(), "dialog");
    }
    
    /**
     * OKボタンをおした時
     */
    public void doPositiveClick() {
        // データを初期化
        TestData testData = TestData.getInstance(); // テストデータクラスを読み込み
        SparseArray<HashMap<String, String>> rootData = new SparseArray<HashMap<String,String>>();
        testData.setRootData(rootData); // クラスに値を保存
        
        Intent intent = new Intent(TestResultActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * NGボタンをおした時
     */
    @Override
    public void doNegativeClick() {

    }
}
