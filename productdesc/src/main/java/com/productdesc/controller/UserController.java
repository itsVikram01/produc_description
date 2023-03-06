package com.productdesc.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.productdesc.entities.Product;
import com.productdesc.entities.User;
import com.productdesc.helper.Message;
import com.productdesc.repositories.ProductRepository;
import com.productdesc.repositories.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {
	
	private static final Object role = null;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;
	
	 // method for adding common data to response  
	 @ModelAttribute public void commonData(Model model, Principal principal) {
		 System.out.println("Inside UserController ModelAttribute commonData ... ");
		 String userName = principal.getName(); System.out.println("USERNAME : " +userName);	  
		 User user = userRepository.getUserByUserName(userName);
		 System.out.println("USER : " + user);		 
		 model.addAttribute("user", user); 
		 model.addAttribute("role", role); 
	 }
	
	@GetMapping("/index")
	public String dashboard() {
		System.out.println("Inside UserController dashboard Handler................");		
		return "normal/user_dashboard";
	}
	
	// open add_product_form
	@GetMapping("/add-product")
	public String openAddProductForm(Model model, Principal principal) {
		System.out.println("Inside UserController openAddProductForm handler... ");
		model.addAttribute("title", "Add Product");
		model.addAttribute("product", new Product());
		return "normal/add_product_form";
	}

	// processing product form process-content
	@PostMapping("/process-product")
	public String processProductForm(@ModelAttribute("product") Product product,
			@RequestParam("profileImage") MultipartFile multipartFile, Principal principal, 
			HttpSession session) {
		System.out.println("Inside UserController processProductForm handler... ");
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);
			System.out.println("DATA : " + product);
			product.setUser(user);
			user.getProducts().add(product);
			// processing and uploading image/file
			if (multipartFile.isEmpty()) {
				System.out.println("File is empty!!...");
				product.setImage("product.png");
			} else {
				System.out.println("file getName : " + multipartFile.getName());
				System.out.println("file getOriginalFilename : " + multipartFile.getOriginalFilename());
				// update the file name to product db
				product.setImage(multipartFile.getOriginalFilename());
				System.out.println("File name added to database");
				// upload the file to folder
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename());
				Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is uploaded!!...");
			}
			// save product
			this.userRepository.save(user);
			// success message
			session.setAttribute("message", new Message("Product is added Successfully!! Add More...", "success"));
		} catch (Exception e) {
			System.out.println("ERROR" + e.getMessage());
			e.printStackTrace();
			// error message
			session.setAttribute("message", new Message("Something went wrong? try again!!...", "danger"));
		}
		return "normal/add_product_form";
	}

	// open view_product_form
	// current page no = [page] : 0
	// product per page = [n] : 5
	@GetMapping("/view-products/{page}")
	public String openViewProductsForm(@PathVariable("page") Integer page, Model model, Principal principal) {
		System.out.println("Inside UserController openViewProductsForm handler... ");
		model.addAttribute("title", "View Products");

		// get user list
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		/*
		 * // get product list for all users List<Product> products =
		 * user.getProducts();
		 */

		// current page no = [page] : 0
		// product per page = [n] : 5
		Pageable pageable = PageRequest.of(page, 3);

		// get product list from productRepository for login user
		Page<Product> products = this.productRepository.findProductsByUser(user.getId(), pageable);
		model.addAttribute("products", products);
		model.addAttribute("currentPage", page);
		model.addAttribute("tatalPages", products.getTotalPages());

		return "normal/view_products_form";
	}

	// showing particular user product detail
	@GetMapping("/{pid}/product")
	public String showProductDetail(@PathVariable("pid") Integer pid, Model model, Principal principal) {
		System.out.println("Inside UserController showProductDetail handler... ");
		System.out.println("PID : " + pid);

		Optional<Product> productOptional = this.productRepository.findById(pid);
		Product product = productOptional.get();

		// user logged in
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		if (user.getId() == product.getUser().getId()) {
			model.addAttribute("product", product);
			model.addAttribute("title", product.getPname());
		}

		return "normal/product_detail";
	}

	// update product
	@PostMapping("/update-product/{pid}")
	public String updateProduct(@PathVariable("pid") Integer pid, Model model, HttpSession session) {
		System.out.println("Inside UserController updateProduct handler... ");

		model.addAttribute("title", "Update Product");

		Product product = this.productRepository.findById(pid).get();

		model.addAttribute("product", product);

		// session.setAttribute("message", new Message("Product updated
		// succesfully...!!", "success"));

		return "normal/update_product";
	}

	// process update product
	// processing update-product form process-update-product
	@PostMapping("/process-update-product")
	public String processUpdateProductForm(@ModelAttribute("product") Product product,
			@RequestParam("profileImage") MultipartFile multipartFile, Principal principal, HttpSession session) {
		System.out.println("Inside UserController processUpdateProductForm handler... ");
		System.out.println("Product : " + product);
		try {
			// old product detail to delete image
			Product oldProductDetails = this.productRepository.findById(product.getPid()).get();
			// System.out.println("multipartFile getName : "+multipartFile.getName());
			// System.out.println("multipartFile getOriginalFilename :
			// "+multipartFile.getOriginalFilename());
			// processing and uploading new image/file
			if (!multipartFile.isEmpty()) {
				System.out.println("File is not empty!!...");
				// delete old image
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file = new File(deleteFile, oldProductDetails.getImage());
				file.delete();
				// update the new file to folder
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename());
				Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				// update the new file name to product db
				product.setImage(multipartFile.getOriginalFilename());
				System.out.println("File name added to database");
				System.out.println("Image is uploaded!!...");
			} else {
				System.out.println("File is empty!!...");
				// product.setImage("product.png");
				product.setImage(oldProductDetails.getImage());
			}
			// String name = principal.getName();
			// User user = this.userRepository.getUserByUserName(name);
			User user = this.userRepository.getUserByUserName(principal.getName());
			user.getProducts().remove(product);
			this.userRepository.save(user);
			//product.setUser(user);
			// save product
			this.productRepository.save(product);
			// success message
			session.setAttribute("message", new Message("Product is updated Successfully...!!", "success"));
		} catch (Exception e) {
			System.out.println("ERROR" + e.getMessage());
			e.printStackTrace();
			// error message
			session.setAttribute("message", new Message("Something went wrong? try again...!!", "danger"));
		}
		System.out.println("Product NAME : " + product.getPname());
		System.out.println("Product ID : " + product.getPid());
		return "redirect:/user/" + product.getPid() + "/product";
	}
	
	// delete user product
	@GetMapping("/delete/{pid}")
	public String deleteProduct(@PathVariable("pid") Integer pid, Model model, HttpSession session) {
		System.out.println("Inside UserController deleteProduct handler... ");

		// Optional<Product> productOptional = this.productRepository.findById(pid);
		// Product product = productOptional.get();
		Product product = this.productRepository.findById(pid).get();

		// set product = null to unlink from product so that it could delete
		product.setUser(null);
		// delete product
		this.productRepository.delete(product);

		session.setAttribute("message", new Message("Product deleted succesfully...!!", "success"));

		return "redirect:/user/view-products/0";
	}

	// open user-settings
	@GetMapping("/user-settings")
	public String openUserSettingForm(Model model, Principal principal) {
		System.out.println("Inside UserController openUserSettingForm handler... ");

		model.addAttribute("title", "Settings");
		model.addAttribute("product", new Product());

		return "normal/settings";
	}
	
}
