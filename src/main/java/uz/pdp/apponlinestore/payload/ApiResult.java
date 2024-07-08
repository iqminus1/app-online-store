package uz.pdp.apponlinestore.payload;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class ApiResult<T> {

    private boolean success;

    private T data;

    private String errorMessage;

    private List<FieldErrorDTO> fieldErrors;

    public static<T> ApiResult<T> success(T data){
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.setSuccess(true);
        apiResult.setData(data);
        return apiResult;
    }

    public static<T,E> ApiResult<T> error(Set<ConstraintViolation<E>> constraintViolations){
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.setSuccess(false);

        List<FieldErrorDTO> fieldErrorDTOList = constraintViolations
                .stream()
                .map(constraintViolation -> new FieldErrorDTO(
                        constraintViolation.getPropertyPath().toString(),
                        constraintViolation.getMessage()
                ))
                .collect(Collectors.toList());

        apiResult.setFieldErrors(fieldErrorDTOList);

        return apiResult;
    }

    public static ApiResult<?> error(String errorMessage){
        ApiResult<Object> apiResult = new ApiResult<>();
        apiResult.setSuccess(false);
        apiResult.setErrorMessage(errorMessage);
        return apiResult;
    }


}
