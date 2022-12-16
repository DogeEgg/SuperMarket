package mob.code.supermarket.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leo
 * @date 2021/6/10
 */
@Getter
public class Receipt {
    private final List<ReceiptItem> list;

    public Receipt(List<ReceiptItem> list) {
        this.list = list;
    }

    public List<String> printReceipt() {
        ArrayList<String> list = new ArrayList<>();
        list.add("****** SuperMarket receipt ******");
        list.addAll(getItems());
        list.add("---------------------------------");
        if (isTwoForOneAndQuantityIsNotOne()) {
            list.add("Two-for-one:");
            list.addAll(getPromotionInfo());
            list.add("---------------------------------");
        }
        list.add(totalPrice());
        if (isPromotion()) {
            list.addAll(getPromotedTotalPrice());
        }
        list.add("*********************************");
        return list;
    }

    private List<String> getItems() {
        return list.stream().map(ReceiptItem::spliceItemInfo).collect(Collectors.toList());
    }

    private String totalPrice() {
        return "total: " + String.format("%.2f", list.stream().mapToDouble(ReceiptItem::getTotalPrice).sum()) + "(CNY)";
    }

    private List<String> getPromotedTotalPrice() {
        return list.stream().filter(ReceiptItem::isNotOne).filter(ReceiptItem::isNotNone).map(ReceiptItem::getPromotionTotalPrice).collect(Collectors.toList());
    }

    private boolean isPromotion() {
        return list.stream().anyMatch(ReceiptItem::isNotNone);
    }

    private boolean isTwoForOneAndQuantityIsNotOne() {
        return list.stream().anyMatch(ReceiptItem::isTwoForOneAndQuantityIsNotOne);
    }

    private List<String> getPromotionInfo() {
        return list.stream().filter(ReceiptItem::isTwoForOneAndQuantityIsNotOne).map(ReceiptItem::promotionInfo).collect(Collectors.toList());
    }
}
