# 图书管理系统（Spring Boot 3 + MyBatis-Plus + MySQL / Vue 3 + Element Plus）

一个前后端分离的图书管理系统演示项目，功能范围为**图书 CRUD（精简版）**：分页列表、关键字/分类/状态筛选、新增、编辑、单条删除、批量删除。

包含完整的 **MySQL 初始化脚本与虚拟测试数据（41 本图书）**，可直接在数据库中重现。

> 另外提供 **零安装的纯 HTML 版前端**（`html/` 目录）：双击即可打开使用，不需要 Java / MySQL / Node。
> 详见 [§三.5 【最简单】用纯 HTML 版](#5-最简单用纯-html-版不装任何环境)。

---

## 一、技术栈

| 层次 | 技术 |
| --- | --- |
| 后端 | **Java 17**（JDK 17 / 21 均可）、Spring Boot 3.3.5、MyBatis-Plus 3.5.16、MySQL 8.0+、Maven |
| 前端 | Vue 3.5、Vite 6、Element Plus 2.8、Axios、Vue Router 4 |
| 前端（零安装版） | 原生 HTML + CSS + JavaScript 单文件，**无需构建 / 无需 npm / 无需 CDN** |
| 数据库 | MySQL 8.0（默认）／ H2（可选，免安装）／ 浏览器 localStorage（纯离线版） |

后端已内置 Maven Wrapper（`mvnw` / `mvnw.cmd`），**本机不需要预装 Maven**。

**JDK 要求：Java 17。**

- `pom.xml` 中 `<java.version>17</java.version>`、`<maven.compiler.release>17</maven.compiler.release>`，
  编译出的字节码为 class 版本 61（Java 17）。
- Spring Boot 3.3.x 的最低要求就是 Java 17，因此 **JDK 17 和 JDK 21 都能编译并运行**；
  用 JDK 21 编译时也会按 17 的 API 校验（`--release 17`），不会误用高版本 API。
- 代码中没有使用 record / sealed / 模式匹配 switch 等 Java 21 专有语法，切换版本无需改代码。

---

## 二、目录结构

```
book-management/
├── README.md
├── .gitignore
├── db/                              # MySQL 初始化脚本（权威版本）
│   ├── schema.sql                   #   建表语句
│   ├── data.sql                     #   虚拟数据（41 本图书）
│   └── init_full.sql                #   一键导入：建库 + 建表 + 数据
├── html/                            # 纯 HTML 版前端（无需构建，双击可用）
│   ├── index.html                   #   ★ 纯离线版：数据存浏览器，不需要后端
│   └── with-backend.html            #   连接版：调用 Spring Boot 的 /api/book 接口
├── backend/                         # Spring Boot 后端
│   ├── pom.xml
│   ├── mvnw / mvnw.cmd / .mvn/      #   Maven Wrapper
│   └── src/main/
│       ├── java/com/example/library/
│       │   ├── LibraryApplication.java
│       │   ├── common/              #   Result、PageResult、BusinessException、全局异常处理
│       │   ├── config/              #   MyBatis-Plus 分页插件、跨域配置
│       │   ├── controller/BookController.java
│       │   ├── dto/BookQuery.java
│       │   ├── entity/Book.java
│       │   ├── mapper/BookMapper.java
│       │   └── service/             #   BookService + impl
│       └── resources/
│           ├── application.yml      #   mysql / h2 两套 profile
│           ├── db/                  #   供 h2 profile 使用的建表与数据脚本
│           └── static/index.html    #   连接版前端（打开 http://localhost:8080/ 即可访问）
└── frontend/                        # Vue 3 前端
    ├── package.json
    ├── vite.config.js               #   含 /api 反向代理
    ├── index.html
    └── src/
        ├── main.js
        ├── App.vue
        ├── router/index.js
        ├── api/                     #   request.js（axios 封装）、book.js
        ├── styles/main.css
        └── views/BookList.vue       #   图书列表页（本项目主页面）
```

> `html/with-backend.html` 与 `backend/src/main/resources/static/index.html` 内容完全相同，
> 后者让后端启动后可以直接用 <http://localhost:8080/> 打开同一个页面。

---

## 三、快速开始

### 1. 初始化数据库（MySQL）

> **前提**：本机已安装并启动 MySQL（Windows 上通常是服务 `MySQL80`）。
> 检查命令：`Get-Service MySQL80`，或看 3306 端口是否在监听。
>
> **常见坑**：MySQL 的 `bin` 目录**默认不在 PATH 里**，所以直接敲 `mysql` 会提示
> "不是内部或外部命令 / 找不到命令"。要么用完整路径，要么把
> `C:\Program Files\MySQL\MySQL Server 8.0\bin` 追加到系统环境变量 `Path`。

脚本 `db/init_full.sql` 会创建数据库 `library_db`、表 `book`，并写入 41 本虚拟图书。
下面几种方式**任选一种**，都不需要 bash。

#### 方式 A：图形化工具（推荐，完全不碰命令行）

| 工具 | 操作 |
| --- | --- |
| **MySQL Workbench**（官方免费） | 用本机已装的 *MySQL Installer for Windows* 添加它 → 连接本地实例 → `File → Open SQL Script…` 选 `db/init_full.sql` → 点 ⚡ 执行 |
| **DBeaver / HeidiSQL / Navicat** | 连上实例 → 打开 `db/init_full.sql` → 执行全部 |
| **IntelliJ IDEA Ultimate** | `View → Tool Windows → Database` → `+ → Data Source → MySQL` 填账号密码 → 打开 `db/init_full.sql` → 右键 `Run 'init_full.sql'`（**社区版没有该工具窗**） |

#### 方式 B：Windows 命令行（PowerShell / cmd，不是 bash）

**PowerShell 不支持 `<` 输入重定向**，所以 `mysql -u root -p < db/init_full.sql` 这种 bash 写法
在 PowerShell 里会直接报错。Windows 上应该这样写：

```powershell
# 方式 1（推荐）：先 cd 到 book-management 目录，再用绝对路径 + 正斜杠
cd C:\Users\18553\Documents\deepseek-harness\default-workspace\book-management
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p `
  -e "source db/init_full.sql"

# 方式 2：不 cd，直接用绝对路径（注意 source 里必须用正斜杠 /，不能写反斜杠）
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p `
  -e "source C:/Users/18553/Documents/deepseek-harness/default-workspace/book-management/db/init_full.sql"

# 方式 3：用输入重定向（由 shell 打开文件，所以反斜杠没问题）
cmd /c '"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < C:\Users\18553\Documents\deepseek-harness\default-workspace\book-management\db\init_full.sql'

# 方式 4：PowerShell 管道（务必 -Raw + UTF8，否则中文会乱码）
cd C:\Users\18553\Documents\deepseek-harness\default-workspace\book-management
Get-Content .\db\init_full.sql -Raw -Encoding UTF8 |
  & "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p
```

> **两个高频坑**
> 1. `ERROR at line 1: Failed to open file 'db\init_full.sql', error: 2`
>    —— 这不是密码问题，是**路径不对**。相对路径以"当前的命令行目录"为基准，
>    要确认你确实在 `book-management` 目录下，或者干脆用绝对路径。
> 2. `source` 后面**必须写正斜杠 `/`**（如 `db/init_full.sql`），写成 `db\init_full.sql`
>    会被 mysql 客户端当成转义字符处理而失败。

若只想分开执行，也可以按顺序跑 `db/schema.sql` 再跑 `db/data.sql`。

#### 方式 C：干脆不手动建库

- **免安装方案**：直接用 `h2` profile，完全不需要 MySQL：
  `java -jar target/library-backend-1.0.0.jar --spring.profiles.active=h2`
- **让 Spring Boot 启动时自动建库建表灌数据**：给 JDBC URL 加
  `createDatabaseIfNotExist=true`，再配置 `spring.sql.init` 指向 `db/schema.sql`、`db/data.sql`。
  项目**默认没有开启**（避免每次启动重置数据），需要的话可以按这个思路自行补上。

#### 然后改账号密码（必做）

`db/init_full.sql` 只负责建库建表；**连不上数据库时优先检查密码**：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/library_db?...
    username: root
    password: 123456      # ← 改成你自己的 MySQL 密码
```

> 报 `Access denied for user 'root'@'localhost'` 就是密码不对；
> 不确定密码的话，用上面的图形化工具连一下即可验证。

**连自己设过的 root 密码都忘了？** 项目里带了一个重置脚本，管理员运行即可：

```powershell
# 以【管理员身份】打开 PowerShell，然后：
cd book-management\tools
.\reset-mysql-root-password.ps1
# 想自定义新密码：
.\reset-mysql-root-password.ps1 -NewPassword "MyPass@2026"
```

原理是用 `mysqld --init-file` 在启动瞬间执行一条 `ALTER USER` 改掉 root 密码，再恢复正常服务；
脚本会自动停服务、重置、重启服务并验证登录。

> 说明：`application.yml` 里的 `password: 123456` **只是一个占位符**，不是真实的默认密码，
> 必须换成你自己 MySQL 的密码。新密码建议至少 8 位并包含大小写+数字+符号，
> 否则可能被 MySQL 的 `validate_password` 策略拒绝。

### 2. 启动后端

```bash
cd backend

# 开发模式直接运行
./mvnw spring-boot:run            # Windows: mvnw.cmd spring-boot:run

# 或打包后运行
./mvnw clean package -DskipTests
java -jar target/library-backend-1.0.0.jar
```

后端启动在 <http://localhost:8080>。

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

浏览器打开 <http://localhost:5173> 即可看到图书列表。

> 前端开发服务器已配置 `/api` 反向代理到 `http://localhost:8080`（见 `vite.config.js`），
> 因此开发时不依赖后端 CORS；后端也同时开启了 CORS，方便前后端分开部署。

### 4. 不想安装 MySQL？用 H2 直接跑

```bash
cd backend
java -jar target/library-backend-1.0.0.jar --spring.profiles.active=h2
```

- 数据库文件会自动生成在 `backend/data/library_db.mv.db`（这就是"虚拟数据库文件"的免安装版本）
- **注意**：`h2` profile 每次启动都会重建示例数据，方便反复演示；正式使用请用 `mysql` profile

### 5. 【最简单】用纯 HTML 版，不装任何环境

`html/` 目录下提供两个**无需构建的单文件前端**（原生 HTML + CSS + JS，不依赖 npm、不依赖 CDN，
内网/离线环境同样可用）：

| 文件 | 说明 | 数据存放位置 | 需要后端吗 |
| --- | --- | --- | --- |
| `html/index.html` | **纯离线版（推荐先试这个）** | 浏览器 localStorage | ❌ 不需要 |
| `html/with-backend.html` | 连接版，数据完全走 Spring Boot 接口 | MySQL / H2 | ✅ 需要 |

#### 用法 A：纯离线版 —— 直接双击 `html/index.html`

不需要安装 Java、Maven、MySQL、Node，页面内置 41 本演示图书，打开就能用。

功能与 Vue 版一致（查询、分页、新增、编辑、单条删除、批量删除），
另外多了三个按钮：**导出 JSON**、**导入 JSON**、**恢复示例数据**。

> 数据保存在**当前浏览器**的 localStorage 中。
> 换浏览器 / 换电脑 / 清理缓存后数据不通用，请用"导出 JSON"备份、"导入 JSON"还原。

#### 用法 B：连接版 —— 让数据存到真正的数据库里

```bash
# 1) 先按上面第 1、2 步准备好数据库并启动后端
# 2) 然后任选一种方式打开连接版页面：

#    方式一（推荐，无跨域问题）：后端已把该页面放在静态目录，直接访问
http://localhost:8080/

#    方式二：直接双击 html/with-backend.html
#           页面会自动请求 http://localhost:8080/api（后端已开启 CORS，含 file:// 场景）
```

---

### 6. 在 IntelliJ IDEA 中运行后端

**关键前提：Maven 项目是 `backend/pom.xml`，不是仓库根目录 `book-management/`。**

`book-management/` 下没有顶层 `pom.xml`，所以**直接把整个 `book-management` 文件夹拖进 IDEA** 时，
IDEA 只会把它建成一个普通 Java 项目（生成一个空的 `book-management.iml`）：
没有源码根、没有依赖、找不到主类 —— 表现就是**运行按钮是灰的 / 右键没有 Run**。

#### 方式一（推荐）：直接用 IDEA 打开 `backend` 目录

`File → Open…` → 选中 **`book-management/backend`**（注意不是外层的 `book-management`）→ OK。
IDEA 发现根目录下的 `pom.xml`，会自动按 Maven 项目导入；右下角进度条跑完即可运行。

#### 方式二：保持当前窗口，把 backend 加为 Maven 模块

1. 在左侧项目树里找到 `backend/pom.xml`
2. 右键 → **Add as Maven Project**
   （若没有该菜单：`File → Project Structure → Modules → + → Import Module`，然后选 `backend/pom.xml`）
3. 等右下角 Maven 导入与索引完成

#### 通用收尾设置

| 设置项 | 位置 | 建议值 |
| --- | --- | --- |
| Project SDK | File → Project Structure → Project | JDK 17 或 21 |
| Language level | 同上 | 17 |
| 文件编码 | Settings → Editor → File Encodings | 全部 UTF-8 |
| 注解处理 | Settings → Build, Execution, Deployment → Compiler → Annotation Processors | 勾选 Enable（Lombok 需要） |

#### 创建运行配置

`Run → Edit Configurations… → + → Spring Boot`（若没有该模板就选 Application），填写：

| 字段 | 值 |
| --- | --- |
| Main class | `com.example.library.LibraryApplication` |
| Program arguments | `--spring.profiles.active=h2` |
| Use classpath of module | `backend` |
| Working directory | `backend` 目录 |

> **强烈建议带上 `--spring.profiles.active=h2`**：默认的 `mysql` profile 要求本机已装好 MySQL 并导入
> `db/init_full.sql`，否则启动时会因为连不上数据库而失败。

配置好后点绿色 ▶ 运行；也可以直接打开 `LibraryApplication.java`，点类名左侧的绿色箭头。

---

## 四、数据库设计

表名：`book`

| 字段 | 类型 | 允许空 | 说明 |
| --- | --- | --- | --- |
| id | BIGINT | 否 | 主键，自增 |
| isbn | VARCHAR(20) | 否 | ISBN，唯一索引 |
| title | VARCHAR(200) | 否 | 书名，普通索引 |
| author | VARCHAR(100) | 否 | 作者 |
| publisher | VARCHAR(100) | 是 | 出版社 |
| category | VARCHAR(50) | 是 | 分类，普通索引 |
| publish_date | DATE | 是 | 出版日期 |
| price | DECIMAL(10,2) | 是 | 定价（元） |
| total_copies | INT | 否 | 馆藏总数 |
| available_copies | INT | 否 | 可借数量 |
| location | VARCHAR(50) | 是 | 馆藏位置 |
| status | TINYINT | 否 | 1-在架 0-下架 |
| remark | VARCHAR(255) | 是 | 备注 |
| create_time | DATETIME | 否 | 创建时间，默认当前时间 |
| update_time | DATETIME | 否 | 更新时间，更新时自动刷新 |

虚拟数据共 **41 本图书**，覆盖 9 个分类（计算机 12、外国文学 9、历史 4、当代小说 4、古典文学 3、科幻小说 3、儿童文学 2、现代文学 2、科普 2），其中包含 1 本"下架"状态、2 本可借数量为 0 的图书记录。

---

## 五、接口文档

统一前缀：`/api/book`

| 方法 | 路径 | 说明 | 参数 |
| --- | --- | --- | --- |
| GET | `/api/book/page` | 分页 + 条件查询 | `pageNum`、`pageSize`、`keyword`（书名/作者/ISBN 模糊）、`category`、`status` |
| GET | `/api/book/{id}` | 查询详情 | 路径参数 id |
| GET | `/api/book/categories` | 全部分类（下拉框用） | 无 |
| POST | `/api/book` | 新增 | JSON 请求体 |
| PUT | `/api/book` | 修改 | JSON 请求体（必须含 `id`） |
| DELETE | `/api/book/{id}` | 删除单本 | 路径参数 id |
| DELETE | `/api/book/batch` | 批量删除 | JSON 数组，如 `[1,2,3]` |

**统一返回结构**

```json
{ "code": 200, "message": "操作成功", "data": { } }
```

| code | 含义 |
| --- | --- |
| 200 | 成功 |
| 400 | 参数校验失败 / 业务校验失败（如"可借数量不能大于馆藏总数"） |
| 404 | 图书不存在 |
| 409 | ISBN 已存在 |
| 500 | 系统异常 |

> 说明：该项目采用常见的"HTTP 200 + 业务 code"约定，即业务异常时 HTTP 状态码仍为 200，
> 错误信息通过 `code` / `message` 返回，由前端统一提示。
> 如需改为真实的 HTTP 状态码，在 `GlobalExceptionHandler` 的方法上加 `@ResponseStatus` 即可。
>
> **纯离线版页面不发送任何网络请求**，但内部数据层（`LocalAPI`）刻意实现了与上表**完全相同**的
> 路径、返回结构和错误码，因此离线版与连接版的界面代码是同一套。

**分页响应示例**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 41,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 5,
    "records": [ { "id": 41, "isbn": "9787532733678", "title": "追忆似水年华", "...": "..." } ]
  }
}
```

---

## 六、功能清单

- 图书列表：分页（每页 10/20/50/100）、按 id 倒序
- 组合查询：关键字（书名/作者/ISBN 模糊匹配）+ 分类 + 在架状态
- 新增 / 编辑：弹窗表单，含完整前端校验与后端 `@Valid` 校验
- 唯一性校验：ISBN 重复时返回 409 / 前端提示"ISBN 已存在"
- 业务校验：可借数量不能大于馆藏总数
- 删除：单条删除（带确认框）、多选批量删除
- 库存与状态展示：可借/馆藏 标签、在架/下架 标签
- **数据备份（HTML 版）**：导出 JSON / 导入 JSON（导入时按 ISBN 自动跳过重复，不覆盖已有数据）
- **一键还原（HTML 版）**："恢复示例数据"按钮可把本地数据重置为 41 本演示图书

---

## 七、已验证内容

本项目在交付前已实际运行验证：

| 验证项 | 结果 |
| --- | --- |
| Maven 构建（**JDK 17** 执行 `clean package`） | ✅ BUILD SUCCESS，字节码 class 版本 **61（Java 17）**，清单 `Build-Jdk-Spec: 17` |
| 运行环境（JDK 17） | ✅ 启动日志 `Starting LibraryApplication v1.0.0 using Java 17.0.12`，Tomcat 10.1.31 正常 |
| 后端接口冒烟测试（JDK 17 运行，16 项） | ✅ **16/16 通过**（增删改查、分页、校验、异常分支） |
| 中文编码（JDK 17 重点回归） | ✅ 预置数据「红楼梦」`U+7EA2 U+697C U+68A6`、「三体」`U+4E09 U+4F53` 码点正确；中文搜索 `三体`→3 条、`刘慈欣`→3 条、`计算机`→1 条；新增中文往返一致 |
| 数据库初始化脚本执行（H2 MySQL 兼容模式） | ✅ 41 行写入成功，ISBN 无重复，库存数据合法 |
| 前端构建（`npm run build`） | ✅ 1679 个模块编译通过，产出 dist |
| 前后端联调（Vite 代理 → 后端） | ✅ `/api/book/page` 经代理返回正确 JSON |
| Vue 页面渲染（DOM 检查） | ✅ 标题、表头、按钮、分页、"共 41 条"、图书数据均正常渲染 |
| HTML 连接版渲染（后端托管，JDK 17） | ✅ 页头"后端已连接"、"共 41 条"、10 行、中文书名/作者/分类均无乱码 |
| HTML 纯离线版渲染 | ✅ **后端 8080 端口关闭状态下**双击打开即渲染 41 条、10 行/页 |
| HTML 纯离线版增删改查 | ✅ 新增→42 条、重复 ISBN 被拒、非法可借数被拦截、编辑生效、删除→41 条，localStorage 持久化正常 |
| HTML 连接版渲染（`file://`） | ✅ 后端 CORS 允许 `Origin: null`，双击打开也能连上本机后端 |
| HTML 版导入 / 导出 | ✅ 导出分页拉全量正常；导入重复 ISBN 记为"跳过"、新数据正常写入 |

---

## 八、常见问题

**1. 启动报 `Communications link failure` / 连接不上 MySQL**
检查 MySQL 是否已启动、`application.yml` 中的账号密码是否正确、`library_db` 是否已创建。

**2. 端口被占用**
后端改 `application.yml` 的 `server.port`；前端改 `vite.config.js` 的 `server.port`。

**3. 前端页面能打开但列表为空并报错**
说明后端没起来或代理目标不对，确认后端已在 `http://localhost:8080` 运行。
若用的是 `html/with-backend.html`，页头会显示"后端未连接"并给出启动命令。

**4. 在 JDK 17 上中文变乱码 / 中文关键字搜不到（重要，本项目的重点坑）**

这是 JDK 17 的典型问题：**Java 18 起默认字符集才是 UTF-8（JEP 400）**，
而 **JDK 17 在中文 Windows 上默认字符集是 GBK**。如果不显式指定，
Spring Boot 加载 SQL 初始化脚本时会按 GBK 去读 UTF-8 的 `db/data.sql`，
把"三体"读成"涓変綋"（UTF-8 字节被当成 GBK），结果就是：

- 页面上中文图书名全是乱码
- 中文关键字（三体、刘慈欣…）模糊搜索查不到任何结果
- 但通过接口**新写入**的中文是正常的（请求体声明了 `charset=utf-8`）——
  这种"预置数据乱码、新数据正常"的现象就是它的典型特征

本项目已在 `application.yml` 中显式声明，**开箱即用、无需额外配置**：

```yaml
server:
  servlet:
    encoding:
      charset: UTF-8
      force: true            # HTTP 请求/响应固定 UTF-8

spring:
  sql:
    init:
      encoding: UTF-8        # 关键：SQL 初始化脚本必须按 UTF-8 读取
      schema-locations: classpath:db/h2-schema.sql
      data-locations: classpath:db/data.sql
```

`mysql` profile 不受此问题影响（建表和数据由 MySQL 客户端执行 `init_full.sql`，
脚本里已有 `SET NAMES utf8mb4`）。

如果仍看到**控制台日志**中文乱码，那只是终端显示问题、不影响数据，可加 JVM 参数消除：

```bash
java -Dfile.encoding=UTF-8 -jar target/library-backend-1.0.0.jar --spring.profiles.active=h2
```

**5. 怎么切换 JDK 版本 / 本机装了多个 JDK**

`pom.xml` 只要求 Java 17+，**JDK 17 和 JDK 21 都能编译、运行**。指定用哪个 JDK：

```powershell
# Windows (PowerShell) —— 临时切换（不影响系统全局配置）
$env:JAVA_HOME="C:\Program Files\Microsoft\jdk-17.0.12.7-hotspot"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"

cd backend
.\mvnw.cmd clean package -DskipTests
java -jar target\library-backend-1.0.0.jar --spring.profiles.active=h2
```

确认实际使用的版本：启动日志第一行会打印
`Starting LibraryApplication v1.0.0 using Java 17.0.12`。

若想改回 Java 21，把 `pom.xml` 里的 `<java.version>` 和 `<maven.compiler.release>` 都改成 `21`
再重新构建即可（代码无需改动）。

**6. `mvnw` 首次运行很慢**
第一次运行会下载 Maven 与依赖，之后会走本地缓存。

**7. HTML 版页面打开后数据是空的 / 换了浏览器数据不见了**
纯离线版把数据存在浏览器的 localStorage 里，属于该浏览器私有：
换个浏览器、换台电脑、清理缓存或使用无痕模式都会看不到原数据。
请用**导出 JSON**备份、**导入 JSON**还原；点**恢复示例数据**可随时还原演示数据。

**8. 只想看效果，不想装任何东西**
直接双击 `html/index.html`（纯离线版）即可，无需 Java / MySQL / Node。
