package com.mis.hrm.util;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @author dailinfu
 */
public class MailUtil {
    private MailUtil(){}

    /**
     * 发送文本文件
     * @param fromUser 发件人
     * @param authorization 授权码 不是密码
     * @param toUser 收件人
     * @param title 题目
     * @param text 内容
     */
    public static void sendTextEmail(String fromUser,
                                     String authorization,
                                     String toUser,
                                     String title,
                                     String text) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.qq.com"); // QQ邮箱smtp发送服务器地址
        //mailSender.setPort(465); // QQ这个端口不可用
        mailSender.setPort(587);// 端口号
        mailSender.setUsername(fromUser); // 使用你自己的账号
        mailSender.setPassword(authorization); // 授权码-发短信获取
        // 邮件信息
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromUser); // 发件人邮箱
        msg.setTo(toUser); // 收件人邮箱
        msg.setSubject(title); // 标题
        msg.setText(text); // 文本信息
        try {
            mailSender.send(msg);
        } catch (MailException ex) {
            System.err.println("发送失败:" + ex.getMessage());
        }
    }
}