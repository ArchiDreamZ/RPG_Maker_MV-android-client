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

import android.content.Context;
import android.webkit.JavascriptInterface;

/**
 * 由 felixjones 于 2017 年 12 月 5 日创建。
 *
 * @noinspection ALL
 */
public class PlayerHelper {

    public static Player create(Context context) {
        return new WebPlayerView(context).getPlayer();
    }

    /**
     *
     */
    public static abstract class Interface {

        protected abstract void onStart();

        protected abstract void onPrepare(boolean webgl, boolean webaudio, boolean showfps);

        @JavascriptInterface
        public void start() {
            onStart();
        }

        @JavascriptInterface
        public void prepare(boolean webgl, boolean webaudio, boolean showfps) {
            onPrepare(webgl, webaudio, showfps);
        }

    }

}