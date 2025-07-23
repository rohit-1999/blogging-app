package com.blog.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.payloads.ApiResponse;
import com.blog.payloads.CategoryDto;
import com.blog.services.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories/")
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;
	
	//create
	
	@PostMapping("/")
	public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto){
		
		CategoryDto createdCategory =  this.categoryService.createCategory(categoryDto);
		
		return new ResponseEntity<CategoryDto>(createdCategory, HttpStatus.CREATED);
		
	}
	
	//update
	@PutMapping("/{categoryId}")
	public ResponseEntity<CategoryDto> updateCategory(@Valid @RequestBody CategoryDto categoryDto, @PathVariable Integer categoryId){
		CategoryDto updatedCategory = this.categoryService.updateCategory(categoryDto, categoryId);
		
		
		return new ResponseEntity<CategoryDto>(updatedCategory, HttpStatus.OK);
	}
	
	//delete
	
	@DeleteMapping("/{categoryId}")
	public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Integer categoryId){
		 this.categoryService.deleteCategory(categoryId);
		
		
		return new ResponseEntity<ApiResponse>(new ApiResponse("Category is deleted successfully !!", true), HttpStatus.OK);
	}
	//getbyid
	
	@GetMapping("/{categoryId}")
	public ResponseEntity<CategoryDto> getCategory(@PathVariable Integer categoryId){
		 CategoryDto catDto = this.categoryService.getCategory(categoryId);
		
		
		return new ResponseEntity<CategoryDto>(catDto, HttpStatus.OK);
	}
	
	//getall
	@GetMapping("/")
	public ResponseEntity<List<CategoryDto>> getCategory(){
		List <CategoryDto> catDto = this.categoryService.getCategories();
		return ResponseEntity.ok(catDto);
	}

}
