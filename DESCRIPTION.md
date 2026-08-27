# BatsRemove — 蝙蝠清除 / Bat Removal

一个轻量、**纯服务端逻辑**的 Minecraft 模组：阻止蝙蝠自然生成，首次加载存档时自动清除世界内已有蝙蝠并打上标记，还提供 `/batsremove` 命令随时一键清除全部蝙蝠。

A lightweight, **server-side** Minecraft mod that stops bats from spawning naturally, auto-clears existing bats once per save on first load, and adds a `/batsremove` command to wipe all bats at any time.

---

## 功能 / Features

**中文**

- 🦇 **阻止自然生成**：蝙蝠从所有生物群系的生成列表中移除，不再自然生成；用 `/summon` 或刷怪蛋召唤的蝙蝠**不受影响**。
- 🧹 **进档自动清理（仅一次）**：首次加载某个存档时自动清除已加载的蝙蝠，并写入标记文件；之后再次加载自动跳过，不会重复清理。
- ⌨️ **手动命令**：`/batsremove` 命令可随时清除所有已加载世界中的全部蝙蝠（需 OP，权限等级 2）。
- 🖥️ **服务端专用**：所有功能都在服务端完成，**客户端无需安装**，与原版客户端完全兼容；单人游戏（集成服务端）同样生效。
- 📦 **双加载器支持**：Fabric 与 NeoForge 均已适配，覆盖 **1.19 – 26.2** 全版本线。

**English**

- 🦇 **No natural spawning**: bats are removed from the spawn list of every biome; bats you summon with `/summon` or spawn eggs are **unaffected**.
- 🧹 **One-time auto-clear**: on the first load of a save, existing bats are cleared and the save is marked; later loads skip the cleanup.
- ⌨️ **Manual command**: `/batsremove` clears every bat in all loaded worlds at any time (requires OP, permission level 2).
- 🖥️ **Server-side only**: everything runs on the server — **no client install needed** and fully compatible with vanilla clients; works in singleplayer (integrated server) too.
- 📦 **Both loaders**: available for Fabric and NeoForge, covering **1.19 – 26.2**.

---

## 兼容性 / Compatibility

**中文**

| 加载器 Loader | 覆盖版本 Covered | 说明 Notes |
|---|---|---|
| Fabric | 1.19.x – 26.x | 按大版本系列分发，一个 jar 覆盖整个系列（需 Fabric Loader + Fabric API） |
| NeoForge | 1.20.1 – 26.2 | 按"API 一致区间"分发（NeoForge 社区标准做法），覆盖 1.20.1 / 1.20.2–1.20.4 / 1.20.5–1.20.6 / 1.21.0–1.21.10 / 1.21.11 / 26.1–26.2 |

**English**

| Loader | Versions covered | Notes |
|---|---|---|
| Fabric | 1.19.x – 26.x | One jar per major version series (requires Fabric Loader + Fabric API) |
| NeoForge | 1.20.1 – 26.2 | Jars split by API-compatible ranges (standard NeoForge practice): 1.20.1 / 1.20.2–1.20.4 / 1.20.5–1.20.6 / 1.21.0–1.21.10 / 1.21.11 / 26.1–26.2 |

---

## 使用 / Usage

**中文**

1. 把对应版本的 jar 放入 `mods/` 文件夹。
2. 首次进入存档会自动清除蝙蝠并打标；之后不再自动清。
3. 想重新触发自动清除：删除该存档的 `<世界目录>/data/bats_removed.dat` 再加载。
4. 命令：`/batsremove`（需 OP，权限等级 2）。

**English**

1. Drop the jar for your version into the `mods/` folder.
2. Bats are cleared automatically on the first load of a save and the save is marked; later loads skip it.
3. To re-trigger the auto-clear, delete `<world>/data/bats_removed.dat` and reload.
4. Command: `/batsremove` (operator permission level 2).

---

## 说明 / Notes

**中文**

- 清除作用于**已加载区块**中的蝙蝠；未加载区域的蝙蝠不会立即移除，但自然生成已被禁止，因此不会再出现新蝙蝠。
- 玩家召唤的蝙蝠会被保留，可随时用 `/batsremove` 手动清除。
- 使用 `BiomeModifications` / `MobSpawnEvent.PositionCheck` 实现，无需 Mixin，兼容性良好。

**English**

- Clearing applies to bats in **loaded chunks**; bats in unloaded areas are not removed immediately, but since natural spawning is disabled, no new bats will appear.
- Player-summoned bats are kept and can be removed any time with `/batsremove`.
- Implemented with `BiomeModifications` / `MobSpawnEvent.PositionCheck` — no Mixins, clean compatibility.

---

## 许可证 / License

MIT