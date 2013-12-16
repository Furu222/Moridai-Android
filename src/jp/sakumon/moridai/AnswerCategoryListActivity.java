package jp.sakumon.moridai;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AnswerCategoryListActivity extends Activity {
	private int year; // 年
	private int grade; // 級
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_list);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		// 前の画面から値を取得
		Intent intent = getIntent();
		year = intent.getIntExtra("year", -1);
		grade = intent.getIntExtra("grade", -1);

		// リストビュー作成
		setContentsListView();
	}

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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) { // if使うとエラー（itemがinteger形式なため）
		case android.R.id.home: // アプリアイコン（ホームアイコン）を押した時の処理
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}
		return true;
	}
	
	/**
	 * 端末の戻るボタンを押した時の処理
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK){
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}
		return true;
	}

	/**
	 * ListViewの作成
	 */
	private void setContentsListView() {
		ListView lv = (ListView) findViewById(R.id.CategoryList);
		String[] category = setCategory(year); // 文字列読み込み

		ArrayAdapter<String> adapter = (new ArrayAdapter<String>(this,
				R.layout.row, category) {
			/**
			 * GetViewをオーバーライドして背景色を交互に変える
			 */
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);

				if (position % 2 == 0) {
					view.setBackgroundResource(R.drawable.listitem_color1);
				} else {
					view.setBackgroundResource(R.drawable.listitem_color2);
				}

				return view;
			}
		});

		/**
		 * リストの項目をクリックしたときの処理
		 * 
		 * @params position タッチした場所（一番上は0）
		 */
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(AnswerCategoryListActivity.this, QuestionAnswerActivity.class);
				// Intentに値をセット
				intent.putExtra("category_id", position + 1);
				intent.putExtra("year", year);
				intent.putExtra("grade", grade);
				
				startActivity(intent);
			}
		});
		
		lv.setAdapter(adapter);
	}

	/**
	 * カテゴリーリストの文字列定義
	 * 
	 * @param year
	 *            問題の年度
	 * @return string 文字列
	 */
	public String[] setCategory(int year) {
		String[] guide = { "盛岡の食", "盛岡の名所", "盛岡の方言", "盛岡の偉人", "盛岡の産業", "その他" };
		String[] past = { "盛岡の現在", "盛岡の気候と地理", "盛岡の産業", "盛岡の文化", "盛岡の先人・著名人",
				"盛岡の歴史", "その他" };
		if (year == 0) { // 入門編の場合
			return guide;
		} else { // 過去問の場合
			return past;
		}
	}

}
