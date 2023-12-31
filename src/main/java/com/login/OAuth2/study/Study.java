package com.login.OAuth2.study;

import com.login.OAuth2.domain.user.users.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "study")
@Getter
@Entity
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "leader_id")
    private User leader;

    @Enumerated(EnumType.STRING)
    private StudyType studyType;

    @Enumerated(EnumType.STRING)
    private StudyMethod studyMethod;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    @Column(columnDefinition = "TEXT")
    private String goal;

    @Column(columnDefinition = "TEXT")
    private String recruitCondition;

    private LocalDateTime createDate;
    private LocalDateTime deadline;

    @Builder.Default
    @ManyToMany(mappedBy = "myStudies")
    private Set<User> myUsers = new HashSet<>();

    @Builder.Default
    @ManyToMany(mappedBy = "scrapStudies")
    private Set<User> scrapUsers = new HashSet<>();
}
