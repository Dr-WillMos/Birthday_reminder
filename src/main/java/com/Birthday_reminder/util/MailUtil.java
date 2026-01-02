package com.Birthday_reminder.util;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Teemo
 */
@Slf4j
@Component
public class MailUtil {
    @Autowired
    private MailProperties mailProperties;

    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * 发送简单文本邮件
     *
     * @param subject 邮件主题
     * @param text    邮件内容
     * @param toList  邮件接收者列表
     */
    public void sendTextMail(String subject, String text, List<String> toList) {
        // 列表转数组
        String[] to = toList.toArray(new String[0]);
        sendTextMail(subject, text, to);
    }

    public void sendTextMail(String from, String subject, String text, List<String> toList) {
        // 列表转数组
        String[] to = toList.toArray(new String[0]);
        // todo from
        sendTextMail(subject, text, to);
    }

    /**
     * 发送简单文本邮件
     *
     * @param subject 邮件主题
     * @param text    邮件内容
     * @param to      邮件接收者
     */
    @SneakyThrows
    public void sendTextMail(String subject, String text, String... to) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        try {
            // 邮件发送来源
            simpleMailMessage.setFrom(mailProperties.getUsername());
            // 邮件发送地址
            simpleMailMessage.setTo(to);
            // 邮件主题
            simpleMailMessage.setSubject(subject);
            // 邮件内容
            simpleMailMessage.setText(text);
            // 发送邮件
            javaMailSender.send(simpleMailMessage);
        } catch (MailException e) {
            log.error("发送简单文本邮件异常：{}", e.getMessage());
            e.printStackTrace();
            throw new MessagingException("发送简单文本邮件异常：" + e.getMessage());
        }
    }


    /**
     * 发送Html邮件
     *
     * @param subject 邮件主题
     * @param html    邮件内容
     * @param toList  邮件接收者列表
     */
    public void sendHtmlMail(String subject, String html, List<String> toList) {
        // 列表转数组
        String[] to = toList.toArray(new String[0]);
        sendHtmlMail(subject, html, to);
    }

    /**
     * 发送Html邮件
     *
     * @param subject 邮件主题
     * @param html    邮件内容
     * @param to      邮件接收者
     */
    @SneakyThrows
    public void sendHtmlMail(String subject, String html, String... to) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            // 邮件发送来源
            mimeMessageHelper.setFrom(mailProperties.getUsername());
            // 邮件发送地址
            mimeMessageHelper.setTo(to);
            // 邮件主题
            mimeMessageHelper.setSubject(subject);
            // 邮件内容
            mimeMessageHelper.setText(html, true);
            // 发送邮件
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("发送Html邮件异常：{}", e.getMessage());
            e.printStackTrace();
            throw new MessagingException("发送Html邮件异常：" + e.getMessage());
        }
    }


}
