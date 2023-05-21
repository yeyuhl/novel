> 源项目：[novel](https://github.com/201206030/novel)

# novel

## 一、项目概述

基于SpringBoot 3开发的前后端分离的后端项目，目标是构建一个完整的小说门户，由小说门户系统、作家后台管理系统、平台后台管理系统等多个子系统构成。包括小说推荐、作品检索、小说排行榜、小说阅读、小说评论、会员中心、作家专区、充值订阅、新闻发布等功能。

## 二、开发环境

以下为该项目涉及到的主要技术

| 相关技术 | 版本  | 说明  |
| --- | --- | --- |
| Java | 17  | -   |
| SpringBoot | 3.0.7 | -   |
| Redis | 7.0 | 分布式缓存 |
| Elasticsearch | 8.2.0 | 搜索引擎 |
| MySQL | 8.0 | 数据库 |
| MyBatis-Plus | 3.5.3 | MyBatis的增强工具 |
| RabbitMQ | 3.10.2 | 消息队列 |
| Sentinel | 1.8.4 | 流量监控组件 |
| Redisson | 3.17.4 | 分布式锁 |
| Springdoc-openapi | 2.0.0 | 接口文档 |

## 三、项目框架

```
io
 +- github
     +- yeyuhl  
        +- novel
            +- NovelApplication.java -- 项目启动类
            |
            +- core -- 项目核心模块，包括各种工具、配置和常量等
            |   +- common -- 业务无关的通用模块
            |   |   +- exception -- 通用异常处理
            |   |   +- constant -- 通用常量   
            |   |   +- req -- 通用请求数据格式封装，例如分页请求数据  
            |   |   +- resp -- 接口响应工具及响应数据格式封装 
            |   |   +- util -- 通用工具   
            |   | 
            |   +- annotation -- 自定义注解类
            |   +- aspect -- Spring AOP 切面
            |   +- auth -- 用户认证授权相关
            |   +- config -- 业务相关配置
            |   +- constant -- 业务相关常量         
            |   +- filter -- 过滤器 
            |   +- interceptor -- 拦截器
            |   +- json -- JSON 相关的包，包括序列化器和反序列化器
            |   +- task -- 定时任务
            |   +- util -- 业务相关工具 
            |   +- wrapper -- 装饰器
            |
            +- dto -- 数据传输对象，包括对各种 Http 请求和响应数据的封装
            |   +- req -- Http 请求数据封装
            |   +- resp -- Http 响应数据封装
            |
            +- dao -- 数据访问层，与底层 MySQL 进行数据交互
            +- manager -- 通用业务处理层，对第三方平台封装、对 Service 层通用能力的下沉以及对多个 DAO 的组合复用 
            +- service -- 相对具体的业务逻辑服务层  
            +- controller -- 主要是处理各种 Http 请求，各类基本参数校验，或者不复用的业务简单处理，返回 JSON 数据等
            |   +- front -- 小说门户相关接口
            |   +- author -- 作家管理后台相关接口
            |   +- admin -- 平台管理后台相关接口
            |   +- app -- app 接口
            |   +- applet -- 小程序接口
            |   +- open -- 开放接口，供第三方调用 
```

## 四、代码实现

### 1. core.common

在common这个包里面，都是一些通用模块，可以优先完成。比如resp类下自定义的通用返回结果，将MyBatis-Plus查询到的Page封装起来，再封装到Http的响应包里面。

### 2. 配置logback-spring.xml

由于SpringBoot使用的是Slf4j作为日志门面（把不同的日志系统的实现进行了具体的抽象化），Logback作为日志实现（真正在干活的日志框架）。由于我们希望输出的log能定制化，方便我们查阅log时能快速定位到问题所在。因此我们可以编写一个logback-spring.xml来进行配置，我们可以参考org.springframework.boot.logging.logback路径下的各种xml配置文件来进行编写。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- 隐藏启动时的info -->
    <statusListener class="ch.qos.logback.core.status.NopStatusListener"/>
    <!-- 彩色日志依赖的渲染类 -->
    <conversionRule conversionWord="clr" converterClass="org.springframework.boot.logging.logback.ColorConverter"/>
    <conversionRule conversionWord="wex"
                    converterClass="org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter"/>
    <conversionRule conversionWord="wEx"
                    converterClass="org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter"/>
    <!-- 彩色日志格式 -->
    <property name="CONSOLE_LOG_PATTERN"
              value="${CONSOLE_LOG_PATTERN:-%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}"/>

    <!-- %m输出的信息,%n表示换行符,%p日志级别,%t线程名,%d日期,%c类的全名,%i索引【从数字0开始递增】,%clr(...){faint}是将括号内的字体设置为淡色,%clr(...){magenta}是颜色设为品红色 -->
    <!-- appender是configuration的子节点，是负责写日志的组件。 -->
    <!-- ConsoleAppender：把日志输出到控制台 -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <!--
            <pattern>%d %p (%file:%line\)- %m%n</pattern>
             -->
            <pattern>${CONSOLE_LOG_PATTERN}</pattern>
            <!-- 控制台也要使用UTF-8，不要使用GBK，否则会中文乱码 -->
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- RollingFileAppender：滚动记录文件，先将日志记录到指定文件，当符合某个条件时，将日志记录到其他文件 -->
    <!-- 以下的大概意思是：1.先按日期存日志，日期变了，将前一天的日志文件名重命名为XXX%日期%索引，新的日志仍然是demo.log -->
    <!-- 2.如果日期没有发生变化，但是当前日志的文件大小超过1KB时，对当前日志进行分割 重命名 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">

        <File>logs/novel.log</File>
        <!-- rollingPolicy:当发生滚动时，决定 RollingFileAppender 的行为，涉及文件移动和重命名。 -->
        <!-- TimeBasedRollingPolicy： 最常用的滚动策略，它根据时间来制定滚动策略，既负责滚动也负责出发滚动 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 文件保存位置以及文件命名规则，这里用到了%d{yyyy-MM-dd}表示当前日期，%i表示这一天的第N个日志 -->
            <!-- 活动文件的名字会根据fileNamePattern的值，每隔一段时间改变一次 -->
            <!-- 文件名：logs/demo.2017-12-05.0.log -->
            <fileNamePattern>logs/debug.%d.%i.log</fileNamePattern>
            <!-- 到期自动清理日志文件 -->
            <cleanHistoryOnStart>true</cleanHistoryOnStart>
            <!-- 每产生一个日志文件，该日志文件的保存期限为30天 -->
            <maxHistory>30</maxHistory>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <!-- maxFileSize:这是活动文件的大小，默认值是10MB，测试时可改成1KB看效果 -->
                <maxFileSize>10MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
        <encoder>
            <!-- pattern节点，用来设置日志的输入格式 -->
            <pattern>
                %d %p (%file:%line\)- %m%n
            </pattern>
            <!-- 记录日志的编码:此处设置字符集 - -->
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    <springProfile name="dev">
        <!-- ROOT 日志级别 -->
        <root level="INFO">
            <appender-ref ref="STDOUT"/>
        </root>
        <!-- 指定项目中某个包，当有日志操作行为时的日志记录级别 -->
        <!-- io.github.yeyuhl 为根包，也就是只要是发生在这个根包下面的所有日志操作行为的权限都是DEBUG -->
        <!-- 级别依次为【从高到低】：FATAL > ERROR > WARN > INFO > DEBUG > TRACE -->
        <logger name="io.github.yeyuhl" level="DEBUG" additivity="false">
            <appender-ref ref="STDOUT"/>
        </logger>
    </springProfile>

    <springProfile name="prod">
        <!-- ROOT 日志级别 -->
        <root level="INFO">
            <appender-ref ref="STDOUT"/>
            <appender-ref ref="FILE"/>
        </root>
        <!-- 指定项目中某个包，当有日志操作行为时的日志记录级别 -->
        <!-- io.github.yeyuhl 为根包，也就是只要是发生在这个根包下面的所有日志操作行为的权限都是DEBUG -->
        <!-- 级别依次为【从高到低】：FATAL > ERROR > WARN > INFO > DEBUG > TRACE -->
        <logger name="io.github.yeyuhl" level="ERROR" additivity="false">
            <appender-ref ref="STDOUT"/>
            <appender-ref ref="FILE"/>
        </logger>
    </springProfile>
</configuration>
```

### 3. 缓存相关

#### 1）缓存变量

在core.constant包内新建CacheConsts类，包含各种小说缓存，比如小说分类列表缓存，小说内容缓存之类的，还包含一些缓存配置常量。

#### 2）缓存配置

在core.config包内新建CacheConfig类，在里面实现caffeineCacheManager和redisCacheManager两个方法，自定义缓存管理器。caffeineCacheManager管理的是本地相关的缓存，而redisCacheManager则管理远程相关的缓存。

### 4. 数据库设计

#### 1）MySQL数据库设计规范

- 表达是与否概念的字段，必须使用 is_xxx 的方式命名，数据类型是 unsigned tinyint（ 1 表示是， 0 表示否）。

> 任何字段如果为非负数，必须是 unsigned ，坚持 is_xxx 的命名方式是为了明确其取值含义与取值范围。表达逻辑删除的字段名 is_deleted，1表示删除，0表示未删除。

- 表名、字段名必须使用小写字母或数字， 禁止出现数字开头，禁止两个下划线中间只出现数字。数据库字段名的修改代价很大，字段名称需要慎重考虑。

> MySQL 在 Windows 下不区分大小写，但在 Linux 下默认是区分大小写。因此，数据库名、表名、字段名，都不允许出现任何大写字母，避免节外生枝。

- 表名不使用复数名词。

> 表名应该仅仅表示表里面的实体内容，不应该表示实体数量，对应于 DO 类名也是单数形式，符合表达习惯。

- 禁用保留字，如 desc、 range、 match、 delayed 等， 请参考 MySQL 官方保留字。
  
- 主键索引名为 pk_字段名；唯一索引名为 uk_字段名；普通索引名则为 idx_字段名。
  

> pk_ 即 primary key； uk_ 即 unique key； idx_ 即 index 的简称。

- 小数类型为 decimal，禁止使用 float 和 double。

> 在存储的时候， float 和 double 都存在精度损失的问题，很可能在比较值的时候，得到不正确的结果。如果存储的数据范围超过 decimal 的范围，建议将数据拆成整数和小数并分开存储。

- 如果存储的字符串长度几乎相等，使用 char 定长字符串类型。
  
- varchar 是可变长字符串，不预先分配存储空间，长度不要超过 5000，如果存储长度大于此值，定义字段类型为 text，独立出来一张表，用主键来对应，避免影响其它字段索引效率。
  
- 表必备三字段： id, create_time, update_time。
  

> 其中 id 必为主键，类型为 bigint unsigned、单表时自增、步长为 1。 create_time, update_time的类型均为 datetime 类型，前者现在时表示主动式创建，后者过去分词表示被动式更新。值得注意的是，在更新数据表记录时，必须同时更新记录对应的 update_time 字段值为当前时间。

- 表的命名最好是遵循“业务名称_表的作用” 。

> 如book_info / book_chapter / user_bookshelf / user_comment / author_info

- 库名与应用名称尽量一致。
  
- 如果修改字段含义或对字段表示的状态追加时，需要及时更新字段注释。
  
- 字段允许适当冗余，以提高查询性能，但必须考虑数据一致。冗余字段应遵循：
  
  - 不是频繁修改的字段。
    
  - 不是唯一索引的字段。
    
  - 不是 varchar 超长字段，更不能是 text 字段。
    

> 各业务线经常冗余存储小说名称，避免查询时需要连表（单体应用）或跨服务（微服务应用）获取。

- 单表行数超过 500 万行或者单表容量超过 2GB，才推荐进行分库分表。

> 如果预计三年后的数据量根本达不到这个级别，请不要在创建表时就分库分表

- 合适的字符存储长度，不但节约数据库表空间、节约索引存储，更重要的是提升检索速度。

> 无符号值可以避免误存负数， 且扩大了表示范围。

- 业务上具有唯一特性的字段，即使是组合字段，也必须建成唯一索引。

> 不要以为唯一索引影响了 insert 速度，这个速度损耗可以忽略，但提高查找速度是明显的； 另外，即使在应用层做了非常完善的校验控制，只要没有唯一索引，根据墨菲定律，必然有脏数据产生。

- 超过三个表禁止 join。需要 join 的字段，数据类型保持绝对一致； 多表关联查询时，保证被关联的字段需要有索引。

> 即使双表 join 也要注意表索引、 SQL 性能。

- 在 varchar 字段上建立索引时，必须指定索引长度，没必要对全字段建立索引，根据实际文本区分度决定索引长度。

> 索引的长度与区分度是一对矛盾体，一般对字符串类型数据，长度为 20 的索引，区分度会高达 90%以上，可以使用 count(distinct left(列名, 索引长度))/count(*)的区分度来确定。

- 创建索引时避免有如下极端误解：
  
  - 索引宁滥勿缺。 认为一个查询就需要建一个索引。
    
  - 吝啬索引的创建。 认为索引会消耗空间、 严重拖慢记录的更新以及行的新增速度。
    
  - 抵制唯一索引。 认为唯一索引一律需要在应用层通过“先查后插”方式解决。
    

#### 2）数据库建模

可以使用国产PDManer来建模，比PowerDesigner能处。注意，虽然建模的时候标出了外键，但是实际编写SQL语句的时候，尽量不使用外键，因为随着需求不断变化，外键成为了累赘。

**首页模块**

![](https://docs.xxyopen.com/img/er/home-ER.png)**新闻模块**

![](https://docs.xxyopen.com/img/er/news-ER.png)

**小说模块**

![](https://docs.xxyopen.com/img/er/book-ER.png)

**用户模块**

![](https://docs.xxyopen.com/img/er/user-ER.png)

**作家模块**

![](https://docs.xxyopen.com/img/er/author-ER.png)

**支付模块**

![](https://docs.xxyopen.com/img/er/pay-ER.png)

**系统模块**

![](https://docs.xxyopen.com/img/er/sys-ER.png)

### 5. 生成代码

#### 1）设置Mybatis-Plus

在NovelApplication中配置 MapperScan 注解，然后在core.config中添加MybatisPlusConfig配置类，将里面的分页插件相关的方法设置为Bean。

#### 2）使用Mybatis-Plus-Generator

由于生成代码是一次性操作，所以可以放到test目录下。在test的resource目录里创建需要的模板文件，然后在java目录下创建Generator类来实现代码的生成。

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230506175050.png)

生成代码的配置基本都差不多，全局配置，数据源配置，包配置，模板配置，策略配置。但其实不使用模板，一样可以默认生成数据库对应的entity，mapper到dao包下。个人更倾向于不使用模板实现，主要是我也不会写模板。

### 6. 完成DTO

DTO是Data Transfer Object的缩写，意为数据传输对象。服务层要向展示层即前端传递数据，为了安全性和规范性考虑，需要对数据进行封装。注意DTO中AuthorInfoDto和UserInfoDto还有一些其他类需要序列化，此处因为是简单的序列化因此直接使用Java默认的序列化。然后因为安全性问题，比如BookCommentRespDto中一些数据的序列化需要调用自己编写的UsernameSerializer工具类(在core.json)，进行脱敏处理。

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230507203800.png)

### 7. 创建service并实现

根据业务需求编写service接口，一共有作者，小说，首页，新闻，资源，搜索，用户七大模块。其中小说模块的相关业务是最为繁重，而资源，搜索，用户需要特定中间件，因此优先实现其他模块的服务类，后面才处理剩余模块。

#### 1）AuthorServiceImpl

> 实现AuthorServiceImpl需要先实现以下类：
> 
> manager.cache.AuthorInfoCacheManager 管理作家信息相关的缓存
> 
> core.constant.DatabaseConsts 数据库常量类
> 
> core.constant.SystemConfigConsts 系统配置常量类

AuthorService有两个方法，一个是作家注册的方法，一个是查询作家状态的方法。前者先检验作家是否注册，如果已经注册则直接返回，如果没注册则进行注册并清空缓存再返回。后者则是如果作家存在，则返回0，如果作家不存在，返回null。

#### 2）BookServiceImpl

> 实现BookServiceImpl需要先实现以下类：
> 
> core.annotation.Key 分布式锁-Key 注解
> 
> core.annotation.Lock 分布式锁 注解
> 
> core.aspect.LockAspect 分布式锁切面
> 
> core.auth.UserHolder
> 
> manager.cache.BookCategoryCacheManager 小说分类缓存管理类
> 
> manager.cache.BookRankCacheManager 小说排行榜缓存管理类
> 
> manager.cache.BookInfoCacheManager 小说信息缓存管理类
> 
> manager.cache.BookChapterCacheManager 小说章节缓存管理类
> 
> manager.cache.BookContentCacheManager 小说内容缓存管理类
> 
> manager.dao.UserDaoManager 用户模块DAO管理类
> 
> manager.mq.AmqpMsgManager AMQP消息管理类

BookService涉及到的业务最多，因此需要实现的方法也最多，部分方法需要用到上述需要实现的类，还有部分方法需要自己修改mapper.xml来实现查询。

```java
    /**
     * 小说点击榜查询
     *
     * @return 小说点击排行列表
     */
    RestResp<List<BookRankRespDto>> listVisitRankBooks();

    /**
     * 小说新书榜查询
     *
     * @return 小说新书排行列表
     */
    RestResp<List<BookRankRespDto>> listNewestRankBooks();

    /**
     * 小说更新榜查询
     *
     * @return 小说更新排行列表
     */
    RestResp<List<BookRankRespDto>> listUpdateRankBooks();

    /**
     * 小说信息查询
     *
     * @param bookId 小说ID
     * @return 小说信息
     */
    RestResp<BookInfoRespDto> getBookById(Long bookId);

    /**
     * 小说内容相关信息查询
     *
     * @param chapterId 章节ID
     * @return 内容相关联的信息
     */
    RestResp<BookContentAboutRespDto> getBookContentAbout(Long chapterId);

    /**
     * 小说最新章节相关信息查询
     *
     * @param bookId 小说ID
     * @return 章节相关联的信息
     */
    RestResp<BookChapterAboutRespDto> getLastChapterAbout(Long bookId);

    /**
     * 小说推荐列表查询
     *
     * @param bookId 小说ID
     * @return 小说信息列表
     */
    RestResp<List<BookInfoRespDto>> listRecBooks(Long bookId) throws NoSuchAlgorithmException;

    /**
     * 增加小说点击量
     *
     * @param bookId 小说ID
     * @return 成功状态
     */
    RestResp<Void> addVisitCount(Long bookId);

    /**
     * 获取上一章节ID
     *
     * @param chapterId 章节ID
     * @return 上一章节ID
     */
    RestResp<Long> getPreChapterId(Long chapterId);

    /**
     * 获取下一章节ID
     *
     * @param chapterId 章节ID
     * @return 下一章节ID
     */
    RestResp<Long> getNextChapterId(Long chapterId);

    /**
     * 小说章节列表查询
     *
     * @param bookId 小说ID
     * @return 小说章节列表
     */
    RestResp<List<BookChapterRespDto>> listChapters(Long bookId);

    /**
     * 小说分类列表查询
     *
     * @param workDirection 作品方向;0-男频 1-女频
     * @return 分类列表
     */
    RestResp<List<BookCategoryRespDto>> listCategory(Integer workDirection);

    /**
     * 发表评论
     *
     * @param dto 评论相关 DTO
     * @return void
     */
    RestResp<Void> saveComment(UserCommentReqDto dto);

    /**
     * 小说最新评论查询
     *
     * @param bookId 小说ID
     * @return 小说最新评论数据
     */
    RestResp<BookCommentRespDto> listNewestComments(Long bookId);

    /**
     * 删除评论
     *
     * @param userId    评论用户ID
     * @param commentId 评论ID
     * @return void
     */
    RestResp<Void> deleteComment(Long userId, Long commentId);

    /**
     * 修改评论
     *
     * @param userId  用户ID
     * @param id      评论ID
     * @param content 修改后的评论内容
     * @return void
     */
    RestResp<Void> updateComment(Long userId, Long id, String content);

    /**
     * 小说信息保存
     *
     * @param dto 小说信息
     * @return void
     */
    RestResp<Void> saveBook(BookAddReqDto dto);

    /**
     * 小说章节信息保存
     *
     * @param dto 章节信息
     * @return void
     */
    RestResp<Void> saveBookChapter(ChapterAddReqDto dto);

    /**
     * 查询作家发布小说列表
     *
     * @param dto 分页请求参数
     * @return 小说分页列表数据
     */
    RestResp<PageRespDto<BookInfoRespDto>> listAuthorBooks(PageReqDto dto);

    /**
     * 查询小说发布章节列表
     *
     * @param bookId 小说ID
     * @param dto    分页请求参数
     * @return 章节分页列表数据
     */
    RestResp<PageRespDto<BookChapterRespDto>> listBookChapters(Long bookId, PageReqDto dto);

    /**
     * 分页查询评论
     *
     * @param userId     会员ID
     * @param pageReqDto 分页参数
     * @return 评论分页列表数据
     */
    RestResp<PageRespDto<UserCommentRespDto>> listComments(Long userId, PageReqDto pageReqDto);

    /**
     * 小说章节删除
     *
     * @param chapterId 章节ID
     * @return void
     */
    RestResp<Void> deleteBookChapter(Long chapterId);

    /**
     * 小说章节查询
     *
     * @param chapterId 章节ID
     * @return 章节内容
     */
    RestResp<ChapterContentRespDto> getBookChapter(Long chapterId);

    /**
     * 小说章节更新
     *
     * @param chapterId 章节ID
     * @param dto       更新内容
     * @return void
     */
    RestResp<Void> updateBookChapter(Long chapterId, ChapterUpdateReqDto dto);
```

#### 3）HomeServiceImpl

> 实现HomeServiceImpl需要先实现以下类：
> 
> manager.cache.HomeBookCacheManager 首页推荐小说缓存管理类
> 
> manager.cache.FriendLinkCacheManager 友情链接缓存管理类

HomeServiceI有两个方法，一个是listHomeBooks，查询首页小说推荐列表，一个是listHomeFriendLinks，查询首页友情链接列表。

#### 4）NewsServiceImpl

> 实现NewsServiceImpl需要先实现以下类：
> 
> manager.cache.NewsCacheManager 新闻缓存管理类

NewsServiceI有两个方法，一个是listLatestNews，查询最新新闻列表，一个是getNews，查询新闻信息。

#### 5）ResourceServiceImpl

> 实现ResourceServiceImpl需要先实现以下类：
> 
> core.common.utils.ImgVerifyCodeUtils 图形验证码工具类
> 
> manager.redis.VerifyCodeManager 验证码管理类

ResourceServiceImpl有两个方法，一个是getImgVerifyCode，获取图片验证码，一个是uploadImage，用于上传图片。

#### 6）UserServiceImpl

> 实现UserServiceImpl需要先实现以下类：
> 
> core.utils.JwtUtils JWT的工具类

```java
    /**
     * 用户注册
     *
     * @param dto 注册参数
     * @return JWT
     */
    RestResp<UserRegisterRespDto> register(UserRegisterReqDto dto);

    /**
     * 用户登录
     *
     * @param dto 登录参数
     * @return JWT + 昵称
     */
    RestResp<UserLoginRespDto> login(UserLoginReqDto dto);

    /**
     * 用户反馈
     *
     * @param userId  反馈用户ID
     * @param content 反馈内容
     * @return void
     */
    RestResp<Void> saveFeedback(Long userId, String content);

    /**
     * 用户信息修改
     *
     * @param dto 用户信息
     * @return void
     */
    RestResp<Void> updateUserInfo(UserInfoUpdateReqDto dto);

    /**
     * 用户反馈删除
     *
     * @param userId 用户ID
     * @param id     反馈ID
     * @return void
     */
    RestResp<Void> deleteFeedback(Long userId, Long id);

    /**
     * 查询书架状态接口
     *
     * @param userId 用户ID
     * @param bookId 小说ID
     * @return 0-不在书架 1-已在书架
     */
    RestResp<Integer> getBookshelfStatus(Long userId, String bookId);

    /**
     * 用户信息查询
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    RestResp<UserInfoRespDto> getUserInfo(Long userId);
```

#### 7）DbSearchServiceImpl和EsSearchServiceImpl

> 实现DbSearchServiceImpl需要先实现以下类：
> 
> dao.mapper.BookInfoMapper 小说信息Mapper接口

BookInfoMapper需要声明searchBooks这个方法，然后去xml里面实现，xml中的``<if test="..."> </if>``是MyBatis的动态SQL语句，只有if判断的语句成立，才会添加相应的SQL语句到前面SQL语句的后面。

```xml
 <select id="searchBooks" resultType="io.github.yeyuhl.novel.dao.entity.BookInfo">
        select id,
        category_id,
        category_name,
        book_name,
        author_id,
        author_name,
        word_count,
        last_chapter_name
        from book_info
        where word_count > 0
        <if test="condition.keyword != null and condition.keyword != ''">
            and (book_name like concat('%',#{condition.keyword},'%') or author_name like concat('%',#{condition.keyword},'%'))
        </if>
        <if test="condition.workDirection != null">
            and work_direction = #{conditionDirection}
        </if>
        <if test="condition.categoryId != null">
            and category_id = #{condition.categoryId}
        </if>
        <if test="condition.isVip != null">
            and is_vip = #{condition.isVip}
        </if>
        <if test="condition.bookStatus != null">
            and book_status = #{condition.bookStatus}
        </if>
        <if test="condition.wordCountMin != null">
            and word_count >= #{condition.wordCountMin}
        </if>
        <if test="condition.wordCountMax != null">
            and word_count <![CDATA[ < ]]> #{condition.wordCountMax}
        </if>
        <if test="condition.updateTimeMin != null">
            and last_chapter_update_time >= #{condition.updateTimeMin}
        </if>
        <if test="condition.sort != null">
            order by ${condition.sort}
        </if>
    </select>
```

这个类里面只需要实现一个search方法，在DbSearchServiceImpl里实现的search方法则是在数据库里面搜索。

> 实现EsSearchServiceImpl需要先实现以下类：
> 
> core.constant.EsConsts elasticsearch相关常量

这个类里面只需要实现一个search方法，在EsSearchServiceImpl里实现的search方法则是调用elasticsearch的方法来搜索。

### 8. controller

![](https://yeyu-1313730906.cos.ap-guangzhou.myqcloud.com/PicGo/20230517140747.png)

### 9. 项目优化

#### 拦截器相关

完善EsConfig、OpenApiConfig、WebConfig。其中WebConfig涉及到四个拦截器，分别是FlowLimitInterceptor（流量限制拦截器）、AuthInterceptor（认证授权拦截器）、FileInterceptor（文件拦截器）、TokenParseInterceptor（Token解析拦截器）。

其中FlowLimitInterceptor是使用Sentinel实现接口防刷和限流。而AuthInterceptor是用策略模式来用户认证授权，以AuthInterceptor为拦截器，根据请求的URI来解析，再交于不同策略来处理，而 AuthStrategy 接口是所有策略的需要实现的接口，该接口定义了一个默认的方法实现用户端所有子系统都需要的统一账号认证逻辑和一个封装各个系统独立认证授权逻辑的待实现方法。

#### 解决XXS攻击

> 跨站脚本攻击（XSS），是最普遍的 Web 应用安全漏洞。能够使得攻击者嵌入恶意脚本代码到正常用户会访问到的页面中，当正常用户访问该页面时，则可导致嵌入的恶意脚本代码的执行，从而达到恶意攻击用户的目的。

可以使用装饰器模式解决表单形式传参的XSS攻击，装饰器模式的相关介绍可以看我的这篇文章：[Java基础常见问题](https://yeyuhl.xyz/2023/05/12/Java%E5%9F%BA%E7%A1%80%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98/)，里面提及到了装饰器模式。Spring MVC 是通过 HttpServletRequest 的 getParameterValues 方法来获取用户端的请求参数并绑定到我们 @RequestMapping 方法定义的对象上。所以我们可以装饰 HttpServletRequest 对象，在 getParameterValues 方法里加上自己的行为（对请求参数值里面的特殊字符进行转义）来解决 XSS 攻击。

但是在前后端分离的项目中，对于 POST 和 PUT 类型的请求方法，后端基本都是通过 @RequestBody 注解接收 application/json 格式的请求数据。上述解决方法就不起作用了，那我们就可以针对 Json ，配置全局的 Json 反序列化器转义特殊字符来解决 XSS 攻击。

### 10. 项目部署及一些问题

> 更多详尽问题查看原项目的教程文档[小说精品屋](https://docs.xxyopen.com/course/novel)

#### 后端

可以使用maven将项目打包成jar放到云服务器上运行，或者编写Dockerfile制作成镜像放到云服务器上的docker，在docker中运行。有些中间件不想在win10上运行，可以放到云服务器上运行，并且不同云服务器可以运行不同中间件，像elasticsearch这种比较吃性能的，可以一台云服务器只用一台，也可以放到win10中运行。

#### 前端

在[novel 前端项目](https://github.com/201206030/novel-front-web)下载前端项目源码，注意node的版本要求，本人配置的是14.17.0，建议使用nvm管理node版本。在项目根目录下运行 `yarn build` 命令构建，如果要真实运行，记得上传`dist`文件夹中的内容到到服务器`/root/nginx/html`目录中。

#### 问题

建议先把业务写好，边写边测试，别写完了再测试，有时候根本不报错，从数据库中读取数据之后卡在那里也有可能的，等到写完发现问题就很难去解决了。此外之前写的项目用的springfox，也就是比较多人用的swagger2.9.2，但是在springboot3.0中由于版本问题不适用（javax变成jakarta），因此要使用springdoc。springdoc配置更简单了，而且实际上实现也是swagger，本项目的swagger文档位置是：[Swagger UI](http://localhost:8888/swagger-ui/index.html)。
