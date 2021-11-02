package org.mvc.studentInit.services;

import org.mvc.studentInit.model.Bid;
import org.mvc.studentInit.model.Comment;
import org.mvc.studentInit.model.Role;
import org.mvc.studentInit.model.User;
import org.mvc.studentInit.repositorys.CommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Date;

@Service
public class CommentsService {
    private final CommentsRepository commentsRepository;

    @Autowired
    public CommentsService(CommentsRepository commentsRepository) {
        this.commentsRepository = commentsRepository;
    }

    public void createNewComment(Model model, Bid bid, User user){
        Comment comment = new Comment();
        comment.setBid(bid);
        model.addAttribute("bid",bid);
        getAttributes(model, user, comment, bid);
    }

    public void getAttributes(Model model, User user, Comment comment, Bid bid) {
        model.addAttribute("newComment", comment);
        model.addAttribute("comments", commentsRepository.getCommentSortedByMessageDate(bid));
        model.addAttribute("user", user);
        model.addAttribute("userRoles", user.getRoles());
        model.addAttribute("super", Role.SUPER_USER);
        model.addAttribute("moder", Role.MODERATOR);

    }

    public void delete(Comment comment){
        commentsRepository.delete(comment);
    }

    public void send(Comment newComment, User user, Bid bid){
        newComment.setBid(bid);
        newComment.setUser(user);
        newComment.setMessageDate(new Date());
        commentsRepository.save(newComment);


    }
}
