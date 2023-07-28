package com.productsService.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@ApiModel(description = "Model of Category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Category {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(notes = "The unique id of the Category")
    private Long id;

    @ApiModelProperty(notes = "The unique name of Category")
    @NotBlank(message = "Category name cannot be null or blank")
    @Size(max = 125, message = "Category name length must not exceed 125 characters")
    private String name;
}
