package com.ll.exam.sbb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

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
        a1.setQuestion(q);
        a1.setCreateDate(LocalDateTime.now());
        answerRepository.save(a1);

        Answer a2 = new Answer();
        a2.setContent("sbb에서는 주로 스프링부트관련 내용을 다룹니다.");
        a2.setQuestion(q);
        a2.setCreateDate(LocalDateTime.now());
        answerRepository.save(a2);
    }

    @Test
    void 저장() {
        Question q = questionRepository.findById(2).get();

        Answer a = new Answer();
        a.setContent("네 자동으로 생성됩니다.");
        a.setQuestion(q);
        a.setCreateDate(LocalDateTime.now());
        answerRepository.save(a);
    }

    @Test
    void 조회() {
        Answer a = this.answerRepository.findById(1).get();
        assertThat(a.getContent()).isEqualTo("sbb는 질문답변 게시판 입니다.");
    }
}