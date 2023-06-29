package chat.twenty.controller;

import chat.twenty.auth.PrincipalDetails;
import chat.twenty.controller.form.RoomAddForm;
import chat.twenty.domain.ChatRoom;
import chat.twenty.domain.User;
import chat.twenty.enums.ChatRoomType;
import chat.twenty.service.lower.ChatRoomService;
import chat.twenty.service.lower.RoomMemberService;
import chat.twenty.service.lower.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class IndexController {
    private final ChatRoomService roomService;
    private final UserService userService;
    private final RoomMemberService memberService;

    @RequestMapping("/")
    public String index(Model model,
                        @RequestParam(value = "needLogin", required = false) boolean needLogin) {

        model.addAttribute("roomList", roomService.findAll());
        model.addAttribute("needLogin", needLogin);
        RoomAddForm initRoomAddForm = new RoomAddForm();
        initRoomAddForm.setRoomType(ChatRoomType.CHAT);
        model.addAttribute("roomAddForm", initRoomAddForm);
        return "index";
    }

    @PostMapping("/room")
    public String createRoom(@Validated @ModelAttribute RoomAddForm roomAddForm,
                             BindingResult bindingResult, Model model,
                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        User currentUser = principalDetails.getUser();
        log.info("chatroom = {}, currentUser = {}", roomAddForm, currentUser);

        if (bindingResult.hasErrors()) {
            log.info("bindingResult = {}", bindingResult);
            model.addAttribute("roomList", roomService.findAll());
            model.addAttribute("isCreateRoomError", true);
            return "index";
        }

        // roomAddForm -> ChatRoom
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(roomAddForm.getName());
        chatRoom.setType(roomAddForm.getRoomType());
        chatRoom.setSubject(roomAddForm.getSubject());
        chatRoom.setCustomSubject(roomAddForm.getCustomSubject());

        // 방 작성
        ChatRoom savedRoom = roomService.save(chatRoom);

        // 방 멤버이자 오너로 입장
        memberService.enterRoomByOwner(savedRoom.getId(), currentUser.getId());

        return "redirect:/";
    }

    @GetMapping("/room/{roomId}")
    public String enterRoom(@PathVariable Long roomId, Model model,
                            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        // room 정보 불러오기
        ChatRoom currentRoom = roomService.findById(roomId);
        model.addAttribute("chatRoom", currentRoom);
        model.addAttribute("currentMember", memberService.findById(roomId, principalDetails.getId()));
        ChatRoomType currentRoomType = currentRoom.getType();

        log.info("roomId = {}, roomType = {}", roomId, currentRoomType);

        // 입장 처리(service.enterRoom())는 EventListener 에서

        if (currentRoomType == ChatRoomType.CHAT) {
            return "room/chatRoom";
        } else if (currentRoomType == ChatRoomType.TWENTY_GAME) {
            return "room/twentyGameRoom";
        } else {
            log.info("enterRoom error, roomId = {}, currentRoomType = {}", roomId, currentRoomType);
            return "index";
        }
    }

}
