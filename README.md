# BookStore 在线图书销售平台

> 基于 Spring Boot + Vue3 前后端分离的企业级图书销售系统

## 项目简介

BookStore 是一套完整的 B2C 在线图书销售平台，包含前台购物和后台管理两大模块。前台提供图书浏览、搜索、收藏、购物车、下单、评论等功能；后台提供用户管理、图书管理、订单管理、数据统计等运营能力。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 2.7 + MyBatis-Plus + MySQL 8.0 + JWT + Druid |
| 前端 | Vue 3 + Vite + Element Plus + Pinia + Vue Router + ECharts |
| 工具 | Lombok + Hutool + Knife4j(Swagger) + NProgress |
| 构建 | Maven + npm |

## 项目结构

```
booksale/
├── docs/
│   └── sql/
│       └── bookstore.sql          # 数据库脚本
├── bookstore-backend/             # 后端项目
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/bookstore/
│       │   ├── BookStoreApplication.java
│       │   ├── config/            # 配置类
│       │   ├── common/            # 通用返回、异常
│       │   ├── security/          # JWT 认证
│       │   ├── utils/             # 工具类
│       │   ├── entity/            # 实体类 (12个)
│       │   ├── mapper/            # Mapper 接口
│       │   ├── dto/               # 数据传输对象
│       │   ├── service/           # 业务逻辑层
│       │   └── controller/        # 控制器层
│       └── resources/
│           ├── application.yml
│           └── mapper/            # MyBatis XML
├── bookstore-frontend/            # 前端项目
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── api/                   # 接口封装
│       ├── assets/                # 静态资源
│       ├── layouts/               # 布局组件
│       ├── router/                # 路由
│       ├── store/                 # Pinia 状态
│       ├── utils/                 # 工具类
│       └── views/                 # 页面
│           ├── front/             # 前台页面 (13个)
│           └── admin/             # 后台页面 (8个)
└── README.md
```

## 环境要求

- JDK 1.8+
- Maven 3.6+
- Node.js 16+
- MySQL 8.0+

## 快速开始

### 1. 数据库配置

```bash
# 登录 MySQL，执行数据库脚本
mysql -u root -p < docs/sql/bookstore.sql
```

### 2. 后端启动

```bash
cd bookstore-backend

# 修改数据库连接配置 (如有需要)
# vi src/main/resources/application.yml

# 编译打包
mvn clean package -DskipTests

# 运行
java -jar target/bookstore-backend-1.0.0.jar
```

后端启动后：
- 接口地址：http://localhost:8080/api
- 接口文档：http://localhost:8080/api/doc.html

### 3. 前端启动

```bash
cd bookstore-frontend

# 安装依赖
npm install

# 开发模式运行
npm run dev

# 生产构建
npm run build
```

前端启动后：http://localhost:5173

## 演示账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 普通用户 | zhangsan | 123456 |

## 功能清单

### 前台功能

| 模块 | 功能 |
|------|------|
| 用户 | 注册、登录、个人资料、修改密码、头像 |
| 首页 | Banner轮播、分类导航、热门推荐、新书推荐 |
| 图书 | 列表分页、分类筛选、排序、搜索、详情 |
| 购物车 | 加入、修改数量、选中、全选、删除、结算 |
| 订单 | 下单、模拟支付、取消、确认收货、再次购买 |
| 收藏 | 收藏、取消收藏、收藏列表 |
| 评论 | 评分、文字评论、点赞 |
| 地址 | 增删改查、设为默认 |

### 后台功能

| 模块 | 功能 |
|------|------|
| 控制台 | 概览数据、订单趋势、销售趋势、热门排行 (ECharts) |
| 用户管理 | 分页搜索、冻结/恢复 |
| 图书管理 | CRUD、上下架、库存 |
| 分类管理 | CRUD、排序、启禁用 |
| 订单管理 | 搜索、发货、详情 |
| 评论管理 | 显示/隐藏、删除 |
| Banner管理 | CRUD |
| 公告管理 | CRUD、发布/下线 |

## 接口规范

所有接口统一返回格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| code | 含义 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

## 安全特性

- JWT Token 认证
- BCrypt 密码加密
- 接口权限拦截 (前台/后台)
- 全局异常处理
- 参数校验
- SQL 注入防护 (MyBatis-Plus)
- 跨域配置

## 数据库 ER 图

主要实体关系：

```
User ──< Address
User ──< Cart ──< CartItem >── Book
User ──< Orders ──< OrderItem >── Book
User ──< Favorite >── Book
User ──< Review >── Book
Book >── Category
```

---

© 2026 BookStore 在线图书销售平台 | 课程设计项目
