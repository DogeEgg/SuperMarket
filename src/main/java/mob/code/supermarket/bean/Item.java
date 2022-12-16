package mob.code.supermarket.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mob.code.supermarket.model.Promotion;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    private String barcode;
    private String name;
    private String unit;
    private double price;
    private String type;
    private Promotion promotion;

    public boolean isWeighingType() {
        return "1".equals(type);
    }

    public boolean isPackagingType() {
        return "0".equals(type);
    }
}
