package com.mihashi_toshitaka.portfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/azure")
public class AzureController {

  @GetMapping("")
  public String error() {
    return "azure-error";
  }
}
