package com.Birthday_reminder.controller;

import com.Birthday_reminder.util.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 邮件发送功能测试控制器
 */
@RestController
@RequestMapping("/mail")
@CrossOrigin
public class MailTestController {

    @Autowired
    private MailUtil mailUtil;

    /**
     * 测试发送文本邮件
     */
    @PostMapping("/sendText")
    public Map<String, Object> sendTextMail(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String to = request.get("to");
            String subject = request.get("subject");
            String text = request.get("text");

            if (to == null || subject == null || text == null) {
                result.put("success", false);
                result.put("message", "收件人、主题和内容不能为空");
                return result;
            }

            mailUtil.sendTextMail(subject, text, Arrays.asList(to));
            result.put("success", true);
            result.put("message", "文本邮件发送成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "邮件发送失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 测试发送HTML邮件
     */
    @PostMapping("/sendHtml")
    public Map<String, Object> sendHtmlMail(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String to = request.get("to");
            String subject = request.get("subject");
            String html = request.get("html");

            if (to == null || subject == null || html == null) {
                result.put("success", false);
                result.put("message", "收件人、主题和内容不能为空");
                return result;
            }

            mailUtil.sendHtmlMail(subject, html, Arrays.asList(to));
            result.put("success", true);
            result.put("message", "HTML邮件发送成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "邮件发送失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 发送测试邮件（使用生日提醒模板）
     */
    @PostMapping("/sendTestNotice")
    public Map<String, Object> sendTestNoticeMail(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String to = request.get("to");
            String relationshipName = request.getOrDefault("relationshipName", "测试用户");
            String birthday = request.getOrDefault("birthday", "2025-01-01");
            String days = request.getOrDefault("days", "3");

            if (to == null) {
                result.put("success", false);
                result.put("message", "收件人不能为空");
                return result;
            }

            // 构建HTML邮件内容（使用生日提醒模板样式）
            String htmlContent = String.format(
                "<!DOCTYPE html>" +
                "<html lang=\"zh-CN\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <title>生日提醒</title>" +
                "</head>" +
                "<body style=\"font-family: 'Helvetica Neue', Arial, sans-serif; background: linear-gradient(135deg, #e3f2fd 0%%, #1e88e5 100%%); margin: 0; padding: 20px;\">" +
                "    <div style=\"max-width: 500px; margin: 0 auto; background: rgba(255,255,255,0.9); border-radius: 20px; overflow: hidden; box-shadow: 0 10px 30px rgba(30, 136, 229, 0.2);\">" +
                "        <div style=\"background: linear-gradient(135deg, #1e88e5, #1565c0); color: white; text-align: center; padding: 30px 20px;\">" +
                "            <h1 style=\"font-size: 28px; margin: 0; text-shadow: 0 2px 4px rgba(0,0,0,0.2);\">🎂 生日提醒 🎂</h1>" +
                "        </div>" +
                "        <div style=\"padding: 30px; text-align: center;\">" +
                "            <div style=\"font-size: 60px; margin: 10px 0 20px;\">🎂</div>" +
                "            <p style=\"font-size: 16px; margin-bottom: 20px; color: #263238; font-weight: 500;\">亲爱的用户，您关注的亲友即将迎来生日</p>" +
                "            <div style=\"background: rgba(255, 255, 255, 0.4); border-radius: 15px; padding: 25px; margin: 25px 0; box-shadow: 0 5px 15px rgba(0,0,0,0.05); border: 1px solid rgba(255,255,255,0.4);\">" +
                "                <div style=\"font-size: 24px; font-weight: 600; color: #1565c0; margin: 10px 0;\">%s</div>" +
                "                <div style=\"color: #546e7a; font-size: 16px; margin: 10px 0; font-weight: 500;\">生日日期: %s</div>" +
                "                <div style=\"font-size: 42px; font-weight: 700; color: #1565c0; margin: 15px 0; text-shadow: 0 2px 4px rgba(0,0,0,0.1);\">%s天</div>" +
                "                <p>别忘了准备生日祝福哦！</p>" +
                "            </div>" +
                "            <p style=\"font-size: 16px; margin-bottom: 20px; color: #263238; font-weight: 500;\">提前准备，让这份祝福更加特别</p>" +
                "        </div>" +
                "        <div style=\"background: rgba(21, 101, 192, 0.1); text-align: center; padding: 20px; color: #455a64; font-size: 14px; border-top: 1px solid rgba(255,255,255,0.3);\">" +
                "            <p style=\"margin: 5px 0;\">生日提醒服务</p>" +
                "            <p style=\"margin: 5px 0;\">愿每一个生日都充满欢声笑语</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>",
                relationshipName, birthday, days
            );

            String subject = String.format("🎂 生日提醒：%s 的生日就要到了！", relationshipName);
            mailUtil.sendHtmlMail(subject, htmlContent, Arrays.asList(to));
            
            result.put("success", true);
            result.put("message", "测试生日提醒邮件发送成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "邮件发送失败: " + e.getMessage());
        }
        return result;
    }
}