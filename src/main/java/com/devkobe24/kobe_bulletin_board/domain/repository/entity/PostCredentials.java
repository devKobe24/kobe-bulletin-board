package com.devkobe24.kobe_bulletin_board.domain.repository.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "post_credentials")
public class PostCredentials {

	@Id
	@Column(name = "post_ic")
	private Long postId;

	@OneToOne
	@MapsId
	@JoinColumn(name = "post_id", referencedColumnName = "id")
	private Post post;

	@Column(name = "hashed_password", length = 60, nullable = false)
	private String hashedPassword;

	@Column(name = "token", nullable = false)
	private String postToken;

	@Column(name = "is_revoked", nullable = false)
	private boolean isRevoked;

	@Column(name = "is_expired", nullable = false)
	private boolean isExpired;
}
