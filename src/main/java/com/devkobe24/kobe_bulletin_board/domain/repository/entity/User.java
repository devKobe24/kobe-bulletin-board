package com.devkobe24.kobe_bulletin_board.domain.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false, name = "user_name")
	private String userName;

	@Column(name = "created_at")
	private Timestamp createdAt;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private UserCredentials userCredentials;

	public void setCredentials(UserCredentials credentials) {
		this.userCredentials = credentials;
	}
}
