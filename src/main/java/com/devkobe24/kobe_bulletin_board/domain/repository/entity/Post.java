package com.devkobe24.kobe_bulletin_board.domain.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "post")
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String content;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "writer", referencedColumnName = "nick_name", nullable = false)
	private User user;

	@Column(name = "created_at")
	private Timestamp createdAt;

	@Column(name = "view_count")
	private Integer viewCount;

	@Column(nullable = false, length = 60)
	private String password;

	// viewCount를 증가시키는 메서드
	public void incrementViewCount() {
		if (viewCount == null) {
			viewCount = 0;
		}
		viewCount++;
	}
}
