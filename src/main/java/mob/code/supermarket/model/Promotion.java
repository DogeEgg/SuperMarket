package mob.code.supermarket.model;

/**
 * @author leo
 * @date 2021/6/24
 */
public enum Promotion {
    /**
     * TWO_FOR_ONE : 买二送一
     * FIVE_PERCENT_OFF : 95折
     * BOTH : 同时参加两个促销活动（但是只有买二送一生效）
     * NONE : 不参加促销活动
     */
    TWO_FOR_ONE,
    FIVE_PERCENT_OFF,
    BOTH,
    NONE;
}
