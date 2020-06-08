package example.rtweb.ctrl;

import example.rtweb.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ModuleCtrl {

    @Autowired
    private MetadataService metadataService;

    @GetMapping("/module/{id}")
    public String show(Model model, @PathVariable Integer id) {
        var metadata = metadataService.get();
        var module = metadata.getModules().get(id);
        model.addAttribute("metadata", metadata);
        model.addAttribute("module", module);
        model.addAttribute("moduleId", id);
        return "module";
    }

    @GetMapping("/module/{moduleId}/entry/{id}")
    public String entry(Model model, @PathVariable Integer moduleId, @PathVariable Integer id) {
        var metadata = metadataService.get();
        var module = metadata.getModules().get(moduleId);
        var entry = module.getStorage().getEntries().get(id);
        model.addAttribute("metadata", metadata);
        model.addAttribute("module", module);
        model.addAttribute("moduleId", moduleId);
        model.addAttribute("entry", entry);
        model.addAttribute("entryId", id);
        return "entry";
    }

    @GetMapping("/module/{moduleId}/call/{id}")
    public String call(Model model, @PathVariable Integer moduleId, @PathVariable Integer id) {
        var metadata = metadataService.get();
        var module = metadata.getModules().get(moduleId);
        var call = module.getCalls().get(id);
        model.addAttribute("metadata", metadata);
        model.addAttribute("module", module);
        model.addAttribute("moduleId", moduleId);
        model.addAttribute("call", call);
        model.addAttribute("callId", id);
        return "call";
    }

    @GetMapping("/module/{moduleId}/event/{id}")
    public String event(Model model, @PathVariable Integer moduleId, @PathVariable Integer id) {
        var metadata = metadataService.get();
        var module = metadata.getModules().get(moduleId);
        var event = module.getEvents().get(id);
        model.addAttribute("metadata", metadata);
        model.addAttribute("module", module);
        model.addAttribute("moduleId", moduleId);
        model.addAttribute("event", event);
        model.addAttribute("eventId", id);
        return "event";
    }

    @GetMapping("/module/{moduleId}/error/{id}")
    public String error(Model model, @PathVariable Integer moduleId, @PathVariable Integer id) {
        var metadata = metadataService.get();
        var module = metadata.getModules().get(moduleId);
        var error = module.getErrors().get(id);
        model.addAttribute("metadata", metadata);
        model.addAttribute("module", module);
        model.addAttribute("moduleId", moduleId);
        model.addAttribute("error", error);
        model.addAttribute("errorId", id);
        return "error";
    }

    @GetMapping("/module/{moduleId}/constant/{id}")
    public String constant(Model model, @PathVariable Integer moduleId, @PathVariable Integer id) {
        var metadata = metadataService.get();
        var module = metadata.getModules().get(moduleId);
        var constant = module.getConstants().get(id);
        model.addAttribute("metadata", metadata);
        model.addAttribute("module", module);
        model.addAttribute("moduleId", moduleId);
        model.addAttribute("constant", constant);
        model.addAttribute("constantId", id);
        return "constant";
    }
}
