/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.xuexiang.templateproject.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.xuexiang.templateproject.R;
import com.xuexiang.templateproject.activity.PostThreadActivity;
import com.xuexiang.templateproject.activity.SearchActivity;
import com.xuexiang.templateproject.adapter.base.delegate.SimpleDelegateAdapter;
import com.xuexiang.templateproject.adapter.entity.NewInfo;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.core.webview.AgentWebActivity;
import com.xuexiang.templateproject.fragment.FavorFragment;
import com.xuexiang.templateproject.fragment.MyThreadsFragment;
import com.xuexiang.templateproject.fragment.SearchFragment;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xutil.XUtil;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import me.jessyan.autosize.external.ExternalAdaptInfo;

import static com.xuexiang.templateproject.core.webview.AgentWebFragment.KEY_URL;
import static com.xuexiang.templateproject.utils.ExchangeInfosWithAli.CancelFavourThread_json;
import static com.xuexiang.xutil.app.ActivityUtils.startActivity;

/**
 * 工具类
 *
 * @author xuexiang
 * @since 2020-02-23 15:12
 */
public final class Utils {

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }
    /**
     * 显示删除的提示
     *
     * @param context
     * @param submitListener 同意的监听
     * @return
     */
    public static Dialog showDeleteFavorDialog(Context context, MaterialDialog.SingleButtonCallback submitListener, BaseFragment baseFragment, String thread_id) {
        MaterialDialog dialog = new MaterialDialog.Builder(context).title(R.string.title_reminder).autoDismiss(false).cancelable(false)
                .positiveText("不更新").onPositive((dialog1, which) -> {
                    if (submitListener != null) {
                        submitListener.onClick(dialog1, which);
                    } else {
                        dialog1.dismiss();
                    }
                })
                .negativeText("删除").onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        try {
                            CancelFavourThread_json(thread_id);
                        } catch (JSONException | IOException e) {
                            XToastUtils.toast("请检查网络后重试");
                            e.printStackTrace();
                        }
                        Log.d("LookThroughActivity.U", thread_id);
                        dialog.dismiss();
                        // 这里是取消收藏的函数接口
                        baseFragment.openNewPage(FavorFragment.class);
                        baseFragment.getActivity().onBackPressed();
                    }
                }).build();
//        dialog.setContent(getPrivacyContent(context));
        dialog.setContent("是否确认删除此收藏？\n");
        //开始响应点击事件
        dialog.getContentView().setMovementMethod(LinkMovementMethod.getInstance());
        dialog.show();
        return dialog;
    }

    public static Dialog showReportDialog(Context context, MaterialDialog.SingleButtonCallback submitListener, String thread_id) {
        MaterialDialog dialog = new MaterialDialog.Builder(context).title(R.string.title_reminder).autoDismiss(false).cancelable(false)
                .positiveText("不举报").onPositive((dialog1, which) -> {
                    if (submitListener != null) {
                        submitListener.onClick(dialog1, which);
                    } else {
                        dialog1.dismiss();
                    }
                })
                .negativeText("举报").onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // 这里是举报的函数接口
                        if (ExchangeInfosWithAli.WhetherReport == 0){
                            try {
                                ExchangeInfosWithAli.ReportThread_json(thread_id);
                                XToastUtils.toast("举报成功");
                                Log.d("LookThroughActivity.U", thread_id);
                                ExchangeInfosWithAli.WhetherReport = 1;
                            } catch (JSONException | IOException e) {
                                XToastUtils.toast("请检查网络后重试");
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        }
                        else {
                            XToastUtils.toast("请勿重复举报");
                            Log.d("LookThroughActivity.U", thread_id);
                            dialog.dismiss();
                        }

                    }
                }).build();
//        dialog.setContent(getPrivacyContent(context));
        dialog.setContent("是否确认举报该帖？\n请慎重举报\n我们一起努力建设良好的社区环境");
        //开始响应点击事件
        dialog.getContentView().setMovementMethod(LinkMovementMethod.getInstance());
        dialog.show();
        return dialog;
    }

    public static Dialog showUpdateDialog(Context context, MaterialDialog.SingleButtonCallback submitListener, String update_url) {
        MaterialDialog dialog = new MaterialDialog.Builder(context).title(R.string.title_reminder).autoDismiss(false).cancelable(false)
//                .positiveText("不更新").onPositive((dialog1, which) -> {
//                    if (submitListener != null) {
//                        submitListener.onClick(dialog1, which);
//                    } else {
//                        dialog1.dismiss();
//                    }
//                })
                .negativeText("更新").onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // 这里是更新的函数接口
                        Uri uri = Uri.parse(update_url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);



                    }
                }).build();
//        dialog.setContent(getPrivacyContent(context));
        dialog.setContent("麻烦请更新至最新版APP～\n否则无法正常使用～");
        //开始响应点击事件
        dialog.getContentView().setMovementMethod(LinkMovementMethod.getInstance());
        dialog.show();
        return dialog;
    }


    public static Dialog showDeleteMyThreadDialog(Context context, MaterialDialog.SingleButtonCallback submitListener, BaseFragment baseFragment, String thread_id) {
        MaterialDialog dialog = new MaterialDialog.Builder(context).title(R.string.title_reminder).autoDismiss(false).cancelable(false)
                .positiveText("不删除").onPositive((dialog1, which) -> {
                    if (submitListener != null) {
                        submitListener.onClick(dialog1, which);
                    } else {
                        dialog1.dismiss();
                    }
                })
                .negativeText("删除").onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        try {
                            ExchangeInfosWithAli.DeleteThread_json(thread_id);
                        } catch (JSONException | IOException e) {
                            XToastUtils.toast("请检查网络后重试");
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                        //这里是删除自己帖子的函数接口
                        baseFragment.openNewPage(MyThreadsFragment.class);
                        baseFragment.getActivity().onBackPressed();
                    }
                }).build();
//        dialog.setContent(getPrivacyContent(context));
        dialog.setContent("是否确认删除此帖？\n一旦删除，不可恢复！\n");
        //开始响应点击事件
        dialog.getContentView().setMovementMethod(LinkMovementMethod.getInstance());
        dialog.show();
        return dialog;
    }

    public static Dialog showSearchDialog(Context context, MaterialDialog.SingleButtonCallback submitListener) {
        MaterialDialog dialog = new MaterialDialog.Builder(context).title("搜索内容")
                .input("请输入关键词", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                    }
                }).autoDismiss(false).cancelable(false)
                .positiveText("搜索").onPositive((dialog1, which) -> {
                    if (submitListener != null) {
                        submitListener.onClick(dialog1, which);
                    } else {
                        String search_context = dialog1.getInputEditText().getText().toString();
                        dialog1.dismiss();
                        Intent intent = new Intent(context, SearchActivity.class);
                        intent.putExtra("query_string", search_context);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                        startActivity(intent);
                    }
                })
                .negativeText("取消").onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).build();
        //开始响应点击事件
        dialog.getContentView().setMovementMethod(LinkMovementMethod.getInstance());
        dialog.show();
        return dialog;
    }

    /**
     * 这里填写你的应用隐私政策网页地址
     */
    private static final String PRIVACY_URL = "https://gitee.com/xuexiangjys/TemplateAppProject/raw/master/LICENSE";

    /**
     * 显示隐私政策的提示
     *
     * @param context
     * @param submitListener 同意的监听
     * @return
     */
    public static Dialog showPrivacyDialog(Context context, MaterialDialog.SingleButtonCallback submitListener) {
        final EditText et = new EditText(context);
        MaterialDialog dialog = new MaterialDialog.Builder(context).title(R.string.title_reminder).autoDismiss(false).cancelable(false)
                .positiveText(R.string.lab_agree).onPositive((dialog1, which) -> {
                    if (submitListener != null) {
                        submitListener.onClick(dialog1, which);
                    } else {
                        dialog1.dismiss();
                    }
                })
                .negativeText(R.string.lab_disagree).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        DialogLoader.getInstance().showConfirmDialog(context, ResUtils.getString(R.string.title_reminder), String.format(ResUtils.getString(R.string.content_privacy_explain_again), ResUtils.getString(R.string.app_name)), ResUtils.getString(R.string.lab_look_again), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                showPrivacyDialog(context, submitListener);
                            }
                        }, ResUtils.getString(R.string.lab_still_disagree), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                DialogLoader.getInstance().showConfirmDialog(context, ResUtils.getString(R.string.content_think_about_it_again), ResUtils.getString(R.string.lab_look_again), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        showPrivacyDialog(context, submitListener);
                                    }
                                }, ResUtils.getString(R.string.lab_exit_app), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        XUtil.get().exitApp();
                                    }
                                });
                            }
                        });
                    }
                }).build();
        dialog.setContent(getPrivacyContent(context));
        //开始响应点击事件
        dialog.getContentView().setMovementMethod(LinkMovementMethod.getInstance());
        dialog.show();
        return dialog;
    }

    /**
     * @return 隐私政策说明
     */
    private static SpannableStringBuilder getPrivacyContent(Context context) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder()
                .append("    欢迎来到").append(ResUtils.getString(R.string.app_name)).append("!\n")
                .append("    我可以回答一句无可奉告，但是你们又不高兴，我怎么办？\n")
                .append("    为了更好地保护你的权益，同时遵守相关监管的要求，我们会用西方那套理论");
        stringBuilder/*.append(getPrivacyLink(context, PRIVACY_URL))*/
                .append("向你说明我们会如何收集、存储、保护、使用及对外提供你的信息，并说明你享有的权利。\n")
                .append("    你问我支持不支持、我当然是支持的")
                /*.append(getPrivacyLink(context, PRIVACY_URL))*/
                .append("");
        return stringBuilder;
    }

    /**
     * @param context 隐私政策的链接
     * @return
     */
    private static SpannableString getPrivacyLink(Context context, String privacyUrl) {
        String privacyName = String.format(ResUtils.getString(R.string.lab_privacy_name), ResUtils.getString(R.string.app_name));
        SpannableString spannableString = new SpannableString(privacyName);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                goWeb(context, privacyUrl);
            }
        }, 0, privacyName.length(), Spanned.SPAN_MARK_MARK);
        return spannableString;
    }


    /**
     * 请求浏览器
     *
     * @param url
     */
    public static void goWeb(Context context, final String url) {
        Intent intent = new Intent(context, AgentWebActivity.class);
        intent.putExtra(KEY_URL, url);
        context.startActivity(intent);
    }


    /**
     * 是否是深色的颜色
     *
     * @param color
     * @return
     */
    public static boolean isColorDark(@ColorInt int color) {
        double darkness =
                1
                        - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color))
                        / 255;
        return darkness >= 0.382;
    }

    public static void initTheme(Activity activity) {
        if (SettingSPUtils.getInstance().isUseCustomTheme()) {
            activity.setTheme(R.style.CustomAppTheme);
        } else {
            XUI.initTheme(activity);
        }
    }



}
