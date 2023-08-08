package com.productsService.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel(description = "Model of Product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Product {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(notes = "The unique id of the Product")
    private Long id;

    @ApiModelProperty(notes = "The unique name of Product")
    @NotBlank(message = "Product name cannot be null or blank")
    @Size(max = 125, message = "Product name length must not exceed 125 characters")
    private String name;

    @ApiModelProperty(notes = "The description of Product")
    private String description;

    @ApiModelProperty(notes = "The price of Product")
    @NotNull(message = "Price of product cannot be null")
    private Double price;

    @ApiModelProperty(notes = "The  quantity of Product")
    @NotNull (message = "Quantity of product cannot be null")
    private Integer quantity;

    @ApiModelProperty(notes = "The categoryId of Product")
    @NotNull
    private Long categoryId;
}
