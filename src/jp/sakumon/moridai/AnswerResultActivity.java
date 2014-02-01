package jp.sakumon.moridai;

import jp.sakumon.moridai.MyJsonHttpResponseHandler.MyJsonResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AnswerResultActivity extends Activity implements android.view.View.OnClickListener, DialogListener{

	String questionId; // 問題のID
	Integer answerNum; // ユーザの答え（多肢選択式の場合）
	String answer; // ユーザの答え（一問一答の場合）
	Integer rightAnswerNum; // 正解番号
	Integer categoryId; // カテゴリーID
	String description; // 解説文
	String rightAnswerStr; // 正解選択肢
	Integer answerFlag; // 正解判定 0:正解, 1:不正解
	String format; // 問題形式
	Integer userId; // ユーザID
	private int year;
	private int grade;
	
	Button backButton;
	Button nextButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_answer_result);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// 前の画面から値を取得
		Intent intent = getIntent();
		
		backButton = (Button)findViewById(R.id.CategoryBackButton);
		backButton.setOnClickListener(this);
		nextButton = (Button)findViewById(R.id.AnswerNextButton);
		nextButton.setOnClickListener(this);
		
		year = intent.getIntExtra("year", -1);
		grade = intent.getIntExtra("grade", -1);
		
		// プリファレンスからユーザIDを呼び出し
		userId = new GetSharedPreferences().getUserId(this);
		questionId = intent.getStringExtra("questionId");
		format = intent.getStringExtra("format");
		
		if (format.equals("multiple-choice")){
			answerNum = intent.getIntExtra("answer", 0);
			rightAnswerNum = intent.getIntExtra("rightAnswerNum", 0);
		}else{
			answer = intent.getStringExtra("answer");
		}
		categoryId = intent.getIntExtra("categoryId", 0);
		description = intent.getStringExtra("description");
		rightAnswerStr = intent.getStringExtra("rightAnswerStr");
		
		if (format.equals("multiple-choice")){ // 多肢選択式の場合
			if (answerNum == rightAnswerNum){ // 正解した場合
				answerFlag = 1;
			}else{ // 不正解の場合
				answerFlag = 0;
			}
		}else{
			if (rightAnswerStr.equals(answer)){ // 正解の場合
				answerFlag = 1;
			}else{ // 不正解の場合
				answerFlag = 0;
			}
		}
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        
        params.put("user_id", Integer.toString(userId));
        params.put("question_id", questionId);
        params.put("category_id", Integer.toString(categoryId));
        params.put("answer_flag", Integer.toString(answerFlag));
        if (format.equals("multiple-choice")){ // 多肢選択式の場合
            params.put("answer_option", Integer.toString(answerNum));
        }else{
            params.put("answer_word", answer);
        }
        params.put("answer_type", "0");
        params.put("client_type", "Android");
        
        String url = "http://sakumon.jp/app/maker/moridai/answer_check.json";
        
        client.post(url, params, new MyJsonHttpResponseHandler(new MyJsonResponseCallback() {
            @Override
            public void onSuccess(JSONObject json) {
                // 通信成功時の処理
                try {
                    String response = json.getString("response"); // サーバからの結果
                    if (response.equals("Saved")){ // 回答情報登録できたとき
                        setContentView();
                    }else if (response.equals("Error")){ // 回答情報登録できないとき（サーバ側のエラー）
                        String title = "Error";
                        String message = "サーバへのアクセスに失敗しました。お手数ですがやり直してみて下さい。";
                        showDialog(title, message, 0);
                    }else if (response.equals("Data is Empty")){ // 回答情報登録できないとき（プログラム側のエラー）
                        String title = "Error";
                        String message = "データが送られていません。お手数ですが最初からやり直してみて下さい。";
                        showDialog(title, message, 0);
                    }
                } catch (JSONException e) {
                    //new AlertDialog.Builder(QuestionAnswerActivity.this).setTitle("Error").setMessage(e.toString()).setPositiveButton("OK", null).show();
                    String title = "Error";
                    String message = "何らかのエラーが発生しました。お手数ですがもう一度やり直して下さい。";
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

	/**
	 * メニュー作成
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
	/**
	 * メニューが選択されたときの処理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){ // if使うとエラー（itemがInt形式なため）
		case android.R.id.home:   // アプリアイコン（ホームアイコン）を押した時の処理
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
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
	 * Viewに値をセット
	 */
	public void setContentView(){
		ImageView AnswerFlagView = (ImageView)findViewById(R.id.AnswerFlagView);
		TextView AnswerText = (TextView)findViewById(R.id.AnswerText);
		TextView description = (TextView)findViewById(R.id.DescriptionText);
		
		// 正解か不正解画像をセット
		if (answerFlag == 1){ // 正解した場合
			AnswerFlagView.setImageResource(R.drawable.correct);
			AnswerFlagView.setContentDescription("正解");
		}else{ // 不正解のとき
			AnswerFlagView.setImageResource(R.drawable.wrong);
			AnswerFlagView.setContentDescription("残念！ハズレです・・・");
		}
		
		// 正解選択肢と解説をセット
		AnswerText.setText(rightAnswerStr);
		description.setText(this.description);
	}

	/**
	 * ボタンが押された時の処理
	 * @param v 押したボタン
	 */
	@Override
	public void onClick(View v) {
		if (v == backButton){ // カテゴリーに戻るボタンが押された時
			Intent intent = new Intent(this, AnswerCategoryListActivity.class);
			intent.putExtra("year", year); // 対象の年と級を設定
			intent.putExtra("grade", grade);
			startActivity(intent);
			finish();
		}else{ // 別の問題を解くボタンが押された時
			Intent intent = new Intent(this, QuestionAnswerActivity.class);
			intent.putExtra("category_id", categoryId); // カテゴリーIDを渡して同じカテゴリーの問題を解くようにする
			intent.putExtra("year", year); // 対象の年と級を設定
			intent.putExtra("grade", grade);
			startActivity(intent);
			finish();
		}
	}

}
