package jp.sakumon.moridai;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class FragmentMake extends Fragment implements OnClickListener{
	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		// 第３引数のbooleanは"container"にreturnするViewを追加するかどうか  
      //trueにすると最終的なlayoutに再度、同じView groupが表示されてしまうのでfalseでOKらしい  
		View v = inflater.inflate(R.layout.fragment_make, container, false);
		
		// ボタンを取得して、ClickListenerをセット
		Button btn = (Button)v.findViewById(R.id.FragmentMakeButton);
		btn.setOnClickListener(this);
		
      return v;
	}

	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		// インテント処理（画面遷移）
		Intent intent = new Intent(getActivity(), QuestionMakeActivity.class);
		startActivity(intent);
	}
}
