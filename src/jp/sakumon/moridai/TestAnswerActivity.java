package jp.sakumon.moridai;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class TestAnswerActivity extends Activity implements OnClickListener{
	
	ImageButton option1Button, option2Button, option3Button, option4Button; // 各選択肢のボタン
	SparseArray<HashMap<String, String>> rootData = new SparseArray<HashMap<String, String>>(); // テストデータ
	Integer[][] optionNum; // 選択肢番号用配列
	Integer position; // どの問題か
	EditText answerEditText;
	Button singleAnswerButton;
	String format; // 問題のフォーマット
	int year;
	int grade;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent(); // Intent取得
		// テストデータと、positionの値取得
		TestData testData = TestData.getInstance();
		rootData = testData.getRootData();
		optionNum = testData.getOptionNum();
		position = intent.getIntExtra("position", -1);
		year = intent.getIntExtra("year", -1);
		grade = intent.getIntExtra("grade", -1);
		
		// 問題形式セット
		format = rootData.get(position).get("format");
		
		if (format.equals("multiple-choice")){ // 多肢選択式の場合(入門編も)
			setContentView(R.layout.multiple_answer);

			// ClicKListenerをセット
			option1Button = (ImageButton)findViewById(R.id.option1);
			option1Button.setOnClickListener(this);
			option2Button = (ImageButton)findViewById(R.id.option2);
			option2Button.setOnClickListener(this);
			option3Button = (ImageButton)findViewById(R.id.option3);
			option3Button.setOnClickListener(this);
			option4Button = (ImageButton)findViewById(R.id.option4);
			option4Button.setOnClickListener(this);
		}else{
			// Viewをセット
			setContentView(R.layout.single_answer);
			
			// 既に回答済みの場合は前の回答結果を表示
			if (Integer.parseInt(rootData.get(position).get("answer_flag")) != -1){ // 無回答じゃない場合
				TextView tv = (TextView)findViewById(R.id.answerCheckLabel);
				tv.setText(rootData.get(position).get("answer_word"));
			}
			// EditTextの入力イベントを取得
			answerEditText = (EditText)findViewById(R.id.answerEditText);
			// 答えをTextViewに書き換える
			answerEditText.addTextChangedListener(new TextWatcher(){
				@Override
				public void afterTextChanged(Editable s) {
					TextView tv = (TextView)findViewById(R.id.answerCheckLabel);
					if (s.toString().equals("")){ // 空白のとき
						tv.setText(R.string.single_answer_check_label);
					}else{
						tv.setText(s);
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s,
						int start, int count, int after) {
				}

				@Override
				public void onTextChanged(CharSequence s,
						int start, int before, int count) {
					
				}
			});
			// ボタンを設定
			singleAnswerButton = (Button)findViewById(R.id.singleAnswerButton);
			singleAnswerButton.setOnClickListener(TestAnswerActivity.this);
		}
		setViewProblem(); // Viewをセット
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater()
				.inflate(R.menu.main_menu, menu);
		return true;
	}

	
	/**
	 * メニューが選択されたときの処理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){ // if使うとエラー（itemがInt形式なため）
		case android.R.id.home:   // アプリアイコン（ホームアイコン）を押した時の処理
			Intent intent = new Intent(this, TestListActivity.class);
			Intent prevIntent = getIntent(); // 前のListViewのスクロール位置を指定
			int listPosition = prevIntent.getIntExtra("listPosition", -1);
			int y = prevIntent.getIntExtra("y", -1);
			intent.putExtra("listPosition", listPosition);
			intent.putExtra("y", y);
			intent.putExtra("year", year);
			intent.putExtra("grade", grade);
			
			startActivity(intent);
			finish();
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
			Intent intent = new Intent(this, TestListActivity.class);
			Intent prevIntent = getIntent(); // 前のListViewのスクロール位置を指定
			int listPosition = prevIntent.getIntExtra("listPosition", -1);
			int y = prevIntent.getIntExtra("y", -1);
			intent.putExtra("listPosition", listPosition);
			intent.putExtra("y", y);
			intent.putExtra("year", year);
			intent.putExtra("grade", grade);
			
			startActivity(intent);
			finish();
			
			return true;
		}
		return false;
	}
	
	/**
	 * Viewに取得したデータをパースしてセット
	 * @param サーバから取得したJSONデータを連想配列に格納したもの
	 */
	public void setViewProblem(){
		TextView question = (TextView)findViewById(R.id.question);
		// TextViewに値をセット
		question.setText(rootData.get(position).get("question"));
		
		if (format.equals("multiple-choice")){ // 多肢選択式の場合(入門編も)
			TextView option1 = (TextView)findViewById(R.id.option_text1);
			TextView option2 = (TextView)findViewById(R.id.option_text2);
			TextView option3 = (TextView)findViewById(R.id.option_text3);
			TextView option4 = (TextView)findViewById(R.id.option_text4);
			
			// 選択肢をoptionNumに従ってランダム配置
			String[] optionStr = new String[4]; // 選択肢の文字列
			// 各番号に対する選択肢を代入
			for (int i = 0; i < 4; i++){
				switch(optionNum[position][i]) {
					case 1:
						optionStr[i] = rootData.get(position).get("option1");
						break;
					case 2:
						optionStr[i] = rootData.get(position).get("option2");
						break;
					case 3:
						optionStr[i] = rootData.get(position).get("option3");
						break;
					case 4:
						optionStr[i] = rootData.get(position).get("option4");
						break;
				}
			}
			int flg = 0; // 文字数オーバーフラグ
			for (int i = 0; i < 3; i++){
				if (optionStr[i].length() >= 30){
					flg = 1;
				}
			}
			if (flg == 1){ // 一つでも文字数オーバーしていた場合
				option1.setTextAppearance(this, android.R.style.TextAppearance_Small);
				option2.setTextAppearance(this, android.R.style.TextAppearance_Small);
				option3.setTextAppearance(this, android.R.style.TextAppearance_Small);
				option4.setTextAppearance(this, android.R.style.TextAppearance_Small);
			}
			option1.setText(optionStr[0]);
			option2.setText(optionStr[1]);
			option3.setText(optionStr[2]);
			option4.setText(optionStr[3]);
		}
	}

	/**
	 * イメージボタン（各選択肢）を押した時の処理
	 * @param v 押したボタンのID
	 */
	@Override
	public void onClick(View v) {
		Integer answerNum = 0;
		Integer answerFlag = 0;
		
		if (format.equals("multiple-choice")){ // 多肢選択式の場合の正解判定
			if (v == option1Button){
				answerNum = optionNum[position][0];
			}else if (v == option2Button){
				answerNum = optionNum[position][1];
			}
			else if (v == option3Button){
				answerNum = optionNum[position][2];
			}else if (v == option4Button){
				answerNum = optionNum[position][3];
			}
			// 正解判定
			if (answerNum == Integer.parseInt(rootData.get(position).get("right_answer"))){
				answerFlag = 1;
			}else{
				answerFlag = 0;
			}
		}else{ // 一問一答の場合の正解判定
			String answer = answerEditText.getText().toString();
			if (rootData.get(position).get("right_answer").equals(answer)){
				answerFlag = 1;
			}else{
				answerFlag = 0;
			}
		}
		
		HashMap<String, String> data = new HashMap<String, String>(); // 一次元配列。問題データ1問を格納
		data = rootData.get(position);
		
		// データに正解判定を記録
		data.put("answer_flag", String.valueOf(answerFlag));
		if (format.equals("multiple-choice")){ // 多肢選択式の場合の正解判定
			data.put("answer_option", String.valueOf(answerNum));
		}else{
			data.put("answer_word", answerEditText.getText().toString());
		}
		
		rootData.put(position, data);
		// テストデータを保存
		TestData testData = TestData.getInstance();
		testData.setRootData(rootData);
		
		Intent intent;
		// 画面遷移
		if (position == (rootData.size() -1)){ // 最後の問題の場合はListに戻る
			intent = new Intent(this, TestListActivity.class);
		}else{ // 最後の問題以外の場合は次の問題へ
			intent = new Intent(this, TestAnswerActivity.class);
			intent.putExtra("position", position + 1); // どの問題かを記録
		}
		Intent prevIntent = getIntent(); // 前のListViewのスクロール位置を指定
		int listPosition = prevIntent.getIntExtra("listPosition", -1);
		int y = prevIntent.getIntExtra("y", -1);
		intent.putExtra("listPosition", listPosition);
		intent.putExtra("y", y);
		intent.putExtra("year", year);
		intent.putExtra("grade", grade);
		
		startActivity(intent);	
		finish();
		//Toast.makeText(this, "問題" + (position + 1) + "に解答しました。", Toast.LENGTH_SHORT).show();
	}

}
