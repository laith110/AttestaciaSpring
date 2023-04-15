package com.example.attestationspring.controllers;
import com.example.attestationspring.models.*;
import com.example.attestationspring.repositories.CartRepository;
import com.example.attestationspring.repositories.CategoryRepository;
import com.example.attestationspring.repositories.OrderRepository;
import com.example.attestationspring.services.PersonService;
import com.example.attestationspring.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.UUID;

@Controller
public class AdminController {


    private final OrderRepository orderRepository;

   private  final PersonService personService;

    private final ProductService productService;

    @Value("${upload.path}")
    private String uploadPath;

    private final CategoryRepository categoryRepository;

    public AdminController(OrderRepository orderRepository, CartRepository cartRepository, PersonService personService, ProductService productService, CategoryRepository categoryRepository) {

        this.orderRepository = orderRepository;
        this.personService = personService;


        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }


    @GetMapping("admin/product/add")
    public String addProduct(Model model){
        model.addAttribute("product",new Product());
        model.addAttribute("category", categoryRepository.findAll());
        return "product/addProduct";
    }

    @PostMapping("admin/product/add")
    public String addProduct(@ModelAttribute("product") @Valid Product product, BindingResult bindingResult,
                           @RequestParam("file_one")MultipartFile first_one,
                           @RequestParam("file_two")MultipartFile first_two,
                           @RequestParam("file_three")MultipartFile first_three,
                           @RequestParam("category") int category,Model model) throws IOException {
        Category category_db= (Category) categoryRepository.findById(category).orElseThrow();
        System.out.println(category_db.getName());
        if(bindingResult.hasErrors()){
            model.addAttribute("category",categoryRepository.findAll());
            return "product/addProduct";
        }
        if(first_one != null){
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + " ." + first_one.getOriginalFilename();
            first_one.transferTo(new File(uploadPath+"/"+resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }
        if(first_two != null){
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + " ." + first_two.getOriginalFilename();
            first_two.transferTo(new File(uploadPath+"/"+resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }
        if(first_three != null){
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + " ." + first_three.getOriginalFilename();
            first_three.transferTo(new File(uploadPath+"/"+resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }
        productService.saveProduct(product,category_db);
        return "redirect:/admin";

    }

    @GetMapping("/admin")
    public String admin(Model model){
        model.addAttribute("products",productService.getAllProduct());
        model.addAttribute("person",personService.getAllUser());
        return "/admin";
    }

    @GetMapping("/admin/product/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id){
        productService.deleteProduct(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/product/edit/{id}")
    public String editProduct(Model model,@PathVariable("id") int id){
        model.addAttribute("product",productService.getProductId(id));
        model.addAttribute("category", categoryRepository.findAll());
        return "product/editProduct";
    }

    @PostMapping("/admin/product/edit/{id}")
    public String editProduct(@ModelAttribute("product") @Valid Product product, BindingResult bindingResult,@PathVariable("id")int id,Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("category", categoryRepository.findAll());
            return "product/editProduct";
        }
        productService.updateProduct(id,product);
        return "redirect:/admin";
    }


    @GetMapping("/admin/orders")
    public String orderUser(Model model){

        List<Order> orderList = orderRepository.findAll();
        model.addAttribute("orders",orderList);
        return "/orders";
    }


    @PostMapping("/admin/search")
    public String orderSearch(@RequestParam("search") String search,Model model){

        model.addAttribute("search_orders",orderRepository.findByNumberEndingWith(search.toLowerCase()));

        List<Order> orderList = orderRepository.findAll();

        model.addAttribute("order",orderList);
        model.addAttribute("value_search" , search);
        return "/orders";

    }





    @GetMapping("/admin/person")
    public String admin1(Model model){
        List<Order> orderList = orderRepository.findAll();

        model.addAttribute("order",orderList);
        model.addAttribute("person",personService.getAllUser());
        return "/person";
    }



}
