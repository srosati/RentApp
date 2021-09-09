package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.ArticleNotFoundException;
import ar.edu.itba.paw.exceptions.CannotCreateArticleException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.interfaces.ArticleService;
import ar.edu.itba.paw.interfaces.RentService;
import ar.edu.itba.paw.interfaces.UserService;
import ar.edu.itba.paw.models.Article;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.forms.CreateArticleForm;
import ar.edu.itba.paw.webapp.forms.RentProposalForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class ArticleController {
    @Autowired
    ArticleService articleService;

    @Autowired
    UserService userService;

    @Autowired
    RentService rentService;

    @RequestMapping("/")
    public ModelAndView marketplace(@RequestParam(value = "name", required = false) String name) {
        final ModelAndView mav = new ModelAndView("marketplace");
        List<Article> articles = articleService.get(name);
        mav.addObject("articles", articles);
        return mav;
    }

    @RequestMapping(value = "/article/{articleId}", method = RequestMethod.GET)
    public ModelAndView viewArticle(@ModelAttribute("rentForm") RentProposalForm rentForm, @PathVariable("articleId") Integer articleId) {
        final ModelAndView mav = new ModelAndView("article");
        Article article = articleService.findById(articleId).orElseThrow(ArticleNotFoundException::new);
        mav.addObject("article", article);
        User owner = userService.findById(article.getIdOwner()).orElseThrow(UserNotFoundException::new);
        mav.addObject("owner", owner);
        return mav;
    }

    @RequestMapping(value = "/article/{articleId}", method = RequestMethod.POST)
    public ModelAndView createProposal(@Valid @ModelAttribute("rentForm") RentProposalForm rentForm,
                                       @PathVariable Integer articleId, BindingResult errors) {
        if (errors.hasErrors())
            return viewArticle(rentForm, articleId);


        rentService.create(rentForm.getMessage(), false, rentForm.getStartDate(), rentForm.getEndDate(), articleId, 1);

        return new ModelAndView("feedback");
    }

    @RequestMapping("/create-article")
    public ModelAndView viewCreateArticleForm(@ModelAttribute("createArticleForm") CreateArticleForm createArticleForm) {
        return new ModelAndView("create-article");
    }

    @RequestMapping(value = "/create-article", method = RequestMethod.POST)
    public ModelAndView createArticle(@Valid @ModelAttribute("createArticleForm") CreateArticleForm createArticleForm,
                                      BindingResult errors) {

        if (errors.hasErrors())
            return viewCreateArticleForm(createArticleForm);

        Article article = articleService.create(createArticleForm.getName(), createArticleForm.getDescription(),
                createArticleForm.getPricePerDay(), 1).orElseThrow(CannotCreateArticleException::new); //TODO: Harcodeado el OwnerId

        return marketplace(article.getTitle());
    }
}
