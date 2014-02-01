package jp.sakumon.moridai;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;

public class MyJsonHttpResponseHandler extends JsonHttpResponseHandler{
    
    // Activityへのコールバック用Interface
    public interface MyJsonResponseCallback {
        void onStart();
        void onSuccess(JSONObject json);
        void onFailure(Throwable e, String response);
        void onFinish();
    }
    
    private MyJsonResponseCallback callback = null; // コールバック用
    private Context context;
    private MyProgressDialogFragment progressDialog;    // ロード中画面のプログレスダイアログ作成
    private int count; // HTTP通信何回やっているか
    private int size; // 通信最大回数
    
    /**
     * コンストラクタ
     * @param context Activity
     * @param myJsonResponseCallback コールバックリスナー
     * @param count 現在の表示回数
     * @param size 最大表示回数
     */
    public MyJsonHttpResponseHandler(MyJsonResponseCallback _callBack, Context context, int count, int size){
        this.context = context;
        this.callback = _callBack;
        this.count = count;
        this.size = size;
    }

    @Override
    public void onStart(){
        // 通信開始時の処理
        progressDialog = MyProgressDialogFragment.newInstance("ネットワークに接続中", "しばらくお待ちください");
        progressDialog.show(((Activity) context).getFragmentManager(), "progress");
        count++;
        callback.onStart();
    }
    
    @Override
    public void onSuccess(JSONObject json){
        super.onSuccess(json);
        callback.onSuccess(json);
    }
    
    @Override
    public void onFailure(Throwable e, String response){
        // 通信失敗時の処理
        if (progressDialog.getShowsDialog())
            progressDialog.getDialog().dismiss();
        count = size; // Countの回数を最大にする
        callback.onFailure(e, response);
    }

    @Override
    public void onFinish(){
        // 通信終了時の処理
        // カウントがサイズと同じ値になったら終了
        if (progressDialog.getShowsDialog() && count == size)
            //progressDialog.getDialog().dismiss();
            progressDialog.onDismiss(progressDialog.getDialog());
        callback.onFinish();
    }
}
