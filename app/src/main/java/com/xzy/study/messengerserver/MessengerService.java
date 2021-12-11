package com.xzy.study.messengerserver;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

/**
 *
 * 服务端就一个Service，可以看到代码相当的简单，只需要去声明一个Messenger对象，然后onBind方法返回mMessenger.getBinder()；
 *
 * 然后坐等客户端将消息发送到handleMessage方法，根据message.what去判断进行什么操作，然后做对应的操作，
 * 最终将结果通过 msgFromClient.replyTo.send(msgToClient);返回。
 *
 * 可以看到我们这里主要是取出客户端传来的两个数字，然后求和返回，这里我有意添加了sleep(2000)模拟耗时,注意在实际使用过程中，
 * 可以换成在独立开辟的线程中完成耗时操作，比如和HandlerThread结合使用。。
 * 原文链接：https://blog.csdn.net/lmj623565791/article/details/47017485
 * @author xzy
 */
public class MessengerService extends Service {

    private static final int MSG_SUM = 0x110;

    /**
     * 实际开发最好换成 HandlerThread 的形式
     */
    @SuppressLint("handlerLeak")
    private final Messenger mMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msgFromClient) {
            // 返回给客户端的消息
            Message msgToClient = Message.obtain(msgFromClient);
            switch (msgFromClient.what) {
                // msg 客户端传来的消息
                case MSG_SUM:
                    msgToClient.what = MSG_SUM;
                    try {
                        // 模拟耗时
                        Thread.sleep(2000);
                        // 模拟加法
                        msgToClient.arg2 = msgFromClient.arg1 + msgFromClient.arg2;
                        // 核心代码
                        msgFromClient.replyTo.send(msgToClient);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }

            super.handleMessage(msgFromClient);
        }
    });

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
