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

        // 관리자인 경우 관리자 전용 페이지로 리다이렉트
        if (user.getIdType() == 1) {
            return "redirect:/board/admin";
        }

        // 일반 사용자 게시판 목록
        if (user.getIdType() == 0) {
            model.addAttribute("boardList", boardService.getBoardList());
            return "board/list";
        }

        // 예외적인 경우 권한 없음 페이지로 연결
        return "error/unauthorized";
    }

    @GetMapping("/admin")
    public String getAdminBoard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        if (user == null) {
            return "redirect:/login";
        }

        // 관리자인 경우만 접근 허용
        if (user.getIdType() == 1) {
            model.addAttribute("boardList", boardService.getBoardList()); // 관리자도 게시판 목록을 볼 수 있음
            return "board/admin";
        }

        // 일반 사용자는 접근 불가
        return "error/unauthorized";
    }

    @GetMapping("/{id}")
    public String getBoardDetail(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        if (user == null) {
            return "redirect:/login";
        }

        // 일반 사용자와 관리자는 게시글 상세 보기 허용
        if (user.getIdType() == 0 || user.getIdType() == 1) {
            model.addAttribute("board", boardService.getBoardById(id));
            return "board/detail";
        }

        // 예외적인 경우 권한 없음 페이지로 연결
        return "error/unauthorized";
    }

    @GetMapping("/form")
    public String createForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        if (user == null) {
            return "redirect:/login";
        }

        // 일반 사용자와 관리자는 글 작성 가능
        if (user.getIdType() == 0 || user.getIdType() == 1) {
            model.addAttribute("board", new BoardDto());
            return "board/form";
        }

        // 예외적인 경우 권한 없음 페이지로 연결
        return "error/unauthorized";
    }

    @PostMapping
    public String saveBoard(@ModelAttribute BoardDto boardDto, HttpSession session) {
        User user = (User) session.getAttribute("user");

        // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        if (user == null) {
            return "redirect:/login";
        }

        // 일반 사용자와 관리자는 게시글 저장 가능
        if (user.getIdType() == 0 || user.getIdType() == 1) {
            boardService.createBoard(boardDto);
            return "redirect:/board";
        }

        // 예외적인 경우 권한 없음 페이지로 연결
        return "error/unauthorized";
    }
}
