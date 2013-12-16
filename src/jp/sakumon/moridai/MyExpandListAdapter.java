package jp.sakumon.moridai;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class MyExpandListAdapter extends BaseExpandableListAdapter{
	// 親グループと子グループのリスト
	private List<Map<String, String>> groupList;
	private List<List<Map<String, String>>> childList;
	private Activity activity;
	private LayoutInflater inflater;
	
	// コンストラクタ
	public MyExpandListAdapter(Activity activity,  List<Map<String, String>> groupList, List<List<Map<String, String>>> childList){
		this.activity = activity;
		this.inflater = activity.getLayoutInflater();
		this.groupList = groupList;
		this.childList = childList;
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childList.get(groupPosition).get(childPosition).get("CHILD_TITLE");
	}
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// 子要素のView作成
		// convertViewがNullの時だけインフレート（再利用）
		if (convertView == null){
			convertView = this.inflater.inflate(R.layout.row, null);
		}
		// 子要素の数取得
		int count = getChildrenCount(groupPosition);
		// 級ごとに色分け
		if (count == 1){ // 2006年の場合（3級のみ）
			convertView.setBackgroundColor(activity.getResources().getColor(R.color.lightslategray));
		}else if (count == 2){ // 2007年の場合（2級まで）
			if (childPosition == 0){
				convertView.setBackgroundColor(activity.getResources().getColor(R.color.blueviolet));
			}else{
				convertView.setBackgroundColor(activity.getResources().getColor(R.color.lightslategray));
			}
		}else{
			if (childPosition == 2){
				convertView.setBackgroundColor(activity.getResources().getColor(R.color.lightslategray));
			}else if (childPosition == 1){
				convertView.setBackgroundColor(activity.getResources().getColor(R.color.blueviolet));
			}else{
				convertView.setBackgroundColor(activity.getResources().getColor(R.color.firebrick));
			}
		}
		// 文字列挿入
		TextView tv = (TextView)convertView.findViewById(android.R.id.text1);
		tv.setText(getChild(groupPosition, childPosition).toString());
		
		return convertView;
	}
	@Override
	public int getChildrenCount(int groupPosition) {
		return childList.get(groupPosition).size();
	}
	@Override
	public Object getGroup(int groupPosition) {
		return groupList.get(groupPosition).get("GROUP_TITLE");
	}
	@Override
	public int getGroupCount() {
		return groupList.size();
	}
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// convertViewがNullの時だけインフレート（再利用）
		if (convertView == null){
			convertView = inflater.inflate(R.layout.expandablelist_row, null);
		}
		// 要素の現在位置が奇数の時と偶数の時で色分け
		if ((groupPosition % 2) == 0){
			convertView.setBackgroundColor(activity.getResources().getColor(R.color.darkorange));
		}else{
			convertView.setBackgroundColor(activity.getResources().getColor(R.color.forestgreen));
		}
		// 文字列挿入
		TextView tv = (TextView)convertView.findViewById(android.R.id.text1);
		tv.setText(getGroup(groupPosition).toString());
		
		return convertView;
	}
	@Override
	public boolean hasStableIds() {
		return true;
	}
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
