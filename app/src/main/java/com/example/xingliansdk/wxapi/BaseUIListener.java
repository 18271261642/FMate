package com.example.xingliansdk.wxapi;

import com.shon.connector.utils.ShowToast;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

public class BaseUIListener implements IUiListener {

    BaseUIListener()
    {

    }

    @Override
    public void onComplete(Object o) {

    }

    @Override
    public void onError(UiError uiError) {
        ShowToast.INSTANCE.showToastLong("分享失败"+uiError.errorMessage);
    }

    @Override
    public void onCancel() {
        ShowToast.INSTANCE.showToastLong("分享取消");
    }

    @Override
    public void onWarning(int i) {

    }
}
