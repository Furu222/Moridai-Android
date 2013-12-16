package jp.sakumon.moridai;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.loopj.android.http.JsonHttpResponseHandler;

public class MyJsonResponseHandler extends JsonHttpResponseHandler{
	private Context context;
	private ProgressDialog progressDialog;	// ロード中画面のプログレスダイアログ作成
	
	public MyJsonResponseHandler(Context context){
		this.context = context;
		progressDialog = new ProgressDialog(this.context);
	}
	
	@Override
	public void onStart(){
		// 通信開始時の処理
		progressDialog.setTitle("ネットワークに接続中です");
		progressDialog.setMessage("しばらくお待ちください");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // プログレスダイアログのスタイルを円スタイルに設定
		// プログレスダイアログのキャンセルが可能かどうかを設定（バックボタンでダイアログをキャンセルできないようにする）
        progressDialog.setCancelable(false);
        // プログレスダイアログを表示
        progressDialog.show();
	}
	
	@Override
	public void onFailure(Throwable e, String response){
		// 通信失敗時の処理
		progressDialog.hide();
		
		new AlertDialog.Builder(context)
			.setTitle("エラー")
			.setMessage("エラーが発生しました。お手数ですが電波状況が良いところでもう一度操作をやり直してみて下さい。")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// OKボタンが押された時
					((Activity) context).finish();
				}
			})
			.setCancelable(false)
			.show();
	}

	@Override
	public void onFinish(){
		// 通信終了時の処理
		progressDialog.hide();
	}
	
}
