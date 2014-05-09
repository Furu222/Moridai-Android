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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

public class QuestionMakeActivity extends Activity implements DialogListener {
    Integer category_id = 0; // カテゴリーID
    String DialogSwitch = ""; // ダイアログの種類を識別

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_make);

        Spinner spinner = (Spinner) findViewById(R.id.categorySpinner);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        showDialog("オリジナル問題を作成！", "作問を終えたら、右上のボタンを押してください。", 0);

        /**
         * Spinnerのアイテムが選択された時に呼び出されるコールバック関数
         */
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                category_id = position + 1; // 選択されたものの番号をIDに入れる
            }

            /**
             * Spinnerが何も選択されなかった場合
             */
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_question_make, menu);
        return true;
    }

    /**
     * メニューが選択されたときの処理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) { // if使うとエラー（itemがInt形式なため）
        case android.R.id.home: // アプリアイコン（ホームアイコン）を押した時の処理
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            break;
        case R.id.menu_make_check:
            createMakeQuestion(); // チェックが押されたら問題作成をする
            break;
        }
        return true;
    }

    /**
     * 問題作成関数
     */
    public void createMakeQuestion() {
        // EditText取得
        EditText questionEdit = (EditText) findViewById(R.id.questionTexitField);
        EditText answerEdit = (EditText) findViewById(R.id.answerMakeTextField);
        EditText descriptionEdit = (EditText) findViewById(R.id.descriptionMakeTextField);

        // null判定のために、Stringを取得
        String questionStr = questionEdit.getText().toString();
        String answerStr = answerEdit.getText().toString();
        String descriptionStr = descriptionEdit.getText().toString();

        // バリデーション確認
        validates(questionStr, answerStr, descriptionStr);
        
        if (!DialogSwitch.equals("Error")) { // 未入力の項目がない場合は、作問を行う
                 // HTTP通信
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();

            // プリファレンスからユーザIDを呼び出し
            Integer userId = new GetSharedPreferences().getUserId(this);
            params.put("user_id", Integer.toString(userId));
            params.put("question", questionStr);
            params.put("right_answer", answerStr);
            params.put("description", descriptionStr);
            params.put("category_id", String.valueOf(category_id));
            params.put("quiz_type", "private");

            String url = "http://sakumon.jp/app/maker/moridai/quiz_create.json";

            client.post(url, params, new MyJsonHttpResponseHandler(
                    new MyJsonResponseCallback() {

                        @Override
                        public void onStart() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onSuccess(JSONObject json) {
                            try {
                                String response = json.getString("response"); // サーバからの結果
                                if (response.equals("Saved")) { // 回答情報登録できたとき
                                    DialogSwitch = "add";
                                    String title = "登録完了！";
                                    String message = "問題の登録が完了しました！あなただけのオリジナル問題を更に作成しましょう！";
                                    showDialog(title, message, 0);
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

                        @Override
                        public void onFinish() {
                            // TODO Auto-generated method stub

                        }

                    }, this, 0, 1));
        }
    }

    /**
     * バリデーション機能
     * 
     * @param questionStr
     * @param answerStr
     * @param descriptionStr
     */
    public void validates(String questionStr, String answerStr,
            String descriptionStr) {
        if (questionStr.equals("") || answerStr.equals("")
                || descriptionStr.equals("")) {
            DialogSwitch = "Error";
            String title = "Error";
            String message = "問題文、答え、解説のいずれかが未入力です。もう一度確認して下さい。";
            showDialog(title, message, 0);
        } else {
            if (questionStr.length() < 3) {
                DialogSwitch = "Error";
                showDialog("Error", "問題文を3文字以上にしてください", 0);
            } else if (answerStr.length() < 3) {
                DialogSwitch = "Error";
                showDialog("Error", "解答文を3文字以上にしてください", 0);
            } else if (descriptionStr.length() < 3) {
                DialogSwitch = "Error";
                showDialog("Error", "解説文を3文字以上にしてください", 0);
            }
        }
    }

    /**
     * ダイアログ表示関数
     * 
     * @param type
     *            ダイアログタイプ 0:OKボタンのみ 1:OK, NGボタン
     */
    public void showDialog(String title, String message, int type) {
        MyDialogFragment newFragment = MyDialogFragment.newInstance(title,
                message, type);
        // リスナーセット
        newFragment.setDialogListener(this);
        if (DialogSwitch.equals("add")) { // この時はsetCancelable=falseにする
            newFragment.setCancelable(false);
        }
        newFragment.show(getFragmentManager(), "dialog");
    }

    /**
     * OKボタンをおした時
     */
    public void doPositiveClick() {
        if (DialogSwitch.equals("add")) {
            Intent intent = new Intent(QuestionMakeActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * NGボタンをおした時
     */
    @Override
    public void doNegativeClick() {

    }
}
