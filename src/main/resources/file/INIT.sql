CREATE TABLE music (
                       id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                       content TEXT COMMENT '歌词内容',
                       title VARCHAR(255) NOT NULL COMMENT '歌曲标题',
                       author VARCHAR(255) COMMENT '作者',
                       tags VARCHAR(500) COMMENT '标签，多个标签逗号分隔',
                       publish_time DATETIME COMMENT '发布时间',
                       picture_url VARCHAR(500) COMMENT '封面图片URL'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='音乐表';


CREATE TABLE system_config (
                               id INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                               code VARCHAR(100) NOT NULL COMMENT '配置编码',
                               value VARCHAR(500) COMMENT '配置值',
                               PRIMARY KEY (id),
                               UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

CREATE TABLE user (
                      id INT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                      name VARCHAR(100) NOT NULL COMMENT '用户名',
                      password VARCHAR(255) NOT NULL COMMENT '密码',
                      age INT COMMENT '年龄',
                      interests VARCHAR(500) COMMENT '兴趣爱好',
                      email VARCHAR(255) COMMENT '邮箱',
                      status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态',
                      register_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
                      PRIMARY KEY (id),
                      UNIQUE KEY uk_email (email),
                      UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE notification (
                              id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

                              `from` INT NOT NULL COMMENT '发送者用户ID',
                              `to` INT NOT NULL COMMENT '接收者用户ID',

                              target_type VARCHAR(50) NOT NULL COMMENT '目标类型（如post、comment）',
                              target_id INT NOT NULL COMMENT '目标ID',

                              operation VARCHAR(50) NOT NULL COMMENT '操作类型（如like、comment、follow）',

                              content VARCHAR(255) DEFAULT NULL COMMENT '通知内容',

                              operation_time DATETIME COMMENT '操作时间'
);
