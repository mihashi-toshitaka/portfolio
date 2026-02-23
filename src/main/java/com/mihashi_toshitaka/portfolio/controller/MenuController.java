package com.mihashi_toshitaka.portfolio.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** メニュー画面コントローラー。認証状態に関わらずアクセス可能。 */
@Controller
@RequestMapping("/menu")
public class MenuController {

  private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

  /**
   * メニュー画面を表示する。
   *
   * @return メニューテンプレート名
   */
  @GetMapping("")
  public String menu() {
    logger.debug("menu accessed");
    return "menu";
  }
}
