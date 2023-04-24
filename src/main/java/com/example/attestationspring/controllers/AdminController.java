package com.example.attestationspring.controllers;
import com.example.attestationspring.enumm.Status;
import com.example.attestationspring.models.*;
import com.example.attestationspring.repositories.*;
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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@Controller
public class AdminController {

    private final PersonRepository personRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
   private  final PersonService personService;
    private final ProductService productService;

    @Value("${upload.path}")
    private String uploadPath;

    List<Status> status = new ArrayList<>(EnumSet.allOf(Status.class));
    private final CategoryRepository categoryRepository;

    public AdminController(OrderRepository orderRepository, CartRepository cartRepository,  PersonRepository personRepository, ProductRepository productRepository, PersonService personService, ProductService productService, CategoryRepository categoryRepository) {
        this.orderRepository = orderRepository;
        this.personRepository = personRepository;
        this.productRepository = productRepository;
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

    @PostMapping("/admin/search/p")
    public String productSearch(@RequestParam("search") String search, @RequestParam("ot")String ot, @RequestParam("do")String Do, @RequestParam(value = "price",required = false,defaultValue = "")String price, @RequestParam(value = "contract",required = false,defaultValue = "")String contract, Model model){
        model.addAttribute("products",productService.getAllProduct());

        if(!ot.isEmpty() & !Do.isEmpty()){
            if(!price.isEmpty()){
                if(price.equals("sorted_by_ascending_price")){
                    if (contract.isEmpty()){
                        if (!contract.equals("tea")){
                            model.addAttribute("search_product",productRepository.findByTitleAndCategoryOrderByPriceAsc(search.toLowerCase(),Float.parseFloat(ot),Float.parseFloat(Do),1));
                        } else if (contract.equals("coffee")) {
                            model.addAttribute("search_product",productRepository.findByTitleAndCategoryOrderByPriceAsc(search.toLowerCase(),Float.parseFloat(ot),Float.parseFloat(Do),2));

                        }else if(contract.equals("cacao")){
                            model.addAttribute("search_product",productRepository.findByTitleAndCategoryOrderByPriceAsc(search.toLowerCase(),Float.parseFloat(ot),Float.parseFloat(Do),3));
                        } else if (contract.equals("healthy_foods")) {
                            model.addAttribute("search_product",productRepository.findByTitleAndCategoryOrderByPriceAsc(search.toLowerCase(),Float.parseFloat(ot),Float.parseFloat(Do),4));
                        }
                    }else {
                        model.addAttribute("search_product",productRepository.findByTitleOrderByPriceAsc(search.toLowerCase(),Float.parseFloat(ot),Float.parseFloat(Do)));
                    }
                }else if(price.equals("sorted_by_descending_price")){
                    if(!contract.isEmpty()){
                        if(contract.equals("tea")){
                            model.addAttribute("search_product",productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(),Float.parseFloat(ot),Float.parseFloat(Do),1));
                        }else if (contract.equals("coffee")) {
                            model.addAttribute("search_product",productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(),Float.parseFloat(ot),Float.parseFloat(Do),2));

                        }else if(contract.equals("cacao")){
                            model.addAttribute("search_product",productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(),Float.parseFloat(ot),Float.parseFloat(Do),3));
                        } else if (contract.equals("healthy_foods")) {
                            model.addAttribute("search_product",productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(),Float.parseFloat(ot),Float.parseFloat(Do),4));
                        }
                    }else {
                        model.addAttribute("search_product",productRepository.findByTitleOrderByPriceDesc(search.toLowerCase(),Float.parseFloat(ot),Float.parseFloat(Do)));
                    }
                }
            }else {
                model.addAttribute("search_product",productRepository.findByTitleAndPriceGreaterThanEqualAndPriceLessThanEqual(search.toLowerCase(),Float.parseFloat(ot),Float.parseFloat(Do)));
            }
        }else {
            model.addAttribute("search_product",productRepository.findByTitleContainingIgnoreCase(search));
        }

        model.addAttribute("value_search",search);
        model.addAttribute("value_price_ot",ot);
        model.addAttribute("value_price_do",Do);
        return "/admin";
    }

    @GetMapping("/admin/search/admin/product/add")
    public String addProductF(Model model){
        model.addAttribute("product",new Product());
        model.addAttribute("category", categoryRepository.findAll());
        return "product/addProduct";
    }

    @GetMapping("/admin/admin/product/add")
    public String addProductZ(Model model){
        model.addAttribute("product",new Product());
        model.addAttribute("category", categoryRepository.findAll());
        return "product/addProduct";
    }

//    @GetMapping("/admin/orders/{id}")
//    public String viewOrdersByPerson(@PathVariable("id") int id, Model model){
//        model.addAttribute("person", personRepository.findAll());
//        model.addAttribute("products", productService.getAllProduct());
//        model.addAttribute("orders", orderRepository.findOrderPersonId(id));
//        model.addAttribute("status", status);
//        return "redirect:/admin/orders";
//    }

//    @PostMapping("/admin/orders/{id}")
//    public String editOrderPerson(@ModelAttribute("orderPerson") @Valid Order order, @PathVariable("id") int id, @RequestParam("status") String status,  Model model){
//        Order order1 = orderPersonRepository.findOrderById(Integer.parseInt(String.valueOf(id)));
//        order1.setStatus(Status.valueOf(status));
//        orderPersonRepository.save(order1);
//        return "redirect:/admin/orders/{id}";
//    }

    @GetMapping("/admin/admin/view_orders/admin/delete_order/{id}")
    public String deletePersonOrderById1(@PathVariable("id") int id, Model model){
        orderRepository.deleteById(id);

        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/edit_person/{id}")
    public String changeRoleById(@PathVariable("id") int id, Model model){
        Person person = personService.getPersonId(id);
        String role_person = person.getRole();
        if (!person.getLogin().equals("admin")) {
            if (!role_person.equals("ROLE_USER")) {
                role_person = "ROLE_USER";
            } else {
                role_person = "ROLE_ADMIN";
            }
        }
        person.setRole(role_person);
        personRepository.save(person);
        model.addAttribute("person");
        return "redirect:/admin/person";
    }
    @GetMapping("/admin/product/edit/admin/product/add")
    public String addProductE(Model model){
        model.addAttribute("product",new Product());
        model.addAttribute("category", categoryRepository.findAll());
        return "product/addProduct";
    }
}
