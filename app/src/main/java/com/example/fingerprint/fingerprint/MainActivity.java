package com.example.fingerprint.fingerprint;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.awei.android.lib.fingerprintidentify.FingerprintIdentify;
import com.awei.android.lib.fingerprintidentify.base.BaseFingerprint;

public class MainActivity extends Activity {

    private TextView mTvInfo;
    private ScrollView mScrollView;
    private FingerprintIdentify mFingerprintIdentify;

    private boolean mNeedToRestartFingerprint = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvInfo = (TextView) findViewById(R.id.mTvInfo);
        mScrollView = (ScrollView) findViewById(R.id.mScrollView);

        mFingerprintIdentify = new FingerprintIdentify(this, new BaseFingerprint.FingerprintIdentifyExceptionListener() {
            @Override
            public void onCatchException(Throwable exception) {
                exception.printStackTrace();
            }
        });

        findViewById(R.id.btn_start).setEnabled(mFingerprintIdentify.isFingerprintEnable());

        tag("指纹功能：" + mFingerprintIdentify.isFingerprintEnable());
        tag("指纹硬件：" + mFingerprintIdentify.isHardwareEnable());
        tag("已录指纹：" + mFingerprintIdentify.isRegisteredFinger());
    }

    public void start(View view) {
        findViewById(R.id.btn_cancel).setVisibility(View.VISIBLE);
        mNeedToRestartFingerprint = true;
        tag("开始验证指纹，请放置你的手指到指纹传感器上");
        mFingerprintIdentify.startIdentify(3, new BaseFingerprint.FingerprintIdentifyListener() {
            @Override
            public void onSucceed() {
                tag("验证成功");
                findViewById(R.id.btn_cancel).setVisibility(View.GONE);
            }

            @Override
            public void onNotMatch(int availableTimes) {
                tag("指纹不匹配，可用次数剩余：" + availableTimes);
            }

            @Override
            public void onFailed() {
                tag("验证遇到错误！！！");
            }
        });
    }

    public void cancel(View view) {
        findViewById(R.id.btn_cancel).setVisibility(View.GONE);
        tag("取消验证");
        mFingerprintIdentify.cancelIdentify();
    }

    public void clear(View view) {
        mTvInfo.setText("");
    }

    private void tag(String msg) {
        mTvInfo.append(msg + "\n");
        mScrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNeedToRestartFingerprint) {
            tag("onResume 恢复指纹验证流程");
            mFingerprintIdentify.resumeIdentify();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNeedToRestartFingerprint) {
            tag("onPause 暂停指纹验证");
            mFingerprintIdentify.cancelIdentify();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mNeedToRestartFingerprint) {
            tag("onStop 暂停指纹验证");
            mFingerprintIdentify.cancelIdentify();
        }
    }
}