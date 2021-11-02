package org.mvc.studentInit.services;

import org.mvc.studentInit.model.Bid;
import org.mvc.studentInit.model.BidStatus;
import org.mvc.studentInit.model.Role;
import org.mvc.studentInit.model.User;
import org.mvc.studentInit.repositorys.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@Service
public class BidService {
    private final BidRepository bidRepository;
    private final String UPLOAD_PATH;
    private final ServletContext context;

    @Autowired
    public BidService(ServletContext context, BidRepository bidRepository) {
        this.bidRepository = bidRepository;
        this.context = context;
        UPLOAD_PATH = context.getRealPath("") + "uploadedFiles" + File.separator;
    }

    public void setUserToNewBid(Model model, User user) {
        Bid bid = new Bid();
        bid.setAuthor(user);
        model.addAttribute("bid", bid);

    }

    public void addNewBid(User user, Bid bid,
                          Model model, MultipartFile file) {
        bid.setAuthor(user);

        if (!file.isEmpty()) {
            try {
                FileCopyUtils.copy(file.getBytes(), new File(UPLOAD_PATH + file.getOriginalFilename()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String fileName = file.getOriginalFilename();
            bid.setFileName(fileName);
        }
        bid.setStatus(BidStatus.New);
        bidRepository.save(bid);

        model.addAttribute("bids", bidRepository.findAll());
        createDefaultParams(model, user);

    }

    public void getFile(String fileName, HttpServletResponse response) {
        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.context, fileName);
        File file = new File(UPLOAD_PATH + fileName);
        response.setContentType(mediaType.getType());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName());
        response.setContentLength((int) file.length());
        BufferedInputStream inStream;
        try {
            inStream = new BufferedInputStream(new FileInputStream(file));
            BufferedOutputStream outStream = new BufferedOutputStream(response.getOutputStream());
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            outStream.flush();
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }


    public void allBidsByName(Model model, User user) {
        model.addAttribute("message", "Все Ваши заявки");
        model.addAttribute("bids", bidRepository.findBidByAuthor(user));
    }

    public void replaceBidsByStatus(Model model, BidStatus bidStatus) {
        model.addAttribute("bids", bidRepository.findByStatus(bidStatus));
    }

    public void replaceEverything(Model model) {
        model.addAttribute("bids", bidRepository.findAll());
        model.addAttribute("message", "Все заявки");
    }

    public void searchByName(String filterName, String filterSurname, Model model, List<BidStatus> statuses) {
        if (filterName != null && !filterName.isBlank() || filterSurname != null && !filterSurname.isBlank()) {
            model.addAttribute("bids", bidRepository.findByNameAndSurnameContains(filterName, filterSurname));
            model.addAttribute("message", "Заявки найденного пользователя");
        } else {
            model.addAttribute("message", "Пользователь не найден");
            for (BidStatus status : statuses) {
                replaceBidsByStatus(model, status);
            }

        }
    }

    public void createDefaultParams(Model model, User user) {
        model.addAttribute("message", "Все текущие заявки и их статус");
        model.addAttribute("user", user);
        model.addAttribute("text", "Перейти в личный кабинет");
        if (user.getRoles().contains(Role.SUPER_USER)) {
            model.addAttribute("page", "/users/superUserPage");
        } else if (user.getRoles().contains(Role.MODERATOR)) {
            model.addAttribute("page", "/bids/bidList");
        } else if (user.getRoles().contains(Role.EXPERT)) {
            model.addAttribute("page", "/users/expertPage");
        } else {
            model.addAttribute("page", "");
            model.addAttribute("text", "");
        }
        model.addAttribute("studGroup", BidStatus.Voting_stud);
    }

    public void searchByText(String filterText, Model model, List<BidStatus> statuses) {
        model.addAttribute("message", "Заявки по введенному тексту");
        if (filterText != null && !filterText.isBlank()) {
            model.addAttribute("bids", bidRepository.findBidByTextContaining(filterText));
        } else {
            model.addAttribute("message", "Заявка не найдена");
            for (BidStatus status : statuses) {
                replaceBidsByStatus(model, status);
            }
        }
    }

    public void saveBid(Bid bid, MultipartFile file) throws IOException {

        if (!file.isEmpty()) {
            FileCopyUtils.copy(file.getBytes(), new File(UPLOAD_PATH + file.getOriginalFilename()));
            String fileName = file.getOriginalFilename();
            bid.setFileName(fileName);
        }
        bidRepository.save(bid);

    }

    public void delete(Bid bid, Model model, User user) {
        bidRepository.delete(bid);
        model.addAttribute("message", "Заявка была удалена");

    }

    public void editForm(Model model, Bid bid, User user) {
        model.addAttribute("bid2", bid);
        model.addAttribute("userRoles", user.getRoles());
        model.addAttribute("moderator", Role.MODERATOR);
        if (user.getRoles().contains(Role.MODERATOR) || user.getRoles().contains(Role.SUPER_USER)) {
            bid.setStatus(BidStatus.Moderation);
            bidRepository.save(bid);
        }

    }

    public void bidDone(Model model) {
        if (!bidRepository.findByStatus(BidStatus.Working).isEmpty()) {
            replaceBidsByStatus(model, BidStatus.Working);
            model.addAttribute("message", "Заявки ожидающие подтверждения");
            return;
        }
        replaceBidsByStatus(model, BidStatus.New);
        model.addAttribute("message", " Нет заявок, ожидающих подтверждения");
    }

    public void confirmBid(Bid bid) {
        bid.setStatus(BidStatus.Done);
        bidRepository.save(bid);
    }
}

