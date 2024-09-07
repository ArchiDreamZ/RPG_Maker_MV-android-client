/*
 * 版权所有 (c) 2017-2019 Altimit 社区贡献者
 *
 * 根据 Apache 许可证 2.0 版（“许可证”）获得许可；
 * 除非遵守许可证，否则您不得使用此文件。
 * 您可以在以下位置获取许可证副本：
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非适用法律要求或书面同意，否则软件
 * 根据许可证分发是在“按原样”基础上分发的，
 * 不提供任何明示或暗示的保证或条件
 * 请参阅许可证以了解管理权限的特定语言和
 * 许可证下的限制。
 */

package jp.hbox.ImoutoFantasy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 由 felixjones 创建于 2017 年 4 月 28 日。
 */
public class WebPlayerActivity extends Activity {

    private static final String TOUCH_INPUT_ON_CANCEL = "TouchInput._onCancel();";

    private Player mPlayer;
    private AlertDialog mQuitDialog;
    private int mSystemUiVisibility;

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //根据重力横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        if (BuildConfig.BACK_BUTTON_QUITS) {
            createQuitDialog();
        }

        mSystemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mSystemUiVisibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            mSystemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            mSystemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            mSystemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mSystemUiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
        }

        mPlayer = PlayerHelper.create(this);

        mPlayer.setKeepScreenOn();

        setContentView(mPlayer.getView());

        if (!addBootstrapInterface(mPlayer)) {
            Uri.Builder projectURIBuilder = Uri.fromFile(new File(getString(R.string.mv_project_index))).buildUpon();
            Bootstrapper.appendQuery(projectURIBuilder, getString(R.string.query_noaudio));
            if (BuildConfig.SHOW_FPS) {
                Bootstrapper.appendQuery(projectURIBuilder, getString(R.string.query_showfps));
            }
            mPlayer.loadUrl(projectURIBuilder.build().toString());
        }
    }

    @Override
    public void onBackPressed() {
        if (BuildConfig.BACK_BUTTON_QUITS) {
            if (mQuitDialog != null) {
                mQuitDialog.show();
            } else {
                super.onBackPressed();
            }
        } else {
            mPlayer.evaluateJavascript(TOUCH_INPUT_ON_CANCEL);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        mPlayer.pauseTimers();
        mPlayer.onHide();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 设置刘海屏相关的系统UI可见性标志
            int flags = mSystemUiVisibility;
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            flags |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            getWindow().getDecorView().setSystemUiVisibility(flags);
        } else {
            // 对于不支持刘海屏的API级别，使用之前设置的系统UI可见性标志
            getWindow().getDecorView().setSystemUiVisibility(mSystemUiVisibility);
        }
        if (mPlayer != null) {
            mPlayer.resumeTimers();
            mPlayer.onShow();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void createQuitDialog() {
        String appName = getString(R.string.app_name);
        String[] quitLines = getResources().getStringArray(R.array.quit_message);
        SpannableStringBuilder quitMessage = new SpannableStringBuilder();
        for (int ii = 0; ii < quitLines.length; ii++) {
            String line = quitLines[ii].replace("$1", appName);
            int startIndex = line.indexOf(appName);
            if (startIndex != -1) {
                int endIndex = startIndex + appName.length();
                quitMessage.append(line);

                // $1 的字体颜色设置 (Color.BLUE)为蓝色，可更换喜欢的颜色。
                quitMessage.setSpan(new ForegroundColorSpan(Color.BLUE), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            } else {
                quitMessage.append(line);
            }
            if (ii < quitLines.length - 1) {
                quitMessage.append("\n");
            }
        }
        if (quitMessage.length() > 0) {
            mQuitDialog = new AlertDialog.Builder(this, R.style.CustomAlertDialogStyle)
                    .setPositiveButton("确定", (dialog, which) -> WebPlayerActivity.super.onBackPressed())
                    .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                    .setOnDismissListener(dialog -> getWindow().getDecorView().setSystemUiVisibility(mSystemUiVisibility))
                    .setMessage(quitMessage)
                    .create();
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private static boolean addBootstrapInterface(Player player) {
        if (BuildConfig.BOOTSTRAP_INTERFACE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            new Bootstrapper(player);
            return true;
        }
        return false;
    }

    /**
     *
     */
    private static final class Bootstrapper extends PlayerHelper.Interface implements Runnable {

        private static Uri.Builder appendQuery(Uri.Builder builder, String query) {
            Uri current = builder.build();
            String oldQuery = current.getEncodedQuery();
            if (oldQuery != null && !oldQuery.isEmpty()) {
                query = oldQuery + "&" + query;
            }
            return builder.encodedQuery(query);
        }

        private static final String INTERFACE = "boot";
        private static final String PREPARE_FUNC = "prepare( webgl(), webaudio(), false )";

        private final Player mPlayer;
        private Uri.Builder mURIBuilder;

        private Bootstrapper(Player player) {
            Context context = player.getContext();
            player.addJavascriptInterface(this, Bootstrapper.INTERFACE);

            mPlayer = player;
            mURIBuilder = Uri.fromFile(new File(context.getString(R.string.mv_project_index))).buildUpon();
            mPlayer.loadData(context.getString(R.string.webview_default_page));
        }

        @Override
        protected void onStart() {
            Context context = mPlayer.getContext();
            final String code = new String(Base64.decode(context.getString(R.string.webview_detection_source), Base64.DEFAULT), StandardCharsets.UTF_8) + INTERFACE + "." + PREPARE_FUNC + ";";
            mPlayer.post(() -> mPlayer.evaluateJavascript(code));
        }

        @Override
        protected void onPrepare(boolean webgl, boolean webaudio, boolean showfps) {
            Context context = mPlayer.getContext();
            if (webgl && !BuildConfig.FORCE_CANVAS) {
                mURIBuilder = appendQuery(mURIBuilder, context.getString(R.string.query_webgl));
            } else {
                mURIBuilder = appendQuery(mURIBuilder, context.getString(R.string.query_canvas));
            }
            if (!webaudio || BuildConfig.FORCE_NO_AUDIO) {
                mURIBuilder = appendQuery(mURIBuilder, context.getString(R.string.query_noaudio));
            }
            if (showfps || BuildConfig.SHOW_FPS) {
                mURIBuilder = appendQuery(mURIBuilder, context.getString(R.string.query_showfps));
            }
            mPlayer.post(this);
        }

        @Override
        public void run() {
            mPlayer.removeJavascriptInterface(INTERFACE);
            mPlayer.loadUrl(mURIBuilder.build().toString());
        }

    }

}