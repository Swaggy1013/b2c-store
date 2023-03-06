package com.hxf.carousel.controller;

import com.hxf.carousel.service.CarouselService;
import com.hxf.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carousel")
public class CarouselController {

    @Autowired
    private CarouselService carouselService;

    @RequestMapping("/list")
    public R list(){
        return carouselService.list();
    }
}
