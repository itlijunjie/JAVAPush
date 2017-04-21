package com.itlijunjie.util;

import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import org.apache.log4j.PropertyConfigurator;

import java.util.ArrayList;
import java.util.List;

public class Test {

    private static List<String> tokens = new ArrayList<String>();
    private static String relativelyPath = "";
    //此处注意导出的证书密码不能为空因为空密码会报错
    private static String certificatePassword = "123123";

    static {
        Test t = new Test();
        relativelyPath = t.getClass().getResource("/").getPath();
        tokens.add("adb664b03655c18fa133d530574898f92fa8d3497d6afad66e814554aebde9e6");// plus
    }

    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure(relativelyPath + "log4j.properties");
        push();
    }

    static void push() {
        boolean sendCount = (tokens.size() == 1);
        try {
            //根据需求选择性注释
            PushNotificationPayload payLoad = PushNotificationPayload.
                    fromJSON("{" +
                            "aps:{" +
                            "alert:{" +
                            "title:\"title\"," +//标题
                            "subtitle:\"subtitle\"," +//子标题
                            "body:\"body\"" +//内容
                            "}," +
                            "sound:\"default\"," +//声音
                            "badge:2," +//角标
                            "mutable-content:1," +//多媒体推送
                            "category: \"seeCategory\"" +//category action
//                            "content-available:1" +//静默推送
                            "}" +
                            "}");

            //自定义多媒体下载链接
            payLoad.addCustomDictionary("imageUrl", "http://pic.qiantucdn.com/58pic/13/20/61/89B58PIC5Nz_1024.jpg");

            PushNotificationManager pushManager = new PushNotificationManager();
            // true：表示的是产品发布推送服务 false：表示的是产品测试推送服务
            String certificatePath = relativelyPath + "push.p12";
            pushManager.initializeConnection(new AppleNotificationServerBasicImpl(
                    certificatePath, certificatePassword, false));
            List<PushedNotification> notifications = new ArrayList<PushedNotification>();
            // 发送push消息
            if (sendCount) {
                Device device = new BasicDevice();
                device.setToken(tokens.get(0));
                PushedNotification notification = pushManager.sendNotification(
                        device, payLoad, true);
                notifications.add(notification);
            } else {
                List<Device> device = new ArrayList<Device>();
                for (String token : tokens) {
                    device.add(new BasicDevice(token));
                }
                notifications = pushManager.sendNotifications(payLoad, device);
            }
            pushManager.stopConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}