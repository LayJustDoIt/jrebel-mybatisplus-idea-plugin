# MyBatis-Plus Reload

**English | [简体中文](#简体中文)**

MyBatis-Plus Mapper XML hot reload with JRebel.

## English

### Introduction

MyBatis-Plus Reload enables Mapper XML hot reload for applications using
JRebel and MyBatis-Plus.

When you start your application with JRebel Run or JRebel Debug, the plugin
automatically injects the bundled `jrebel-mybatisplus` runtime. No manual
`-Drebel.plugins` configuration is required.

### Features

- Hot reload Mapper XML SQL changes without restarting the JVM.
- Automatically load the bundled runtime plugin.
- Preserve existing `rebel.plugins` entries.
- Avoid duplicate runtime plugin configuration.
- Replace older `jrebel-mybatisplus` runtime entries.
- Preserve MyBatis-Plus injected SQL statements such as `selectPage`.

### Compatibility

- IntelliJ IDEA 2026.2: supported
- IntelliJ IDEA 2026.3: supported, verified in a real business project
- Runtime: `jrebel-mybatisplus` 1.0.8
- JRebel 2026.3.1 (real-project verification environment)
- MyBatis-Plus 3.5.2 / 3.5.7 (verified)

> Not every MyBatis-Plus 3.x version has been individually regression-tested.

### Requirements

- IntelliJ IDEA 2026.2 or 2026.3
- JRebel installed and properly configured
- A compatible MyBatis-Plus project

### Installation

Install **MyBatis-Plus Reload** from
[JetBrains Marketplace](https://plugins.jetbrains.com/plugin/33495-mybatis-plus-reload),
or download the ZIP from
[GitHub Releases](https://github.com/LayJustDoIt/jrebel-mybatisplus-idea-plugin/releases)
and install it via `Settings → Plugins → ⚙️ → Install Plugin from Disk`.

### Quick Start

1. Install and configure JRebel in IntelliJ IDEA.
2. Install MyBatis-Plus Reload from JetBrains Marketplace.
3. Start your application using JRebel Run or JRebel Debug.
4. Modify your MyBatis Mapper XML.
5. Build or update resources.
6. Run your query again to verify that the new SQL takes effect.

No manual `-Drebel.plugins` configuration is required.

### How It Works

This plugin uses the `JavaProgramPatcher` extension point to intercept Java
run configurations launched via JRebel Run / JRebel Debug. Before the JVM
starts, it locates the bundled `jr-mybatisplus-1.0.8.jar` in the plugin's
`lib/` directory and injects `-Drebel.plugins=<path>` into the VM arguments.

If `-Drebel.plugins` already exists, the bundled runtime is merged using a
comma separator (per JRebel documentation). Old `jr-mybatisplus-*.jar` entries
are replaced; other plugin entries are preserved.

### Troubleshooting

- **SQL changes do not take effect**: make sure the modified Mapper XML was
  compiled or copied to the build output (`Build → Build Project` or
  `Build → Recompile`), then re-run the query.
- **`selectPage` returns incorrect results after reload**: verify that only
  one `jr-mybatisplus-*.jar` entry exists in `rebel.plugins` — this plugin
  automatically replaces old runtime entries.
- **No `-Drebel.plugins` injected**: confirm the application is launched via
  JRebel Run or JRebel Debug; the plugin only patches JRebel executors.

---

## 简体中文

### 项目简介

MyBatis-Plus Reload 是一款配合 JRebel 使用的 Mapper XML 热加载插件。

通过 JRebel Run 或 JRebel Debug 启动项目时，插件会自动注入内置的
`jrebel-mybatisplus` Runtime，无需手工配置 `-Drebel.plugins`。

### 核心功能

- 修改 Mapper XML 后无需重启 JVM，即可更新 SQL
- 自动加载内置 Runtime
- 保留已有的 `rebel.plugins` 配置
- 避免重复加载 Runtime
- 自动替换旧版 `jrebel-mybatisplus` Runtime
- 保护 MyBatis-Plus 自动注入的 `selectPage` 等 SQL

### 兼容性

- IntelliJ IDEA 2026.2：支持
- IntelliJ IDEA 2026.3：支持，已在真实业务项目验证
- Runtime：`jrebel-mybatisplus` 1.0.8
- JRebel 2026.3.1（真实项目验证环境）
- MyBatis-Plus 3.5.2 / 3.5.7（已验证）

> 并非所有 MyBatis-Plus 3.x 版本都逐一做过回归测试。

### 使用要求

- IntelliJ IDEA 2026.2 或 2026.3
- 已安装并正确配置 JRebel
- 使用兼容版本 MyBatis-Plus 的项目

### 安装方式

在 [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/33495-mybatis-plus-reload)
安装 **MyBatis-Plus Reload**，或从
[GitHub Releases](https://github.com/LayJustDoIt/jrebel-mybatisplus-idea-plugin/releases)
下载 ZIP，通过 `Settings → Plugins → ⚙️ → Install Plugin from Disk` 安装。

### 快速开始

1. 在 IDEA 中安装并配置 JRebel
2. 在 JetBrains Marketplace 安装 MyBatis-Plus Reload
3. 使用 JRebel Run 或 JRebel Debug 启动项目
4. 修改 MyBatis Mapper XML
5. 编译或更新项目资源
6. 重新请求接口，确认新 SQL 生效

无需手工添加 `-Drebel.plugins` 参数。

### 工作原理

插件通过 `JavaProgramPatcher` 扩展点拦截 JRebel Run / JRebel Debug 启动的
Java 运行配置：JVM 启动前，定位插件 `lib/` 目录中内置的
`jr-mybatisplus-1.0.8.jar`，并注入 `-Drebel.plugins=<路径>`。

若已存在 `-Drebel.plugins`，则按 JRebel 官方文档使用逗号分隔符合并：
旧 `jr-mybatisplus-*.jar` 条目会被替换，其他插件条目全部保留。

### 常见问题

- **修改 SQL 未生效**：确认修改后的 Mapper XML 已编译或复制到构建输出
  （`Build → Build Project` 或 `Build → Recompile`），再重新请求验证
- **reload 后 `selectPage` 结果异常**：检查 `rebel.plugins` 中是否只有一个
  `jr-mybatisplus-*.jar` 条目 —— 插件会自动替换旧 Runtime 条目
- **未注入 `-Drebel.plugins`**：确认是通过 JRebel Run 或 JRebel Debug 启动，
  插件只对 JRebel 执行器生效

---

## Releases

- GitHub Releases: https://github.com/LayJustDoIt/jrebel-mybatisplus-idea-plugin/releases
- Marketplace: https://plugins.jetbrains.com/plugin/33495-mybatis-plus-reload
- Runtime: https://github.com/LayJustDoIt/jrebel-mybatisplus

## Build

```shell
git clone https://github.com/LayJustDoIt/jrebel-mybatisplus-idea-plugin.git
cd jrebel-mybatisplus-idea-plugin
./gradlew buildPlugin
```

The plugin ZIP is generated at `build/distributions/jrebel-mybatisplus-idea-plugin-1.0.1.zip`.

## Credits

Original runtime: **SweetInk / suchu**

https://github.com/SweetInk/jrebel-mybatisplus

## License

MIT License — see [LICENSE](LICENSE).

Third-party notices: [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).
