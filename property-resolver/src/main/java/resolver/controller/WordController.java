package resolver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import resolver.service.WordService;

/**
 * Created by viacheslav on 5/22/17.
 */
@RestController
public class WordController {

    @Autowired (required = false)
    private WordService wordService;

    @RequestMapping("/word")
    @CrossOrigin //CORS annotation allowing AJAX calls from JavaScript side target this endpoint
    public String getWord() {
        if (wordService != null) {
            return wordService.getWord();
        } else {
            return "default";
        }
    }

    @RequestMapping("/map")
    public String getMap() {
        return wordService.getMap().toString();
    }
}
