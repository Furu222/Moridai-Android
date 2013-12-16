package jp.sakumon.moridai;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * プリファレンスに保存されている値を呼びだし
 * @author furudate
 *
 */
public class GetSharedPreferences {
	/**
	 * プリファレンスに保存されているユーザIDを呼び出し
	 * @param context アクティビティ
	 * @return ユーザID
	 */
	public Integer getUserId(Context context){
		SharedPreferences pref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
		
		// 保存されている値を呼び出し
		return pref.getInt("user_id", -1);
	}
}
