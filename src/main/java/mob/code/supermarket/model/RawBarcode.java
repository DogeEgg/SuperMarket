package mob.code.supermarket.model;

import mob.code.supermarket.legacy.BarcodeReader;

import java.util.Arrays;
import java.util.List;

/**
 * @author leo
 * @date 2021/6/11
 */
public class RawBarcode {
    private final String[] barcodes;

    public RawBarcode(String[] barcodes) {
        this.barcodes = barcodes;
    }

    public String[] judgeAndHandle() {
        try {
            boolean isRawBarcode = Arrays.stream(barcodes).anyMatch(s -> s.contains("|"));
            if (isRawBarcode) {
                return handle(barcodes).toArray(new String[]{});
            }
            return barcodes;
        } catch (SupermarketException ex) { //InvocationTargetException
            throw new SupermarketException("can not recognize barcode:\n" +
                    String.join("\n", Arrays.asList(barcodes)));
        }
    }

    private List<String> handle(String[] barcodes) {
        BarcodeReader barcodeReader = BarcodeReader.barcodeFactory();
        barcodeReader.getBarcode(barcodes);
        return barcodeReader.barcodes;
    }
}
