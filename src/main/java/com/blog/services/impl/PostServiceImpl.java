package com.blog.services.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.blog.entities.Category;
import com.blog.entities.Post;
import com.blog.entities.User;
import com.blog.exceptions.ResourceNotFoundException;
import com.blog.payloads.PostDto;
import com.blog.payloads.PostResponse;
import com.blog.repositories.CategoryRepo;
import com.blog.repositories.PostRepo;
import com.blog.repositories.UserRepo;
import com.blog.services.PostService;

@Service
public class PostServiceImpl implements PostService {
    
	@Autowired
	private PostRepo postRepo;
	
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private CategoryRepo categoryRepo;
	@Override
	public PostDto createPost(PostDto postDto, Integer userId, Integer categoryId) {
		
		User user = this.userRepo.findById(userId).orElseThrow(()->new ResourceNotFoundException("User", "User ID", userId));
		
		Category category = this.categoryRepo.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("category", "category ID", categoryId));
		
		Post post = this.modelMapper.map(postDto, Post.class);
		post.setImageName("default.png");
		post.setAddeddate(new Date());
		post.setUser(user);
		post.setCategory(category);
		
		Post newPost = this.postRepo.save(post);
				
		return this.modelMapper.map(newPost, PostDto.class);
	
	}
	@Override
	public PostDto updatePost(PostDto postDto, Integer postId) {
		Post post = this.postRepo.findById(postId).orElseThrow(()->new ResourceNotFoundException("post", "Post ID", postId));
		
		post.setImageName(postDto.getImageName());
		post.setContent(postDto.getContent());
		post.setTitle(postDto.getTitle());
		
		Post save = this.postRepo.save(post);
		
		return this.modelMapper.map(save, PostDto.class);
		
		
	}
	@Override
	public void deletePost(Integer postId) {
		Post post = this.postRepo.findById(postId).orElseThrow(()->new ResourceNotFoundException("post", "Post ID", postId));
		this.postRepo.delete(post);
	}
	@Override
	public PostDto getPostById(Integer postId) {
		 Post post = this.postRepo.findById(postId).orElseThrow(()-> new ResourceNotFoundException("Post", "Post Id", postId));
		 PostDto postDtos = this.modelMapper.map(post, PostDto.class);
		 return postDtos;
	}
	@Override
	public PostResponse getAllPost(Integer pageNumber, Integer pageSize, String sortBy,String sortDir) {
		
		Sort sort =null;
		if(sortDir.equalsIgnoreCase("asc")) {
			sort = Sort.by(sortBy).ascending();
		}
		else {
		sort = Sort.by(sortBy).descending();
		}
		
		Pageable p =PageRequest.of(pageNumber, pageSize, sort); 
		Page<Post> pagePost = this.postRepo.findAll(p);
		List<Post> allPost = pagePost.getContent();
		List<PostDto> postDtos = allPost.stream().map((post)->this.modelMapper.map(post, PostDto.class)).collect(Collectors.toList());
		
		PostResponse postResponse = new PostResponse();
		postResponse.setContent(postDtos);
		postResponse.setPageNumber(pagePost.getNumber());
		postResponse.setPageSize(pagePost.getSize());
		postResponse.setTotalElements(pagePost.getTotalElements());
		postResponse.setTotalPages(pagePost.getTotalPages());
		postResponse.setLastPage(pagePost.isLast());
		
		return postResponse;
	}
	@Override
	public List<PostDto> getPostsbyCategory(Integer categoryId) {
		Category cat = this.categoryRepo.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("category", "category ID", categoryId));
		List<Post> posts = this.postRepo.findByCategory(cat);
		
		List<PostDto> postDto = 	posts.stream().map(((post)->this.modelMapper.map(post, PostDto.class))).collect(Collectors.toList());
		
		return postDto;
	}
	@Override
	public List<PostDto> getPostsbyUser(Integer userId) {
		User user = this.userRepo.findById(userId).orElseThrow(()->new ResourceNotFoundException("User", "user Id", userId));
		
		List<Post> posts = this.postRepo.findByUser(user);
		
		List<PostDto> postDtos = posts.stream().map((post)->this.modelMapper.map(post, PostDto.class)).collect(Collectors.toList());
		
		return postDtos;
	}
	@Override
	public List<PostDto> searchPosts(String keyword) {
		List<Post> posts = this.postRepo.findByTitleContaining(keyword);
		List<PostDto> postDtos = posts.stream().map((post)->this.modelMapper.map(post, PostDto.class)).collect(Collectors.toList());
		return postDtos;
	}
	

}
