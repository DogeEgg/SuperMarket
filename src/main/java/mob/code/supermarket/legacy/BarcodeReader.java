package mob.code.supermarket.legacy;

import mob.code.supermarket.model.SupermarketException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BarcodeReader {

    List<StringBuilder> symbols;
    public List<String> barcodes;
    private static final int NB_CHARS_BY_LINE = 27;
    public static final String SA =
            " _ | ||_|   |  |   _  _||_  _  _| _|   |_|  |" +
                    " _ |_  _| _ |_ |_| _   |  | _ |_||_| _ |_| _|";

    public static BarcodeReader barcodeFactory() {
        BarcodeReader barcode = new BarcodeReader();
        barcode.initSymbols();
        barcode.barcodes = new ArrayList<>();
        return barcode;
    }

    private void initSymbols() {
        this.symbols = Stream.generate(StringBuilder::new)
                .limit(NB_CHARS_BY_LINE)
                .collect(Collectors.toList());
    }

    private void storeTheNumber() {
        StringBuilder n = new StringBuilder();
        symbols.stream().filter(s -> s.length() != 0).forEach(s ->
                n.append(String.valueOf(getSymbolFromStringRepresentation(s.toString())))
        );
        barcodes.add(n.toString());
    }

    private void splitLineAndFillAccounts(String stringLine) {

        for (int i = 3, j = 0; i <= stringLine.length(); i += 3, j++) {
            symbols.get(j).append(stringLine.substring(i - 3, i));

        }
    }

    public void getBarcode(String in) {
        // Read from string
        // read the line by 4-tuple
        String[] entryLines = in.split("\n");
        for (int i = 0; i < entryLines.length; i++) {
            if (((i + 1) % 4) != 0) {
                splitLineAndFillAccounts(entryLines[i]);
            } else {
                storeTheNumber();
                initSymbols();
            }
        }
        barcodes.forEach(System.out::println);
    }

    public void getAccountNumbers() {
        // Read from file
        // read the line by 4-tuple
        // Every 4 lines get the account number
        // Store  the number in a List
        // Then re-init the accountsymbols list
        // Print the account list
    }

    public int getSymbolFromStringRepresentation(final String representation) {
        if (!SA.contains(representation)) {
            throw new SupermarketException("");
        }
        //indexOf 返回指定字符在字符串中第一次出现处的索引，如果此字符串中没有这样的字符，则返回 -1
        return SA.indexOf(representation) / 9;
    }

    public void getBarcode(String[] inBarcodes) {
        for (int i = 0; i < inBarcodes.length; i++) {
            if (((i + 1) % 4) != 0) {
                splitLineAndFillAccounts(inBarcodes[i]);
            } else {
                barcodes.add(storeTheNumber(inBarcodes[i]));
                initSymbols();
            }
        }
    }

    private String storeTheNumber(String quantity) {
        StringBuilder sb = new StringBuilder();
        symbols.stream().filter(s -> s.length() != 0).forEach(s ->
                sb.append(getSymbolFromStringRepresentation(s.toString()))
        );
        return getBarcodeQuantityString(sb, quantity);
    }

    private String getBarcodeQuantityString(StringBuilder sb, String quantity) {
        if (quantity.isEmpty()) {
            return sb.toString();
        } else {
            return String.format("%s-%s", sb, quantity);
        }
    }


}
