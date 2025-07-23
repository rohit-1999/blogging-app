package com.blog.payloads;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDto {

	private Integer categoryId;
	
	@NotNull
	@NotEmpty
	@Size(min=4, message="min size should be 4")
	private String categoryTitle;
	
	@NotNull
	@NotEmpty
	@Size(min =10,  message="min size should be 4")
	private String categoryDescription;
}
