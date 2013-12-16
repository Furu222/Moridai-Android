package jp.sakumon.moridai;

import java.util.EventListener;

public interface DialogListener extends EventListener{

    /**
     * OKボタンが押されたイベントを通知
     */
    public void doPositiveClick();
    
    /**
     * Cancelボタンが押されたイベントを通知
     */
    public void doNegativeClick();
}
