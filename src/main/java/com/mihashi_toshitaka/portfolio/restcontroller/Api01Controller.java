package com.mihashi_toshitaka.portfolio.restcontroller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/01")
public class Api01Controller {

  @GetMapping("")
  public String login() {
    return "Api01";
  }
}
