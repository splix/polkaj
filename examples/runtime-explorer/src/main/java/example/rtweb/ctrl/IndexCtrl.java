package example.rtweb.ctrl;

import example.rtweb.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexCtrl {

    @Autowired
    private MetadataService metadataService;

    @GetMapping
    public String show(Model model) {
        model.addAttribute("metadata", metadataService.get());
        return "index";
    }

}
