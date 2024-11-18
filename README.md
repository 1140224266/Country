# KillerEntity 项目

## 项目概述

KillerEntity 是一个基于 Minecraft 的自定义实体项目，旨在创建一个具有特定行为的实体。该实体能够在游戏世界中执行多种任务，如攻击敌对生物、拾取物品、与玩家互动等。

## 功能

- **移动到出生点**：实体会在其出生点附近游荡，并能够返回到出生点。
- **攻击敌对生物**：实体具有攻击敌对生物的能力。
- **拾取物品**：实体可以拾取特定的物品，并将其存储在自身的库存中。
- **与玩家互动**：实体可以与玩家进行简单的互动。
- **自定义目标**：通过不同的目标类，实体可以执行多种行为，如寻找箱子、改变物品、放置物品等。

## 主要类和方法

- `KillerEntity`：核心实体类，继承自 `PathAwareEntity`，实现了 `InventoryOwner` 接口。
  - `initGoals()`：初始化实体的行为目标。
  - `interactMob()`：定义实体与玩家互动的行为。
  - `tickMovement()`：定义实体的每个游戏刻的行为。
  - `setMainHandItem()`：根据策略选择主手物品。

- `MethodLogger`：用于记录方法调用的日志类。

- 自定义目标类：
  - `MoveToBirthPositionGoal`
  - `ModAttackGoal`
  - `FindChestAndChangeItemGoal`
  - `PutItemsInChest`

## 安装与使用

1. 克隆项目到本地：
   ```bash
   git clone <repository-url>
   ```

2. 导入项目到你的 Java 开发环境（如 IntelliJ IDEA 或 Eclipse）。

3. 确保你有 Minecraft Modding 的开发环境（如 Minecraft Forge 或 Fabric）。

4. 编译并运行项目。

## 贡献

欢迎对本项目进行贡献！请提交 pull request 或报告问题。

## 许可证

该项目遵循 MIT 许可证。详情请参阅 LICENSE 文件。

## 联系

如有任何问题或建议，请联系 [你的邮箱]。