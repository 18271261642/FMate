package com.example.xingliansdk.ui.fragment.map.task;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Build;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

/**
 * 功能:这是一个避免内存泄露的AsyncTask
 */


public abstract class WeakAsyncTask<Params, Progress, Result, WeakTarget>
        extends AsyncTask<Params, Progress, Result> {
    private Method publishProgressMethod;
    private WeakReference<WeakTarget> mTarget;

    public WeakAsyncTask(WeakTarget target) {
        mTarget = new WeakReference<WeakTarget>(target);
        try {
            publishProgressMethod = AsyncTask.class.getDeclaredMethod("publishProgress", Object[].class);
            publishProgressMethod.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void publishToMainThread(Progress... values) {
        try {
            publishProgressMethod.invoke(this,values);
        } catch (Exception ignored) {
        }
    }


    @Override
    protected final void onPreExecute() {
        final WeakTarget target = mTarget.get();
        if (canNext(target)) {
            this.prepare(target);
        }
    }


    @Override
    protected final Result doInBackground(Params... params) {
        final WeakTarget target = mTarget.get();
        if (canNext(target)) {
            try {
                return this.run(target, params);
            } catch (Throwable throwable) {
                error(throwable);
            }
        }

        return null;

    }

    @Override
    protected final void onPostExecute(Result result) {
        final WeakTarget target = mTarget.get();
        if (canNext(target)) {
            this.done(target, result);
        }
    }

    @Override
    protected void onProgressUpdate(Progress... values) {
        final WeakTarget target = mTarget.get();
        if (canNext(target)) {
            this.progress(target, values);
        }
    }

    protected boolean canNext(WeakTarget target) {
        if (target == null) {
            return false;
        }
        Activity activity = null;
        if (target instanceof Activity) {
            activity = (Activity) target;
        }
        if (target instanceof Fragment) {
            activity = ((Fragment) target).getActivity();
        }
        if (activity != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (activity.isFinishing() || activity.isDestroyed()) {
                    return false;
                }
            } else {
                if (activity.isFinishing()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void prepare(WeakTarget target) {
    }

    public abstract Result run(WeakTarget target, Params... params) throws Throwable;

    public void progress(WeakTarget target, Progress... values) {
    }

    public void done(WeakTarget target, Result result) {
    }

    public void error(Throwable e) {
        e.printStackTrace();
    }
}