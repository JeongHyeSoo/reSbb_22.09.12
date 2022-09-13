package com.ll.exam.sbb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AnswerRepositoryTests {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    private int lastSampleDataId;

    @BeforeEach
    //본 어노테이션을 붙인 메서드는 테스트 메서드(@Test) 실행 이전에 수행된다.
    void beforeEach() {
        clearData();
        createSampleData();
    }

//clearData와 createSampleData가 question,answerTests 모두에
// 중복으로 있었기에 questionRepository의 것을 가져와 사용한다.
    private void clearData() {
        QuestionRepositoryTests.clearData(questionRepository);
        //questionRepository truncate는 이제 questionRepositoryTests의 clearData에서 실행한다.
        answerRepository.deleteAll(); // DELETE FROM question;
        answerRepository.truncateTable();
        /*RepositoryUtil을 인터페이스 다중 상속을 통해 QuestionRepository와 EnswerRepository 모두
        각자의 repository를 참조하여 외래키 관계를 끊을 수 있도록 변경*/
        //truncateTable()
    }

    private void createSampleData() {
        QuestionRepositoryTests.createSampleData(questionRepository);

        Question q = questionRepository.findById(1).get();

        //answer sampledata 추가
        Answer a1 = new Answer();
        a1.setContent("sbb는 질문답변 게시판 입니다.");
        a1.setCreateDate(LocalDateTime.now());
        q.addAnswer(a1);

        q.getAnswerList().add(a1);

        Answer a2 = new Answer();
        a2.setContent("sbb에서는 주로 스프링부트관련 내용을 다룹니다.");
        a2.setCreateDate(LocalDateTime.now());
        q.addAnswer(a2);

        questionRepository.save(q);
        //CascadeType.ALL로 바뀌면서 questionRepository.save만 해줘도 알아서 answerRepository.save가된 다.
    }

    @Test
    @Transactional
    @Rollback(false)
    void 저장() {
        Question q = questionRepository.findById(2).get();

        Answer a1 = new Answer();
        a1.setContent("네 자동으로 생성됩니다.");
        a1.setCreateDate(LocalDateTime.now());

        q.addAnswer(a1);

        Answer a2 = new Answer();
        a2.setContent("네네 맞아요!!~:)");
        a2.setCreateDate(LocalDateTime.now());

        q.addAnswer(a2);
    }

    @Test
    @Transactional
    @Rollback(false)
    void 조회() {
        Answer a = this.answerRepository.findById(1).get();
        assertThat(a.getContent()).isEqualTo("sbb는 질문답변 게시판 입니다.");
    }

    //실행시켜보면 ANSWER만 가져오는 게 아니라 QUESTION 정보도 함께 가져온다.
    @Test
    @Transactional
    @Rollback(false)
    void 관련된_question_조회() {
        Answer a = this.answerRepository.findById(1).get();
        Question q = a.getQuestion();

        assertThat(q.getId()).isEqualTo(1);
    }


    @Test
    @Transactional
    @Rollback(false)
    void question으로부터_관련된_질문들_조회() {
        // SELECT * FROM question WHERE id = 1
        Question q = questionRepository.findById(1).get();
        // DB 연결이 끊김

        // SELECT * FROM answer WHERE question_id = 1
        List<Answer> answerList = q.getAnswerList();

        assertThat(answerList.size()).isEqualTo(2);
        assertThat(answerList.get(0).getContent()).isEqualTo("sbb는 질문답변 게시판 입니다.");
    }
}