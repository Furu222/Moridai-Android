package jp.sakumon.moridai;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

public class MyProgressDialogFragment extends DialogFragment{
    private static ProgressDialog progressDialog = null;
    
    // インスタンス生成はこれを使う
    public static MyProgressDialogFragment newInstance(String title, String message){
        MyProgressDialogFragment instance = new MyProgressDialogFragment();
        
        // ダイアログにパラメータを渡す
        Bundle arguments = new Bundle();
        arguments.putString("title", title);
        arguments.putString("message", message);
        
        instance.setArguments(arguments);
        
        return instance;
    }
    
    // ProgressDialog作成
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        if (progressDialog != null)
            return progressDialog;
        
        // パラメータを取得
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // プログレスダイアログのスタイルを円スタイルに設定
        // プログレスダイアログのキャンセルが可能かどうかを設定（バックボタンでダイアログをキャンセルできないようにする）
        setCancelable(false);
        
        return progressDialog;
    }
    
    // progressDialog取得
    @Override
    public Dialog getDialog(){
        return progressDialog;
    }
    
    // ProgressDialog破棄
    @Override
    public void onDestroy(){
        super.onDestroy();
        progressDialog = null;
    }
}
