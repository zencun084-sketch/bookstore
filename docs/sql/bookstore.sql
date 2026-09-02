-- ============================================================
-- 在线图书销售平台 BookStore 数据库脚本
-- MySQL 8.0+
-- 作者: 架构师
-- 日期: 2026-07-10
-- ============================================================

DROP DATABASE IF EXISTS bookstore_db;
CREATE DATABASE bookstore_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bookstore_db;

-- ============================================================
-- 1. 用户表 user
-- ============================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
  `password`    VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
  `email`       VARCHAR(100) NOT NULL COMMENT '邮箱',
  `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
  `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `gender`      TINYINT      DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
  `role`        VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色 USER-普通用户 ADMIN-管理员',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0-冻结 1-正常',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`  TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 2. 收货地址表 address
-- ============================================================
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `id`           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id`      BIGINT      NOT NULL COMMENT '用户ID',
  `receiver`     VARCHAR(50) NOT NULL COMMENT '收货人',
  `phone`        VARCHAR(20) NOT NULL COMMENT '联系电话',
  `province`     VARCHAR(50) DEFAULT NULL COMMENT '省份',
  `city`         VARCHAR(50) DEFAULT NULL COMMENT '城市',
  `district`     VARCHAR(50) DEFAULT NULL COMMENT '区县',
  `detail`       VARCHAR(255) NOT NULL COMMENT '详细地址',
  `is_default`   TINYINT     NOT NULL DEFAULT 0 COMMENT '是否默认 0-否 1-是',
  `create_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`   TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- ============================================================
-- 3. 分类表 category
-- ============================================================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name`        VARCHAR(50)  NOT NULL COMMENT '分类名称',
  `icon`        VARCHAR(255) DEFAULT NULL COMMENT '分类图标URL',
  `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序(越小越靠前)',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`  TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';

-- ============================================================
-- 4. 图书表 book
-- ============================================================
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
  `id`            BIGINT         NOT NULL AUTO_INCREMENT COMMENT '图书ID',
  `title`         VARCHAR(200)   NOT NULL COMMENT '书名',
  `author`        VARCHAR(100)   NOT NULL COMMENT '作者',
  `publisher`     VARCHAR(100)   DEFAULT NULL COMMENT '出版社',
  `isbn`          VARCHAR(20)    DEFAULT NULL COMMENT 'ISBN',
  `category_id`   BIGINT         DEFAULT NULL COMMENT '分类ID',
  `cover`         VARCHAR(255)   DEFAULT NULL COMMENT '封面图URL',
  `price`         DECIMAL(10,2)  NOT NULL COMMENT '售价',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
  `stock`         INT            NOT NULL DEFAULT 0 COMMENT '库存',
  `sales`         INT            NOT NULL DEFAULT 0 COMMENT '销量',
  `description`   TEXT           DEFAULT NULL COMMENT '图书简介',
  `status`        TINYINT        NOT NULL DEFAULT 1 COMMENT '状态 0-下架 1-上架',
  `publish_date`  DATE           DEFAULT NULL COMMENT '出版日期',
  `create_time`   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`    TINYINT        NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_sales` (`sales`),
  KEY `idx_isbn` (`isbn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- ============================================================
-- 5. 购物车表 cart
-- ============================================================
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `id`          BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id`     BIGINT NOT NULL COMMENT '用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ============================================================
-- 6. 购物车项表 cart_item
-- ============================================================
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item` (
  `id`          BIGINT    NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
  `cart_id`     BIGINT    NOT NULL COMMENT '购物车ID',
  `book_id`     BIGINT    NOT NULL COMMENT '图书ID',
  `quantity`    INT       NOT NULL DEFAULT 1 COMMENT '数量',
  `checked`     TINYINT   NOT NULL DEFAULT 1 COMMENT '是否选中 0-否 1-是',
  `create_time` DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cart_book` (`cart_id`, `book_id`),
  KEY `idx_book_id` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车项表';

-- ============================================================
-- 7. 订单表 orders (order 为关键字, 使用 orders)
-- ============================================================
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id`             BIGINT         NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no`       VARCHAR(32)    NOT NULL COMMENT '订单编号',
  `user_id`        BIGINT         NOT NULL COMMENT '用户ID',
  `total_amount`   DECIMAL(10,2)  NOT NULL COMMENT '订单总金额',
  `status`         TINYINT        NOT NULL DEFAULT 0 COMMENT '订单状态 0-待付款 1-待发货 2-已发货 3-已完成 4-已取消',
  `receiver`       VARCHAR(50)    NOT NULL COMMENT '收货人',
  `phone`          VARCHAR(20)    NOT NULL COMMENT '联系电话',
  `address`        VARCHAR(500)   NOT NULL COMMENT '收货地址',
  `remark`         VARCHAR(255)   DEFAULT NULL COMMENT '订单备注',
  `pay_time`       DATETIME       DEFAULT NULL COMMENT '支付时间',
  `ship_time`      DATETIME       DEFAULT NULL COMMENT '发货时间',
  `finish_time`    DATETIME       DEFAULT NULL COMMENT '完成时间',
  `cancel_time`    DATETIME       DEFAULT NULL COMMENT '取消时间',
  `create_time`    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`     TINYINT        NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ============================================================
-- 8. 订单明细表 order_item
-- ============================================================
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id`          BIGINT         NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id`    BIGINT         NOT NULL COMMENT '订单ID',
  `book_id`     BIGINT         NOT NULL COMMENT '图书ID',
  `book_title`  VARCHAR(200)   NOT NULL COMMENT '图书名称(快照)',
  `book_cover`  VARCHAR(255)   DEFAULT NULL COMMENT '图书封面(快照)',
  `price`       DECIMAL(10,2)  NOT NULL COMMENT '购买单价(快照)',
  `quantity`    INT            NOT NULL COMMENT '购买数量',
  `create_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_book_id` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- ============================================================
-- 9. 评论表 review
-- ============================================================
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `book_id`     BIGINT       NOT NULL COMMENT '图书ID',
  `user_id`     BIGINT       NOT NULL COMMENT '用户ID',
  `order_id`    BIGINT       DEFAULT NULL COMMENT '订单ID',
  `rating`      TINYINT      NOT NULL DEFAULT 5 COMMENT '评分 1-5',
  `content`     VARCHAR(500) DEFAULT NULL COMMENT '评论内容',
  `likes`       INT          NOT NULL DEFAULT 0 COMMENT '点赞数',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0-隐藏 1-显示',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`  TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_book_id` (`book_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书评论表';

-- ============================================================
-- 10. 收藏表 favorite
-- ============================================================
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
  `book_id`     BIGINT   NOT NULL COMMENT '图书ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_book` (`user_id`, `book_id`),
  KEY `idx_book_id` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- ============================================================
-- 11. 轮播图表 banner
-- ============================================================
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'BannerID',
  `title`       VARCHAR(100) DEFAULT NULL COMMENT '标题',
  `image_url`   VARCHAR(255) NOT NULL COMMENT '图片URL',
  `link_url`    VARCHAR(255) DEFAULT NULL COMMENT '跳转链接',
  `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`  TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='首页轮播图表';

-- ============================================================
-- 12. 公告表 notice
-- ============================================================
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title`       VARCHAR(100) NOT NULL COMMENT '公告标题',
  `content`     TEXT         NOT NULL COMMENT '公告内容',
  `status`      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态 0-未发布 1-已发布 2-已下线',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`  TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统公告表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 管理员账号 (密码: admin123, BCrypt加密)
INSERT INTO `user` (`username`, `password`, `email`, `nickname`, `role`, `status`) VALUES
('admin', '$2a$10$N.ZOn9G6/YLFixAOPMg/h.z7pCu6v2XyFDtC4q.jeeGm/TEZyj3C6', 'admin@bookstore.com', '系统管理员', 'ADMIN', 1);

-- 测试用户 (密码: 123456)
INSERT INTO `user` (`username`, `password`, `email`, `phone`, `nickname`, `gender`, `role`, `status`) VALUES
('zhangsan', '$2a$10$N.ZOn9G6/YLFixAOPMg/h.z7pCu6v2XyFDtC4q.jeeGm/TEZyj3C6', 'zhangsan@test.com', '13800138001', '张三', 1, 'USER', 1),
('lisi', '$2a$10$N.ZOn9G6/YLFixAOPMg/h.z7pCu6v2XyFDtC4q.jeeGm/TEZyj3C6', 'lisi@test.com', '13800138002', '李四', 2, 'USER', 1),
('wangwu', '$2a$10$N.ZOn9G6/YLFixAOPMg/h.z7pCu6v2XyFDtC4q.jeeGm/TEZyj3C6', 'wangwu@test.com', '13800138003', '王五', 1, 'USER', 1);

-- 分类
INSERT INTO `category` (`name`, `icon`, `sort`, `status`) VALUES
('文学', NULL, 1, 1),
('小说', NULL, 2, 1),
('编程', NULL, 3, 1),
('历史', NULL, 4, 1),
('科幻', NULL, 5, 1),
('教材', NULL, 6, 1),
('少儿', NULL, 7, 1),
('艺术', NULL, 8, 1);

-- 图书数据
INSERT INTO `book` (`title`, `author`, `publisher`, `isbn`, `category_id`, `cover`, `price`, `original_price`, `stock`, `sales`, `description`, `status`, `publish_date`) VALUES
('Java核心技术 卷I', '凯S.霍斯特曼', '机械工业出版社', '9787111619821', 3, NULL, 119.00, 149.00, 200, 1580, 'Java领域最有影响力的著作之一，全面覆盖Java SE核心技术。', 1, '2020-01-01'),
('深入理解Java虚拟机', '周志明', '机械工业出版社', '9787111252128', 3, NULL, 99.00, 129.00, 150, 2340, 'JVM经典著作，第三版全面升级，涵盖JDK 8~11。', 1, '2019-12-01'),
('Spring实战', '克雷格沃斯', '人民邮电出版社', '9787115417169', 3, NULL, 59.00, 79.00, 120, 980, 'Spring框架入门到精通，第4版全面更新。', 1, '2016-04-01'),
('三体', '刘慈欣', '重庆出版社', '9787536692930', 5, NULL, 23.00, 36.00, 500, 8900, '中国科幻里程碑，雨果奖获奖作品。', 1, '2008-01-01'),
('活着', '余华', '作家出版社', '9787506365437', 1, NULL, 28.00, 35.00, 300, 6700, '余华代表作，讲述一个人一生的苦难与坚韧。', 1, '2012-08-01'),
('百年孤独', '加西亚马尔克斯', '南海出版公司', '9787544253994', 1, NULL, 39.50, 55.00, 250, 4500, '魔幻现实主义经典，诺贝尔文学奖作品。', 1, '2011-06-01'),
('红楼梦', '曹雪芹', '人民文学出版社', '9787020002207', 1, NULL, 59.70, 98.00, 180, 3200, '中国古典四大名著之首，封建社会百科全书。', 1, '1996-12-01'),
('小王子', '圣埃克苏佩里', '人民文学出版社', '9787020042494', 7, NULL, 22.00, 28.00, 400, 5600, '法国经典童话，写给大人的哲理故事。', 1, '2003-08-01'),
('人类简史', '尤瓦尔赫拉利', '中信出版社', '9787508647357', 4, NULL, 45.00, 68.00, 220, 4100, '从动物到上帝，重新审视人类十万年发展史。', 1, '2014-11-01'),
('明朝那些事儿', '当年明月', '北京联合出版公司', '9787550213833', 4, NULL, 35.00, 45.00, 160, 3800, '通俗讲史经典，全景展现明朝276年。', 1, '2011-10-01'),
('算法导论', '托马斯H.科尔曼', '机械工业出版社', '9787111407010', 3, NULL, 128.00, 168.00, 90, 2100, '算法领域标准教材，麻省理工学院经典课程。', 1, '2013-01-01'),
('设计模式', '埃里希伽马', '机械工业出版社', '9787111075752', 3, NULL, 35.00, 45.00, 110, 1900, 'GoF经典著作，面向对象设计必读。', 1, '2000-09-01'),
('围城', '钱钟书', '人民文学出版社', '9787020024759', 2, NULL, 19.00, 25.00, 280, 5200, '钱钟书代表作，讽刺文学经典。', 1, '1991-02-01'),
('平凡的世界', '路遥', '北京十月文艺出版社', '9787530216781', 2, NULL, 68.00, 98.00, 200, 6100, '路遥呕心沥血之作，茅盾文学奖获奖作品。', 1, '2017-06-01'),
('JavaScript高级程序设计', '马特弗里斯比', '人民邮电出版社', '9787115545381', 3, NULL, 99.00, 129.00, 130, 2700, 'JavaScript红宝书第四版，前端工程师必读。', 1, '2020-09-01'),
('Vue.js设计与实现', '霍春阳', '人民邮电出版社', '9787115583864', 3, NULL, 79.00, 99.00, 100, 1500, 'Vue.js源码核心作者力作，深入框架设计原理。', 1, '2022-05-01'),
('艺术的故事', '贡布里希', '广西美术出版社', '9787807465717', 8, NULL, 188.00, 280.00, 60, 1200, '西方艺术史经典入门，贡布里希传世之作。', 1, '2008-04-01'),
('十万个为什么', '卢嘉锡', '少年儿童出版社', '9787532468926', 7, NULL, 29.80, 38.00, 350, 4300, '经典少儿科普读物，启迪科学思维。', 1, '2013-06-01');

-- 新增图书数据（扩充至全分类覆盖）
INSERT INTO `book` (`title`, `author`, `publisher`, `isbn`, `category_id`, `cover`, `price`, `original_price`, `stock`, `sales`, `description`, `status`, `publish_date`) VALUES
-- 文学 (1)
('边城', '沈从文', '北京十月文艺出版社', '9787530215668', 1, NULL, 23.00, 32.00, 320, 3100, '沈从文代表作，湘西边地纯美爱情故事，一曲田园牧歌。', 1, '2018-09-01'),
('骆驼祥子', '老舍', '人民文学出版社', '9787020137178', 1, NULL, 26.00, 35.00, 280, 2800, '老舍经典长篇，旧北京人力车夫的悲欢离合。', 1, '2017-06-01'),
('茶馆', '老舍', '人民文学出版社', '9787020137185', 1, NULL, 22.00, 28.00, 260, 2400, '老舍话剧巅峰之作，三幕写尽半个世纪社会变迁。', 1, '2017-06-01'),
('白鹿原', '陈忠实', '人民文学出版社', '9787020091686', 1, NULL, 49.00, 69.00, 200, 5200, '陈忠实呕心沥血之作，茅盾文学奖，渭河平原五十年风云。', 1, '2019-04-01'),
('飞鸟集', '泰戈尔', '译林出版社', '9787544291909', 1, NULL, 18.00, 25.00, 380, 3600, '泰戈尔诺贝尔文学奖代表作，325首清丽小诗。', 1, '2018-06-01'),
('月亮与六便士', '毛姆', '南海出版公司', '9787544291305', 1, NULL, 39.50, 59.00, 340, 7800, '毛姆代表作，理想与现实的永恒抉择，读完久久不能平静。', 1, '2019-09-01'),
('解忧杂货店', '东野圭吾', '南海出版公司', '9787544270874', 1, NULL, 39.50, 45.00, 420, 9200, '东野圭吾治愈系代表作，穿越时空的奇妙书信，温暖人心。', 1, '2014-05-01'),
('麦田里的守望者', '塞林格', '译林出版社', '9787544292845', 1, NULL, 29.50, 39.00, 250, 3400, '塞林格传世之作，一代青年的精神肖像。', 1, '2018-11-01'),
-- 小说 (2)
('倾城之恋', '张爱玲', '北京十月文艺出版社', '9787530213213', 2, NULL, 28.00, 38.00, 230, 2600, '张爱玲中短篇小说集，苍凉华丽的都市爱情传奇。', 1, '2019-01-01'),
('白夜行', '东野圭吾', '南海出版公司', '9787544258609', 2, NULL, 39.50, 47.00, 380, 8600, '东野圭吾巅峰之作，没有罪行的恶与没有救赎的爱。', 1, '2013-01-01'),
('挪威的森林', '村上春树', '上海译文出版社', '9787532770273', 2, NULL, 38.00, 48.00, 360, 7200, '村上春树代表作，青春、爱情与死亡的哀婉交响。', 1, '2018-05-01'),
('嫌疑人X的献身', '东野圭吾', '南海出版公司', '9787544248358', 2, NULL, 28.00, 35.00, 410, 8900, '东野圭吾直木奖作品，最纯粹的爱情与最极致的诡计。', 1, '2014-03-01'),
('追风筝的人', '卡勒德·胡赛尼', '上海人民出版社', '9787208061644', 2, NULL, 29.00, 36.00, 350, 9500, '阿富汗少年背叛与救赎的故事，催泪畅销经典。', 1, '2006-05-01'),
('杀死一只知更鸟', '哈珀·李', '译林出版社', '9787544292852', 2, NULL, 39.50, 52.00, 280, 4100, '普利策小说奖，关于正义、种族与成长的不朽之作。', 1, '2018-11-01'),
('了不起的盖茨比', '菲茨杰拉德', '上海译文出版社', '9787532765170', 2, NULL, 26.00, 35.00, 270, 3800, '爵士时代的挽歌，美国梦的幻灭与爱情的执念。', 1, '2017-07-01'),
('傲慢与偏见', '简·奥斯汀', '人民文学出版社', '9787020090207', 2, NULL, 25.00, 33.00, 310, 4500, '奥斯汀代表作，机智幽默的爱情经典，永不过时。', 1, '2016-08-01'),
-- 编程 (3)
('代码整洁之道', '罗伯特·马丁', '人民邮电出版社', '9787115216878', 3, NULL, 59.00, 79.00, 160, 3400, 'Bob大叔经典，教你写出可读、可维护、可测试的好代码。', 1, '2020-01-01'),
('重构：改善既有代码的设计', '马丁·福勒', '人民邮电出版社', '9787115510086', 3, NULL, 99.00, 129.00, 120, 2800, 'Martin Fowler重构圣经第二版，JavaScript示例全面更新。', 1, '2019-04-01'),
('Effective Java', '约书亚·布洛克', '机械工业出版社', '9787111638750', 3, NULL, 89.00, 119.00, 130, 3600, 'Java工程师必读，90条最佳实践，第三版涵盖Java 9。', 1, '2019-06-01'),
('Python编程：从入门到实践', '埃里克·马瑟斯', '人民邮电出版社', '9787115428020', 3, NULL, 79.00, 89.00, 280, 7600, 'Python入门畅销书，从语法到项目实战，零基础友好。', 1, '2016-07-01'),
('流畅的Python', '卢西亚诺·拉马略', '人民邮电出版社', '9787115454157', 3, NULL, 99.00, 139.00, 100, 3100, '深入Python语言特性，写出地道、简洁、高效的代码。', 1, '2017-12-01'),
('深入浅出MySQL', '翟东方', '人民邮电出版社', '9787115545398', 3, NULL, 89.00, 119.00, 140, 2200, '数据库开发、优化与管理运维一本通，第三版。', 1, '2019-08-01'),
('图解HTTP', '上野宣', '人民邮电出版社', '9787115358612', 3, NULL, 49.00, 59.00, 200, 4200, '用图解讲透HTTP协议，前端后端网络入门必读。', 1, '2014-05-01'),
('计算机程序的构造和解释', '哈罗德·阿贝尔森', '机械工业出版社', '9787111353419', 3, NULL, 69.00, 89.00, 90, 1800, 'MIT经典教材SICP，程序设计领域的圣经级著作。', 1, '2011-09-01'),
('计算机网络：自顶向下方法', '库罗斯', '机械工业出版社', '9787111657669', 3, NULL, 79.00, 99.00, 110, 2600, '全球计算机网络经典教材，自顶向下全新视角第七版。', 1, '2021-05-01'),
-- 历史 (4)
('史记', '司马迁', '中华书局', '9787101003041', 4, NULL, 89.00, 128.00, 120, 2900, '中国第一部纪传体通史，二十四史之首，鲁迅誉为史家之绝唱。', 1, '2013-08-01'),
('资治通鉴', '司马光', '中华书局', '9787101003676', 4, NULL, 168.00, 248.00, 80, 2100, '编年体通史巨著，1362年兴衰治乱，帝王将相必读。', 1, '2012-10-01'),
('中国通史', '吕思勉', '民主与建设出版社', '9787513921891', 4, NULL, 59.00, 88.00, 160, 3300, '吕思勉史学名著，纵览中华五千年文明脉络。', 1, '2018-01-01'),
('万历十五年', '黄仁宇', '中华书局', '9787101055825', 4, NULL, 22.00, 28.00, 300, 5400, '黄仁宇大历史观代表作，从一个年份读懂明朝衰亡。', 1, '2007-01-01'),
('全球通史', '斯塔夫里阿诺斯', '北京大学出版社', '9787301110201', 4, NULL, 78.00, 98.00, 140, 3900, '世界史经典读本，从史前到21世纪的全球视野。', 1, '2006-10-01'),
('历史的温度', '张玮', '中信出版社', '9787508684093', 4, NULL, 45.00, 58.00, 220, 4600, '馒头大师说历史，用温度还原历史人物的真实面孔。', 1, '2018-01-01'),
-- 科幻 (5)
('流浪地球', '刘慈欣', '长江文艺出版社', '9787535483712', 5, NULL, 39.50, 49.00, 360, 7600, '刘慈欣短篇科幻集，同名电影原著，带着地球去流浪。', 1, '2019-02-01'),
('球状闪电', '刘慈欣', '四川科学技术出版社', '9787536476719', 5, NULL, 35.00, 45.00, 280, 4900, '刘慈欣长篇科幻，追寻球状闪电背后的宇宙终极真理。', 1, '2016-06-01'),
('沙丘', '弗兰克·赫伯特', '江苏凤凰文艺出版社', '9787559415048', 5, NULL, 68.00, 98.00, 190, 5200, '科幻文学里程碑，雨果奖与星云奖双冠，同名电影原著。', 1, '2017-04-01'),
('基地', '艾萨克·阿西莫夫', '江苏文艺出版社', '9787539954178', 5, NULL, 45.00, 59.00, 240, 4300, '阿西莫夫基地七部曲开篇，银河帝国兴衰的恢弘史诗。', 1, '2015-08-01'),
('1984', '乔治·奥威尔', '上海译文出版社', '9787532770167', 5, NULL, 28.00, 38.00, 320, 6800, '反乌托邦三部曲之首，警示极权主义的永恒预言。', 1, '2018-05-01'),
('神经漫游者', '威廉·吉布森', '江苏凤凰文艺出版社', '9787559415031', 5, NULL, 42.00, 56.00, 180, 2700, '赛博朋克开山之作，雨果奖、星云奖、菲利普·迪克奖大满贯。', 1, '2017-04-01'),
-- 教材 (6)
('高等数学（第七版）上册', '同济大学数学系', '高等教育出版社', '9787040396638', 6, NULL, 47.80, 58.00, 300, 6800, '同济版高数经典教材，工科高校通用，考研必备。', 1, '2014-07-01'),
('线性代数（第六版）', '同济大学数学系', '高等教育出版社', '9787040396812', 6, NULL, 32.80, 39.80, 280, 5400, '同济版线代经典教材，考研数学基础用书。', 1, '2014-07-01'),
('概率论与数理统计', '浙江大学', '高等教育出版社', '9787040238969', 6, NULL, 36.80, 45.00, 260, 4900, '浙大版概率统计经典教材，理工科广泛采用。', 1, '2008-06-01'),
('大学物理', '张三慧', '清华大学出版社', '9787302523214', 6, NULL, 55.00, 68.00, 180, 2600, '清华大学经典物理教材，力学热学电磁学光学近代物理。', 1, '2019-01-01'),
('离散数学', '左孝凌', '上海科学技术文献出版社', '9787543910196', 6, NULL, 38.00, 48.00, 200, 3400, '计算机专业离散数学经典教材，数理逻辑与图论基础。', 1, '2015-08-01'),
('新视野大学英语读写教程', '郑树棠', '外语教学与研究出版社', '9787513555988', 6, NULL, 49.90, 59.90, 320, 5800, '大学英语通用教材第三版，读写能力综合训练。', 1, '2015-06-01'),
-- 少儿 (7)
('安徒生童话', '安徒生', '人民文学出版社', '9787020090191', 7, NULL, 35.00, 48.00, 420, 7800, '世界童话宝库，精选安徒生经典名篇，彩色注音版。', 1, '2016-08-01'),
('格林童话', '格林兄弟', '译林出版社', '9787544291985', 7, NULL, 32.00, 42.00, 400, 6500, '格林兄弟童话全集，伴随孩子成长的不朽经典。', 1, '2018-10-01'),
('伊索寓言', '伊索', '人民文学出版社', '9787020090184', 7, NULL, 25.00, 32.00, 380, 5200, '世界最早的寓言集，小故事蕴含大智慧。', 1, '2016-08-01'),
('夏洛的网', 'E.B.怀特', '上海译文出版社', '9787532751341', 7, NULL, 28.00, 37.00, 360, 6100, '怀特童话经典，一只蜘蛛和一头小猪的动人友谊。', 1, '2014-08-01'),
('哈利·波特与魔法石', 'J.K.罗琳', '人民文学出版社', '9787020068091', 7, NULL, 49.00, 59.00, 500, 12000, '哈利波特系列第一部，魔法世界的大门就此打开。', 1, '2018-10-01'),
('窗边的小豆豆', '黑柳彻子', '南海出版公司', '9787544250580', 7, NULL, 25.00, 32.00, 460, 9800, '连续多年少儿畅销榜，巴学园里最温暖的童年故事。', 1, '2011-01-01'),
-- 艺术 (8)
('美的历程', '李泽厚', '生活·读书·新知三联书店', '9787108030375', 8, NULL, 45.00, 58.00, 180, 3800, '李泽厚美学经典，纵览中国数千年艺术审美流变。', 1, '2009-07-01'),
('中国书法史', '沃兴华', '上海古籍出版社', '9787532580846', 8, NULL, 68.00, 88.00, 120, 1600, '系统梳理中国书法发展脉络，从甲骨到行草的演变。', 1, '2019-03-01'),
('西方音乐通史', '于润洋', '上海音乐出版社', '9787806679555', 8, NULL, 78.00, 98.00, 100, 1400, '高等艺术院校教材，从古希腊到现代的音乐长河。', 1, '2016-04-01'),
('摄影笔记', '宁思潇潇', '人民邮电出版社', '9787115546036', 8, NULL, 69.00, 89.00, 220, 3200, '摄影入门畅销书，从曝光构图到后期实战指南。', 1, '2020-01-01');

-- Banner
INSERT INTO `banner` (`title`, `image_url`, `link_url`, `sort`, `status`) VALUES
('新书速递 - Java核心技术', 'https://placeholder.com/banner1', '/book/1', 1, 1),
('科幻经典 - 三体系列', 'https://placeholder.com/banner2', '/book/4', 2, 1),
('文学殿堂 - 百年孤独', 'https://placeholder.com/banner3', '/book/6', 3, 1);

-- 公告
INSERT INTO `notice` (`title`, `content`, `status`) VALUES
('欢迎使用BookStore在线图书销售平台', '欢迎来到BookStore！我们致力于为您提供优质的图书购物体验。新用户注册即享首单优惠。', 1),
('系统维护通知', '系统将于每周日凌晨2:00-4:00进行例行维护，届时部分功能可能不可用，请提前安排。', 1);

-- 地址
INSERT INTO `address` (`user_id`, `receiver`, `phone`, `province`, `city`, `district`, `detail`, `is_default`) VALUES
(2, '张三', '13800138001', '广东省', '深圳市', '南山区', '科技园南区T3栋8楼', 1),
(2, '张三', '13800138001', '广东省', '深圳市', '福田区', '华强北路赛格广场12楼', 0);

-- ============================================================
-- 完成
-- ============================================================
SELECT 'bookstore_db 初始化完成' AS message;
