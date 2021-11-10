package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.ArticleService;
import ar.edu.itba.paw.interfaces.service.RentService;
import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.forms.AccountForm;
import ar.edu.itba.paw.webapp.forms.EditAccountForm;
import ar.edu.itba.paw.webapp.forms.PasswordForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RentService rentService;

    @Autowired
    private LoggedUserAdvice userAdvice;

    @Autowired
    private AuthenticationManager authenticationManager;

    private final Logger userLogger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private ServletContext servletContext;

    private List<Locations> getLocationsOrdered() {
        return Arrays.stream(Locations.values())
                .sorted(Comparator.comparing(Locations::getName))
                .collect(Collectors.toList());
    }

    @RequestMapping("/register")
    public ModelAndView register(@ModelAttribute("accountForm") AccountForm accountForm) {
        final ModelAndView mav = new ModelAndView("account/create");
        mav.addObject("locations", getLocationsOrdered());

        return mav;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView register(HttpServletRequest request,
                                 @Valid @ModelAttribute("accountForm") AccountForm accountForm,
                                 BindingResult errors) {
        if (errors.hasErrors())
            return register(accountForm);

        userLogger.info("Registering new user --> email: {}, location: {}, type: {}",
                accountForm.getEmail(), accountForm.getLocation(), accountForm.getIsOwner() ? UserType.OWNER : UserType.RENTER);

        String webpageUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().scheme("http").replacePath(null).build().toUriString() + servletContext.getContextPath();
        userService.register(accountForm.getEmail(), accountForm.getPassword()
                , accountForm.getFirstName(), accountForm.getLastName(), accountForm.getLocation(),
                accountForm.getImg(), accountForm.getIsOwner() ? UserType.OWNER : UserType.RENTER, webpageUrl
        );

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(accountForm.getEmail(), accountForm.getPassword());
        authToken.setDetails(new WebAuthenticationDetails(request));

        Authentication authentication = authenticationManager.authenticate(authToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new ModelAndView("redirect:/");
    }

    @RequestMapping("/login")
    public ModelAndView login(@RequestParam(value = "error", defaultValue = "false") boolean loginError) {
        ModelAndView mv = new ModelAndView("account/login");

        if (loginError) {
            mv.addObject("loginError", true);
        }
        userLogger.info("User login");
        return mv;
    }

    @RequestMapping("logout")
    public ModelAndView logout() {
        userLogger.info("User logout");
        return new ModelAndView();
    }

    @RequestMapping("/edit")
    public ModelAndView edit(@ModelAttribute("accountForm") EditAccountForm accountForm) {
        final ModelAndView mav = new ModelAndView("account/edit");
        populateForm(accountForm);
        mav.addObject("showPanel", false);
        mav.addObject("locations", getLocationsOrdered());

        return mav;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ModelAndView edit(@Valid @ModelAttribute("accountForm") EditAccountForm accountForm, BindingResult errors) {
        if (errors.hasErrors()) {
            return edit(accountForm);
        }

        userLogger.info("Editing user --> name: {}, lastName: {}, location: {}",
                accountForm.getFirstName(), accountForm.getLastName(), accountForm.getLocation());

        userService.update(userAdvice.loggedUser().getId(),
                accountForm.getFirstName(),
                accountForm.getLastName(),
                accountForm.getLocation()
        );

        return new ModelAndView("redirect:/user/view");
    }


    @RequestMapping("/view")
    public ModelAndView view(@ModelAttribute("accountForm") AccountForm accountForm,
                             @RequestParam(value = "page", required = false, defaultValue = "1") long page) {
        final ModelAndView mav = new ModelAndView("account/view");
        mav.addObject("ownedArticles", articleService.get(null, null,
                null, userAdvice.loggedUser().getId(), null, null, null, page));
        mav.addObject("ownedMaxPage", articleService.getMaxPage(null,
                null, userAdvice.loggedUser().getId(), null, null, null));

        mav.addObject("rentedArticles", articleService.rentedArticles(userAdvice.loggedUser().getId(), page));
        mav.addObject("rentedMaxPage", articleService.getRentedMaxPage(userAdvice.loggedUser().getId()));
        mav.addObject("locations", getLocationsOrdered());

        populateForm(accountForm);

        return mav;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ModelAndView delete() {
        userLogger.info("deleting account --> id: {}, email: {}", userAdvice.loggedUser().getId(), userAdvice.loggedUser().getEmail());
        userService.delete(userAdvice.loggedUser().getId());
        return new ModelAndView("redirect:/user/logout");
    }

    private void populateForm(EditAccountForm accountForm) {
        User user = userAdvice.loggedUser();
        accountForm.setFirstName(user.getFirstName());
        accountForm.setLastName(user.getLastName());
        accountForm.setLocation((long) user.getLocation().ordinal());
    }

    private void populateForm(AccountForm accountForm) {
        User user = userAdvice.loggedUser();
        accountForm.setFirstName(user.getFirstName());
        accountForm.setLastName(user.getLastName());
        accountForm.setEmail(user.getEmail());
        accountForm.setIsOwner(user.getType() == UserType.OWNER);
        accountForm.setLocation((long) user.getLocation().ordinal());
    }

    @RequestMapping("/my-requests/received/accepted")
    public ModelAndView acceptedReceivedRequests(@RequestParam(value = "page", required = false, defaultValue = "1") long page) {
        return getRentRequests(userAdvice.loggedUser(), RentState.ACCEPTED, page, true);
    }

    @RequestMapping("/my-requests/received/pending")
    public ModelAndView pendingReceivedRequests(@RequestParam(value = "page", required = false, defaultValue = "1") long page) {
        return getRentRequests(userAdvice.loggedUser(), RentState.PENDING, page, true);
    }

    @RequestMapping("/my-requests/received/declined")
    public ModelAndView declinedReceivedRequests(@RequestParam(value = "page", required = false, defaultValue = "1") long page) {
        return getRentRequests(userAdvice.loggedUser(), RentState.DECLINED, page, true);
    }

    @RequestMapping("/my-requests/sent/accepted")
    public ModelAndView acceptedSentRequests(@RequestParam(value = "page", required = false, defaultValue = "1") long page) {
        return getRentRequests(userAdvice.loggedUser(), RentState.ACCEPTED, page, false);
    }

    @RequestMapping("/my-requests/sent/pending")
    public ModelAndView pendingSentRequests(@RequestParam(value = "page", required = false, defaultValue = "1") long page) {
        return getRentRequests(userAdvice.loggedUser(), RentState.PENDING, page, false);
    }

    @RequestMapping("/my-requests/sent/declined")
    public ModelAndView declinedSentRequests(@RequestParam(value = "page", required = false, defaultValue = "1") long page) {
        return getRentRequests(userAdvice.loggedUser(), RentState.DECLINED, page, false);
    }


    @RequestMapping(value = "/my-requests/{requestId}/accept", method = RequestMethod.POST)
    @PreAuthorize("@webSecurity.checkIsRentOwner(authentication, #requestId)")
    public ModelAndView acceptRequest(@PathVariable("requestId") long requestId) {
        String webpageUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().scheme("http").replacePath(null).build().toUriString() + servletContext.getContextPath();
        userLogger.info("accepting request with id {}", requestId);
        rentService.acceptRequest(requestId, webpageUrl);
        return new ModelAndView("redirect:/user/my-requests/received/accepted");
    }

    @RequestMapping(value = "/my-requests/{requestId}/delete", method = RequestMethod.POST)
    @PreAuthorize("@webSecurity.checkIsRentOwner(authentication, #requestId)")
    public ModelAndView rejectRequest(@PathVariable("requestId") long requestId) {
        String webpageUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().scheme("http").replacePath(null).build().toUriString() + servletContext.getContextPath();
        userLogger.info("rejecting request with id {}", requestId);
        rentService.rejectRequest(requestId, webpageUrl);
        return new ModelAndView("redirect:/user/my-requests/received/declined");
    }

    private ModelAndView getRentRequests(User user, RentState state, long page, boolean isReceived) {
        final ModelAndView mav = new ModelAndView("account/myRequests");

        List<RentProposal> proposals;
        long maxPage;
        if (isReceived) {
            proposals = rentService.ownerRequests(user.getId(), state, page);
            maxPage = rentService.getReceivedMaxPage(user.getId(), state);
        } else {
            proposals = rentService.sentRequests(user.getId(), state, page);
            maxPage = rentService.getSentMaxPage(user.getId(), state);
        }

        mav.addObject("state", state);
        mav.addObject("isReceived", isReceived);
        mav.addObject("proposals", proposals);
        mav.addObject("maxPage", maxPage);
        return mav;
    }

    @RequestMapping(value = "/updatePassword", method = RequestMethod.GET)
    public ModelAndView updatePassword(@ModelAttribute(value = "passwordForm") PasswordForm passwordForm) {
        return new ModelAndView("account/updatePassword");
    }

    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    public ModelAndView updatePassword(@Valid @ModelAttribute(value = "passwordForm") PasswordForm passwordForm, BindingResult errors) {
        ModelAndView mv = new ModelAndView("account/updatePassword");

        userLogger.info("updating password");
        if (errors.hasErrors()) {
            mv.addObject("showPanel", false);
            return mv;
        }
        mv.addObject("showPanel", true);

        userService.updatePassword(userAdvice.loggedUser().getId(), passwordForm.getPassword());
        return new ModelAndView("redirect:/user/view");
    }
}