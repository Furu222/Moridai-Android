package jp.sakumon.moridai;

import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class QuestionConfirmActivity extends Activity implements OnClickListener, DialogListener{
	Button backbtn, topbtn; // ボタンを定義
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_confirm);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		backbtn = (Button)findViewById(R.id.testResultBack);
		backbtn.setOnClickListener(this);
		topbtn = (Button)findViewById(R.id.testEnd);
		topbtn.setOnClickListener(this);
		
		setViewQuestion();
	}

	/**
	 * Viewに問題をセット
	 */
	public void setViewQuestion(){
		// テストデータと、positionの値取得
		TestData testData = TestData.getInstance();
		SparseArray<HashMap<String, String>> questionData = testData.getRootData();
		Intent intent = getIntent();
		Integer position = intent.getIntExtra("position", -1);
		
		TextView question = (TextView)findViewById(R.id.questionConfirm);
		TextView answer = (TextView)findViewById(R.id.answerConfirmText);
		TextView description = (TextView)findViewById(R.id.descriptionConfirmText);
		
		question.setText(questionData.get(position).get("question"));
		answer.setText(questionData.get(position).get("right_answer"));
		description.setText(questionData.get(position).get("description"));
	}
	
	/**
	 * ボタンをクリックしたとき
	 */
	@Override
	public void onClick(View v) {
		if (v == backbtn){ // 前の画面に戻る場合
			finish();
		}else{ // TOPに戻る場合
		    String title = "トップページに戻ります";
            String message = "試験結果画面を終了し、トップページに戻ります。よろしいですか？";
            showDialog(title, message, 1);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_question_confirm, menu);
		return true;
	}

	/**
	 * メニューが選択されたときの処理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){ // if使うとエラー（itemがInt形式なため）
		case android.R.id.home:   // アプリアイコン（ホームアイコン）を押した時の処理
			finish();
			break;
	}
	return true;
	}
	
	/**
     * ダイアログ表示関数
     * @param type ダイアログタイプ 0:OKボタンのみ 1:OK, NGボタン
     */
    public void showDialog(String title, String message, int type){
        MyDialogFragment newFragment = MyDialogFragment.newInstance(title, message, type);
        // リスナーセット
        newFragment.setDialogListener(this);
        newFragment.show(getFragmentManager(), "dialog");
    }
    
    /**
     * OKボタンをおした時
     */
    public void doPositiveClick() {
        // データを初期化
        TestData testData = TestData.getInstance(); // テストデータクラスを読み込み
        SparseArray<HashMap<String, String>> questionData = new SparseArray<HashMap<String,String>>();
        testData.setRootData(questionData); // クラスに値を保存し、初期化
        
        Intent intent = new Intent(QuestionConfirmActivity.this, MainActivity.class);
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
