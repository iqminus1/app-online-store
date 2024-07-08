package uz.pdp.apponlinestore.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDTO {

    private Long id;

    @NotBlank
    private String name;

    @NotNull
    @Positive
    private Double price;

    @NotNull
    private Long categoryId;

    @NotNull
    private Long photoId;

    @NotNull
    private Integer quantity;

    private Timestamp createdAt;

}
