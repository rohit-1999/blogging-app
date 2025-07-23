package com.blog.services;

import java.util.List;

import com.blog.payloads.PostDto;
import com.blog.payloads.PostResponse;

public interface PostService {
	
	//create
	
	PostDto createPost(PostDto postDto, Integer userId, Integer categoryId);
	
	//update
	PostDto updatePost(PostDto postDto,Integer postId);
	
	//delete
	void deletePost(Integer postId);
	//get
	PostDto getPostById(Integer postId);
	//getall
	PostResponse getAllPost(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
	
	List<PostDto> getPostsbyCategory(Integer categoryId);
	
	List<PostDto> getPostsbyUser(Integer userId);
	
	List<PostDto> searchPosts(String keyword);

}
