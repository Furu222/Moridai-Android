package jp.sakumon.moridai;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;

public class MyDialogFragment extends DialogFragment{
    private DialogListener listener = null;
	/**
	 * ファクトリーメソッド
	 * @param type ダイアログタイプ 0:OKボタンのみ 1:OK, NGボタン
	 */
	public static MyDialogFragment newInstance(String title, String message, int type){
	    MyDialogFragment instance = new MyDialogFragment();
	    
	    // ダイアログに渡すパラメータはBundleにまとめる
	    Bundle arguments = new Bundle();
	    arguments.putString("title", title);
	    arguments.putString("message", message);
	    arguments.putInt("type", type);
	    
	    instance.setArguments(arguments);
	    
	    return instance;
	}
	
	/**
	 * AlertDialog作成
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
	    String title = getArguments().getString("title");
	    String message = getArguments().getString("message");
	    int type = getArguments().getInt("type");
	    
	    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //dismiss();
                    getDialog().dismiss();
                    // OKボタンが押された時
                    listener.doPositiveClick();
                }
            });
	    if (type == 1){ // NGボタンも付ける場合
	        alert.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //dismiss();
                    getDialog().dismiss();
                    // Cancelボタンが押された時
                    listener.doNegativeClick();
                }
            });
	    }
	    
	    return alert.create();
	}
	
	/**
	 * リスナーを追加
	 */
	public void setDialogListener(DialogListener listener){
	    this.listener = listener;
	}
	
	/**
	 * リスナー削除
	 */
	public void removeDialogListener(){
	    this.listener = null;
	}
}