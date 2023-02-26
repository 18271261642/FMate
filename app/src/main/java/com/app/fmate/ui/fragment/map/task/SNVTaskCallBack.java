package com.app.fmate.ui.fragment.map.task;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

/**
 * 功能:异步任务+无内存泄漏
 */
public abstract class SNVTaskCallBack {
    private static final int WHAT_PREPARE = -0x1103000;
    private static final int WHAT_DONE = -0x1103001;
    private static final int WHAT_ERROR = -0x1103002;
    private WeakReference<Object> mTarget;

    public SNVTaskCallBack(Object target) {
        mTarget = new WeakReference<>(target);
    }

    public SNVTaskCallBack() {
    }


    protected void sleep(long millis) throws InterruptedException {
        	Thread.sleep(millis);
    }

//    Handler getWeakHandler() {
//        return weakHandler;
//    }

    public <WeakTarget> WeakTarget getTarget() {
        if (mTarget == null) {
            throw new RuntimeException("你没有设置WeakTarget");
        }
        return (WeakTarget) mTarget.get();
    }


//    private Handler weakHandler = new WeakHandler<>(new WeakHandler.CallBack() {
//        @Override
//        public void handleMessage(Message msg) {
//
//            switch (msg.what) {
//                case WHAT_PREPARE:
//                    SNVTaskCallBack.this.prepare();
//                    break;
//                case WHAT_DONE:
//                    SNVTaskCallBack.this.done();
//                    break;
//                case WHAT_ERROR:
//                    SNVTaskCallBack.this.error((Throwable) msg.obj);
//                    break;
//                default:
//                    SNVTaskCallBack.this.main(msg.what, msg.obj);
//                    break;
//            }
//        }
//    });
    private BaseAsyncTask<Void, Object, Void> execute;

    @SuppressLint("StaticFieldLeak")
    void exec() {
//        sendEmptyMessages(WHAT_PREPARE);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    SNVTaskCallBack.this.run();
//                    sendEmptyMessages(WHAT_DONE);
//                } catch (Throwable throwable) {
//                    sendMessages(WHAT_ERROR, throwable);
//                }
//            }
//        }).start();


        execute = new BaseAsyncTask<Void, Object, Void>() {
            boolean hasErr = false;
            @Override
            protected void onPreExecute() {
                hasErr = false;
                SNVTaskCallBack.this.prepare();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    SNVTaskCallBack.this.run();
                } catch (Throwable throwable) {
                    Message message = new Message();
                    message.what = WHAT_ERROR;
                    message.obj = throwable;
                    publishProgress2(message);
                }
                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                if(!hasErr) {
                    SNVTaskCallBack.this.done();
                }
            }

            @Override
            protected void onProgressUpdate(Object... values) {
                Message value = (Message) values[0];
                int what =value.what;
                switch (what) {
                    case WHAT_ERROR:
                        hasErr = true;
                        SNVTaskCallBack.this.error((Throwable)value.obj);
                        break;
                    default:
                        Object[] obj = (Object[]) value.obj;
                        SNVTaskCallBack.this.main(what,obj );
                        break;
                }

            }
        };

        execute.execute();
    }

    private abstract class BaseAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
        private volatile Method publishProgressMethod;

        BaseAsyncTask() {
            try {
                publishProgressMethod = AsyncTask.class.getDeclaredMethod("publishProgress", Object[].class);
                publishProgressMethod.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void publishProgress2(Progress  values) {
            try {
                if(values==null){
                    publishProgressMethod.invoke(this);
                }else {
                    //可变参数的反射 比较特殊 需要先new 一层数组
                    Object[] args = {values};
                    publishProgressMethod.invoke(this, new Object[]{args});
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void publishToMainThread(int what, Object... obj) {
//        sendMessages(what, obj);
        Message message = new Message();
        message.what = what;
        message.obj = obj;
        execute.publishProgress2(message);
    }



    public void publishToMainThread(int what) {
//        sendEmptyMessages(what);
        publishToMainThread(what, new Object());
    }


//    private void sendMessages(int what, Object obj) {
////        if (weakHandler.hasMessages(what)) {
////            weakHandler.removeMessages(what);
////        }
//        weakHandler.sendMessage(weakHandler.obtainMessage(what, obj));
//
//
//    }
//
//    private void sendEmptyMessages(int what) {
////        if (weakHandler.hasMessages(what)) {
////            weakHandler.removeMessages(what);
////        }
//        weakHandler.sendEmptyMessage(what);
//    }

    public void prepare() {
    }

    public abstract void run() throws Throwable;

    public void main(int what, Object... obj) {

    }

    public void done() {

    }

    public void error(Throwable e) {
        e.printStackTrace();
    }
}