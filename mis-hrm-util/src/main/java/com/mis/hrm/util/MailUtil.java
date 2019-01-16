package com.mis.hrm.util;

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
    public static String sendHtmlEmail(){
        JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();

        //设定mail server
        senderImpl.setHost("smtp.qq.com");

        //建立邮件消息,发送简单邮件和html邮件的区别
        MimeMessage mailMessage = senderImpl.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage);

        //设置收件人，寄件人
        try {
            messageHelper.setTo("1589056125@qq.com");
        messageHelper.setFrom("1204695257@qq.com");
        messageHelper.setSubject("测试HTML邮件！");
        //true 表示启动HTML格式的邮件
        messageHelper.setText("<html><head></head><body><h1>hello!!spring html Mail</h1></body></html>",true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        senderImpl.setUsername("1204695257@qq.com") ; // 根据自己的情况,设置username
        senderImpl.setPassword("wmnoidyjbzhkgjje") ; // 根据自己的情况, 设置password
        Properties prop = new Properties() ;
        prop.put("mail.smtp.auth", "true") ; // 将这个参数设为true，让服务器进行认证,认证用户名和密码是否正确
        prop.put("mail.smtp.timeout", "25000") ;
        senderImpl.setJavaMailProperties(prop);
        //发送邮件
        senderImpl.send(mailMessage);

        System.out.println("邮件发送成功..");
        return null;
    }
    public static void main(String[] args) {
        String text =
                "<div class=\"bg\" id=\"webmail\" style=\"\">\n" +
                        "\t<table cellpadding=\"0\" align=\"center\" style=\"margin:0 auto;overflow:hidden;text-align:left;font-size:14px;font-family:'lucida Grande',Verdana;line-height:1.5;border-collapse:collapse;width:720;\" class=\"table_wrap\">\n" +
                        "\t<tbody><tr><td style=\"padding:26px 60px 29px;background:#fff;\">\n" +
                        "    <p style=\"font-size:14px;line-height:17px;\">Hi， <span style=\"border-bottom:1px dashed #ccc;z-index:1\" t=\"7\" onclick=\"return false;\" data=\"1204695257\">1204695257</span>:</p>\n" +
                        "    <!--<p style=\"font-size:14px;line-height:17px;margin-top:10px;\">今年圣诞，在哪儿过呢？不论你身在何处，邮箱一直在这里。寄出你的心意，圣诞季的温暖，让我们帮你传递。</p>-->\n" +
                        "    <p style=\"font-size:14px;line-height:17px;margin-top:10px;\">今年圣诞，在哪儿过呢？</p><p></p>不论你身在何处，邮箱一直在这里。<p></p>寄出你的心意，圣诞季的温暖，让我们帮你传递。<p></p>\n" +
                        "    <div class=\"send\">\n" +
                        "      <p style=\"font-size:14px;line-height:17px;margin-top:18px;color:#A8A8A8\">给8位好友发祝福</p>\n" +
                        "      <a onclick=\"getTop().goUrlMainFrm('/cgi-bin/cardlist?t=card&amp;today_tips=200&amp;sid=hF3rKBjxtnhOaC9z&amp;friendnum=8&amp;loc=pushmail&amp;ListType=Cards&amp;Cate1Idx=listall&amp;select=hot&amp;auto=preview_1921321034&amp;pagenum=0&amp;kvclick=webmail|sendcard|2016|chrismas|pick|title5&amp;cardfrom=webmail|pushcard|2016|chrismas');\" href=\"javascript:;\" style=\"text-decoration:none !important;\"><table style=\"border-collapse:collapse;margin-top:10px\">\n" +
                        "        <tbody><tr>\n" +
                        "          <td width=\"38\"><img style=\"border-radius:4px;\" src=\"https://mail.qq.com/cgi-bin/getqqicon?u=-1107093880\" width=\"38px\" height=\"38px\"></td><td width=\"20\"></td><td width=\"38\"><img style=\"border-radius:4px;\" src=\"https://mail.qq.com/cgi-bin/getqqicon?u=-1687574021\" width=\"38px\" height=\"38px\"></td><td width=\"20\"></td><td width=\"38\"><img style=\"border-radius:4px;\" src=\"https://mail.qq.com/cgi-bin/getqqicon?u=-4027518575\" width=\"38px\" height=\"38px\"></td><td width=\"20\"></td><td width=\"38\"><img style=\"border-radius:4px;\" src=\"https://mail.qq.com/cgi-bin/getqqicon?u=-2166166457\" width=\"38px\" height=\"38px\"></td><td width=\"20\"></td><td width=\"38\"><img style=\"border-radius:4px;\" src=\"https://mail.qq.com/cgi-bin/getqqicon?u=-368061543\" width=\"38px\" height=\"38px\"></td><td width=\"20\"></td><td width=\"38\"><img style=\"border-radius:4px;\" src=\"https://mail.qq.com/cgi-bin/getqqicon?u=-914108677\" width=\"38px\" height=\"38px\"></td><td width=\"20\"></td><td width=\"38\"><img style=\"border-radius:4px;\" src=\"https://mail.qq.com/cgi-bin/getqqicon?u=-1388768203\" width=\"38px\" height=\"38px\"></td><td width=\"20\"></td><td width=\"38\"><img style=\"border-radius:4px;\" src=\"https://mail.qq.com/cgi-bin/getqqicon?u=-2261686179\" width=\"38px\" height=\"38px\"></td><td width=\"20\"></td>\n" +
                        "          <td width=\"26\" style=\"vertical-align:middle;\"><img src=\"https://rescdn.qqmail.com/zh_CN/htmledition/images/letter/20141110_christmas_push_mail/icon_forwards_small.png\" width=\"26px\" height=\"15px\"></td>\n" +
                        "          <td>&nbsp;</td>\n" +
                        "        </tr>\n" +
                        "      </tbody></table></a>\n" +
                        "      <div class=\"card\" style=\"margin-top:18px;padding:16px 17px;border:1px solid #e0e0e0;border-radius:10px;box-shadow:0 1px 6px 0 rgba(0,0,0,0.09);\">\n" +
                        "        <img src=\"https://rescdn.qqmail.com/qqmail/images/mailchristmasskating2018.jpg\" style=\"display:block;width:100%;height:auto;\" width=\"564\" height=\"260\" alt=\"\">\n" +
                        "        <!--<p style=\"margin-top:9px;font-size:14px;line-height:18px;\">圣诞老人来啦！准备好收礼物哦！</p>-->\n" +
                        "\t      <!--<p style=\"margin-top:9px;font-size:14px;line-height:18px;\">松树闪动着冬日的祥瑞，钟声洋溢着快乐的音符。圣诞节到了，祝愿亲爱的你，平安幸福，万事胜意。圣诞节快乐！</p>-->\n" +
                        "\t       <p style=\"margin-top:9px;font-size:14px;line-height:18px;\">愿洁白的雪花送去我的祝福，天气寒冷，也不减我对欢聚的热切盼望。圣诞快乐！</p>\n" +
                        "        <a onclick=\"getTop().sendCardSpread('/cgi-bin/sendcard?t=send_succ&amp;sendcardkey=Key15457505043479611894006600609799&amp;friendnum=8&amp;step=send&amp;acctype=chrismas&amp;mailto=1589056125&amp;mailto=136111535&amp;mailto=1099522822&amp;mailto=1353236065&amp;mailto=511394696&amp;mailto=649762218&amp;mailto=1830238540&amp;mailto=764477377');\" href=\"javascript:;\" class=\"sendbtn\" style=\"display:block;max-width:306px;margin:14px auto 0;height:40px;line-height:40px;text-align:center;color:#fff;font-size:16px;background-color:#4f9aee;border-radius:5px;text-decoration:none !important;\">发送</a>\n" +
                        "      </div>\n" +
                        "    </div>\n" +
                        "\t</td></tr>\n" +
                        "\t</tbody></table>\n" +
                        "</div>";
        sendHtmlEmail();
    }
}