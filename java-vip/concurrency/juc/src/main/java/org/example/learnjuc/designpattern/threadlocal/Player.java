package org.example.learnjuc.designpattern.threadlocal;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家类，用于保存每个线程对应的玩家状态信息
 */
public class Player {
    /**
     * 使用 ThreadLocal 变量保存每个线程对应的玩家状态信息
     */
    private static final ThreadLocal<PlayerState> state = ThreadLocal.withInitial(PlayerState::new);

    /**
     * 玩家移动方法，更新玩家位置信息
     * @param x 新的 x 坐标
     * @param y 新的 y 坐标
     */
    public void move(int x, int y) {
        // 获取当前线程对应的 PlayerState 对象
        PlayerState currentState = state.get();
        // 更新玩家位置信息
        currentState.setPosition(x, y);
        // TODO: 更新玩家位置信息
    }

    /**
     * 玩家受到伤害方法，更新玩家生命值信息
     * @param damage 受到的伤害值
     */
    public void takeDamage(int damage) {
        // 获取当前线程对应的 PlayerState 对象
        PlayerState currentState = state.get();
        // 更新玩家生命值信息
        currentState.reduceHealth(damage);
        // TODO: 更新玩家生命值信息
    }

    /**
     * 玩家使用能量方法，更新玩家能量值信息
     * @param energy 使用的能量值
     */
    public void useEnergy(int energy) {
        // 获取当前线程对应的 PlayerState 对象
        PlayerState currentState = state.get();
        // 更新玩家能量值信息
        currentState.reduceEnergy(energy);
        // TODO: 更新玩家能量值信息
    }



}

/**
 * 玩家状态类，用于保存玩家的位置、生命值、能量值等信息
 */
class PlayerState {
    private int x; // 玩家 x 坐标
    private int y; // 玩家 y 坐标
    private int health; // 玩家生命值
    private int energy; // 玩家能量值

    /**
     * 更新玩家位置信息
     * @param x 新的 x 坐标
     * @param y 新的 y 坐标
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 减少玩家生命值
     * @param damage 减少的生命值
     */
    public void reduceHealth(int damage) {
        this.health -= damage;
    }

    /**
     * 减少玩家能量值
     * @param energy 减少的能量值
     */
    public void reduceEnergy(int energy) {
        this.energy -= energy;
    }
}
