package com.devkobe24.kobe_bulletin_board.domain.repository.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "manager_credentials")
public class ManagerCredentials {

	@Id
	@Column(name = "manager_number", nullable = false, length = 60)
	private String managerNumber;

	@OneToOne(optional = false)
	@JoinColumn(name = "manager_id", unique = true)
	private Manager manager;

	@Column(nullable = false, name = "hashed_password", length = 60)
	private String hashedPassword;
}
