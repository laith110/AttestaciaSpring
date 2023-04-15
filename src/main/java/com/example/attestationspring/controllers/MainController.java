package com.example.attestationspring.controllers;


import com.example.attestationspring.enumm.Status;
import com.example.attestationspring.models.Cart;
import com.example.attestationspring.models.Order;
import com.example.attestationspring.models.Person;
import com.example.attestationspring.models.Product;
import com.example.attestationspring.repositories.CartRepository;
import com.example.attestationspring.repositories.OrderRepository;
import com.example.attestationspring.repositories.ProductRepository;
import com.example.attestationspring.security.PersonDetails;
import com.example.attestationspring.services.ProductService;
import com.example.attestationspring.services.PersonService;
import com.example.attestationspring.util.PersonValidator;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class MainController {

    private final ProductRepository productRepository;

    private final ProductService productService;

    private final PersonService personService;

    private final PersonValidator personValidator;

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    public MainController(ProductRepository productRepository, ProductService productService, PersonService personService, PersonValidator personValidator, CartRepository cartRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.productService = productService;
        this.personService = personService;
        this.personValidator = personValidator;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/personal_account")
    public String index(Model model){
        //Получаем объект аутентификации, с помощью SecurityContextHolder обращаемся к контексту и на нем вызываем метод аутентификации. Из сессии текущего пользователя получаем объект, который был положен в данную сессию послу аутентификации пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        String role = personDetails.getPerson().getRole();
        if(role.equals("ROLE_ADMIN")){
            return "redirect:/admin";
        }
//        System.out.println(personDetails.getPerson());
//        System.out.println("ID пользователя: " + personDetails.getPerson().getId());
//        System.out.println("Логин пользователя: " + personDetails.getPerson().getLogin());
//        System.out.println("Пароль пользователя: " + personDetails.getPerson().getPassword());
        model.addAttribute("products",productService.getAllProduct());


    return "/user/index";
    }


    @GetMapping("/registration")
    public String registration(@ModelAttribute("person") Person person){
        return "/registration";
    }

    @PostMapping("/registration")
    public String resultRegistration(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult){
        personValidator.validate(person,bindingResult);
        if(bindingResult.hasErrors()){
            return  "registration";
        }
        personService.register(person);
        return "redirect:/personal_account";
    }



    @GetMapping("/personal_account/product/info/{id}")
    public String infoProduct(@PathVariable("id") int id, Model model){
        model.addAttribute("product",productService.getProductId(id));
        return "/user/infoProduct";

    }
    @PostMapping("/person_account/product/search")
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
        return "/product/product";
    }

    @GetMapping("/cart/add/{id}")
    public String addProductInCart(@PathVariable("id") int id,Model model){
        //Получаем продукт по id
        Product product = productService.getProductId(id);
        //Извлекаем аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        //Извлекаем id пользователя из объекта
        int id_person = personDetails.getPerson().getId();
        Cart cart = new Cart(id_person, product.getId());
        cartRepository.save(cart);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String cart(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        //Извлекаем id пользователя из объекта
        int id_person = personDetails.getPerson().getId();

        List<Cart> cartList = cartRepository.findByPersonId(id_person);
        List<Product> productList =new ArrayList<>();

        //Получаем товары из корзины по id
        for(Cart cart: cartList){
            productList.add(productService.getProductId(cart.getProductId()));
        }

        //Вычесление итоговой цены
        float price=0;
        for(Product product: productList){
            price += product.getPrice();
        }

        model.addAttribute("price",price);
        model.addAttribute("cart_product",productList);
        return "/user/cart";
    }

    @GetMapping("/cart/delete/{id}")
    public String deleteProductFromCart(Model model, @PathVariable("id") int id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        //Извлекаем id пользователя из объекта
        int id_person = personDetails.getPerson().getId();
        List<Cart> cartList = cartRepository.findByPersonId(id_person);
        List<Product> productList =new ArrayList<>();

        //Получаем товары из корзины по id
        for(Cart cart: cartList){
            productList.add(productService.getProductId(cart.getProductId()));
        }
        cartRepository.deleteCartByProductId(id);
        return "redirect:/cart";


    }

    @GetMapping("/order/create")
    public String order(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        //Извлекаем id пользователя из объекта
        int id_person = personDetails.getPerson().getId();

        List<Cart> cartList = cartRepository.findByPersonId(id_person);
        List<Product> productList =new ArrayList<>();

        //Получаем товары из корзины по id
        for(Cart cart: cartList){
            productList.add(productService.getProductId(cart.getProductId()));
        }

        //Вычесление итоговой цены
        float price=0;
        for(Product product: productList){
            price += product.getPrice();
        }

        String uuid = UUID.randomUUID().toString();
        for(Product product : productList){
            Order newOrder = new Order(uuid,product,personDetails.getPerson(),1,product.getPrice(), Status.Ожидает);
            orderRepository.save(newOrder);
            cartRepository.deleteCartByProductId(product.getId());
        }
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orderUser(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        List<Order> orderList = orderRepository.findByPerson(personDetails.getPerson());
        model.addAttribute("orders",orderList);
        return "/user/orders";
    }



}
