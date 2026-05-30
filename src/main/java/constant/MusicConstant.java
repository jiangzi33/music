package constant;

public class MusicConstant {
    public final static double LIKE_SCORE = 1.0;
    public final static String EMAIL_HTML = "<!DOCTYPE html>\n" +
            "<html lang=\"zh-CN\">\n" +
            "<head>\n" +
            "  <meta charset=\"UTF-8\">\n" +
            "  <title>账号激活邮件</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "  <h2>账号激活</h2>\n" +
            "\n" +
            "  <p>您好，欢迎注册！</p >\n" +
            "\n" +
            "  <p>您的激活码为：</p >\n" +
            "\n" +
            "  <h1 style=\"color: #2f80ed; letter-spacing: 4px;\">{{code}}</h1>\n" +
            "\n" +
            "  <p>请在页面中输入该激活码完成账号激活。</p >\n" +
            "\n" +
            "  <p>如果这不是您本人操作，请忽略此邮件。</p >\n" +
            "</body>\n" +
            "</html>";
}
