package com.devkobe24.kobe_bulletin_board.domain.repository.entity;

import com.devkobe24.kobe_bulletin_board.common.role.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_credentials")
public class UserCredentials {

	@Id
	private Long userId;

	@OneToOne
	@MapsId
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;

	@Column(nullable = false, name = "hashed_password", length = 60)
	private String hashedPassword;

	@Enumerated(EnumType.STRING) // Enum을 문자열로 저장
	@Column(nullable = false, length = 50)
	private UserRole role; // Enum 사용.
}
