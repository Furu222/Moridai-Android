package jp.sakumon.moridai;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener, DialogListener {

	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Action barのモードをタブモードに切り替え
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// FragmentPagerAdapterを継承したクラスのアダプターを作成
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// ViewPagerにSectionPagerAdapterをセット
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		/**
		 * スワイプしたときにもActionbarのタブ（NavigationItem）を常に表示させる処理
		 */
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// getCountでタブの数を指定。
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Actionbarにタブを追加。
			// getPageTitleでタブのタイトルを表示
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		
		// Facebookログインスタート
//		Session.openActiveSession(this, true, new Session.StatusCallback() {
//			
//			// セッションの状態が変わったらコールバック
//			@Override
//			public void call(Session session, SessionState state, Exception exception) {
//				// Callメソッドが複数回発生されないように、ユーザの最後の正常な状態のみ取り出す
//				if (session.isOpened()){
//					// ユーザの基本情報を取るAPIにリクエストを送る
//					Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
//						
//						@Override
//						public void onCompleted(GraphUser user, Response response) {
//							// TODO 自動生成されたメソッド・スタブ
//							if (user != null){
//								TextView welcome = (TextView) findViewById(R.id.welcome);
//								welcome.setText("Hello " + user.getName() + "!");
//							}
//						}
//					});
//				}
//			}
//		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	/**
	 * タブを選択した時の処理
	 */
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// ここで表示するフラグメントを決定する
		mViewPager.setCurrentItem(tab.getPosition());
	}

	/**
	 * タブの選択が外れた場合の処理
	 */
	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * タブが2度目以降に選択された場合の処理
	 */
	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * ViewPagerの動作を作成
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		/**
		 * 各ページにFragmentを設定する
		 */
		@Override
		public Fragment getItem(int position) {
			if (position == 0){
				FragmentAnswer fragment = new FragmentAnswer();
				return fragment;
			} else if (position == 1) {
				FragmentTest fragment = new FragmentTest();
				return fragment;
			}else{
				FragmentMake fragment = new FragmentMake();
				return fragment;
			}
		}

		/**
		 * タブの数を決定
		 */
		@Override
		public int getCount() {
			return 3;
		}

		/**
		 * タブのタイトルを決定
		 */
        @Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase();
			case 1:
				return getString(R.string.title_section2).toUpperCase();
			case 2:
				return getString(R.string.title_section3).toUpperCase();
			}
			return null;
		}
	}
	
	
	/**
	 * 端末の戻るボタンを押した時の処理
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK){
		    String title = "アプリケーションの終了";
            String message = "アプリケーションを終了してよろしいですか？";
            showDialog(title, message, 1);
			
			return true;
		}
		return false;
	}
	
	/**
     * ダイアログ表示関数
     * @param type ダイアログタイプ 0:OKボタンのみ 1:OK, NGボタン
     */
    public void showDialog(String title, String message, int type){
        MyDialogFragment newFragment = MyDialogFragment.newInstance(title, message, type);
        // リスナーセット
        newFragment.setDialogListener(this);
        newFragment.show(getFragmentManager(), "dialog");
    }
    
    /**
     * OKボタンをおした時
     */
    public void doPositiveClick() {
        moveTaskToBack(true);
    }

    /**
     * NGボタンをおした時
     */
    @Override
    public void doNegativeClick() {

    }

}
