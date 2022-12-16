package mob.code.supermarket.service;

import mob.code.supermarket.model.*;
import mob.code.supermarket.bean.Item;
import mob.code.supermarket.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author leo
 * @date 2021/6/2
 */
@Service
public class SupermarketService {
    private final ItemRepository itemRepository;

    public SupermarketService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<String> printReceiptInfo(String[] barcodes) {
        String[] parsedBarcodes = parseRawBarcode(barcodes);
        List<BarcodeQuantity> barcodeQuantities = handleSymbol(parsedBarcodes);
        checkQuantityAndType(barcodeQuantities);
        List<ReceiptItem> receiptItems = handleQuantityAndSummary(barcodeQuantities);
        return new Receipt(receiptItems).printReceipt();
    }

    private String[] parseRawBarcode(String[] barcodes) {
        return new RawBarcode(barcodes).judgeAndHandle();
    }

    private List<ReceiptItem> handleQuantityAndSummary(List<BarcodeQuantity> barcodeQuantities) {
        return barcodeQuantities.stream().map(barcodeQuantity -> getReceiptItem(barcodeQuantity.getBarcode(), barcodeQuantity.getQuantity()))
                .collect(Collectors.toMap(ReceiptItem::getBarcode, ReceiptItem::getQuantity, Double::sum))
                .entrySet().stream().map(e -> new BarcodeQuantity(e.getKey(), String.valueOf(e.getValue())))
                .map(barcodeQuantity -> getReceiptItem(barcodeQuantity.getBarcode(), barcodeQuantity.getQuantity())).collect(Collectors.toList());
    }

    private void checkQuantityAndType(List<BarcodeQuantity> list) {
        list.forEach(barcodeQuantity -> checkMapItem(barcodeQuantity.getBarcode(), barcodeQuantity.getQuantity()));
    }

    private void checkMapItem(String barcode, String quantity) {
        if (!quantity.isEmpty() && new BigDecimal(quantity).scale() > 1) {
            throw new SupermarketException("wrong quantity of " + barcode);
        }
        if (getItem(barcode).isPackagingType() && quantity.contains(".")) {
            throw new SupermarketException("wrong quantity of " + barcode);
        }
        if (getItem(barcode).isWeighingType() && quantity.isEmpty()) {
            throw new SupermarketException("wrong quantity of " + barcode);
        }
        if ("0".equals(quantity)) {
            throw new SupermarketException("wrong quantity of " + barcode);
        }
    }

    private List<BarcodeQuantity> handleSymbol(String[] barcodes) {
        return Arrays.stream(barcodes).map(BarcodeQuantity::new).collect(Collectors.toList());
    }

    private ReceiptItem getReceiptItem(String barcode, String quantity) {
        if (quantity.isEmpty()) {
            return new ReceiptItem(getItem(barcode), 1.0);
        }
        return new ReceiptItem(getItem(barcode), Double.parseDouble(quantity));
    }

    private Item getItem(String barcode) {
        return itemRepository.findByBarcode(barcode).orElseThrow(() -> new SupermarketException("item doesn't exist: " + barcode));
    }
}
