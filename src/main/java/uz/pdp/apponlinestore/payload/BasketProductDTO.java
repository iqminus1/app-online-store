package uz.pdp.apponlinestore.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BasketProductDTO {

    private String name;

    private String imageUrl;

    private double price;

    private int quantity;

}
