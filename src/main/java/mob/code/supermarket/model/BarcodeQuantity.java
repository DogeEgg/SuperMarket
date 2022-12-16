package mob.code.supermarket.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author leo
 * @date 2021/6/10
 */
@Getter
@Setter
public class BarcodeQuantity {
    private String barcode;
    private String quantity;

    public BarcodeQuantity(String barcode, String quantity) {
        this.barcode = barcode;
        this.quantity = quantity;
    }

    public BarcodeQuantity(String rawBarcode) {
        if (rawBarcode.contains("-")) {
            String[] split = rawBarcode.split("-");
            barcode = split[0];
            quantity = split[1];
        } else {
            barcode = rawBarcode;
            quantity = "";
        }
    }


}
