package com.ll.exam.sbb.question;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/question")
@Controller
@RequiredArgsConstructor
// 생성자 주입 -> 이제 final이 붙은 객체들은 모두 autowired가 된다.

// 지시 방향(일방적이고 폐쇄적이다)
// controller -> service -> repository
// SPRING DATA JPA(리포지터리) -> JPA -> 하이버네이트 -> JDBC -> MySQL Driver -> MySQL

// 컨트롤러는 Repository가 있는지 몰라야 한다.
// 서비스는 웹브라우저라는것이 이 세상에 존재하는지 몰라야 한다.
// 리포지터리는 서비스를 몰라야 한다.
// 서비스는 컨트롤러를 몰라야 한다.
// DB는 리포지터리를 몰라야 한다.
// SPRING DATA JPA는 MySQL을 몰라야 한다.

public class QuesionController {
    // @Autowired // 필드 주입
    private final QuestionService questionService;
    @RequestMapping("/list")
    /* 이 자리에 @ResponseBody가 없으면
    resources/question_list/question_list.html 파일을 뷰로 삼는다.
    */
    public String list(Model model) {
        List<Question> questionList = questionService.getList();
        //모든 question들을 조회해 리스트로 만든다.
        /*
        미래에 실행될 question_list.html 에서
        questionList 라는 이름으로 questionList 변수를 사용할 수 있다.
        */
        model.addAttribute("questionList", questionList);
        //questionList를 question_list에 전달해주는 스프링부트의 전용 변수 - Model

            return "question_list";
        }
    @RequestMapping("/detail/{id}")
    public String detail(Model model, @PathVariable int id) {
        Question question = questionService.getQuestion(id);

        model.addAttribute("question", question);

        return "question_detail";
    }


    @GetMapping("/create")
    public String questionCreate() {
        return "question_form";
    }

    @PostMapping("/create")
    //controller가 model이라는 객체를 파라미터로 받게 하는 메서드
    public String questionCreate(Model model, QuestionFrom questionFrom) {
        boolean hasError = false;

        if (questionFrom.getSubject() == null || questionFrom.getSubject().trim().length() == 0) {
            model.addAttribute("subjectErrorMsg", "제목 좀...");
            hasError = true;
        }

        if (questionFrom.getContent() == null || questionFrom.getContent().trim().length() == 0) {
            model.addAttribute("contentErrorMsg", "내용 좀...");
            hasError = true;
        }

        if (hasError) {
            model.addAttribute("questionFrom", questionFrom);
            return "question_form";
        }

        questionService.create(questionFrom.getSubject(), questionFrom.getContent());
        return "redirect:/question/list"; // 질문 저장후 질문목록으로 이동
    }
}
