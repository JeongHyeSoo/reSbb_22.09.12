package com.ll.exam.sbb.question;

import com.ll.exam.sbb.DataNotFoundException;
import com.ll.exam.sbb.answer.AnswerForm;
import com.ll.exam.sbb.user.SiteUser;
import com.ll.exam.sbb.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
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
    private final UserService userService;
    @GetMapping("/list")
    /* 이 자리에 @ResponseBody가 없으면
    resources/question_list/question_list.html 파일을 뷰로 삼는다.
    */
    public String list(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<Question> paging = questionService.getList(page);
        //question들을 조회해 리스트로 만든다.
        /*
        미래에 실행될 question_list.html 에서
        questionList 라는 이름으로 questionList 변수를 사용할 수 있다.
        */
        model.addAttribute("paging", paging);
        //questionList를 question_list에 전달해주는 스프링부트의 전용 변수 - Model

            return "question_list";
        }
    //@RequestMapping("/detail/{id}")아래 함수의 리턴값을 그대로 브라우저에 표시
    //파라미터가 있어야 하니 get으로 변경
    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable long id, AnswerForm answerForm) {
        Question question = questionService.getQuestion(id);

        model.addAttribute("question", question);

        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Question question = this.questionService.getQuestion(id);

        if ( question == null ) {
            throw new DataNotFoundException("%d번 질문은 존재하지 않습니다.");
        }

        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());

        return "question_form";
    }


    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm) {
        //빈 객체라도 생기도록 써준 것 QuestionForm과 같이 바인딩한 객체는 Model 객체로 전달하지 않아도 템플릿에서 사용이 가능하다.
        return "question_form";
    }

    @PostMapping("/create")
    //controller가 model이라는 객체를 파라미터로 받게 하는 메서드
    public String questionCreate(Principal principal, Model model, @Valid QuestionForm questionForm, BindingResult bindingResult) {
        //@Valid 사용시 QuestionForm의 룰을 하나씩 체크, 문제는 BindingResult안에 담긴다.
        // 위처럼 @Valid와 BindingResult는 순서를 바꾸지 않고 이어서 적어야 한다.

        if (bindingResult.hasErrors()) {
            //BindingError 안에 이미 에러에 대한 것이 구현되어 있다.
            return "question_form";
        }
        SiteUser siteUser = userService.getUser(principal.getName());
        questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
        return "redirect:/question/list"; // 질문 저장후 질문목록으로 이동
    }
}
