# BatsRemove — Minecraft Fabric 蝙蝠清除 Mod

一个按**大版本系列**分模块的 Fabric 模组：阻止蝙蝠自然生成（召唤不受影响）、在加载存档时一次性自动清除已有蝙蝠并标记该存档、以及提供 `/batsremove` 手动清除命令。

**四个模块均已在本机构建成功，产物 jar 已生成（含 26.x）。**

---

## 功能

1. **阻止蝙蝠自然生成**
   - 蝙蝠从生物群系生成列表（AMBIENT 组）中移除，不再自然生成。
   - 玩家用 `/summon bat` 或刷怪蛋召唤的蝙蝠**不受影响**。
2. **加载存档时自动清除已有蝙蝠（仅一次）**
   - 第一次加载某个存档时，自动清除世界内已加载的蝙蝠，并写入标记文件。
   - 之后再次加载该存档，检测到标记则跳过自动清除。
   - 标记文件：`<世界目录>/data/bats_removed.dat`。
3. **手动命令清除全部蝙蝠**
   - 命令：`/batsremove`（需要管理员权限，OP）。

---

## 版本覆盖

每个目录是一个**独立的 Fabric 工程**，对应一个大版本系列：

| 目录 | 构建目标 | 覆盖系列 | Java | Loom | Gradle | 映射 |
|------|---------|---------|------|------|--------|------|
| `1.19/` | 1.19.4 | 1.19.0 – 1.19.4 | 17 | 1.5.8 | 8.10.2 | Yarn |
| `1.20/` | 1.20.1 | 1.20.x | 17 | 1.7.4 | 8.10.2 | Yarn |
| `1.21/` | 1.21.1 | 1.21.x | 21 | 1.9.2 | 8.12 | Yarn |
| `26/` | 26.2 | 26.x | 25 | 1.16.2 | 9.7.1 | 无（未混淆） |

### 蝙蝠机制（经实际反编译验证）

- **1.19 / 1.20 / 1.21**：蝙蝠从生物群系生成列表（AMBIENT 组）自然生成。统一用 Fabric API 的
  `BiomeModifications.create(...).add(ModificationPhase.REMOVALS, ...)` 把蝙蝠从所有群系移除，无需 mixin。
- **26.x**：Mojang 从 1.21.11 之后**彻底移除了混淆**，游戏 jar 自带真实类名（Mojang 名），
  版本 json 不再提供映射下载（这是预期，不是缺失）。因此 26 模块**不声明任何 mappings**，
  直接用游戏内建名字（如 `net.minecraft.world.entity.ambient.Bat`、`EntityTypes.BAT`、
  `Identifier`、`Permissions.COMMANDS_ADMIN`）编译。

> 26.x 构建要点（与 1.19-1.21 完全不同）：
> - `gradle.properties` 加 `fabric.loom.disableObfuscation=true`，让 Loom 进入"非混淆环境"模式。
> - 依赖用普通 `implementation` 而非 `modImplementation`（非混淆模式下没有 remap 配置）。
> - 不写 `mappings` 行；Loom 1.16.2（与 Fabric API 自身 26.2 构建一致）。
> - 早期曾误以为 26.x"无映射做不了"，实为"无需映射"——详见 Mojang 公告
>   [Removing obfuscation in Java Edition](https://www.minecraft.net/zh-hans/article/removing-obfuscation-in-java-edition)
>   与 [Fabric 迁移映射文档](https://docs.fabricmc.net/zh_cn/develop/porting/mappings/)。

---

## 构建方法

四个工程已在本机（已装 JDK 17 / JDK 21 / JDK 25 / Gradle）构建成功。你自己构建时：

```bash
# 1.19 或 1.20：JDK 17 + Gradle 8.10.2
cd 1.19 && gradle build

# 1.21：JDK 21 + Gradle 8.12
cd 1.21 && gradle build

# 26.x：JDK 25 + Gradle 9.7.1（需先启动 tools/proxy 镜像代理下载 MC，见下文）
cd 26 && gradle build
```

产物位于各工程的 `build/libs/`（也已汇总复制到 `dist/`）：
- `batsremove-1.19-1.0.0.jar`
- `batsremove-1.20-1.0.0.jar`
- `batsremove-1.21-1.0.0.jar`
- `batsremove-26-1.0.0.jar`

> **Loom 与 Gradle 版本必须匹配**：Loom 1.5/1.7 与 Gradle 8.12 的 Problems API 不兼容，
> 需用 Gradle 8.10.2；Loom 1.9 需要 Gradle ≥ 8.11。上面表格里的组合是验证过的。

> 各 `gradle.properties` 里的 MC / Yarn / Loader / Fabric-API 版本均为**实际存在的版本**，
> 已核对过 Fabric Maven。升级时到 [fabricmc.net/develop](https://fabricmc.net/develop/) 取最新。

### 依赖

- 需要 **Fabric Loader** 和 **Fabric API**（版本与目标 Minecraft 匹配）。
- 不支持 NeoForge / Forge。

---

## 使用

| 命令 | 权限 | 说明 |
|------|------|------|
| `/batsremove` | OP（权限等级 2） | 清除所有已加载世界中的全部蝙蝠 |

- 首次进入某存档会自动清除并写标记；之后不再自动清。想**重新触发**：删除该存档的
  `data/bats_removed.dat` 再加载。
- 想让普通玩家也能用：把 `BatsRemoveCommand.java` 里 `hasPermissionLevel(2)` 改成 `1`。

---

## 实现原理

- **阻止自然生成**：`BatRemoveMod#onInitialize` 中注册 biome 修改，用
  `getSpawnSettings().removeSpawnsOfEntityType(EntityType.BAT)` 把蝙蝠从所有群系移除。
- **一次性自动清除**：`BatRemovalManager#onServerStarted` 挂在
  `ServerLifecycleEvents.SERVER_STARTED`。检查标记文件存在则跳过，否则清除全部已加载蝙蝠并写标记。
- **存档标记**：刻意使用普通文件 `<世界>/data/bats_removed.dat`（Java Files API），而不是
  `PersistentStateManager`（其 API 在 1.20.2 变了），让同一份代码跨版本统一。
- **手动命令**：`BatsRemoveCommand` 注册 `/batsremove`。
- 三个版本仅有的差异：`sendFeedback` 签名（1.19.x 用 `Text`，1.20.1+ 用 `Supplier<Text>`）、
  `Identifier` 构造（1.19/1.20 用 `new Identifier(...)`，1.21 用 `Identifier.of(...)`）。

### 说明与边界

- 清除作用于**已加载区块**中的蝙蝠（所有"清除实体"命令的通行语义）；未加载区块的蝙蝠不会立即移除，
  但因自然生成已被禁止，不会再出现新蝙蝠。
- 已加载蝙蝠若被重新生成（刷怪蛋/召唤），可用 `/batsremove` 手动清除。

---

## 本地构建镜像代理（Mojang 被墙时的工具）

本网络环境里 Mojang 的 `piston-meta.mojang.com` / `piston-data.mojang.com` 被墙，Loom 无法直接下载
Minecraft。项目内置了一个**本地反向代理**，把这两个域名转发到快速镜像 `bmclapi.bangbang93.com`。

脚本与代理代码都在 **`tools/proxy/`** 下：

| 脚本 | 作用 |
|------|------|
| `tools/proxy/start-proxy.bat` | 加 hosts 映射 → 启动代理 → 验证 443 |
| `tools/proxy/stop-proxy.bat` | 停代理 + 删 hosts 映射 + 刷 DNS |
| `tools/proxy/status-proxy.bat` | 查看 443 / hosts / 连通性 |
| `tools/proxy/revert-proxy.bat` | 完全还原（含删除 JDK 信任库里的代理证书） |

> ⚠️ `hosts` 把这两个域名指到了 `127.0.0.1`，所以**停代理时必须同时删 hosts**（`stop-proxy.bat`
> 已绑定）。代理证书已导入 JDK 17/21 的 cacerts，`start-proxy.bat` 不会动它，重启代理后仍有效；
> 只有 `revert-proxy.bat` 会删除证书。

---

## 目录结构

```
BatsRemove/
├── 1.19/     # 独立 Fabric 工程：1.19.0 - 1.19.4
├── 1.20/     # 独立 Fabric 工程：1.20.x
├── 1.21/     # 独立 Fabric 工程：1.21.x
├── dist/     # 构建出的成品 jar
├── tools/proxy/  # 本地 Mojang 镜像代理 + 启停脚本
└── README.md
```

每个 `1.xx/` 内部：

```
src/main/java/com/batsremove/
├── BatRemoveMod.java          # Mod 入口（含 biome 移除）
├── BatRemovalManager.java     # 核心逻辑 + 存档标记
└── BatsRemoveCommand.java     # /batsremove 命令
src/main/resources/fabric.mod.json
build.gradle · settings.gradle · gradle.properties · LICENSE
```
