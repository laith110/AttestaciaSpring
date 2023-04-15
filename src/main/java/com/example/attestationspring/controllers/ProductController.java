package com.example.attestationspring.controllers;

import com.example.attestationspring.repositories.ProductRepository;
import com.example.attestationspring.services.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductRepository productRepository;

    private final ProductService productService;

    public ProductController(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @GetMapping("")
    public String getAllProduct(Model model){
        model.addAttribute("products",productService.getAllProduct());
        return "/product/product";
    }

    @GetMapping("/info/{id}")
    public String infoProduct(@PathVariable("id") int id, Model model){
        model.addAttribute("product",productService.getProductId(id));
        return "/product/infoProduct";

    }

    @PostMapping("/search")
    public String productSearch(@RequestParam("search") String search,@RequestParam("ot")String ot,@RequestParam("do")String Do,@RequestParam(value = "price",required = false,defaultValue = "")String price,@RequestParam(value = "contract",required = false,defaultValue = "")String contract,Model model){
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
}
