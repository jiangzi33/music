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

    public final static String GROUP_ID = "test-group";
    public final static String RECOMMEND_EMAIL_HTML ="<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>音乐上新推荐</title>\n" +
            "</head>\n" +
            "<body style=\"font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px;\">\n" +
            "<div style=\"max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 8px; padding: 24px;\">\n" +
            "    <h2 style=\"color: #333333;\">\uD83C\uDFB5 音乐上新推荐</h2>\n" +
            "\n" +
            "    <p>亲爱的用户，以下是最新上架的音乐：</p>\n" +
            "\n" +
            "    <table style=\"width:100%; border-collapse: collapse;\">\n" +
            "        <thead>\n" +
            "        <tr>\n" +
            "            <th style=\"border-bottom:1px solid #ddd; text-align:left; padding:10px;\">歌曲名称</th>\n" +
            "            <th style=\"border-bottom:1px solid #ddd; text-align:left; padding:10px;\">作者</th>\n" +
            "        </tr>\n" +
            "        </thead>\n" +
            "        <tbody>\n" +
            "\n" +
            "        <!-- 循环开始 -->\n" +
            "        <tr>\n" +
            "            <td style=\"padding:10px;\">{{musicTitle}}</td>\n" +
            "            <td style=\"padding:10px;\">{{musicAuthor}}</td>\n" +
            "        </tr>\n" +
            "        <!-- 循环结束 -->\n" +
            "\n" +
            "        </tbody>\n" +
            "    </table>\n" +
            "\n" +
            "    <p style=\"margin-top:20px;\">\n" +
            "        快来打开音乐平台，发现更多精彩内容！\n" +
            "    </p>\n" +
            "\n" +
            "    <hr style=\"border:none;border-top:1px solid #eee;\">\n" +
            "\n" +
            "    <p style=\"font-size:12px;color:#999;\">\n" +
            "        本邮件由 Music 推荐系统自动发送，请勿直接回复。\n" +
            "    </p>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";
}
