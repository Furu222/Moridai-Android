package jp.sakumon.moridai;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

public class TestGradeListActivity extends Activity {
	
	MyExpandListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grade_list);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// 親グループと子グループのリスト
		List<Map<String, String>> groupList = new ArrayList<Map<String, String>>();
		List<List<Map<String, String>>> childList = new ArrayList<List<Map<String,String>>>();
		
		// 親グループの文字定義
		final Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		for (int i = 2006; i < year; i++){
			 Map<String, String> groupElement = new HashMap<String, String>();
			 groupElement.put("GROUP_TITLE", i + "年");
			 groupList.add(groupElement);
			// Childのリスト
			List<Map<String, String>> childElements = new ArrayList<Map<String, String>>();
			int grade = 1; // 級
			if (i == 2006){ // 2006年の場合は3級だけ
				grade = 3;
			}else if (i == 2007 || i == 2008){ // 2007年の場合は2級だけ
				grade = 2;
			}
			// 子リストの文字定義
			for (int j = grade; j <= 3; j++){
				Map<String, String> child = new HashMap<String, String>();
				child.put("CHILD_TITLE", j + "級");
				childElements.add(child);
			}
			childList.add(childElements);
		}
		// 入門編の設定
		Map<String, String> groupElement = new HashMap<String, String>();
		groupElement.put("GROUP_TITLE", "入門編");
		groupList.add(groupElement);
		List<Map<String, String>> childElements = new ArrayList<Map<String, String>>();
		Map<String, String> child = new HashMap<String, String>();
		child.put("CHILD_TITLE", "もりけん入門編");
		childElements.add(child);
		childList.add(childElements);
		
		// アダプター
		adapter = new MyExpandListAdapter(this, groupList, childList);
		ExpandableListView lv = (ExpandableListView)findViewById(R.id.expandableListView1);
		lv.setAdapter(adapter);
		
		// リスト項目（子）がクリックされた時の処理
		lv.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// 子要素を取得
				Object item = adapter.getChild(groupPosition, childPosition);
				// 親要素を取得
				Object parentItem = adapter.getGroup(groupPosition);
				int year; // 年
				int grade; // 級
				if (parentItem.toString().equals("入門編")){
					year = 0;
					grade = 0;
				}else{ // 入門編じゃないときは数値を取り出す
					year = Integer.parseInt(parentItem.toString().replaceAll("[^0-9]",""));
					grade = Integer.parseInt(item.toString().replaceAll("[^0-9]", ""));
				}
				Intent intent = new Intent(TestGradeListActivity.this, TestListActivity.class);
				
				intent.putExtra("year", year); // 対象の年と級を設定
				intent.putExtra("grade", grade);
				
				startActivity(intent);
				
				return false;
			}
		});
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
}
