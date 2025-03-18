# 自动答题助手

这是一个基于Android无障碍服务的自动答题应用。

## 功能特点

- 自动识别答题界面
- 自动选择选项
- 自动提交答案
- 自动点击确定按钮
- 操作间隔3秒，避免操作过快
- Toast提示当前操作状态

## 使用说明

1. 安装应用
2. 打开系统设置 -> 无障碍 -> 已下载的服务
3. 找到"自动答题助手"并启用
4. 返回答题应用，服务会自动运行

## 工作流程

1. 检测到答题窗口时：
   - 自动识别并点击选项
   - 等待3秒后点击提交按钮
   - 等待3秒后点击确定按钮

2. 检测到答案解析窗口时：
   - 等待3秒后点击确定按钮

## 注意事项

- 使用前请确保已启用无障碍服务
- 每个操作之间有3秒延时，请耐心等待
- 操作状态会通过Toast提示显示
- 如遇问题，可以查看Logcat日志进行调试

## 开发环境

- Android Studio
- minSdkVersion: 24
- targetSdkVersion: 33

## 项目结构

```
app/
  ├── src/main/
  │   ├── java/com/example/autoanswer/
  │   │   ├── MainActivity.java
  │   │   └── AutoAnswerService.java
  │   ├── res/
  │   │   ├── layout/
  │   │   │   └── activity_main.xml
  │   │   └── xml/
  │   │       └── accessibility_service_config.xml
  │   └── AndroidManifest.xml
  └── build.gradle
```

## 许可证

MIT License 