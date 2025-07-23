package com.blog.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.blog.config.AppConstants;
import com.blog.entities.Post;
import com.blog.payloads.ApiResponse;
import com.blog.payloads.PostDto;
import com.blog.payloads.PostResponse;
import com.blog.repositories.PostRepo;
import com.blog.services.FileService;
import com.blog.services.PostService;

import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/api/")
public class PostController {

    private final ModelMapper modelMapper;

    private final PostRepo postRepo;
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private FileService fileservice;
	
	@Value("${project.image}")
	private String path;
	
    PostController(PostRepo postRepo, ModelMapper modelMapper) {
        this.postRepo = postRepo;
        this.modelMapper = modelMapper;
    }
	
	//create
	@PostMapping("/user/{userId}/category/{categoryId}/posts")
	public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto,@PathVariable Integer userId, @PathVariable Integer categoryId){
		PostDto createPost = this.postService.createPost(postDto, userId, categoryId);
		
		return new ResponseEntity<PostDto>(createPost, HttpStatus.CREATED);
	}
	
	//get all posts
	
	@GetMapping("/posts/")
	public ResponseEntity<PostResponse> getAllPosts(
			@RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
			@RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_Size,required = false) Integer pageSize,
			@RequestParam(value = "sortBy",defaultValue = AppConstants.SORT_BY, required=false) String sortBy,
			@RequestParam(value = "sortDir",defaultValue = AppConstants.SORT_DIR, required=false) String sortDir
			){
		 PostResponse postResponse = this.postService.getAllPost(pageNumber,pageSize, sortBy, sortDir);
		return new ResponseEntity<PostResponse>(postResponse,HttpStatus.OK);
	}
	
	//get post by id
	@GetMapping("/posts/{postId}")
	public ResponseEntity<PostDto> getPostById(@PathVariable Integer postId){
		
		PostDto postDto = this.postService.getPostById(postId);
		
		return new ResponseEntity<PostDto>(postDto,HttpStatus.OK);
		
	}
	
	//update post
	
	@PutMapping("/posts/{postId}")
	public ResponseEntity<PostDto> updatePost(@RequestBody PostDto postDto, @PathVariable Integer postId ){
		PostDto postDtos = this.postService.updatePost(postDto, postId);
		
		return new ResponseEntity<PostDto>(postDtos,HttpStatus.OK);
		
	}
	
	//Delete Post
	@DeleteMapping("/posts/{postId}")
	public ApiResponse deletePost(@PathVariable Integer postId){
		
		this.postService.deletePost(postId);
		return new ApiResponse("Post Deleted succesfully !!", true);
	}
	
	//get by user
	@GetMapping("/user/{userId}/posts")
	public ResponseEntity<List<PostDto>> getPostsByUser(@PathVariable Integer userId){
		
		List<PostDto> posts =   this.postService.getPostsbyUser(userId);
		
		return new ResponseEntity<List<PostDto>>(posts,HttpStatus.OK);
		
		
	}
	//get by Category
		@GetMapping("/Category/{categoryId}/posts")
		public ResponseEntity<List<PostDto>> getPostsByCategory(@PathVariable Integer categoryId){
			
			List<PostDto> posts =   this.postService.getPostsbyCategory(categoryId);		
			return new ResponseEntity<List<PostDto>>(posts,HttpStatus.OK);
			
			
		}
		
		//searching 
		@GetMapping("/post/search/{keyword}")
		public ResponseEntity<List<PostDto>> searchPostByTitle(@PathVariable("keyword") String keyword){
			List<PostDto>  postDtos = this.postService.searchPosts(keyword);
			
			return new ResponseEntity<List<PostDto>>(postDtos,HttpStatus.OK);
		}
	//post image upload
		
	@PostMapping("post/image/upload/{postId}")
	public ResponseEntity<PostDto> uploadPostImage(@RequestParam("image") MultipartFile image,
			@PathVariable Integer postId) throws IOException{
		PostDto postDto =this.postService.getPostById(postId);
		String fileName = this.fileservice.uploadImage(path,image);
		
		postDto.setImageName(fileName);
		PostDto updatePost = this.postService.updatePost(postDto, postId);
		
		
		return new ResponseEntity<PostDto>(updatePost,HttpStatus.OK);
	}
	@GetMapping(value = "/post/image/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE )
	public void downloadImage(@PathVariable("imageName") String imageName,
			HttpServletResponse response) throws IOException {
		InputStream resource = this.fileservice.getResponse(path,imageName);
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		StreamUtils.copy(resource, response.getOutputStream());
	}

}
