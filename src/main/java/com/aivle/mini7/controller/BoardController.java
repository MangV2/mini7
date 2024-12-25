package com.aivle.mini7.controller;

import com.aivle.mini7.dto.BoardDto;
import com.aivle.mini7.model.User;
import com.aivle.mini7.service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public String getBoardList(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        if (user == null) {
            return "redirect:/login";
        }

        // 사용자 유형(관리자 또는 일반 사용자)을 전달
        model.addAttribute("userType", user.getIdType());
        model.addAttribute("boardList", boardService.getBoardList());
        return "board/list";
    }

    @GetMapping("/{id}")
    public String getBoardDetail(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        if (user == null) {
            return "redirect:/login";
        }

        // 게시글 상세 보기
        model.addAttribute("board", boardService.getBoardById(id));
        return "board/detail";
    }

    @GetMapping("/form")
    public String createForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        if (user == null) {
            return "redirect:/login";
        }

        // 사용자 유형을 전달 (관리자 여부 확인용)
        model.addAttribute("userType", user.getIdType());
        model.addAttribute("board", new BoardDto());
        return "board/form";
    }

    @PostMapping
    public String saveBoard(@ModelAttribute BoardDto boardDto, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        if (user == null) {
            return "redirect:/login";
        }

        // 공지사항 작성 권한 제한
        if ("공지사항".equals(boardDto.getPost_type()) && user.getIdType() != 1) {
            model.addAttribute("error", "관리자만 공지사항을 작성할 수 있습니다.");
            return "board/form";
        }

        boardService.createBoard(boardDto);
        return "redirect:/board";
    }
}
