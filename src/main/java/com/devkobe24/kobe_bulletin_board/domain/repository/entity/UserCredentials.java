package com.devkobe24.kobe_bulletin_board.domain.repository.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_credentials")
public class UserCredentials {

	@Id
	@OneToOne
	@JoinColumn(name = "user_id", unique = true)
	private User user;

	@Column(nullable = false, name = "hashed_password", length = 60)
	private String hashedPassword;
}
