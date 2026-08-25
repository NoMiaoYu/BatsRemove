# BatsRemove — 蝙蝠清除 Mod / Bat Removal Mod

A Minecraft Fabric mod that stops bats from spawning naturally, clears existing bats once per save on load, and lets you wipe all bats with a command.
一款 Minecraft Fabric 模组：阻止蝙蝠自然生成、在加载存档时一次性清除已有蝙蝠，并可用命令一键清除全部蝙蝠。

---

## 简介 / Introduction

**中文**
BatsRemove 是一款轻量的 Fabric 客户端/服务端通用模组，专治蝙蝠骚扰：
- 禁止蝙蝠在世界中**自然生成**（用 `/summon` 或刷怪蛋召唤的蝙蝠不受影响）。
- 首次加载存档时自动清除已存在的蝙蝠，并给该存档打上标记；之后再加载不会再重复清除。
- 提供 `/batsremove` 命令，随时手动清空世界内所有已加载的蝙蝠。
- 按**大版本系列**分发，一个 jar 覆盖整个系列的所有小版本。

**English**
BatsRemove is a lightweight Fabric mod (client + server) that keeps bats away:
- Prevents bats from **spawning naturally** in the world (bats you summon with `/summon` or spawn eggs are unaffected).
- On the first load of a save it automatically removes existing bats and marks that save, so later loads skip the cleanup.
- Adds the `/batsremove` command to manually clear every loaded bat at any time.
- Distributed per **major version series** — one jar covers every minor version in the series.

---

## 功能 / Features

| 中文 | English |
|------|---------|
| 阻止蝙蝠自然生成（召唤不受影响） | Stops natural bat spawning (summoned bats unaffected) |
| 加载存档时自动清除已有蝙蝠（仅一次，带存档标记） | Auto-clears existing bats once per save on load (with a save marker) |
| `/batsremove` 命令清除全部蝙蝠 | `/batsremove` command clears all bats |
| 按大版本系列（1.19 / 1.20 / 1.21 …）支持所有小版本 | Supports every minor version per major series (1.19 / 1.20 / 1.21 …) |

---

## 版本支持 / Supported Versions

| 系列 Series | 目标 Target | 覆盖 Coverage | 要求 Java |
|------|------|------|------|
| 1.19 | 1.19.4 | 1.19.0 – 1.19.4 | 17 |
| 1.20 | 1.20.1 | 1.20.x | 17 |
| 1.21 | 1.21.1 | 1.21.x | 21 |

> **中文**：每个目录是一个独立的 Fabric 工程，产物为一个可直接放入 `mods/` 的 jar。
> **English**: Each folder is a standalone Fabric project whose jar goes straight into your `mods/` folder.

---

## 使用 / Usage

**中文**
- 需要 Fabric Loader 与 Fabric API（版本须与你的 Minecraft 匹配）。
- 首次进入某存档会自动清除蝙蝠并打标；之后不再自动清。
- 想重新触发自动清除：删除该存档的 `data/bats_removed.dat` 文件再加载。
- 命令：`/batsremove`（需 OP，权限等级 2）。

**English**
- Requires Fabric Loader and Fabric API matching your Minecraft version.
- The first time you enter a save, bats are cleared automatically and the save is marked; later loads skip it.
- To re-trigger the auto-clear, delete `<world>/data/bats_removed.dat` and reload.
- Command: `/batsremove` (operator permission level 2).

---

## 说明 / Notes

**中文**
- 清除作用于**已加载区块**中的蝙蝠；未加载区块不会立刻清除，但因自然生成已被禁止，不会再出现新蝙蝠。
- 玩家召唤的蝙蝠保留，可用 `/batsremove` 随时手动清除。

**English**
- Clearing applies to bats in **loaded chunks**; unloaded areas are not cleared immediately, but since natural spawning is disabled, no new bats will appear.
- Player-summoned bats are kept and can be removed any time with `/batsremove`.
