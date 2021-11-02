package org.mvc.studentInit.controllers;

import org.mvc.studentInit.model.Bid;
import org.mvc.studentInit.model.BidStatus;
import org.mvc.studentInit.model.User;
import org.mvc.studentInit.services.VotingService;
import org.mvc.studentInit.services.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;


@Controller
@RequestMapping("users/userPage")
public class UserController {
    private final VotingService votingService;
    private final BidService bidService;


    @Autowired
    public UserController(VotingService votingService, BidService bidService) {
        this.votingService = votingService;
        this.bidService = bidService;
    }


    @PostMapping("/studVote")
    public String studentVote(@AuthenticationPrincipal User user, @RequestParam Bid bid,
                              @RequestParam boolean vote) {
        votingService.voting(user, bid, BidStatus.Voting_expert, vote);
        return "redirect:/users/userPage";
    }

    @GetMapping("/addNewBid")
    public String addNew(Model model, @AuthenticationPrincipal User user) {
        bidService.setUserToNewBid(model, user);
        return "bids/addNewBid";
    }

    @GetMapping()
    public String main(Model model, @AuthenticationPrincipal User user) {
        bidService.createDefaultParams(model, user);
        bidService.replaceEverything(model);
        return "users/userPage";
    }

    @PostMapping("/addNewBid")
    public String addBid(@AuthenticationPrincipal User user, @Valid Bid bid,
                         BindingResult bindingResult, Model model,
                         @RequestParam(value = "file") MultipartFile file) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("bid", bid);
            return "bids/addNewBid";
        }

        bidService.addNewBid(user, bid, model, file);
        return "redirect:/users/userPage";
    }

    @GetMapping("/getFile/{fileName}")
    public void getFile(@PathVariable String fileName, HttpServletResponse response){
        bidService.getFile(fileName, response);
    }

    @PostMapping("/filterText")
    public String filterText(@RequestParam String filterText,
                             Model model, @AuthenticationPrincipal User user) {
        bidService.searchByText(filterText, model, Arrays.asList(BidStatus.values().clone()));
        bidService.createDefaultParams(model, user);
        return "users/userPage";
    }

    @PostMapping("/filterName")
    public String filterName(
            @RequestParam String filterName,
            @RequestParam String filterSurname,
            Model model, @AuthenticationPrincipal User user) {
        bidService.searchByName(filterName, filterSurname, model, Arrays.asList(BidStatus.values().clone()));
        bidService.createDefaultParams(model, user);
        return "/users/userPage";
    }

    @PostMapping("/allBidsByName")
    public String allBidsByName(
            @AuthenticationPrincipal User user,
            Model model) {
        bidService.allBidsByName(model, user);
        bidService.createDefaultParams(model, user);
        return "users/userPage";
    }

    @GetMapping("/{bid}")
    public String bidEditForm(@PathVariable Bid bid, Model model, @AuthenticationPrincipal User user) {
        bidService.editForm(model, bid, user);
        return "bids/bidEdit";
    }

    @PostMapping("/studVotes")
    public String replaceAvailableVotes(Model model, @AuthenticationPrincipal User user) {
        bidService.replaceBidsByStatus(model, BidStatus.Voting_stud);
        model.addAttribute("message", "Доступные голосования");
        bidService.createDefaultParams(model, user);
        return "users/userPage";
    }


}
