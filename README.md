# plugin-APIConfigStore

将其他插件的配置通过它们的api地址获取后存储到数据库内，在由后端通过finder让主题端可以使用标签调用对应的配置

缺点：无法实时更新，需手动更新json
逻辑：将需要的配置信息存储到数据库，然后在去调用你保存过的数据。

## 使用方法

### 新增

编辑和新增逻辑基本相同
![image](https://github.com/user-attachments/assets/b51c5c01-e8b9-4521-ba48-3183f90b834b)

### 预览 

可以查看对应保存到数据库内的数据

<br/>

![image](https://github.com/user-attachments/assets/cd5d8bba-7c1b-4dea-bfe9-6ea6b55b5766)
![image](https://github.com/user-attachments/assets/004b75f0-9c9a-48a3-8f43-6d63135d3cd3)

### 刷新json

当你的配置发生变化时，需要手动去进行一个刷新

### 主题端端使用方式

${APIConfigStoreDataFinder.get('这个地方填写你的标识')}

![image](https://github.com/user-attachments/assets/0704d52b-e4b6-40df-b537-7f5cd524ae05)


```sh
  <div th:with="customData = ${APIConfigStoreDataFinder.get('pinglunpeizhi')}">
            <p th:text="${customData.remark}"></p>
            <p th:text="${customData.identifier}"></p>
            <p th:text="${customData.apiAddress}"></p>
            <p th:text="${customData.apiData}"></p>
        </div>
```

## 开发环境

插件开发的详细文档请查阅：<https://docs.halo.run/developer-guide/plugin/introduction>

所需环境：

1. Java 17
2. Node 20
3. pnpm 9
4. Docker (可选)

克隆项目：

```sh
git clone git@github.com:halo-sigs/plugin-starter.git

# 或者当你 fork 之后

git clone git@github.com:{your_github_id}/plugin-starter.git
```

```sh
cd path/to/plugin-starter
```

### 运行方式 1（推荐）

> 此方式需要本地安装 Docker

```sh
# macOS / Linux
./gradlew pnpmInstall

# Windows
./gradlew.bat pnpmInstall 
```

```sh
# macOS / Linux
./gradlew haloServer

# Windows
./gradlew.bat haloServer
```

执行此命令后，会自动创建一个 Halo 的 Docker 容器并加载当前的插件，更多文档可查阅：<https://docs.halo.run/developer-guide/plugin/basics/devtools>

<br/>

```sh
# 插件前端使用了这两个库，可能需要手动安装一下
pnpm add pinyin@3

pnpm add  @formkit/vue
```

### 运行方式 2

> 此方式需要使用源码运行 Halo

编译插件：

```sh
# macOS / Linux
./gradlew build

# Windows
./gradlew.bat build
```

修改 Halo 配置文件：

```yaml
halo:
  plugin:
    runtime-mode: development
    fixedPluginPath:
      - "/path/to/plugin-starter"
```

最后重启 Halo 项目即可。
