package uz.pdp.apponlinestore.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryDTO {

    private Long id;

    @NotBlank
    private String name;

    private Long parentCategoryId;

    @NotNull
    private Long photoId;

}
