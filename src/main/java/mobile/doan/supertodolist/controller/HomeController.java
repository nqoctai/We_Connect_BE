package mobile.doan.supertodolist.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class HomeController {

    @GetMapping("/")
    public String HomeController() {
        return "Hello Đặng Ngọc Tài";
    }

}
