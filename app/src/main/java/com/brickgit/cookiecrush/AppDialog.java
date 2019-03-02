package com.brickgit.cookiecrush;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class AppDialog {

  public static AlertDialog getStartMenu(
      Context context,
      DialogInterface.OnClickListener startListener,
      DialogInterface.OnClickListener exitListener) {
    int highestScore = AppPreference.getHighestScore(context);
    String scoreMessage =
        String.format(context.getResources().getString(R.string.highest_score), highestScore);

    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setCancelable(false);
    builder.setTitle(R.string.welcome).setMessage(scoreMessage);
    builder.setPositiveButton(R.string.start_game, startListener);
    builder.setNegativeButton(R.string.exit, exitListener);
    return builder.create();
  }

  public static AlertDialog getPauseMenu(
      Context context,
      DialogInterface.OnClickListener resumeListener,
      DialogInterface.OnClickListener exitListener) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setCancelable(false);
    builder.setTitle(R.string.pause);
    builder.setPositiveButton(R.string.resume, resumeListener);
    builder.setNegativeButton(R.string.exit, exitListener);
    return builder.create();
  }

  public static AlertDialog getOverMenu(
      Context context,
      DialogInterface.OnClickListener restartListener,
      DialogInterface.OnClickListener exitListener) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setCancelable(false);
    builder.setTitle(R.string.game_over);
    builder.setPositiveButton(R.string.restart, restartListener);
    builder.setNegativeButton(R.string.exit, exitListener);
    return builder.create();
  }
}
