package jp.sakumon.moridai;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import jp.sakumon.moridai.MyJsonHttpResponseHandler.MyJsonResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class QuestionAnswerActivity extends Activity implements OnClickListener, DialogListener{
	Map<String, String> data = new HashMap<String, String>(); // JSONデータを格納する連想配列
	ImageButton option1Button, option2Button, option3Button, option4Button; // 各選択肢のボタン
	EditText answerEditText;
	Button singleAnswerButton;
	Integer[] optionNum = new Integer[4]; // ランダム表示するための配列
	private int year;
	private int grade;
	String format; // 問題のフォーマット
	Integer userId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent(); // Intent取得
		// 前の画面のデータ取得
		int category_id = intent.getIntExtra("category_id", 0);
		// プリファレンスからユーザIDを呼び出し
		userId = new GetSharedPreferences().getUserId(this);
		year = intent.getIntExtra("year", -1);
		grade = intent.getIntExtra("grade", -1);
		
        // HTTP通信
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        if (category_id == 0){ // IntentでうまくカテゴリーIDを渡されてないとき
            String title = "Error";
            String message = "エラーが発生しました。お手数ですがもう一度操作をやり直してみて下さい。前の画面へ戻ります。";
            showDialog(title, message, 0);
        }
        String url = new String();
        if (year == 0){ // 入門編の場合
            params.put("category_id", Integer.toString(category_id));
            params.put("user_id", Integer.toString(userId));
            url = "http://sakumon.jp/app/maker/moridai/question.json";
        }else{
            params.put("grade", Integer.toString(grade));
            params.put("year", Integer.toString(year));
            params.put("category_id", Integer.toString(category_id));
            url = "http://sakumon.jp/app/maker/moridai/pasttest/" + grade + "/" + year +"/" + category_id + ".json";
        }
        
        client.get(url, params, new MyJsonHttpResponseHandler(new MyJsonResponseCallback() {
            @Override
            public void onSuccess(JSONObject json) {
                // 通信成功時の処理
                try {
                    JSONObject rootObject = json.getJSONObject("response");
                    JSONObject object = rootObject.getJSONObject("MoridaiQuestion");
                    
                    if (year != 0){ // 過去問の場合はフォーマットをセット
                        format = object.getString("format");
                    }else{ // 入門編の場合はMultiple-choiceに
                        format = "multiple-choice";
                    }
                    // 共通データ格納
                    data.put("id", object.getString("id"));
                    data.put("question", object.getString("question"));
                    data.put("right_answer", object.getString("right_answer"));
                    data.put("description", object.getString("description"));
                    data.put("category_id", object.getString("category_id"));
                    
                    if (format.equals("multiple-choice")){ // 多肢選択式の場合(入門編も)
                        // Viewをセット
                        setContentView(R.layout.multiple_answer);

                        // ClicKListenerをセット
                        option1Button = (ImageButton)findViewById(R.id.option1);
                        option1Button.setOnClickListener(QuestionAnswerActivity.this);
                        option2Button = (ImageButton)findViewById(R.id.option2);
                        option2Button.setOnClickListener(QuestionAnswerActivity.this);
                        option3Button = (ImageButton)findViewById(R.id.option3);
                        option3Button.setOnClickListener(QuestionAnswerActivity.this);
                        option4Button = (ImageButton)findViewById(R.id.option4);
                        option4Button.setOnClickListener(QuestionAnswerActivity.this);
                        
                        // 選択肢データを連想配列に格納
                        data.put("option1", object.getString("option1"));
                        data.put("option2", object.getString("option2"));
                        data.put("option3", object.getString("option3"));
                        data.put("option4", object.getString("option4"));
                        
                    }else{ // 一問一答の場合
                        // Viewをセット
                        setContentView(R.layout.single_answer);
                        
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
                        singleAnswerButton.setOnClickListener(QuestionAnswerActivity.this);
                    }
                    setViewProblem(); // 連想配列をビューに表示
                } catch (JSONException e) {
                    String title = "Error";
                    String message = "このカテゴリーは問題がありません。違うカテゴリーを選択してください。";
                    showDialog(title, message, 0);
                }
            }
            
            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(Throwable e, String response) {
                String message = "エラーが発生しました。お手数ですが電波状況が良いところでもう一度操作をやり直してみて下さい。";
                showDialog("Error", message, 0);
            }
        }, this, 0, 1));
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
			finish(); // 前のページに戻る
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
        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(), "dialog");
    }
    
    /**
     * OKボタンをおした時
     */
    public void doPositiveClick() {
        finish();
    }

    /**
     * NGボタンをおした時
     */
    @Override
    public void doNegativeClick() {

    }
	
	/**
	 * Viewに取得したデータをパースしてセット
	 * @param サーバから取得したJSONデータを連想配列に格納したもの
	 */
	public void setViewProblem(){
		TextView question = (TextView)findViewById(R.id.question);
		question.setText(data.get("question"));
		if (format.equals("multiple-choice")){ // 多肢選択式の場合
			TextView option1 = (TextView)findViewById(R.id.option_text1);
			TextView option2 = (TextView)findViewById(R.id.option_text2);
			TextView option3 = (TextView)findViewById(R.id.option_text3);
			TextView option4 = (TextView)findViewById(R.id.option_text4);
			
			// 選択肢をランダムにする
			Random rand = new Random();
			// 配列の初期化
			for (int i = 0; i < 4; i++){
				if (i == 0){
					optionNum[0] = rand.nextInt(4) + 1;
				}else{
					optionNum[i] = 0; 
				}
			}
			while(optionNum[1] == 0 || optionNum[1] == optionNum[0]){
				optionNum[1] = rand.nextInt(4) + 1;
			}
			while(optionNum[2] == 0 || optionNum[2] == optionNum[0] || optionNum[2] == optionNum[1]){
				optionNum[2] = rand.nextInt(4) + 1;
			}
			for (int i = 1; i < 5; i++){
				if (i != optionNum[0] && i != optionNum[1] && i != optionNum[2]){
					optionNum[3] = i;
					break;
				}
			}
			String[] optionStr = new String[4]; // 選択肢の文字列
			// 各番号に対する選択肢を代入
			for (int i = 0; i < 4; i++){
				switch(optionNum[i]) {
					case 1:
						optionStr[i] = data.get("option1");
						break;
					case 2:
						optionStr[i] = data.get("option2");
						break;
					case 3:
						optionStr[i] = data.get("option3");
						break;
					case 4:
						optionStr[i] = data.get("option4");
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
	 * イメージボタン（各選択肢）と、確定ボタンを押した時の処理
	 * @param v 押したボタンのID
	 */
	@Override
	public void onClick(View v) {
		String rightAnswerStr = null;
		// インテント処理（画面遷移）
		Intent intent = new Intent(this, AnswerResultActivity.class);
		
		if (format.equals("multiple-choice")){
			Integer answerNum = 0;
			if (v == option1Button){
				answerNum = optionNum[0];
			}else if (v == option2Button){
				answerNum = optionNum[1];
			}
			else if (v == option3Button){
				answerNum = optionNum[2];
			}else if (v == option4Button){
				answerNum = optionNum[3];
			}
			
			// 正解の選択肢を格納
			Integer rightNum = Integer.parseInt(data.get("right_answer"));
			if (rightNum == 1){
				rightAnswerStr = data.get("option1");
			}else if (rightNum == 2){
				rightAnswerStr = data.get("option2");
			}else if (rightNum == 3){
				rightAnswerStr = data.get("option3");
			}else if (rightNum == 4){
				rightAnswerStr = data.get("option4");
			}
			// 正解選択肢番号と解答選択肢番号をセット
			intent.putExtra("answer", answerNum);
			intent.putExtra("rightAnswerNum", rightNum);
		}else{ // singleの場合は入力した答えをセット
			intent.putExtra("answer", answerEditText.getText().toString());
			rightAnswerStr = data.get("right_answer");
		}
		// Intentに値をセット
		intent.putExtra("format", format);
		intent.putExtra("questionId", data.get("id"));
		intent.putExtra("description", data.get("description"));
		intent.putExtra("rightAnswerStr", rightAnswerStr);
		intent.putExtra("categoryId", Integer.parseInt(data.get("category_id")));
		intent.putExtra("year", year);
		intent.putExtra("grade", grade);
		
		startActivity(intent);
		finish();
	}
}
