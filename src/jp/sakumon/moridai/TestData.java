package jp.sakumon.moridai;

import java.util.HashMap;

import android.util.SparseArray;

public class TestData {
	// シングルトンパターンでインスタンスを共通化
	private static TestData instance = new TestData();
	
	// コンストラクタ。ここでは何もしない
	private TestData(){};
	
	public static TestData getInstance(){
		return instance;
	}
	// ここまでの処理で、共通化（シングルトン）を行う
	
	// テスト問題データの配列
	SparseArray<HashMap<String, String>> rootData = new SparseArray<HashMap<String, String>>();
	// 選択肢番号格納用配列
	Integer[][] optionNum;
	
	/**
	 * ゲッター
	 * @return 保存されているテストデータ
	 */
	public SparseArray<HashMap<String, String>> getRootData() {
		return rootData;
	}
	
	/**
	 * セッター
	 * @param rootData 保存するテストデータ
	 */
	public void setRootData(SparseArray<HashMap<String, String>> rootData) {
		this.rootData = rootData;
	}
	
	/**
	 * ゲッター
	 * @return 保存されている選択肢番号配列
	 */
	public Integer[][] getOptionNum() {
		return optionNum;
	}

	/**
	 * セッター
	 * @param optionNum 保存するテストデータ
	 */
	public void setOptionNum(Integer[][] optionNum) {
		this.optionNum = optionNum;
	}

}
