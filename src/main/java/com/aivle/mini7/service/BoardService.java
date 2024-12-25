package com.aivle.mini7.service;

import com.aivle.mini7.dto.BoardDto;
import com.aivle.mini7.model.Board;
import com.aivle.mini7.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardDto createBoard(BoardDto boardDto) {
        Board board = toEntity(boardDto);
        board.setCreated_time(getCurrentTime());
        return new BoardDto(boardRepository.save(board));
    }

    public List<BoardDto> getBoardList() {
        return boardRepository.findAll().stream()
                .map(BoardDto::new)
                .collect(Collectors.toList());
    }

    public BoardDto getBoardById(Long board_id) {
        return boardRepository.findById(board_id)
                .map(BoardDto::new)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    private Board toEntity(BoardDto dto) {
        return Board.builder()
                .author_id(dto.getAuthor_id())
                .title(dto.getTitle())
                .content(dto.getContent())
                .post_type(dto.getPost_type())
                .build();
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
