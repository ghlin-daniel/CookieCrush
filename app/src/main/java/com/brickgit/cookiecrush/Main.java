package com.brickgit.cookiecrush;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class Main extends AppCompatActivity {

	private static final int ONE_SEC = 1000;

	private int time = 30;
	private TextView txtTime;
	private int score = 0;
	private TextView txtScore;

	private ScoreListener scoreListener = new ScoreListener() {
		@Override
		public void onScore(int addedScore) {
			score += addedScore;
			String strScore = String.format(getString(R.string.score), score);
			txtScore.setText(strScore);
		}
	};

	private AlertDialog startMenu;
	private AlertDialog pauseMenu;
	private AlertDialog overMenu;

	private TimerTask timerTask;
	private Timer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txtTime = (TextView) findViewById(R.id.time);
		txtScore = (TextView) findViewById(R.id.score);

		String strTime = String.format(getString(R.string.time), time);
		txtTime.setText(strTime);

		String strScore = String.format(getString(R.string.score), score);
		txtScore.setText(strScore);

		GameView gameView = (GameView) findViewById(R.id.game_view);
		gameView.setScoreListener(scoreListener);

		startMenu = AppDialog.getStartMenu(this, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startNewTimer();
			}
		}, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		pauseMenu = AppDialog.getPauseMenu(this, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startNewTimer();
			}
		}, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		overMenu = AppDialog.getOverMenu(this, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				time = 30;
				String strTime = String.format(getString(R.string.time), time);
				txtTime.setText(strTime);
				score = 0;
				String strScore = String.format(getString(R.string.score), score);
				txtScore.setText(strScore);
				GameView gameView = (GameView) findViewById(R.id.game_view);
				gameView.restart();
				startNewTimer();
			}
		}, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		startMenu.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected  void onPause() {
		super.onPause();

		if (null != timer) {
			timer.cancel();
			timer = null;
		}

		if (!startMenu.isShowing() && !pauseMenu.isShowing()) pauseMenu.show();
	}

	private void startNewTimer() {
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						time--;
						String strTime = String.format(getString(R.string.time), time);
						txtTime.setText(strTime);

						if (0 == time) {
							if (null != timer) {
								timer.cancel();
								timer = null;
							}

							int highestScore = AppPreference.getHighestScore(Main.this);
							String strScore;
							if (score > highestScore) {
								AppPreference.setHighestScore(Main.this, score);
								strScore = String.format(getString(R.string.new_record), score);
							} else {
								strScore = String.format(getString(R.string.score), score);
							}
							overMenu.setMessage(strScore);
							overMenu.show();
						}
					}
				});
			}
		};
		timer.schedule(timerTask, ONE_SEC, ONE_SEC);
	}

	@Override
	public void onBackPressed() {
		if (null != timer) {
			timer.cancel();
			timer = null;
		}

		if (!startMenu.isShowing() && !pauseMenu.isShowing()) pauseMenu.show();
	}
}
