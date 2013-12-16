package jp.sakumon.moridai;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FragmentTest extends Fragment implements OnClickListener{

	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		// 第３引数のbooleanは"container"にreturnするViewを追加するかどうか  
       //trueにすると最終的なlayoutに再度、同じView groupが表示されてしまうのでfalseでOKらしい  
		View v = inflater.inflate(R.layout.fragment_test, container, false);
		
		// ボタンを取得して、ClickListenerをセット
		Button btn = (Button)v.findViewById(R.id.FragmentTestButton);
		btn.setOnClickListener(this);
		
       return v;
	}

	/**
	 * ボタンが押された時の処理
	 * @param v 押されたボタンのID
	 */
	@Override
	public void onClick(View v) {
		// インテント処理（画面遷移）
		Intent intent = new Intent(getActivity(), TestGradeListActivity.class);
		startActivity(intent);
	}

}
