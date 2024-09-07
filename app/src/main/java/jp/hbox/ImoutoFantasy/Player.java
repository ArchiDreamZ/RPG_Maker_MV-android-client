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
import android.view.View;

/**
 * 由 felixjones 创建于 12/05/2017。
 */
public interface Player {

    void setKeepScreenOn();

    View getView();

    void loadUrl(String url);

    void addJavascriptInterface(Object object, String name);

    Context getContext();

    void loadData(String data);

    void evaluateJavascript(String script);

    void post(Runnable runnable);

    void removeJavascriptInterface(String name);

    void pauseTimers();

    void onHide();

    void resumeTimers();

    void onShow();

    void onDestroy();

}