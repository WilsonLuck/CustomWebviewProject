# 项目结构
- ----app Android客户端源码
- ----electron-quick-start 桌面端，监控设备列表，连接后自动刷新
- ----server/Book 服务器
# Usage
## 一、客户端
### Base Environment
- Android SDK
- Gradle
- [或者直接下载Android Studio 通过IDE运行项目](https://developer.android.com/studio) (推荐)
### 1.修改参数
```
vim path/AndroidProject/AccessAbilityService/gradle.properties 
```
1. windows可以用写字板打开该文件，显示如下：
```
# Project-wide Gradle settings.
# IDE (e.g. Android Studio) users:
# Gradle settings configured through the IDE *will override*
# any settings specified in this file.
# For more details on how to configure your build environment visit
# http://www.gradle.org/docs/current/userguide/build_environment.html
# Specifies the JVM arguments used for the daemon process.
# The setting is particularly useful for tweaking memory settings.
org.gradle.jvmargs=-Xmx1536m
# When configured, Gradle will run in incubating parallel mode.
# This option should only be used with decoupled projects. More details, visit
# http://www.gradle.org/docs/current/userguide/multi_project_builds.html#sec:decoupled_projects
# org.gradle.parallel=true
# AndroidX package structure to make it clearer which packages are bundled with the
# Android operating system, and which are packaged with your app's APK
# https://developer.android.com/topic/libraries/support-library/androidx-rn
android.useAndroidX=true
# Automatically convert third-party libraries to use AndroidX
android.enableJetifier=true
# Kotlin code style for this project: "official" or "obsolete":
kotlin.code.style=official
API_HOST="http://172.27.16.243:3000"
```
2. 修改 API_HOST的host为服务器ip 例如 服务器ip为 192.168.0.1</br>
```
API_HOST="http://192.168.0.1:3000"
```
3. 保存文件
### 2.打包
在修改完成API_HOST参数后可以 在当前目录下使用脚本打包
1. 查看当前地址目录结构如下：
```
ls
```
```
AccessAbilityService.iml                 app                                      electron-quick-start                     gradlew                                  request.txt
Android Webview Automation (Stage 1).pdf build                                    gradle                                   gradlew.bat                              server
README.md                                build.gradle                             gradle.properties                        local.properties                         settings.gradle
```
2. 执行打包脚本
```
./gradle assembleDebug
macos下命令为 ./gradlew assembleDebug 请注意区分
```
执行完后会输出当前信息 例如:
```
~/AndroidProject/AccessAbilityService on  x5! ⌚ 18:14:49
$ ./gradlew assembleDebug                                                                                                                                                                                                                                   ‹ruby-2.6.0›
Starting a Gradle Daemon, 1 incompatible Daemon could not be reused, use --status for details

> Task :app:compileDebugJavaWithJavac
Note: Some input files use or override a deprecated API.
Note: Recompile with -Xlint:deprecation for details.
Note: /Users/chenhong/AndroidProject/AccessAbilityService/app/src/main/java/com/play/accessabilityservice/api/WebviewProxySetting.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.

Deprecated Gradle features were used in this build, making it incompatible with Gradle 6.0.
Use '--warning-mode all' to show the individual deprecation warnings.
See https://docs.gradle.org/5.4.1/userguide/command_line_interface.html#sec:command_line_warnings

BUILD SUCCESSFUL in 31s
26 actionable tasks: 7 executed, 19 up-to-date

~/AndroidProject/AccessAbilityService on  x5! ⌚ 18:15:32
$                  
```
### 3.查看输出的APK
```
~/AndroidProject/AccessAbilityService on  x5! ⌚ 18:16:41
$ cd app/build/outputs/apk/debug                                                                                                                                                                                                                        
~/AndroidProject/AccessAbilityService/app/build/outputs/apk/debug on  x5! ⌚ 18:17:01
$ ls
app-debug.apk output.json

```
### 4.安装app-debug.apk到手机
## 二、服务器
1. 进入服务器目录 如下：
```
~/AndroidProject/AccessAbilityService/app/build/outputs/apk/debug on  x5! ⌚ 18:17:03
$ cd ~/AndroidProject/AccessAbilityService 

~/AndroidProject/AccessAbilityService on  x5! ⌚ 18:22:17
$ cd server/Book 

~/AndroidProject/AccessAbilityService/server/Book on  x5! ⌚ 18:22:37
$ ls 
README.md         bin               door.js           logs           package-lock.json package.json      public            routes            views
```
2. 安装依赖 可以看到多了 node_modules 这个目录
```
~/AndroidProject/AccessAbilityService/server/Book on  x5! ⌚ 18:24:12
$ npm install                  
added 216 packages from 224 contributors in 2.301s

~/AndroidProject/AccessAbilityService/server/Book on  x5! ⌚ 18:24:27
$ ls       
README.md         bin               door.js           logs              node_modules      package-lock.json package.json      public            routes            views
```
3. 启动服务
```
~/AndroidProject/AccessAbilityService/server/Book on  x5! ⌚ 18:24:31
$ npm start                      

> book@0.0.0 start /Users/chenhong/AndroidProject/AccessAbilityService/server/Book
> node ./bin/www

new client connected

```
## 三、桌面端
1. 进入桌面端目录
```
~/AndroidProject/AccessAbilityService on  x5! ⌚ 18:27:38
$ cd electron-quick-start      

~/AndroidProject/AccessAbilityService/electron-quick-start on  x5! ⌚ 18:27:42
$ ls                            
LICENSE.md        OutApp            README.md         app               index.html        main.js        package-lock.json package.json      preload.js        renderer.js       splash.html

~/AndroidProject/AccessAbilityService/electron-quick-start on  x5! ⌚ 18:27:43
$     
```
2. 安装依赖 node_modules
```
~/AndroidProject/AccessAbilityService/electron-quick-start on  x5! ⌚ 18:28:45
$ npm install                                                                                                                                                                                                                                               ‹ruby-2.6.0›

> core-js@2.6.11 postinstall /Users/chenhong/AndroidProject/AccessAbilityService/electron-quick-start/node_modules/babel-runtime/node_modules/core-js
> node -e "try{require('./postinstall')}catch(e){}"

Thank you for using core-js ( https://github.com/zloirock/core-js ) for polyfilling JavaScript standard library!

The project needs your help! Please consider supporting of core-js on Open Collective or Patreon: 
> https://opencollective.com/core-js 
> https://www.patreon.com/zloirock 

Also, the author of core-js ( https://github.com/zloirock ) is looking for a good job -)


> core-js@3.6.4 postinstall /Users/chenhong/AndroidProject/AccessAbilityService/electron-quick-start/node_modules/core-js
> node -e "try{require('./postinstall')}catch(e){}"


> electron@8.0.0 postinstall /Users/chenhong/AndroidProject/AccessAbilityService/electron-quick-start/node_modules/electron
> node install.js

added 174 packages from 148 contributors in 4.723s

6 packages are looking for funding
  run `npm fund` for details

```
3. 启动桌面端
```
~/AndroidProject/AccessAbilityService/electron-quick-start on  x5! ⌚ 18:28:54
$ npm start                       

> electron-quick-start@1.0.0 start /Users/chenhong/AndroidProject/AccessAbilityService/electron-quick-start
> electron .

(electron) The default value of app.allowRendererProcessReuse is deprecated, it is currently "false".  It will change to be "true" in Electron 9.  For more information please check https://github.com/electron/electron/issues/18397

```
4. 启动界面 
![启动界面](https://github.com/honglvt/CustomWebviewProject/blob/x5/electron-quick-start/app/assets/img/demo.png)
5. 连接服务器
出现启动界面后输入服务器地址，如果是本机，则输入 127.0.0.1 随后点击右侧搜索按钮链接服务器，可以看见第一步连接的手机已经在列表中
![连接成功](https://github.com/honglvt/CustomWebviewProject/blob/x5/electron-quick-start/app/assets/img/connect_successfully.png)
6. 当有手机连接时，桌面端会自动刷新，无需多次点击搜索
## 四、测试接口
## 拦截xhr请求，不穿正则判断
![xhr](https://github.com/honglvt/CustomWebviewProject/blob/x5/server/Book/public/images/demo_xhr.png)
## 拦截xhr请求传入正则判断
![xhr_regex](https://github.com/honglvt/CustomWebviewProject/blob/x5/server/Book/public/images/demo_xhr_regex.png)
