package com.mis.hrm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author dailinfu
 */
public class MailUtil {
    private MailUtil() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MailUtil.class);
    private static final String QQ_MAIL_SERVER = "smtp.qq.com";
    //1204695257@qq.com的专用认证码。如果使用其他邮箱，这里要改
    private static final String DAILINFU_AUTHORIZATION = "wmnoidyjbzhkgjje";

    /**
     * 发送文本文件
     *
     * @param fromUser 发件人
     * @param authorization 授权码 不是密码
     * @param toUser 收件人
     * @param title 题目
     * @param text 内容
     */
    public static boolean sendTextEmail(String fromUser,
                                        String authorization,
                                        String toUser,
                                        String title,
                                        String text) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(QQ_MAIL_SERVER); // QQ邮箱smtp发送服务器地址
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
            LOGGER.info("发送文本邮件成功");
            return true;
        } catch (MailException ex) {
            LOGGER.error(ex.toString());
            return false;

        }
    }

    /**
     * 发送html邮件
     *
     * @param fromUser 发件人
     * @param authorization 授权码 不是密码
     * @param toUser 收件人
     * @param title 题目
     * @param text 内容
     */
    public static boolean sendHtmlEmail(String fromUser,
                                        String authorization,
                                        String toUser,
                                        String title,
                                        String text) {
        JavaMailSenderImpl senderImpl = getJavaMailSenderImpl();
        MimeMessage mailMessage = senderImpl.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage);
        try {
            messageHelper.setTo(toUser);
            messageHelper.setFrom(fromUser);
            messageHelper.setSubject(title);
            //true 表示启动HTML格式的邮件
            messageHelper.setText(text, true);
        } catch (MessagingException e) {
            LOGGER.error(e.toString());
            return false;
        }
        senderImpl.setUsername(fromUser); // 根据自己的情况,设置username
        senderImpl.setPassword(authorization); // 根据自己的情况, 设置password
        //发送邮件
        senderImpl.send(mailMessage);
        LOGGER.info("发送网页邮件成功");
        return true;
    }

    public static boolean projectWarning(String toUser, String projectName,String projectUrl) {
        JavaMailSenderImpl senderImpl = getJavaMailSenderImpl();
        MimeMessage mailMessage = senderImpl.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, "UTF-8");
            messageHelper.setTo(toUser);
            messageHelper.setFrom("1204695257@qq.com");
            messageHelper.setSubject("项目报错");
            //true 表示启动HTML格式的邮件
            messageHelper.setText("项目"+projectName + "出错了"+"项目网址为"+projectUrl, true);
        } catch (MessagingException e) {
            LOGGER.error(e.toString());
            return false;
        }
        senderImpl.setUsername("1204695257@qq.com"); // 根据自己的情况,设置username
        senderImpl.setPassword(DAILINFU_AUTHORIZATION); // 根据自己的情况, 设置password
        //发送邮件
        senderImpl.send(mailMessage);
        LOGGER.info("发送网页邮件成功");
        return true;
    }

    private static JavaMailSenderImpl getJavaMailSenderImpl() {
        JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
        senderImpl.setHost(QQ_MAIL_SERVER);
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true"); // 将这个参数设为true，让服务器进行认证,认证用户名和密码是否正确
        prop.put("mail.smtp.timeout", "25000");
        senderImpl.setJavaMailProperties(prop);
        return senderImpl;
    }
}
