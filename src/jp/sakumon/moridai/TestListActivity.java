package jp.sakumon.moridai;

import java.util.HashMap;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TestListActivity extends Activity implements DialogListener{

	SparseArray<HashMap<String, String>> rootData;
	Integer[][] optionNum;
	private int year; // 年
	private int grade; // 級
	String DialogSwitch = ""; // ダイアログの種類を識別
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_list);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// 前の画面から値を取得
		Intent intent = getIntent();
		year = intent.getIntExtra("year", -1);
		grade = intent.getIntExtra("grade", -1);
		
		// テストデータ取得
		TestData testData = TestData.getInstance();
		rootData = testData.getRootData();
		optionNum = testData.getOptionNum();
		
		if (rootData.size() == 0){ // テストデータがない場合はHTTP通信で取得
			// HTTP通信をし、成功時にListView作成関数呼び出し
			
			// 最初にアラートを出して試験モードの案内
		    DialogSwitch = "message";
		    String title = "試験モードに挑戦！";
            String message = "試験モードでは、全ての問題を解いていきます。解答を終了したい場合は、右上のボタンを押して下さい。";
            showDialog(title, message, 0);
			
			AsyncHttpClient client = new AsyncHttpClient();
			
			String url;
			if (year == 0){ // 入門編の場合
				url = "http://n0.x0.to/rskweb/moridai/test.json";
			}else{
				url = "http://n0.x0.to/rskweb/moridai/pasttest/" + grade + "/" + year + ".json";
			}
			
			client.get(url, new MyJsonResponseHandler(this){
				@Override
				public void onSuccess(JSONObject json){
					// 通信成功時の処理
					try {
						JSONArray rootArray = json.getJSONArray("response");
						optionNum = new Integer[rootArray.length()][4]; // 選択肢番号配列のNew宣言
						
						for (int i = 0; i < rootArray.length(); i++){
							JSONObject jsonObject = rootArray.getJSONObject(i);
							JSONObject questionObject = jsonObject.getJSONObject("MoridaiQuestion");
							
							HashMap<String, String> data = new HashMap<String, String>(); // 一次元配列。問題データ1問を格納
							
							String format;
							if (year != 0){ // 過去問の場合はフォーマットをセット
								format = questionObject.getString("format");
							}else{ // 入門編の場合はMultiple-choiceに
								format = "multiple-choice";
							}
							
							if (format.equals("multiple-choice")){ // 多肢選択式の場合
								// 選択肢をランダムにする
								Random rand = new Random();
								// 配列の初期化
								for (int j = 0; j < 4; j++){
									if (j == 0){
										optionNum[i][j] = rand.nextInt(4) + 1;
									}else{
										optionNum[i][j] = 0; 
									}
								}
								while(optionNum[i][1] == 0 || optionNum[i][1] == optionNum[i][0]){
									optionNum[i][1] = rand.nextInt(4) + 1;
								}
								while(optionNum[i][2] == 0 || optionNum[i][2] == optionNum[i][0] || optionNum[i][2] == optionNum[i][1]){
									optionNum[i][2] = rand.nextInt(4) + 1;
								}
								for (int j = 1; j < 5; j++){
									if (j != optionNum[i][0] && j != optionNum[i][1] && j != optionNum[i][2]){
										optionNum[i][3] = j;
										break;
									}
								}
								// 選択肢を格納
								data.put("option1", questionObject.getString("option1"));
								data.put("option2", questionObject.getString("option2"));
								data.put("option3", questionObject.getString("option3"));
								data.put("option4", questionObject.getString("option4"));
								data.put("answer_option", String.valueOf(0)); // ユーザの解答情報
							}else{ // 一問一答の場合
								data.put("answer_word", "");
							}
								
							// 共通データを連想配列に格納
							data.put("id", questionObject.getString("id"));
							data.put("question", questionObject.getString("question"));
							data.put("right_answer", questionObject.getString("right_answer"));
							data.put("description", questionObject.getString("description"));
							data.put("category_id", questionObject.getString("category_id"));
							data.put("category_name", questionObject.getString("category_name"));
							data.put("format", format); // 問題毎の形式を記録
							data.put("answer_flag", String.valueOf(-1)); // 0:不回答、1：正解、-1：無回答
							
							rootData.put(i, data);
						}
						
						TestData testData = TestData.getInstance(); // テストデータクラスを読み込み
						testData.setRootData(rootData); // クラスに値を保存
						testData.setOptionNum(optionNum);
						
						setContentsListView(); // ListViewにデータを入れる
					} catch (JSONException e) {
					    DialogSwitch = "ErrorList";
					    String title = "Error";
			            String message = "何らかのエラーが発生しました。お手数ですがもう一度やり直して下さい。";
			            showDialog(title, message, 0);
					}
				}
			});
		}else{ // TestDataがある場合は、Viewにセット
			setContentsListView(); // ListViewにデータを入れる
		}
	}

	/**
	 * ListViewの作成
	 */
	private void setContentsListView() {
		ListView lv = (ListView)findViewById(R.id.TestList);
		
		String[] value = new String[rootData.size()];
		for (int i = 0; i < rootData.size(); i++){ // データの数だけ実行
			value[i] = "問題" + (i + 1);
		}
		
		ArrayAdapter<String> adapter = (new ArrayAdapter<String>(this, R.layout.row_checked, value){
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
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		Intent prevIntent = getIntent(); // ListViewのスクロール位置を指定
		int listPosition = prevIntent.getIntExtra("listPosition", -1);
		int y = prevIntent.getIntExtra("y", -1);
		if (listPosition != -1 && y != -1){
			lv.setSelectionFromTop(listPosition, y);
		}
		
		// 解答している問題は、チェックマークを入れる
		setCheckedListView(lv);
		
		/**
		 * リストの項目をクリックしたときの処理
		 * @param position タッチした場所（一番上は0）
		 */
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				// インテント処理（画面遷移）
				Intent intent = new Intent(TestListActivity.this, TestAnswerActivity.class);
				
				intent.putExtra("position", position); // どの問題かを記録
				
				// タッチでチェックさせないようにする。既に回答している場合は、何もしない
				ListView listView = (ListView)parent;
				Integer answer_flag = Integer.parseInt(rootData.get(position).get("answer_flag"));

				if (answer_flag == -1){ // 無回答（初めて問題を解く場合）
					listView.setItemChecked(position, false);
				}else{ // 無回答じゃない場合は、trueで変更させない
					listView.setItemChecked(position, true);
				}
				
				int listPosition = listView.getFirstVisiblePosition();
				int y = listView.getChildAt(0).getTop();
				
				intent.putExtra("year", year);
				intent.putExtra("grade", grade);
				intent.putExtra("listPosition", listPosition);
				intent.putExtra("y", y);
				
				startActivity(intent);
			}
		});
		
	}
	
	
	/**
	 * 解答済みの問題にチェックマークを入れる
	 */
	public void setCheckedListView(ListView lv){
		for (Integer i = 0; i < rootData.size(); i++){
			Integer answer_flag = Integer.parseInt(rootData.get(i).get("answer_flag"));
			if (answer_flag != -1){
				lv.setItemChecked(i, true);
			}
		}
	}
	
	/**
	 * 集計処理
	 */
	public void answerTotal(){
		int size = rootData.size(); // データ数
		int rightCount = 0; // 正解数
		int questionCount = 0; // 不正解問題数
		// プリファレンスからユーザIDを呼び出し
		Integer userId = new GetSharedPreferences().getUserId(this);
		SparseArray<HashMap<String, String>> questionData = new SparseArray<HashMap<String,String>>();
		// プログレスダイアログ
		ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("ネットワークに接続中です");
		progressDialog.setMessage("しばらくお待ちください");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // プログレスダイアログのスタイルを円スタイルに設定
		// プログレスダイアログのキャンセルが可能かどうかを設定（バックボタンでダイアログをキャンセルできないようにする）
        progressDialog.setCancelable(false);
        // プログレスダイアログを表示
        progressDialog.show();
        
		for (int i = 0; i < size; i++){
			Integer answer_flag = Integer.parseInt(rootData.get(i).get("answer_flag"));
			
			if (answer_flag != -1){ // 無回答じゃない場合、集計する
				// HTTP通信で回答情報送信
				AsyncHttpClient client = new AsyncHttpClient();
				RequestParams params = new RequestParams();
				
				params.put("user_id", Integer.toString(userId));
				params.put("question_id", rootData.get(i).get("id"));
				params.put("category_id", rootData.get(i).get("category_id"));
				params.put("answer_flag", rootData.get(i).get("answer_flag"));
				if(rootData.get(i).get("format").equals("multiple-choice")){ // 多肢選択式の場合
					params.put("answer_option", rootData.get(i).get("answer_option"));
				}else{ // 一問一答の場合
					params.put("answer_word", rootData.get(i).get("answer_word"));
				}
				params.put("answer_type", "1");
				params.put("client_type", "Android");
				
				String url = "http://n0.x0.to/rskweb/moridai/answer_check.json";
				
				client.post(url, params, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject json){
						// 通信成功時の処理
						try {
							String response = json.getString("response"); // サーバからの結果
							if (response.equals("Error")){ // 回答情報登録できないとき（サーバ側のエラー）
							    DialogSwitch = "Error";
	                            String message = "サーバへのアクセスに失敗しました。お手数ですがやり直してみて下さい。";
	                            showDialog("Error", message, 0);
							}else if (response.equals("Data is Empty")){ // 回答情報登録できないとき（プログラム側のエラー）
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
					public void onFailure(Throwable e, String response){
					    DialogSwitch = "Error";
	                    String message = "エラーが発生しました。お手数ですが電波状況が良いところでもう一度操作をやり直してみて下さい。";
	                    showDialog("Error", message, 0);
					}
				});
				if (answer_flag == 1){ // 正解の場合
					rightCount++; // カウント+1
				}else{ // 不正解のときは問題を記録
					Integer right_asnwer_num; // 正解番号
					String right_answer; // 正解選択肢
					
					if (rootData.get(i).get("format").equals("multiple-choice")){ // 多肢選択式の場合
						right_asnwer_num = Integer.parseInt(rootData.get(i).get("right_answer")); 
						if (right_asnwer_num == 1){
							right_answer = rootData.get(i).get("option1");
						}else if(right_asnwer_num == 2){
							right_answer = rootData.get(i).get("option2");
						}else if(right_asnwer_num == 3){
							right_answer = rootData.get(i).get("option3");
						}else{
							right_answer = rootData.get(i).get("option4");
						}
					}else{ // 一問一答の場合
						right_answer = rootData.get(i).get("right_answer");
					}
					
					HashMap<String, String> data = new HashMap<String, String>(); // 一次元配列。問題データ1問を格納
					// データを連想配列に格納
					data.put("question", rootData.get(i).get("question"));
					data.put("right_answer", right_answer);
					data.put("description", rootData.get(i).get("description"));
					
					questionData.put(questionCount, data);
					questionCount++; // 不正解数の問題カウント
				}
			}
		}
		progressDialog.dismiss(); // ProgressDialogを閉じる
		
		Intent intent = new Intent(TestListActivity.this, TestResultActivity.class);
		intent.putExtra("size", size); // データ件数
		intent.putExtra("rightCount", rightCount); // 正解数
		intent.putExtra("year", year);
		intent.putExtra("grade", grade);
		TestData testData = TestData.getInstance(); // テストデータクラスを読み込み
		testData.setRootData(questionData); // クラスに値を保存
		startActivity(intent);
		TestListActivity.this.finish();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_list_menu, menu);
		return true;
	}
	
	/**
	 * メニューが選択されたときの処理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){ // if使うとエラー（itemがinteger形式なため）
		case android.R.id.home:   // アプリアイコン（ホームアイコン）を押した時の処理
		    DialogSwitch = "Back";
		    String title = "試験を中止します";
            String message = "試験を中止してトップページに戻ります。よろしいですか？";
            showDialog(title, message, 1);
			break;
		case R.id.menu_test_check:
		    DialogSwitch = "finish";
            String title2 = "試験を終了します";
            String message2 = "試験を終了します。よろしいですか？";
            showDialog(title2, message2, 1);
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
		    DialogSwitch = "Back";
            String title = "試験を中止します";
            String message = "試験を中止してトップページに戻ります。よろしいですか？";
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
        if(DialogSwitch.equals("ErrorList")){
            finish();
        }else if (DialogSwitch.equals("Back")){
            // データを初期化
            TestData testData = TestData.getInstance(); // テストデータクラスを読み込み
            rootData = new SparseArray<HashMap<String,String>>();
            testData.setRootData(rootData); // クラスに値を保存
            
            Intent intent = new Intent(TestListActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else if (DialogSwitch.equals("finish")){
            answerTotal(); // 集計処理へ
        }
    }

    /**
     * NGボタンをおした時
     */
    @Override
    public void doNegativeClick() {

    }
}
