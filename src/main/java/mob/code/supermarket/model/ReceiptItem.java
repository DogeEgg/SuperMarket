package mob.code.supermarket.model;

import lombok.*;
import mob.code.supermarket.bean.Item;

import javax.persistence.Id;
import java.text.MessageFormat;

/**
 * @author leo
 * @date 2021/6/2
 */
@Getter
@Setter
@NoArgsConstructor
public class ReceiptItem {
    @Id
    private String barcode;
    private String name;
    private String unit;
    private double price;
    private String type;
    private double quantity;
    private Promotion promotion;

    public ReceiptItem(Item item, double quantity) {
        this.barcode = item.getBarcode();
        this.name = item.getName();
        this.unit = item.getUnit();
        this.price = item.getPrice();
        this.type = item.getType();
        this.promotion = item.getPromotion();
        this.quantity = quantity;
    }

    private String getFormatUnit() {
        if (unit.isEmpty()) {
            return "";
        }
        return "(" + unit + ")";
    }

    public String spliceItemInfo() {
        if (isFivePercentOff()) {
            return MessageFormat.format("{0}: {1}{2} x {3} --- {4} (-{5})"
                    , name, quantity, getFormatUnit(), formatDouble(price), formatDouble(getTotalPrice()), formatDouble(getFivePercentOffPrice()));
        }
        if (isTwoForOneAndQuantityIsNotOne() || isBoth()) {
            return MessageFormat.format("{0}: {1}{2} x {3} --- {4}"
                    , name, quantity, getFormatUnit(), formatDouble(price), formatDouble(getTotalPrice()));
        }
        return MessageFormat.format("{0}: {1}{2} x {3} --- {4}"
                , name, quantity, getFormatUnit(), formatDouble(price), formatDouble(getTotalPrice()));
    }

    public String getPromotionTotalPrice() {
        if (isTwoForOneAndQuantityIsNotOne() || isBoth()) {
            return "      -" + formatDouble(getTwoForOneItemPrice()) + "(CNY)";
        }
        return "      -" + formatDouble(getFivePercentOffPrice()) + "(CNY)";
    }

    private double getTwoForOneItemPrice() {
        return getTwoForOneItemQuantity() * price;
    }

    private double getTwoForOneItemQuantity() {
        if ((quantity % 3 == 0)) {
            return quantity / 3;
        }
        return quantity % 3;
    }

    public String promotionInfo() {
        return String.format("%s: %s", name, new Double(getTwoForOneItemQuantity()).intValue());
    }

    private double getFivePercentOffPrice() {
        return getTotalPrice() - (getTotalPrice() * 0.95);
    }

    private String formatDouble(double number) {
        return String.format("%.2f", number);
    }

    public double getTotalPrice() {
        return price * quantity;
    }

    public boolean isTwoForOneAndQuantityIsNotOne() {
        return isTwoForOne() && quantity != 1.0;
    }

    private boolean isTwoForOne() {
        return promotion.equals(Promotion.TWO_FOR_ONE);
    }

    public boolean isFivePercentOff() {
        return promotion.equals(Promotion.FIVE_PERCENT_OFF);
    }

    public boolean isBoth() {
        return promotion.equals(Promotion.BOTH);
    }

    public boolean isNotNone() {
        return !promotion.equals(Promotion.NONE);
    }

    public boolean isNotOne() {
        return quantity != 1.0;
    }
}
